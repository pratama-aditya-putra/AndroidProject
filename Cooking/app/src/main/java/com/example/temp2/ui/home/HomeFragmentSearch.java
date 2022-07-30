package com.example.temp2.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.temp2.AddActivity;
import com.example.temp2.Database;
import com.example.temp2.MainActivity;
import com.example.temp2.R;
import com.example.temp2.recipe;
import com.example.temp2.recipeAdapterNew;
import com.example.temp2.recipeList;
import com.example.temp2.ui.recipeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragmentSearch extends Fragment {
    protected Cursor cursor;
    Database database;
    GridView listView;
    String value;
    TextView hasilQuery;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_homesearch,container,false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        listView = getActivity().findViewById(R.id.list_view);
        hasilQuery = getActivity().findViewById(R.id.hasilQuery);
        hasilQuery.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String qryCategory = ((MainActivity) getActivity()).categoryQry;
        String qryTitle = ((MainActivity) getActivity()).titleQry;

        /*database = new Database(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM resep", null);
        cursor.moveToFirst();
        ArrayList<recipeList> listResep = new ArrayList<>();
        for(int i=0;i< cursor.getCount();i++){
            cursor.moveToPosition(i);
            recipeList TEMP = new recipeList(cursor.getInt(0), cursor.getString(2), cursor.getString(4));
            listResep.add(TEMP);
        }
        recipeAdapter adapter = new recipeAdapter(getActivity(), R.layout.main_listview_layout, listResep);*/
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
                if((listResep.size() > 0) && getActivity() != null) {
                    recipeAdapterNew adapter = new recipeAdapterNew(getActivity(), R.layout.main_listview_layout, listResep);
                    listView.setAdapter(adapter);
                }
                else{
                    hasilQuery.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        if((qryCategory == "")&&(qryTitle == ""))
            mDatabase.child("recipe")
                    .limitToFirst(20)
                    .addValueEventListener(valueEventListener);
        if(qryTitle != ""){
            mDatabase.child("recipe")
                    .orderByChild("title")
                    .startAt(qryTitle)
                    .endAt(qryTitle + "\uf8ff")
                    .limitToFirst(20)
                    .addValueEventListener(valueEventListener);
        }
        else{
            mDatabase.child("recipe")
                    .orderByChild("category")
                    .limitToFirst(20)
                    .equalTo(((MainActivity) getActivity()).categoryQry)
                    .addValueEventListener(valueEventListener);
        }
        listView.setSelected(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mId = listResep.get(i).getId();
                String mTitle = listResep.get(i).getTitle();
                String mDes = listResep.get(i).getDesc();
                String mAuthor = listResep.get(i).getAuthor();
                String mBahan = listResep.get(i).getIngredient();
                String mLangkah = listResep.get(i).getSteps();
                String mGambar = listResep.get(i).getImageURL();
                Integer mLike = listResep.get(i).getLike();
                ((MainActivity) getActivity()).idResep = mId;
                ((MainActivity) getActivity()).titleResep = mTitle;
                ((MainActivity) getActivity()).desResep = mDes;
                ((MainActivity) getActivity()).authResep = mAuthor;
                ((MainActivity) getActivity()).bhnResep = mBahan;
                ((MainActivity) getActivity()).gmbrResep = mGambar;
                ((MainActivity) getActivity()).lngResep = mLangkah;
                ((MainActivity) getActivity()).likeResep = mLike;
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch_to_detailFragment);
                //
                // Toast.makeText(getActivity(), "" + String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout myBtn;
        myBtn = getActivity().findViewById(R.id.homeBtn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch_to_nav_home);
            }
        });

        value = ((MainActivity) getActivity()).username;
        FloatingActionButton myBtn1;
        myBtn1 = getActivity().findViewById(R.id.fabSearch);
        myBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra("uname",value);
                startActivity(intent);
            }
        });
    }
}