package com.example.apptenants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private CircularImageView profileImage;
    //    FloatingActionButton uploadPhoto;
    private Button closeBtn, saveBtn;
    private TextView changeProfileBtn;
    private EditText editName,editContact,editEmail;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser tenant;
    private String tenantID;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        tenant = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Tenants");
        tenantID = tenant.getUid();
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Images");

        profileImage = findViewById(R.id.profileImage);

        closeBtn = findViewById(R.id.closeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        editName = findViewById(R.id.tenantName);
        editEmail = findViewById(R.id.tenantEmail);
        editContact = findViewById(R.id.tenantContact);
        changeProfileBtn = findViewById(R.id.changeProfileBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfile.this,Profile.class));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSave();
//                uploadProfileImage();
            }
        });


        changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CropImage.activity().setAspectRatio(1,1).start(EditProfile.this);
                ImagePicker.Companion.with(EditProfile.this)
                        .galleryOnly()
                        .crop(1,1)
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start(10);
            }
        });

        getUserinfo();
    }

    private void validateAndSave(){
        if (TextUtils.isEmpty(editName.getText().toString()))
        {
            Toast.makeText(this, "Please enter you Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(editEmail.getText().toString()))
        {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(editContact.getText().toString()))
        {
            Toast.makeText(this, "Please enter your Contact Number", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String,Object> userMap = new HashMap<>();
            userMap.put("nameTenant",editName.getText().toString());
            userMap.put("emailTenant",editEmail.getText().toString());
            userMap.put("contactTenant",editContact.getText().toString());

//            reference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
            reference.child(tenantID).updateChildren(userMap);
            uploadProfileImage();
            startActivity(new Intent(EditProfile.this,Profile.class));
        }
    }

    private void getUserinfo() {
        reference.child(tenantID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tenant tenantEdit = snapshot.getValue(Tenant.class);
                if (tenantEdit != null) {
                    String name = tenantEdit.nameTenant;
                    String email = tenantEdit.emailTenant;
                    String contact = tenantEdit.contactTenant;

                    editName.setText(name);
                    editEmail.setText(email);
                    editContact.setText(contact);

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 100 && data != null && data.getData() != null) {
//
//            imageUri = data.getData();
//            binding.profileImage.setImageURI(imageUri);
//        }
        if (requestCode == 10 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set your profile picture");
        progressDialog.setMessage("Please wait, while we are setting your data");
        progressDialog.show();

        if (imageUri != null){
//            final StorageReference fileRef = storageProfilePicsRef
//                    .child(mAuth.getCurrentUser().getUid()+".jpg");
            final StorageReference fileRef = storageProfilePicsRef
                    .child(tenantID+".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }) .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl =task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image",myUri);

//                        reference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        reference.child(tenantID).updateChildren(userMap);

                        progressDialog.dismiss();
                    }
                }
            });
        }

        else {
            progressDialog.dismiss();
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }
}