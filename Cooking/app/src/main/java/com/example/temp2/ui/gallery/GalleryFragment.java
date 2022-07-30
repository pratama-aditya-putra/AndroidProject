package com.example.temp2.ui.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.temp2.Database;
import com.example.temp2.EditActivity;
import com.example.temp2.MainActivity;
import com.example.temp2.R;
import com.example.temp2.databinding.FragmentGalleryBinding;
import com.example.temp2.recipe;
import com.example.temp2.recipeAdapterNew;
import com.example.temp2.recipeList;
import com.example.temp2.ui.recipeAdapter;
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

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    GridView listView;
    Database database;
    Cursor cursor;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery,container,false);
        //getActivity().getActionBar().setTitle("YOUR TITLE");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        listView = getActivity().findViewById(R.id.list_viewGalery);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        String user;
        user = ((MainActivity) getActivity()).username;

        /*database = new Database(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM resep WHERE author ='" +
                user + "'", null);
        cursor.moveToFirst();
        ArrayList<recipeList> listResep = new ArrayList<>();
        for(int i=0;i< cursor.getCount();i++){
            cursor.moveToPosition(i);
            recipeList TEMP = new recipeList(cursor.getInt(0), cursor.getString(2), cursor.getString(4));
            listResep.add(TEMP);
        }*/
        ArrayList<recipe> listResep = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listResep.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    recipe resep = ds.getValue(recipe.class);
                    listResep.add(resep);
                }
                if(getActivity() != null){
                    recipeAdapterNew adapter = new recipeAdapterNew(getActivity(), R.layout.main_listview_layout, listResep);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("recipe")
                .orderByChild("author")
                .equalTo(user)
                .addValueEventListener(valueEventListener);
        listView.setSelected(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String mId = listResep.get(i).getId();
                final String mTitle = listResep.get(i).getTitle();
                final String mDes = listResep.get(i).getDesc();
                final String mBahan = listResep.get(i).getIngredient();
                final String mAuthor = listResep.get(i).getAuthor();
                final String mGambar = listResep.get(i).getImageURL();
                final String mSteps = listResep.get(i).getSteps();
                final String mCategory = listResep.get(i).getCategory();
                final Integer mLike = listResep.get(i).getLike();
                final CharSequence[] dialogItem = {"Update Resep", "Hapus Resep"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pilihan");
                builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        switch (item){
                            case 0 :
                                Intent intent = new Intent(getActivity(), EditActivity.class);
                                intent.putExtra("id",mId);
                                intent.putExtra("judul",mTitle);
                                intent.putExtra("category",mCategory);
                                intent.putExtra("author",mAuthor);
                                intent.putExtra("deskripsi",mDes);
                                intent.putExtra("bahan",mBahan);
                                intent.putExtra("gambar",mGambar);
                                intent.putExtra("langkah",mSteps);
                                intent.putExtra("like",String.valueOf(mLike));
                                startActivity(intent);
                                break;
                            case 1 :
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                builder1.setCancelable(true);


                                builder1.setMessage("Apa anda yakin ingin menghapus resep ini?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                /*Database helper = new Database(getActivity());
                                                SQLiteDatabase db;
                                                db= helper.getWritableDatabase();
                                                db.execSQL("DELETE FROM resep WHERE id = " + mId.toString());
                                                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_gallery_self);
                                                Toast.makeText(getActivity(), mId.toString() + " Telah Berhasil Dihapus", Toast.LENGTH_SHORT).show();*/

                                                StorageReference storageReference = storage.getReference("images/" + mGambar);
                                                storageReference.delete().addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Toast.makeText(getActivity(), mId.toString() + " Gagal Dihapus", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                mDatabase.child("recipe").child(mId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getActivity(), mId.toString() + " Telah Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Toast.makeText(getActivity(), mId.toString() + " Gagal Dihapus", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                // User cancelled the dialog
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                                break;
                        }

                    }
                });
                builder.create().show();
            }
        });
    }
}