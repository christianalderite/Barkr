package com.example.christianalderite.barkr.ProgramStuff;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.christianalderite.barkr.EditAccount;
import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.PetStuff.AddPet;
import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.UserModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.nio.channels.GatheringByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddProgram extends AppCompatActivity {
    private String[] imagePermissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    private EditText editTitle, editLocation, editStartTime, editEndTime, editDescription;
    private Button btnSubmit, btnCancel;
    private String programId, hostDisplayName, hostEmail;
    String programImageUri;
    Uri programImageFirebaseUri;
    ImageView programImage;
    boolean newImage;

    final static int PLACE_PICKER_REQUEST = 17;
    final static int CAMERA_REQUEST = 12;
    final static int GALLERY_REQUEST = 15;

    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_program);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF33B5E5));
        getSupportActionBar().setElevation(0);
        
        programImage = findViewById(R.id.programHeader);

        editTitle = findViewById(R.id.titleET);
        editLocation = findViewById(R.id.locationET);
        editStartTime = findViewById(R.id.starttimET);
        editEndTime = findViewById(R.id.endtimeET);
        editDescription = findViewById(R.id.descET);

        btnSubmit = findViewById(R.id.okButton);
        btnCancel = findViewById(R.id.cancelButton);

        prepareUserInfo();

        Intent fromAdapter = getIntent();
        if(fromAdapter.getExtras() != null) {
            Bundle bundle = fromAdapter.getExtras();
            programId = bundle.getString("programId");
            programImageUri = (bundle.getString("programImageUri"));

            editTitle.setText(bundle.getString("name"));
            editLocation.setText(bundle.getString("location"));
            editStartTime.setText(bundle.getString("startTime"));
            editEndTime.setText(bundle.getString("endTime"));
            editDescription.setText(bundle.getString("description"));

            try {
                Picasso.with(AddProgram.this).load(programImageUri).fit().centerCrop().into(programImage);
            }catch (Exception e){
                programImage.setScaleType(ImageView.ScaleType.CENTER);
                programImage.setImageResource(R.drawable.ic_menu_camera);
            }
        }

        programImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
                    if(arePermssionsEnabled()){
                        final CharSequence[] items = {"from Camera", "from Gallery"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddProgram.this);
                        builder.setTitle("Get your pet's photo...");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int item) {
                                if(items[item].equals("from Camera")){
                                    takePicture();
                                }
                                if(items[item].equals("from Gallery")){
                                    pickPhoto();
                                }
                            }
                        });
                        builder.show();
                    }else{
                        requestMultiplePermissions();
                    }
                }
            }
        });


        editEndTime.setEnabled(false);

        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePicker(0);
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePicker(1);
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddProgram.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    Toast.makeText(AddProgram.this, "Please update Google Play Services", Toast.LENGTH_SHORT).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Toast.makeText(AddProgram.this, "Google Play Services is not installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(editTitle.getText().toString())
                        && !TextUtils.isEmpty(editLocation.getText().toString())
                        && !TextUtils.isEmpty(editDescription.getText().toString())
                        && !TextUtils.isEmpty(editStartTime.getText())
                        && !TextUtils.isEmpty(editEndTime.getText())) {

                    if(programId==null){
                        if(programImageFirebaseUri!=null){
                            uploadImage();
                        }else{
                            Toast.makeText(AddProgram.this, "Please provide image first.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(programImageFirebaseUri!=null) {
                            uploadImage();
                        }else{
                            saveProgramToDb(programImageUri);
                        }
                    }

                }else{
                    Toast.makeText(AddProgram.this, "Please fill all fields." , Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    public void uploadImage() {
        StorageReference filePath = storage.getReference("images").child(UUID.randomUUID().toString());
        filePath.putFile(programImageFirebaseUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri = taskSnapshot.getDownloadUrl();
                        saveProgramToDb(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProgram.this, "Image upload error.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void saveProgramToDb(String uri){
        final DatabaseReference refPrograms = database.getReference("programs");
        final DatabaseReference refProgramParticipants = database.getReference("programParticipants");

        if(programId==null) {
            programId = refPrograms.push().getKey();
        }

        ProgramModel program = new ProgramModel(programId,
                user.getUid(),
                editTitle.getText().toString(),
                hostDisplayName,
                hostEmail,
                editLocation.getText().toString(),
                editStartTime.getText().toString(),
                editEndTime.getText().toString(),
                editDescription.getText().toString(),
                uri);

        refPrograms.child(program.getProgramId()).setValue(program);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddProgram.this);
        ParticipantModel you =  new ParticipantModel(user.getUid(),
                sharedPreferences.getString("userName",""),
                sharedPreferences.getString("userImageUri",""));

        refProgramParticipants.child(programId).child(user.getUid()).setValue(you);

        finish();
    }


    public void prepareUserInfo(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        hostDisplayName = sharedPreferences.getString("userName","");
        hostEmail = sharedPreferences.getString("userEmail","");
    }

    public void dateTimePicker(final int requestCode){
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(AddProgram.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                final String date = month + "/" + day + "/" + year;

                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog1 = new TimePickerDialog(AddProgram.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String append = "AM";
                        if(hour==12){
                            append="PM";
                        }
                        else if(hour>12 && hour<24){
                            hour-=12;
                            append="PM";
                        }else if(hour==24){
                            hour-=12;
                            append="AM";
                        }
                        String time = hour +":"+minute+" "+append;
                        String datetime = date +" "+ time;
                        if(requestCode==0){
                            editStartTime.setText(datetime);
                            editEndTime.setEnabled(true);
                        }
                        if(requestCode==1){
                            editEndTime.setText(datetime);
                        }
                    }
                }, hour, minute, true);
                dialog1.show();
            }
        },year, month, day);
        dialog.show();
    }

    public void takePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture,CAMERA_REQUEST);
    }

    public void pickPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode,resultCode,imageReturnedIntent);
        if(resultCode==RESULT_OK){
            if(requestCode==CAMERA_REQUEST){
                Bitmap selectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                programImageFirebaseUri = Uri.parse(MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), selectedImage, "pet_pic",null));
                programImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                programImage.setImageBitmap(selectedImage);
                newImage = true;
            }
            if(requestCode==GALLERY_REQUEST){
                try{
                    programImageFirebaseUri = imageReturnedIntent.getData();
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), programImageFirebaseUri);
                    programImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    programImage.setImageBitmap(selectedImage);
                    newImage = true;
                }catch (Exception e){
                    Toast.makeText(AddProgram.this, "Oops! Could not parse image.", Toast.LENGTH_SHORT).show();
                }
            }
            if(requestCode == PLACE_PICKER_REQUEST){
                final Place place = PlacePicker.getPlace(this, imageReturnedIntent);
                Geocoder geocoder = new Geocoder(this);
                try{
                    List<Address> addressList = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    String address = addressList.get(0).getAddressLine(0);
                    String city = addressList.get(0).getAddressLine(1);
                    String country= addressList.get(0).getAddressLine(2);
                    editLocation.setText(address +", "+city +", "+country);
                }catch (Exception e){
                    editLocation.setText(place.getAddress().toString());
                }

            }
        }

    }

    public boolean arePermssionsEnabled(){
        for (String permission: imagePermissions){
            if(checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for(String permission: imagePermissions){
            if(checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 101){
            for(int i = 0; i<grantResults.length; i++){
                if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(imagePermissions[i])){
                        new AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setMessage("You need "+imagePermissions[i]+" in order to access this feature.")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestMultiplePermissions();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }
                    return;
                }
            }
        }
    }
}