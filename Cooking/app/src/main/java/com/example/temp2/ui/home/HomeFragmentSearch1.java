package com.example.temp2.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.HorizontalGridView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.temp2.AddActivity;
import com.example.temp2.AppUser;
import com.example.temp2.GridElementAdapter;
import com.example.temp2.MainActivity;
import com.example.temp2.R;
import com.example.temp2.recipe;
import com.example.temp2.recipeAdapterNew;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragmentSearch1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference mDatabase;
    private GridElementAdapter listAdapter;
    private ArrayList<recipe> listResep;
    private ArrayList<AppUser> listUser;
    private RecyclerView horizontalGridView;
    private GridView listView2, listView3, listView4;
    private ImageView searchMakanan, searchMinuman, searchBakery;
    private String value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_search1, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        horizontalGridView = getActivity().findViewById(R.id.recycleView);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listResep = new ArrayList<>();
        LinearLayout myBtn = getActivity().findViewById(R.id.homeBtn1);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch1_to_nav_home);
            }
        });
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listResep.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    recipe resep = ds.getValue(recipe.class);
                    listResep.add(resep);
                }
                //horizontalGridView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(RecyclerView.HORIZONTAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                horizontalGridView.setLayoutManager(layoutManager);
                GridElementAdapter adapter = new GridElementAdapter(listResep,getActivity());

                horizontalGridView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("recipe")
                .orderByChild("like")
                .limitToLast(5)
                .addValueEventListener(valueEventListener);

        /*listUser = new ArrayList<>();
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUser.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    AppUser user = ds.getValue(AppUser.class);
                    listUser.add(user);
                }
                //horizontalGridView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                horizontalGridView.setLayoutManager(layoutManager);
                GridElementAdapter adapter = new GridElementAdapter(listResep,getActivity());

                horizontalGridView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("user")
                .limitToFirst(5)
                .addValueEventListener(valueEventListener1);*/

        listView2 = getActivity().findViewById(R.id.list_view2);
        listView3 = getActivity().findViewById(R.id.list_view3);
        listView4 = getActivity().findViewById(R.id.list_view4);
        recipe2grid(listView2, "Makanan");
        recipe2grid(listView3, "Minuman");
        recipe2grid(listView4, "Bakery");
        searchMakanan = getActivity().findViewById(R.id.searchMakanan);
        searchMakanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).titleQry = "";
                ((MainActivity) getActivity()).categoryQry = "Makanan";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch1_to_homeFragmentSearch);
            }
        });
        searchMinuman = getActivity().findViewById(R.id.searchMinuman);
        searchMinuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).titleQry = "";
                ((MainActivity) getActivity()).categoryQry = "Minuman";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch1_to_homeFragmentSearch);
            }
        });
        searchBakery = getActivity().findViewById(R.id.searchBakery);
        searchBakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).titleQry = "";
                ((MainActivity) getActivity()).categoryQry = "Bakery";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch1_to_homeFragmentSearch);
            }
        });


        value = ((MainActivity) getActivity()).username;
        FloatingActionButton myBtn1;
        myBtn1 = getActivity().findViewById(R.id.fabSearch1);
        myBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra("uname",value);
                startActivity(intent);
            }
        });
    }

    public void recipe2grid(GridView listView, String tipe){
        ArrayList<recipe> listResep = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listResep.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    recipe resep = ds.getValue(recipe.class);
                    listResep.add(resep);
                }
                if (getActivity()!=null){
                    recipeAdapterNew adapter = new recipeAdapterNew(getActivity(), R.layout.main_listview_layout, listResep);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("recipe")
                .orderByChild("category")
                .equalTo(tipe)
                .limitToLast(2)
                .addValueEventListener(valueEventListener);
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
                ((MainActivity) getActivity()).idResep = mId;
                ((MainActivity) getActivity()).titleResep = mTitle;
                ((MainActivity) getActivity()).desResep = mDes;
                ((MainActivity) getActivity()).authResep = mAuthor;
                ((MainActivity) getActivity()).bhnResep = mBahan;
                ((MainActivity) getActivity()).gmbrResep = mGambar;
                ((MainActivity) getActivity()).lngResep = mLangkah;
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_homeFragmentSearch1_to_detailFragment);
                //
                // Toast.makeText(getActivity(), "" + String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
        });
    }
}