package com.example.christianalderite.barkr;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.christianalderite.barkr.IntroStuff.AddFirstPet;
import com.example.christianalderite.barkr.IntroStuff.Register;
import com.example.christianalderite.barkr.PetStuff.AddPet;
import com.example.christianalderite.barkr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EditAccount extends AppCompatActivity {

    private String[] imagePermissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private EditText editFirstName, editLastName, editBirthDate, editBio;
    private RadioGroup radioGroup;
    private RadioButton radioBtnGender, radioBtnMale, radioBtnFemale;
    private Button btnSubmit, btnCancel;
    private ImageView userImage;
    private Uri userImageFirebaseUri;
    private String userImageUri, existenceId;

    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        setTitle("Edit your profile...");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF669900));
        getSupportActionBar().setElevation(0);

        userImage = findViewById(R.id.imageViewUser);

        editFirstName= findViewById(R.id.userDisplayName);
        editBirthDate = findViewById(R.id.userBirthDate);
        editBio = findViewById(R.id.userBio);
        btnSubmit = findViewById(R.id.userAdd);
        btnCancel = findViewById(R.id.btnCancel);
        radioBtnMale = findViewById(R.id.radioBtnMale);
        radioBtnFemale = findViewById(R.id.radioBtnFemale);

        radioGroup = findViewById(R.id.radioGroup);

            userImageUri = sharedPreferences.getString("userImageUri","");
            editFirstName.setText(sharedPreferences.getString("userName",""));
            editBirthDate.setText(sharedPreferences.getString("userBirthDate",""));
            editBio.setText(sharedPreferences.getString("userBio",""));

            if(sharedPreferences.getString("userGender","").equalsIgnoreCase("male")){
                radioBtnMale.setChecked(true);
            }else{
                radioBtnFemale.setChecked(true);
            }

            try {
                Picasso.with(this).load(userImageUri).fit().centerCrop().into(userImage);
            }catch (Exception e){
                userImage.setScaleType(ImageView.ScaleType.CENTER);
                userImage.setImageResource(R.drawable.ic_menu_camera);
            }

        userImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
                    if(arePermssionsEnabled()){
                        final CharSequence[] items = {"from Camera", "from Gallery"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccount.this);
                        builder.setTitle("Get your user's photo...");
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

        editBirthDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EditAccount.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        final String date = month + "/" + day + "/" + year;
                        editBirthDate.setText(date);
                    }
                },year, month, day);
                dialog.show();
            }

        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioBtnGender = findViewById(radioGroup.getCheckedRadioButtonId());

                if(!TextUtils.isEmpty(editFirstName.getText().toString())
                        && !TextUtils.isEmpty(editBio.getText().toString())
                        && !TextUtils.isEmpty(editBirthDate.getText())
                        && !TextUtils.isEmpty(radioBtnGender.getText())){

                        if(userImageFirebaseUri!=null) {
                            uploadImage();
                        }else{
                            uploadUserInfo(userImageUri);
                        }

                }else{
                    Toast.makeText(EditAccount.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void uploadImage() {
        Utilities.showLoadingDialog(EditAccount.this);
        StorageReference filePath = storage.getReference("images").child(UUID.randomUUID().toString());
        filePath.putFile(userImageFirebaseUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Utilities.dismissDialog();
                        Uri uri = taskSnapshot.getDownloadUrl();
                        uploadUserInfo(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utilities.dismissDialog();
                        Toast.makeText(EditAccount.this, "Image upload error.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void uploadUserInfo(String uri){
        final DatabaseReference refUsers = database.getReference("users").child(user.getUid());

        UserModel you = new UserModel(user.getUid(), user.getEmail(),
                editFirstName.getText().toString(), editBirthDate.getText().toString(), 
                radioBtnGender.getText().toString(), editBio.getText().toString(), uri);

        refUsers.setValue(you);
        loadUserToSharedPref(you);
        returnToAccount();

    }

    public void loadUserToSharedPref(UserModel userModel){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userModel.getUid());
        editor.putString("userName", userModel.getDisplayName());
        editor.putString("userImageUri", userModel.getPhotoUri());
        editor.putString("userBio", userModel.getBio());
        editor.putString("userEmail", userModel.getEmail());
        editor.putString("userBirthDate", userModel.getBirthDate());
        editor.putString("userGender", userModel.getGender());
        editor.apply();
    }

    public void returnToAccount(){
        Intent returnToAccount = new Intent(EditAccount.this, HomeActivity.class);
        this.setResult(RESULT_OK, returnToAccount);
        this.finish();
    }

    public void takePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture,0);
    }

    public void pickPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode,resultCode,imageReturnedIntent);
        if(resultCode==RESULT_OK){
            if(requestCode==0){
                Bitmap selectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                userImageFirebaseUri = Uri.parse(MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), selectedImage, "user_pic",null));
                userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                userImage.setImageBitmap(selectedImage);
            }
            if(requestCode==1){
                try{
                    userImageFirebaseUri = imageReturnedIntent.getData();
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), userImageFirebaseUri);
                    userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    userImage.setImageBitmap(selectedImage);
                }catch (Exception e){
                    Toast.makeText(EditAccount.this, "Oops! Could not parse image.", Toast.LENGTH_SHORT).show();
                }
            }

        }else{
            Toast.makeText(this, "You haven't chosen an image.", Toast.LENGTH_SHORT).show();
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

