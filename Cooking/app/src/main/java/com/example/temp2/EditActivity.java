package com.example.temp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    EditText teJudul, teBahan, teKet, teLang, teCat;
    String mJudul, mBahan, mKet, mImg, mAuthor, mSteps;
    String mCategory = "";
    protected Cursor cursor;
    Database database;
    Button myBtn;
    String id;
    Integer mLike;
    ImageView imgResep;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_edit);
        teJudul = findViewById(R.id.editJudul);
        teBahan = findViewById(R.id.editBahan);
        teKet = findViewById(R.id.editKet);
        imgResep = findViewById(R.id.editFoto);
        teLang = findViewById(R.id.editLangkah);
        teCat = findViewById(R.id.editCategory);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        mJudul = intent.getStringExtra("judul");
        mAuthor = intent.getStringExtra("author");
        mKet = intent.getStringExtra("deskripsi");
        mBahan = intent.getStringExtra("bahan");
        mImg = intent.getStringExtra("gambar");
        mCategory = intent.getStringExtra("category");
        mSteps = intent.getStringExtra("langkah");
        mLike = Integer.valueOf(intent.getStringExtra("like"));

        teBahan.setText(mBahan);
        teJudul.setText(mJudul);
        teKet.setText(mKet);
        teLang.setText(mSteps);
        teCat.setText(mCategory);
        teCat.setFocusable(false);
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + mImg);

        mImageRef.getBytes(2048 * 2048 * 5)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgResep.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(EditActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
            }
        });
        /*database = new Database(this);
        SQLiteDatabase db = database.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM resep WHERE id = "+ Integer.valueOf(id), null);
        cursor.moveToFirst();
        for(int i=0;i<1;i++){
            cursor.moveToPosition(i);
            recipeList TEMP = new recipeList(cursor.getInt(0), cursor.getString(2), cursor.getString(4));
            teBahan.setText(cursor.getString(3));
            teJudul.setText(cursor.getString(2));
            teKet.setText(cursor.getString(4));
        }*/

        myBtn = findViewById(R.id.editBtn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJudul = teJudul.getText().toString();
                mBahan = teBahan.getText().toString();
                mKet = teKet.getText().toString();
                mSteps = teLang.getText().toString();
                mCategory = teCat.getText().toString();

                recipe mRecipe = new recipe(id, mJudul, mKet, mAuthor, mBahan, mImg, mCategory, mSteps, mLike);
                mDatabase.child("recipe").child(id).setValue(mRecipe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditActivity.this, mJudul + " Berhasil diupdate", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(EditActivity.this, mJudul + " Gagal diupdate", Toast.LENGTH_SHORT).show();
                    }
                });
                /*Database helper = new Database(EditActivity.this);
                SQLiteDatabase db;
                db= helper.getWritableDatabase();
                db.execSQL("UPDATE resep SET judul = '" +
                        mJudul +"', bahan = '" +
                        mBahan + "', keterangan = '" +
                        mKet + "' WHERE id = " + id);
                Toast.makeText(EditActivity.this, mJudul + " Diubah", Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}