package com.example.billit_all.Calculate;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.billit_all.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class OcrActivity extends AppCompatActivity {

    private MaterialButton inputImageBtn, recognizeTextBtn, pickImage;
    private EditText recognizedTextEt;
    private ShapeableImageView imageIv;

    private static final String TAG = "MAIN_TAG";

    private Uri imageUri = null;

    //to handle the result of camera/gallery permissions
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    //arrays of permission required to pick image from gallery
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private ProgressDialog progressDialog;

    private TextRecognizer textRecognizer;
    Button pb,pc,pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        //init//
        inputImageBtn = findViewById(R.id.inputImageBtn);
        recognizeTextBtn = findViewById(R.id.recognizeTextBtn);
        recognizedTextEt = findViewById(R.id.recognizedTextEt);
        imageIv = findViewById(R.id.imageIv);
        pickImage = findViewById(R.id.pickImage);
        pb = findViewById(R.id.proceed_btn);

        //read text button set to disabled as default
        recognizeTextBtn.setEnabled(false);

        //init array of permissions required for camera/gallery
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        //handle click, showing of input image dialog
        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();
            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        recognizeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri == null){
                    Toast.makeText(OcrActivity.this, "Pick an image first", Toast.LENGTH_SHORT).show();
                }
                else{
                    recognizeTextFromImage();

                    pb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent k = new Intent(OcrActivity.this, ElectricityCalculate.class);
                            k.putExtra ("CR", recognizedTextEt.getText().toString());
                            startActivity(k);
                        }
                    });


                }


            }
        });





    }


    private void pickImage(){

        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};


        ImagePicker.with(this)
                .galleryMimeTypes(mimeTypes)
                .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                .galleryOnly()
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less tha n 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent(intent -> {
                    galleryActivityResultLauncher.launch(intent);
                    return null;
                });

    }





    private void recognizeTextFromImage() {
        Log.d(TAG, "recognizeTextFromImage: ");

        progressDialog.setMessage("Preparing Image");
        progressDialog.show();

        try{
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);

            TextRecognizerOptions options = new TextRecognizerOptions.Builder()
                    .build();

            textRecognizer = TextRecognition.getClient(options);

            progressDialog.setMessage("Recognizing Text");

            Task<Text> textTaskResult = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {

                    progressDialog.dismiss();
                    String recognizedText = text.getText().toString();
                    Log.d(TAG, "onSuccess: recognizedText: "+recognizedText);

                    String numbersOnly = recognizedText.replaceAll("[^\\d]", "");
                    recognizedTextEt.setText(numbersOnly);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onFailure: ", e);
                    Toast.makeText(OcrActivity.this, "Failed recognizing text"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            Log.d(TAG, "recognizeTextFromImage: ", e);
            Toast.makeText(this, "Failed preparing image"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void showInputImageDialog() {
//        PopupMenu popupMenu = new PopupMenu(this, inputImageBtn);
//        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
//        popupMenu.show();

        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Camera Clicked");
                if(checkCameraPermissions()){
                    pickImageCamera();
                }
                else{
                    requestCameraPermissions();
                }
            }
        });
    }

//    private void showInputImageDialog() {
//        PopupMenu popupMenu = new PopupMenu(this, inputImageBtn);
//
//        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
//
//        popupMenu.show();
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//
//
//                    Log.d(TAG, "onMenuItemClick: Camera Clicked");
//                    if(checkCameraPermissions()){
//                        pickImageCamera();
//                    }
//                    else{
//                        requestCameraPermissions();
//                    }
//
//
//                return true;
//            }
//        });
//
//    }

    private void pickImageGallery() {
//        Log.d(TAG, "pickImageGallery: ");
//        //intent to pick image from gallery
//        Intent intent = new Intent(Intent.ACTION_PICK);
//
//        //set type of file wanted to be pick
//        intent.setType("image/*");
//        galleryActivityResultLauncher.launch(intent);
    }


    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    //receiving of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //image picked
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri "+imageUri);
                        //setting to imageview
                        imageIv.setImageURI(imageUri);
                        recognizeTextBtn.setEnabled(true);
                    }
                    else{
                        Log.d(TAG, "onActivityResult: Cancelled");
                        //cancelled
                        Toast.makeText(OcrActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera(){
        Log.d(TAG, "pickImageCamera: ");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //the image will be received here, if taken from camera
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: imageUri "+imageUri);
                        imageIv.setImageURI(imageUri);
                    }
                    else{
                        //if cancelled
                        Log.d(TAG, "onActivityResult: Cancelled");
                        Toast.makeText(OcrActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){

        boolean cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult;
    }

    private void requestCameraPermissions(){

        //request camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted&&storageAccepted){
                        pickImageCamera();
                    }
                    else{

                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(storageAccepted){
                        pickImageGallery();
                    }
                    else{
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }


}