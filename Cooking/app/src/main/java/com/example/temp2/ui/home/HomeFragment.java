package com.example.temp2.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.temp2.AddActivity;
import com.example.temp2.MainActivity;
import com.example.temp2.MapsActivity;
import com.example.temp2.R;
import com.example.temp2.databinding.FragmentHomeBinding;
import com.example.temp2.googleMaps;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    String value;
    Button locationCheck;
    LinearLayout minumanBtn, makananBtn, bakeryBtn;
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
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
        LinearLayout myBtn;
        myBtn = getActivity().findViewById(R.id.searchBtn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).categoryQry = "";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_home_to_homeFragmentSearch1);
            }
        });

        value = ((MainActivity) getActivity()).username;
        FloatingActionButton myBtn1;
        myBtn1 = getActivity().findViewById(R.id.fabHome);
        myBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra("uname",value);
                startActivity(intent);
            }
        });

        minumanBtn = getActivity().findViewById(R.id.minumanBtn);
        minumanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).titleQry = "";
                ((MainActivity) getActivity()).categoryQry = "Minuman";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_home_to_homeFragmentSearch2);
            }
        });
        makananBtn = getActivity().findViewById(R.id.makananBtn);
        makananBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).titleQry = "";
                ((MainActivity) getActivity()).categoryQry = "Makanan";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_home_to_homeFragmentSearch2);
            }
        });
        bakeryBtn = getActivity().findViewById(R.id.bakeryBtn);
        bakeryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).titleQry = "";
                ((MainActivity) getActivity()).categoryQry = "Bakery";
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_home_to_homeFragmentSearch2);
            }
        });

        locationCheck = getActivity().findViewById(R.id.cekLokasi);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });
    }

    public void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "?z=13&q=pasar");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
                else{
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    Toast.makeText(getContext(), "Tekan setuju untuk menggunakan fitur ini!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}