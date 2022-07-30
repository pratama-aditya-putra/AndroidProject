package com.example.temp2.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.temp2.Database;
import com.example.temp2.EditActivity;
import com.example.temp2.MainActivity;
import com.example.temp2.R;
import com.example.temp2.databinding.FragmentSlideshowBinding;
import com.example.temp2.recipe;
import com.example.temp2.recipeAdapterNew;
import com.example.temp2.recipeKoleksi;
import com.example.temp2.recipeList;
import com.example.temp2.ui.recipeAdapter;
import com.example.temp2.userLikes;
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

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;
    GridView listView, listView1;
    Database database;
    Cursor cursor;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow,container,false);
        return root;
    }

    public void onResume() {
        super.onResume();
        listView = getActivity().findViewById(R.id.list_viewSlideshow);
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
        ArrayList<userLikes> listUserLikes = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUserLikes.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    userLikes temp = ds.getValue(userLikes.class);
                    listUserLikes.add(temp);
                    Log.d("recipe ID", "onDataChange: " + temp.getIdResep());
                }
                if(listUserLikes.size() <= 0){
                    if(getActivity() != null)
                        Toast.makeText(getActivity(), "Tidak ada koleksi", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(getActivity() != null){
                        recipeKoleksi adapter = new recipeKoleksi(getActivity(), R.layout.main_listview_layout, listUserLikes);
                        listView.setAdapter(adapter);

                        listView.setSelected(true);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                String id = listUserLikes.get(i).getIdResep();
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                ArrayList<recipe> listResep = new ArrayList<>();
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        listResep.clear();
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            recipe resep = ds.getValue(recipe.class);
                                            listResep.add(resep);
                                            Log.d("Resep ID", "onDataChange: " + resep.getTitle());
                                        }
                                        if((listResep.size() > 0) && (getActivity() != null)) {
                                            String mId = listResep.get(0).getId();
                                            String mTitle = listResep.get(0).getTitle();
                                            String mDes = listResep.get(0).getDesc();
                                            String mAuthor = listResep.get(0).getAuthor();
                                            String mBahan = listResep.get(0).getIngredient();
                                            String mLangkah = listResep.get(0).getSteps();
                                            String mGambar = listResep.get(0).getImageURL();
                                            Integer mLike = listResep.get(0).getLike();
                                            ((MainActivity) getActivity()).idResep = mId;
                                            ((MainActivity) getActivity()).titleResep = mTitle;
                                            ((MainActivity) getActivity()).desResep = mDes;
                                            ((MainActivity) getActivity()).authResep = mAuthor;
                                            ((MainActivity) getActivity()).bhnResep = mBahan;
                                            ((MainActivity) getActivity()).gmbrResep = mGambar;
                                            ((MainActivity) getActivity()).lngResep = mLangkah;
                                            ((MainActivity) getActivity()).likeResep = mLike;
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

                                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_slideshow_to_detailFragment);

                                //
                                // Toast.makeText(getActivity(), "" + String.valueOf(i), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("recipeLikes")
                .child(user)
                .addValueEventListener(valueEventListener);

        listView1 = getActivity().findViewById(R.id.list_viewSlideshow1);
        database = new Database(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM resep", null);
        cursor.moveToFirst();
        ArrayList<recipe> listResep = new ArrayList<>();
        for(int i=0;i< cursor.getCount();i++){
            cursor.moveToPosition(i);
            recipe TEMP = new recipe(String.valueOf(cursor.getInt(0)), cursor.getString(2), cursor.getString(5), cursor.getString(1), cursor.getString(3), cursor.getString(6),"", cursor.getString(4), 0);
            listResep.add(TEMP);
        }
        recipeAdapterNew adapter = new recipeAdapterNew(getActivity(), R.layout.main_listview_layout, listResep);
        listView1.setAdapter(adapter);

        listView1.setSelected(true);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mId = listResep.get(0).getId();
                String mTitle = listResep.get(0).getTitle();
                String mDes = listResep.get(0).getDesc();
                String mAuthor = listResep.get(0).getAuthor();
                String mBahan = listResep.get(0).getIngredient();
                String mLangkah = listResep.get(0).getSteps();
                String mGambar = listResep.get(0).getImageURL();
                Integer mLike = listResep.get(0).getLike();
                ((MainActivity) getActivity()).idResep = mId;
                ((MainActivity) getActivity()).titleResep = mTitle;
                ((MainActivity) getActivity()).desResep = mDes;
                ((MainActivity) getActivity()).authResep = mAuthor;
                ((MainActivity) getActivity()).bhnResep = mBahan;
                ((MainActivity) getActivity()).gmbrResep = mGambar;
                ((MainActivity) getActivity()).lngResep = mLangkah;
                ((MainActivity) getActivity()).likeResep = mLike;

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_slideshow_to_detailFragment);
            }
        });
    }
}