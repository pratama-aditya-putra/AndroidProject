package com.example.temp2.ui.detail;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.temp2.Database;
import com.example.temp2.MainActivity;
import com.example.temp2.R;
import com.example.temp2.recipe;
import com.example.temp2.recipeAdapterNew;
import com.example.temp2.recipeList;
import com.example.temp2.userLikes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;

public class DetailFragment extends Fragment {
    protected Cursor cursor;
    Database database;
    TextView tvJudul, tvAuthor, tvKeterangan, tvBahan, tvLangkah;
    String mJudul, mAuthor, mKeterangan, mBahan;
    ImageView imageResep, likeBtn, shareBtn;
    recipe resep;
    Integer likes;
    Bitmap bm;
    FloatingActionButton downloadBtn;
    private DatabaseReference mDatabase;
    private  DatabaseReference mRef;
    private userLikes userlike;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detail_fragment,container,false);
        //getActivity().getActionBar().setTitle("YOUR TITLE");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvAuthor = getActivity().findViewById(R.id.author);
        tvBahan = getActivity().findViewById(R.id.bahanDet);
        tvJudul = getActivity().findViewById(R.id.judulDet);
        tvKeterangan = getActivity().findViewById(R.id.ketDet);
        tvLangkah = getActivity().findViewById(R.id.langkahDet);
        imageResep =  getActivity().findViewById(R.id.imageResep);
        likeBtn = getActivity().findViewById(R.id.like);
        downloadBtn = getActivity().findViewById(R.id.fabDownload);
        shareBtn = getActivity().findViewById(R.id.shareResep);
        likeBtn.setTag("");
        ArrayList<userLikes> listLikes = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listLikes.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    userLikes temp = ds.getValue(userLikes.class);
                    listLikes.add(temp);
                }
                if((listLikes.size() > 0) && getActivity() != null) {
                    likeBtn.setImageResource(R.drawable.love2);
                    likeBtn.setTag("liked");
                }
                else{
                    likeBtn.setImageResource(R.drawable.love1);
                    likeBtn.setTag("like");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("userLikes")
                .child(((MainActivity) getActivity()).idResep)
                .orderByChild("idUser")
                .equalTo(((MainActivity) getActivity()).username)
                .addValueEventListener(valueEventListener);

        tvAuthor.setText("by " + ((MainActivity) getActivity()).authResep);
        tvBahan.setText(((MainActivity) getActivity()).bhnResep);
        tvJudul.setText(((MainActivity) getActivity()).titleResep);
        tvKeterangan.setText(((MainActivity) getActivity()).desResep);
        tvLangkah.setText(((MainActivity) getActivity()).lngResep);
        likes = ((MainActivity) getActivity()).likeResep;
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<userLikes> listLikes = new ArrayList<>();
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listLikes.clear();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            userLikes temp = ds.getValue(userLikes.class);
                            listLikes.add(temp);
                        }
                        if((listLikes.size() > 0) && getActivity() != null) {

                            //userlike = new userLikes(((MainActivity) getActivity()).username, ((MainActivity) getActivity()).idResep,"0");
                            //mDatabase.child("userLikes").child(((MainActivity) getActivity()).username).child(((MainActivity) getActivity()).idResep).removeValue();

                            likeBtn.setTag("liked");
                        }
                        else{
                            likeBtn.setTag("like");
                            //userlike = new userLikes(((MainActivity) getActivity()).username, ((MainActivity) getActivity()).idResep,"1");
                            //mDatabase.child("userLikes").child(((MainActivity) getActivity()).username).child(((MainActivity) getActivity()).idResep).setValue(userlike);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                mDatabase.child("userLikes")
                        .child(((MainActivity) getActivity()).idResep)
                        .orderByChild("idUser")
                        .equalTo(((MainActivity) getActivity()).username)
                        .addValueEventListener(valueEventListener);
                if(likeBtn.getTag().equals("like")){
                    likeBtn.setImageResource(R.drawable.love2);
                    userlike = new userLikes(((MainActivity) getActivity()).username, ((MainActivity) getActivity()).idResep);
                    mDatabase.child("userLikes").child(((MainActivity) getActivity()).idResep).child(((MainActivity) getActivity()).username).setValue(userlike);
                    mDatabase.child("recipeLikes").child(((MainActivity) getActivity()).username).child(((MainActivity) getActivity()).idResep).setValue(userlike);
                    mDatabase.child("userLikes")
                            .child(((MainActivity) getActivity()).idResep)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                    int size = (int) snapshot.getChildrenCount();
                                    Log.d("children", "onDataChange: " + String.valueOf(size));
                                    mDatabase.child("recipe").child(((MainActivity) getActivity()).idResep).child("like").setValue(size);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                    return;
                }
                if(likeBtn.getTag().equals("liked")){
                    likeBtn.setImageResource(R.drawable.love1);
                    userlike = new userLikes(((MainActivity) getActivity()).username, ((MainActivity) getActivity()).idResep);
                    mDatabase.child("userLikes").child(((MainActivity) getActivity()).idResep).child(((MainActivity) getActivity()).username).removeValue();
                    mDatabase.child("recipeLikes").child(((MainActivity) getActivity()).username).child(((MainActivity) getActivity()).idResep).removeValue();
                    ((MainActivity) getActivity()).likeResep = likes;mDatabase.child("userLikes")
                            .child(((MainActivity) getActivity()).idResep)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                    int size = (int) snapshot.getChildrenCount();
                                    Log.d("children", "onDataChange: " + String.valueOf(size));
                                    mDatabase.child("recipe").child(((MainActivity) getActivity()).idResep).child("like").setValue(size);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                    return;
                }
                Toast.makeText(getActivity(), "" + likeBtn.getTag() , Toast.LENGTH_SHORT).show();
             }
        });
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + ((MainActivity) getActivity()).gmbrResep);

        mImageRef.getBytes(2048 * 2048 * 5)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageResep.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Gagal Upload", Toast.LENGTH_SHORT).show();
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database helper = new Database(getActivity().getApplicationContext());
                SQLiteDatabase db;
                db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", ((MainActivity) getActivity()).idResep);
                values.put("author", ((MainActivity) getActivity()).authResep);
                values.put("judul", ((MainActivity) getActivity()).titleResep);
                values.put("bahan", ((MainActivity) getActivity()).bhnResep);
                values.put("langkah", ((MainActivity) getActivity()).lngResep);
                values.put("keterangan", ((MainActivity) getActivity()).desResep);
                values.put("image", ((MainActivity) getActivity()).gmbrResep);
                db.insert("resep", null,values);
            }
        });
        shareBtn.setSelected(true);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "\n" +
                        "Anda harus mencoba resep ini : " +
                        ((MainActivity) getActivity()).titleResep + "\n\n" +
                        ((MainActivity) getActivity()).bhnResep + "\n\n" +
                        ((MainActivity) getActivity()).desResep + "\n\n" +
                        ((MainActivity) getActivity()).lngResep + "\n\n");
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    getActivity().startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*database = new Database(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM resep WHERE id = "+ ((MainActivity) getActivity()).idResep, null);
        cursor.moveToFirst();

        for(int i=0;i<1;i++){
            cursor.moveToPosition(i);
            recipeList TEMP = new recipeList(cursor.getInt(0), cursor.getString(2), cursor.getString(4));
            tvAuthor.setText("by " + cursor.getString(1));
            tvBahan.setText(cursor.getString(3));
            tvJudul.setText(cursor.getString(2));
            tvKeterangan.setText(cursor.getString(4));
            /*File imgFile = new  File(cursor.getString(5));
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageResep.setImageBitmap(myBitmap);
            }
        }*/
    }
}