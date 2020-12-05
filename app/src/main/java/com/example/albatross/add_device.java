package com.example.albatross;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class add_device extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseAuth firebaseAuth ;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser curr_user;


    //firebase connection

    private FirebaseFirestore dp = FirebaseFirestore.getInstance();
    private CollectionReference   collectionReference =dp.collection("User's") ;
    private StorageReference mStorageRef ;

    String currentPhotoPath;
    String downloadUri;

    private EditText first ;
    private EditText last  ;
    private EditText Email;
    private EditText password ;
    private EditText password2 ;
    private EditText phone_num ;
    private ProgressBar prog ;
    private Button  connect  ;
    private Button getPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device3);

        firebaseAuth = FirebaseAuth.getInstance() ;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        first = findViewById(R.id.Name) ;
        last = findViewById(R.id.Name2);
        connect = findViewById(R.id.connect_device) ;
        prog = findViewById(R.id.create_user);
        Email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        phone_num = findViewById(R.id.Phone);
        getPhoto = findViewById(R.id.uploadphoto);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                curr_user = firebaseAuth.getCurrentUser() ;
                if(curr_user != null){
                    //user is already logged in
                }else {
                    //no user yet
                }
            }
        };


        getPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            askCameraPermission();

            }
        });



        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(Email.getText().toString())
                    &&!TextUtils.isEmpty(password.getText().toString())
                    &&!TextUtils.isEmpty(password2.getText().toString())
                    &&!TextUtils.isEmpty(phone_num.getText().toString())
                    &&!TextUtils.isEmpty(first.getText().toString())
                    &&!TextUtils.isEmpty(last.getText().toString())){

                    if( password.getText().toString() != password2.getText().toString()){
                        Log.d("DBUG", "pass1 :" + password.getText().toString() + "pass2 : " +
                                password2.getText().toString());
                        //need to correct password
                    }

                    String email = Email.getText().toString();
                    String pass  = password.getText().toString() ;
                    String phone = phone_num.getText().toString();
                    String first_name = first.getText().toString();
                    String last_name  = last.getText().toString();
                    String imageUrl_copy = downloadUri ;


                    createUser(email, pass, phone,first_name ,last_name ,imageUrl_copy );

                    // what to do iupon success
                    Intent  goToLoggedIn  = new Intent(add_device.this , LogedIn.class);
                    goToLoggedIn.putExtra("FullName",first_name + last_name );
                    goToLoggedIn.putExtra("Email" ,email );
                    goToLoggedIn.putExtra( "Phone" ,phone );
                    startActivity( goToLoggedIn) ;

                } else {

                    Toast.makeText(add_device.this ,
                            "Empty fields Not allowed"
                            ,Toast.LENGTH_SHORT).show();

                }

            }

        });

    }

    private  void askCameraPermission() {


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this ,new String[] {Manifest.permission.CAMERA} ,101);


        }else {

            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 101){



            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED ){

                dispatchTakePictureIntent();

            }else{
                Toast.makeText(add_device.this ,
                        "Camera Permission is required"
                        ,Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == 102 ){
            if(resultCode== Activity.RESULT_OK){

                File f = new File(currentPhotoPath);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);

                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFireBase(f.getName(), contentUri);


            }
        }
    }

    private void uploadImageToFireBase(String name, Uri contentUri) {
         StorageReference Image = mStorageRef.child("images/" + name ) ;
         Image.putFile( contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Toast.makeText(add_device.this ,
                                "Nest Connected"
                                ,Toast.LENGTH_LONG).show();
                                downloadUri = uri.toString();
                                connect.setVisibility(View.VISIBLE);

                    }
                });
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {


             }
         });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;

            try {

                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, 102);


            }
        }
    }

    private  void  createUser(String  Email , String password , String phone_num , String first_name
                                 , String last_name , String imageUrl_copy ){

        if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(password)
                       && !TextUtils.isEmpty(password) ){

                prog.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(Email ,phone_num)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //we take user
                                //create document for the user
                                //finsh the creation

                                curr_user  = firebaseAuth.getCurrentUser() ;
                                String curr_user_ID = curr_user.getUid() ;

                                Map<String ,String>  userObj =new HashMap<>() ;

                                userObj.put("userID",curr_user_ID ) ;
                                userObj.put("First Name" ,first_name) ;
                                userObj.put("Last Name" ,last_name) ;
                                userObj.put("userphone" ,phone_num) ;
                                userObj.put("Nest Url" , imageUrl_copy) ;

                                //save to firestore database
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                      if(task.getResult().exists()){
                                                          prog.setVisibility(View.INVISIBLE);
                                                          Toast.makeText(add_device.this ,
                                                                  "Congratulations your Nest is Connected !!"
                                                                  ,Toast.LENGTH_SHORT).show();
                                                          prog.setVisibility(View.INVISIBLE);
                                                          // need to create post succes activity ...

                                                      }else{
                                                          Toast.makeText(add_device.this ,
                                                                  "Registration failed, Retry"
                                                                  ,Toast.LENGTH_LONG).show();
                                                          prog.setVisibility(View.INVISIBLE);
                                                      }


                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                prog.setVisibility(View.INVISIBLE);
                                                Toast.makeText(add_device.this ,
                                                        "Registration failed, Retry"
                                                        ,Toast.LENGTH_LONG).show();

                                            }
                                        });

                            } else {
                                prog.setVisibility(View.INVISIBLE);
                                Toast.makeText(add_device.this ,
                                        "Registration failed, Retry"
                                        ,Toast.LENGTH_LONG).show();

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                prog.setVisibility(View.INVISIBLE);
                                Toast.makeText(add_device.this ,
                                        "Registration failed, Retry"
                                        ,Toast.LENGTH_LONG).show();
                            }
                        });


        }else{
            Toast.makeText(add_device.this ,
                    "Empty fields Not allowed"
                    ,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        curr_user = firebaseAuth.getCurrentUser() ;
        firebaseAuth.addAuthStateListener(authStateListener);

    }
    public void gotoProfile(View v){
        Intent intent= new Intent(this,LogedIn.class);
        startActivity(intent);
    }
}