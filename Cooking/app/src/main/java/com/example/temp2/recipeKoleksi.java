package com.example.temp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.List;

public class recipeKoleksi extends ArrayAdapter<userLikes> {

    private Context mContext;
    int mResource;
    StorageReference mImageRef;
    private DatabaseReference mDatabase;

    public recipeKoleksi(@NonNull @NotNull Context context, int resource, @NonNull @NotNull ArrayList<userLikes> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String id = getItem(position).getIdResep();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ArrayList<recipe> listResep = new ArrayList<>();
        View finalConvertView = convertView;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listResep.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    recipe resep = ds.getValue(recipe.class);
                    listResep.add(resep);
                    Log.d("Resep ID", "onDataChange: " + resep.getTitle());
                }
                if((listResep.size() > 0)) {

                    TextView tvJudul = (TextView) finalConvertView.findViewById(R.id.listJudul);
                    TextView tvDeskripsi = (TextView) finalConvertView.findViewById(R.id.listKet);
                    ImageView ivGambar = (ImageView) finalConvertView.findViewById(R.id.gambarR);

                    tvJudul.setText(listResep.get(0).getTitle());
                    tvDeskripsi.setText(listResep.get(0).getAuthor());

                    ProgressBar progressBar = (ProgressBar) finalConvertView.findViewById(R.id.progressBar1);
                    StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + listResep.get(0).getImageURL());
                    progressBar.setVisibility(View.VISIBLE);
                    ivGambar.setVisibility(View.GONE);
                    mImageRef.getBytes(2048 * 2048 * 5)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    ivGambar.setImageBitmap(bm);
                                    ivGambar.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(mContext, "Gagal Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("recipe")
                .orderByChild("id")
                .equalTo(id)
                .addValueEventListener(valueEventListener);
        /*String judul = getItem(position).getTitle();
        String deskripsi = getItem(position).getDesc();
        String author = getItem(position).getAuthor();
        String bahan = getItem(position).getIngredient();
        String gambar = getItem(position).getImageURL();
        String category = getItem(position).getCategory();
        String langkah = getItem(position).getSteps();
        Integer like = getItem(position).getLike();*/

        //recipe Resep = new recipe(id,judul,deskripsi,author,bahan,gambar,category, langkah, like);
        /*StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference("image/test.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new onSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        imageView.setMinimumHeight(dm.heightPixels);
                        imageView.setMinimumWidth(dm.widthPixels);
                        imageView.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/

        /*String internetUrl = "http://i.imgur.com/DvpvklR.png";
        Glide
                .with(mContext)
                .load(internetUrl)
                .into(ivGambar);*/
        /* Reference to an image file in Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("image/test.jpg");

        // Load the image using Glide
        Glide.with()
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(ivGambar);*/

        return convertView;
    }
}

