package com.example.temp2.ui.menu1;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.temp2.AddActivity;
import com.example.temp2.MainActivity;
import com.example.temp2.R;
import com.example.temp2.recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

public class menu1 extends Fragment {
    ImageView myImage;
    CircularImageView navImg;
    Button myBtn;
    Button myBtnCon;
    Uri myURI = null;
    FirebaseStorage storage;
    String randomID;
    private DatabaseReference mDatabase;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.menu1_fragment,container,false);
        //getActivity().getActionBar().setTitle("YOUR TITLE");
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        if ((((MainActivity) getActivity()).profile != "null") && (myURI == null)) {
            StorageReference ref1 = storage.getReference()
                    .child("imagesUser/" + ((MainActivity) getActivity()).profile);

            ref1.getBytes(2048 * 2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            myImage.setImageBitmap(bm);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    //Toast.makeText(getActivity(), "Gagal Upload", Toast.LENGTH_SHORT).show();
                }
            });
        }
        ((MainActivity) getActivity()).setActionBarTitle("Profile");
        myImage = getActivity().findViewById(R.id.profileImg);
        myBtn = getActivity().findViewById(R.id.uploadBtn);

        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.CAMERA};
                        requestPermissions(permissions, CAMERA_PERMISSION_CODE);
                    }
                    else{
                        getImageFromCamera();
                    }
                }
                else{
                    getImageFromCamera();

                }
            }
        });
        myBtnCon = getActivity().findViewById(R.id.ConfirmBtn);
        myBtnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    private void getImageFromCamera() {
        CropImage.activity()
                .setCropMenuCropButtonTitle("Done")
                .setAspectRatio(4,4)
                .setRequestedSize(400, 400)
                .start(getContext(), this);
    }



    void insertData() {
        String user;
        user = ((MainActivity) getActivity()).username;
        uploadImage(myURI, user);
    }

    private void uploadImage(Uri filePath, String user)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            storage = FirebaseStorage.getInstance();
            randomID = UUID.randomUUID().toString();
            // Defining the child of storageReference
            StorageReference ref
                    = storage.getReference()
                    .child(
                            "imagesUser/"
                                    + randomID);

            // adding listeners on upload
            // or failure of image
            StorageReference ref1 = storage.getReference()
                                    .child("imagesUser/" + ((MainActivity) getActivity()).profile);
            ref1.delete();
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    mDatabase.child("user").child(user).child("profile").setValue(randomID).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {

                                            Toast
                                                    .makeText(getActivity(),
                                                            "Profil " + user + " Gagal Ditambahkan",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                    Toast
                                            .makeText(getActivity(),
                                                    "Profil " + user + " Berhasil Ditambahkan",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getActivity(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                myImage.setImageURI(resultUri);
                myURI = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                    getImageFromCamera();
                }
                else{
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    Toast.makeText(getContext(), "Tekan setuju untuk menggunakan fitur ini!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}