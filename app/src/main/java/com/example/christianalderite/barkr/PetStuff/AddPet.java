package com.example.christianalderite.barkr.PetStuff;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddPet extends AppCompatActivity {
    private String[] imagePermissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private EditText editName, editBreed, editBirthDate, editDescription, gender;
    private RadioGroup radioGroup;
    private RadioButton radioBtnGender;
    private RadioButton radioBtnMale;
    private RadioButton radioBtnFemale;
    private Button btnSubmit, btnCancel;
    private ImageView petImage;
    private Uri petImageFirebaseUri;
    private int position;
    private String petId, ownerDisplayName, petImageUri;

    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        getSupportActionBar().setElevation(0);

        petImage = findViewById(R.id.imageViewPet);

        editName = findViewById(R.id.petnameET);
        editBreed = findViewById(R.id.breedET);
        editBirthDate = findViewById(R.id.birthdateET);
        editDescription = findViewById(R.id.descriptionET);
        btnSubmit = findViewById(R.id.addpetButton);
        btnCancel = findViewById(R.id.btnCancel);

        radioGroup = findViewById(R.id.radioGroup);
        radioBtnMale = findViewById(R.id.radioBtnMale);
        radioBtnFemale = findViewById(R.id.radioBtnFemale);

        prepareUserInfo();

        Intent fromViewPet = getIntent();
        if(fromViewPet.getExtras() != null) {

            Bundle bundle = fromViewPet.getExtras();
            this.setTitle("Edit "+bundle.getString("name")+"'s info...");

            position = bundle.getInt("position");
            petId = bundle.getString("petId");
            petImageUri = (bundle.getString("imageUri"));

            editName.setText(bundle.getString("name"));
            editBreed.setText(bundle.getString("breed"));
            editBirthDate.setText(bundle.getString("birthDate"));
            editDescription.setText(bundle.getString("others"));

            if(bundle.getString("gender").equalsIgnoreCase("male")){
                radioBtnMale.setChecked(true);
            }else{
                radioBtnFemale.setChecked(true);
            }

            Utilities.loadImage(AddPet.this, petImageUri, petImage);
        }

        petImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
                    if(arePermssionsEnabled()){
                        final CharSequence[] items = {"from Camera", "from Gallery"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddPet.this);
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

        editBirthDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddPet.this, new DatePickerDialog.OnDateSetListener() {
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

                if(!TextUtils.isEmpty(editName.getText().toString())
                        && !TextUtils.isEmpty(editBreed.getText().toString())
                        && !TextUtils.isEmpty(editDescription.getText().toString())
                        && !TextUtils.isEmpty(editBirthDate.getText())
                        && !TextUtils.isEmpty(radioBtnGender.getText())){

                    if(petId==null){
                        if(petImageFirebaseUri!=null){
                            uploadImage();
                        }else{
                            Toast.makeText(AddPet.this, "Please provide image first.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(petImageFirebaseUri!=null) {
                            uploadImage();
                        }else{
                            saveUserToDb(petImageUri);
                        }
                    }
                }else{
                    Toast.makeText(AddPet.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void uploadImage() {
        StorageReference filePath = storage.getReference("images").child(UUID.randomUUID().toString());
        filePath.putFile(petImageFirebaseUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri = taskSnapshot.getDownloadUrl();
                        saveUserToDb(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPet.this, "Image upload error.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void saveUserToDb(String setUri){
        final DatabaseReference refPets = database.getReference("pets");

        if(petId==null){
            petId = refPets.push().getKey();
        }

        PetModel pet = new PetModel(petId,
                user.getUid(),
                editName.getText().toString(),
                ownerDisplayName,
                editBreed.getText().toString(),
                editBirthDate.getText().toString(),
                radioBtnGender.getText().toString(),
                editDescription.getText().toString(),
                setUri);

        refPets.child(pet.getPetId()).setValue(pet);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddPet.this);
        if(sharedPreferences.getString("currentPetId","").equals(pet.getPetId())){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("currentPetName", pet.getName());
            editor.putString("currentPetImageUri", pet.getPetImageUri());
            editor.apply();
        }

        returnToViewPet(pet);
    }

    public void returnToViewPet(PetModel pet){
        Intent toViewPet = new Intent(AddPet.this, ViewPet.class);
        toViewPet.putExtra("position", position);
        toViewPet.putExtra("petId", pet.getPetId());
        toViewPet.putExtra("name", pet.getName());
        toViewPet.putExtra("ownerDisplayName", pet.getOwnerDisplayName());
        toViewPet.putExtra("breed", pet.getBreed());
        toViewPet.putExtra("gender", pet.getGender());
        toViewPet.putExtra("birthDate", pet.getBirthdate());
        toViewPet.putExtra("others", pet.getOthers());
        toViewPet.putExtra("imageUri", pet.getPetImageUri());
        startActivity(toViewPet);
        finish();
    }

    public void prepareUserInfo(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ownerDisplayName = sharedPreferences.getString("userName","");
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
                petImageFirebaseUri = Uri.parse(MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), selectedImage, "pet_pic",null));
                petImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                petImage.setImageBitmap(selectedImage);
            }
            if(requestCode==1){
                try{
                    petImageFirebaseUri = imageReturnedIntent.getData();
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), petImageFirebaseUri);
                    petImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    petImage.setImageBitmap(selectedImage);
                }catch (Exception e){
                    Toast.makeText(AddPet.this, "Oops! Could not parse image.", Toast.LENGTH_SHORT).show();
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