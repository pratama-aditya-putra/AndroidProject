package com.example.temp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GridElementAdapter extends RecyclerView.Adapter<GridElementAdapter.RecipeHolder> {
    private ArrayList<recipe> recipeList;
    private Context mContext;

    public GridElementAdapter(ArrayList<recipe> recipeList, Context mContext) {
        this.recipeList = recipeList;
        this.mContext = mContext;
    }

    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.grid_element, parent, false);
        return new RecipeHolder(view);
    }

    @Override
    public int getItemCount() {
        return recipeList == null? 0: recipeList.size();
    }


    // This method is called when binding the data to the views being created in RecyclerView
    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, final int position) {
        final recipe mRecipe = recipeList.get(position);

        // Set the data to the views here
        holder.setRecipeTitle(mRecipe.getTitle());
        holder.setRecipeImage(mRecipe.getImageURL());
        holder.setAuthorTitle(mRecipe.getAuthor());
        holder.setUserImage(mRecipe.getAuthor());
        holder.setTxtLike(String.valueOf(mRecipe.getLike()));
        holder.getGridBtn().setSelected(true);
        holder.getGridBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mId = mRecipe.getId();
                String mTitle = mRecipe.getTitle();
                String mDes = mRecipe.getDesc();
                String mAuthor = mRecipe.getAuthor();
                String mBahan = mRecipe.getIngredient();
                String mLangkah = mRecipe.getSteps();
                String mGambar = mRecipe.getImageURL();
                Integer mLike = mRecipe.getLike();
                ((MainActivity) mContext).idResep = mId;
                ((MainActivity) mContext).titleResep = mTitle;
                ((MainActivity) mContext).desResep = mDes;
                ((MainActivity) mContext).authResep = mAuthor;
                ((MainActivity) mContext).bhnResep = mBahan;
                ((MainActivity) mContext).gmbrResep = mGambar;
                ((MainActivity) mContext).lngResep = mLangkah;
                ((MainActivity) mContext).likeResep = mLike;
                Navigation.findNavController(((MainActivity) mContext),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch1_to_detailFragment);

            }
        });
        holder.getGridImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "" + mRecipe.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        // You can set click listners to indvidual items in the viewholder here
        // make sure you pass down the listner or make the Data members of the viewHolder public

    }

    // This is your ViewHolder class that helps to populate data to the view
    public class RecipeHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtAuthor;
        private ImageView gridImg;
        private TextView txtLike;
        private CircularImageView userImg;
        private ProgressBar progressBar;
        private CircularImageView gridBtn;

        public RecipeHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.grid_judul);
            txtAuthor = itemView.findViewById(R.id.grid_author);
            gridImg = itemView.findViewById(R.id.grid_Img);
            progressBar = itemView.findViewById(R.id.progressBar2);
            userImg = itemView.findViewById(R.id.userImageGrid);
            txtLike = itemView.findViewById(R.id.gridLike);
            gridBtn = itemView.findViewById(R.id.gridBtn);
        }

        public void setTxtLike(String s){ txtLike.setText(s); }

        public void setRecipeTitle(String name) {
            txtName.setText(name);
        }

        public void setAuthorTitle(String author) {
            txtAuthor.setText(author);
        }

        public ImageView getGridImg(){
            return gridImg;
        }

        public CircularImageView getGridBtn(){
            return gridBtn;
        }

        public void setRecipeImage(String gambar) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + gambar);
            progressBar.setVisibility(View.VISIBLE);
            gridImg.setVisibility(View.GONE);
            mImageRef.getBytes(2048 * 2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            gridImg.setImageBitmap(bm);
                            gridImg.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(mContext, "Gagal Load", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setUserImage(String author){
            ArrayList<AppUser> listAppUser = new ArrayList<>();
            DatabaseReference dbreff;
            dbreff = FirebaseDatabase.getInstance().getReference();
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listAppUser.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        AppUser appUserTemp = ds.getValue(AppUser.class);
                        listAppUser.add(appUserTemp);
                    }
                    String profile = listAppUser.get(0).getProfile();
                    StorageReference mImageRef = FirebaseStorage.getInstance().getReference("imagesUser/" + profile);
                    mImageRef.getBytes(2048 * 2048)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    userImg.setImageBitmap(bm);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(mContext, "Gagal Load", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            dbreff.child("user")
                    .orderByChild("username")
                    .equalTo(author)
                    .addValueEventListener(valueEventListener);
        }
    }
}