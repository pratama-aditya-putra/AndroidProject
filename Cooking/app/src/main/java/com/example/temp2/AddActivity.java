package com.example.temp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText Judul, Bahan, Ket, Steps;
    String mJudul, mBahan, mKet,  user, mCategory, mSteps;
    Button myBtn;
    Uri myURI;
    String x;
    ImageView myImgBtn;
    DatabaseReference dbreff;
    FirebaseStorage storage;
    String randomID;
    String mimeType;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Judul = findViewById(R.id.judulResep);
        Bahan = findViewById(R.id.bahanResep);
        Steps = findViewById(R.id.langkahResep);
        Ket = findViewById(R.id.ketResep);
        myBtn = findViewById(R.id.insertBtn);
        myImgBtn = findViewById(R.id.fotoResep);
        myImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        pickImageFromGalery();

                    }
                }
                else{
                    pickImageFromGalery();
                }
            }
        });
        Spinner mySpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);

        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    void insertData() {
        Intent intent = getIntent();
        user = intent.getStringExtra("uname");
        mJudul = Judul.getText().toString();
        mBahan = Bahan.getText().toString();
        mKet = Ket.getText().toString();
        mSteps = Steps.getText().toString();

        /*Database helper = new Database(getApplicationContext());
        SQLiteDatabase db;
        db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("author", user);
            values.put("judul", mJudul);
            values.put("bahan", mBahan);
            values.put("keterangan", mKet);
            values.put("image", x);
            db.insert("resep", null,values);*/
        uploadImage(myURI);
        long startDate;
        dbreff = FirebaseDatabase.getInstance().getReference().child("recipe");
        startDate = System.currentTimeMillis() ;
        recipe myRecipe = new recipe(String.valueOf(startDate),mJudul, mKet, user, mBahan, randomID, mCategory, mSteps, 0);
        dbreff.child(String.valueOf(startDate)).setValue(myRecipe);

    }

    private void uploadImage(Uri filePath)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            storage = FirebaseStorage.getInstance();
            randomID = UUID.randomUUID().toString();
            // Defining the child of storageReference
            StorageReference ref
                    = storage.getReference()
                    .child(
                            "images/"
                                    + randomID);

            // adding listeners on upload
            // or failure of image
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
                                    Toast
                                            .makeText(AddActivity.this,
                                                    mJudul + " Berhasil Ditambahkan",
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
                                    .makeText(AddActivity.this,
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

    private void pickImageFromGalery() {

        /*CropImage.activity()
                .start(this);*/
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            myURI = data.getData();
            x = getPath(myURI);
            myImgBtn.setImageURI(myURI);
            mimeType = getContentResolver().getType(myURI);
        }
    }

    public String getPath(Uri uri){
        if(uri == null) return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGalery();
                } else {
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    Toast.makeText(this, "Allow Permission to Use This Function!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mCategory = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}