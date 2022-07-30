package com.example.temp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temp2.databinding.ActivityMainBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    savesharedprefrences pref;
    public String username = "", full_name="", profile="" ;
    public String idResep = new String();
    public String titleResep = new String();
    public String desResep = new String();
    public String authResep = new String();
    public String bhnResep = new String();
    public String gmbrResep = new String();
    public String lngResep = new String();
    public Integer likeResep = 0;
    public String categoryQry = "";
    public String titleQry = "";
    private CircularImageView navImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new savesharedprefrences(this);
        if(pref.getstate() == true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        TextView navUsernameReal = (TextView) headerView.findViewById(R.id.navUsernameReal);
        Intent intent = getIntent();
        username = intent.getStringExtra("uname");
        full_name = intent.getStringExtra("full_name");
        profile = intent.getStringExtra("profile");
        if (profile != "null") {
            navImg = headerView.findViewById(R.id.imageViewNav);

            StorageReference mImageRef = FirebaseStorage.getInstance().getReference("imagesUser/" + profile);
            mImageRef.getBytes(2048 * 2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            navImg.setImageBitmap(bm);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                }
            });
        }
        navUsername.setText(full_name);
        navUsernameReal.setText("@" + username);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.menu12, R.id.homeFragmentSearch, R.id.homeFragmentSearch1)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*navigationView.getMenu().findItem(R.id.menu12).setOnMenuItemClickListener(menuItem -> {
            getActionBar().setTitle("Menu 1");
            return true;
        });*/

        navigationView.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(menuItem -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setCancelable(true);


            builder1.setMessage("Apakah anda yakin ingin keluar?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            Intent switchActivityIntent = new Intent(MainActivity.this, Login.class);
                            startActivity(switchActivityIntent);
                            Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
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
            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                titleQry = s;
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.homeFragmentSearch);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent switchActivityIntent = new Intent(this, Setting.class);
                switchActivityIntent.putExtra("uname",username);
                startActivity(switchActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}