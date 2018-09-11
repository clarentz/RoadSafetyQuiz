package msu.olive.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import msu.olive.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnReturnToUpload;
    private String full_address, road_name, sub_admin_area, admin_area, country;
    public static  final int RESULT_OK = 99;
    LatLng selectedPosition;
    private double currentLatitude, currentLongitude;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addControls();
        addEvents();
        if (Geocoder.isPresent()){
            Toast.makeText(this, "GEOCODER IS PRESENT", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "NO GEOCODER", Toast.LENGTH_SHORT).show();
        }
    }

    private void addControls() {
        //btnGetData = findViewById(R.id.btnGetData);
        btnReturnToUpload = findViewById(R.id.btnReturnToUpload);
    }

    private void addEvents() {
//        btnGetData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getData();
//            }
//        });

        btnReturnToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                returnData();
            }
        });
    }

    private void returnData() {
        Intent returnIntent = new Intent(MapsActivity.this, UploadActivity.class);
        // returnIntent.putExtra("latitude", latitude);
        // returnIntent.putExtra("longitude", longitude);
        returnIntent.putExtra("address", full_address);
        returnIntent.putExtra("admin_area", admin_area);
        returnIntent.putExtra("sub_admin_area", sub_admin_area);
        returnIntent.putExtra("road_name", road_name);
        returnIntent.putExtra("country", country);
        setResult(MapsActivity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        currentLatitude = getIntent().getDoubleExtra("current_lat", 13.751330328);
        currentLongitude = getIntent().getDoubleExtra("current_long",  100.489664708);

        // Add a marker at the current position
        LatLng Current = new LatLng(currentLatitude, currentLongitude);
        mMap.addMarker(new MarkerOptions().position(Current).title("Current Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Current));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                markerOptions = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("Selected position");
                mMap.addMarker(markerOptions);
                selectedPosition = new LatLng(latLng.latitude, latLng.latitude);
            }
        });
    }

    private void getData() {


        if (markerOptions == null){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList1 = geocoder.getFromLocation(currentLatitude,currentLongitude,1);
                Address addressCurrent = addressList1.get(0);
                full_address = addressCurrent.getAddressLine(0); //Detail address
                country = addressCurrent.getCountryName();
                //locality =  addressCurrent.getLocality(); //sub-district
                admin_area = addressCurrent.getAdminArea(); //City
                sub_admin_area = addressCurrent.getSubAdminArea(); //District
                road_name = addressCurrent.getThoroughfare(); // road
                Toast.makeText(this, full_address, Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                Log.e("error geocoder", e.toString());
            }
        }
        if (markerOptions != null){
            double latitude = markerOptions.getPosition().latitude;
            double longitude = markerOptions.getPosition().longitude;

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                Address address = addressList.get(0);
                full_address = address.getAddressLine(0); //Detail address
                country = address.getCountryName();
                admin_area = address.getAdminArea(); //City
                sub_admin_area = address.getSubAdminArea(); //District
                road_name = address.getThoroughfare(); // road
                Toast.makeText(this, full_address, Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                Log.i("error geocoder", e.toString());
            }
        }
    }



}
