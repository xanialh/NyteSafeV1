package com.example.nytesafev0;

// Importing necessary classes for the application
import android.Manifest;

import androidx.annotation.RawRes;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText addressInput;
    private Button buttonRoute;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userMarker;
    private DrawerLayout drawerLayout;
    private TileOverlay heatmapOverlay;
    private Switch heatmapToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        heatmapToggle = findViewById(R.id.heatmap_toggle);
        heatmapToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addHeatMap(); // Add the heatmap when the toggle is checked
                } else {
                    removeHeatMap(); // Remove the heatmap when the toggle is unchecked
                }
            }
        });

        // Add these lines in the onCreate method
        drawerLayout = findViewById(R.id.drawerLayout);
        ImageView imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationView navigationView = findViewById(R.id.navigationView);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Initialize UI elements
        addressInput = findViewById(R.id.address_input);
        buttonRoute = findViewById(R.id.button_route);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set a click listener for the route button
        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findRoute();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Get the current location as LatLng object if permissions are granted
    private LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
        return null;
    }

    private void addHeatMap() {
        List<LatLng> latLngs = null;

        try {
            latLngs = readItems(R.raw.heatmap_data);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .build();

        heatmapOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }

    private void removeHeatMap() {
        if (heatmapOverlay != null) {
            heatmapOverlay.remove();
        }
    }

    private List<LatLng> readItems(@RawRes int resource) throws JSONException {
        List<LatLng> result = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("long");
            result.add(new LatLng(lat, lng));
        }
        return result;
    }


    // This method is called when the GoogleMap instance is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a click listener for the My Location button
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LatLng currentLocation = getCurrentLocation();
                if (currentLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
                return true;
            }
        });

        // Check if the heatmap toggle switch is enabled before adding the heatmap
        if (heatmapToggle.isChecked()) {
            addHeatMap();
        }

        // Enable the My Location layer if the required permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Update the map with the user's current location
        updateLocationOnMap();
    }

    // Update the user's location marker on the map
    private void updateLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get the user's last known location and update the marker
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (userMarker != null) {
                        userMarker.remove();
                    }
                    // Add a new marker for the current location
                    userMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                    // Animate the camera to the current location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
        });
    }

    // Get the LatLng object for the given address
    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void getDirections(LatLng origin, LatLng destination, String mode) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyAjrlg4NBLq129goAxYfq5tf94aauCBYPc")
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(geoApiContext)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.valueOf(mode.toUpperCase()))
                .alternatives(false);

        try {
            DirectionsResult result = request.await();

            if (result.routes != null && result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];
                if (route.legs != null && route.legs.length > 0) {
                    DirectionsLeg leg = route.legs[0];
                    Duration duration = leg.duration;
                    String durationText = duration.humanReadable;
                    Toast.makeText(this, "Estimated arrival time: " + durationText, Toast.LENGTH_LONG).show();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (com.google.maps.errors.ApiException e) {
            throw new RuntimeException(e);
        }
    }


    // Find and display the route to the entered address
    private void findRoute() {
        String address = addressInput.getText().toString().trim();
        if (!address.isEmpty()) {
            LatLng destinationLatLng = getLatLngFromAddress(address);

            if (destinationLatLng != null) {
                LatLng currentLatLng = getCurrentLocation();

                // Get estimated arrival time for walking
                getDirections(currentLatLng, destinationLatLng, "walking");

                // Set up the Directions API with the API key
                GeoApiContext geoApiContext = new GeoApiContext.Builder()
                        .apiKey("AIzaSyAjrlg4NBLq129goAxYfq5tf94aauCBYPc")
                        .build();

                // Convert LatLng objects to the format required by the Directions API
                com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(currentLatLng.latitude, currentLatLng.longitude);
                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude);

                try {
                    // Request the route from the Directions API
                    DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                            .mode(TravelMode.DRIVING)
                            .origin(origin)
                            .destination(destination)
                            .await();

                    // Check if a valid route was found
                    if (result != null && result.routes.length > 0) {
                        DirectionsRoute route = result.routes[0];
                        mMap.clear();

                        // Add a marker at the destination
                        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(address));

                        // Draw the polyline for the route
                        PolylineOptions polylineOptions = new PolylineOptions();
                        for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
                            polylineOptions.add(new LatLng(point.lat, point.lng));
                            polylineOptions.color(Color.BLUE);
                        }
                        Polyline polyline = mMap.addPolyline(polylineOptions);

                        // Move the camera to show the route
                        LatLngBounds latLngBounds = new LatLngBounds(
                                new LatLng(route.bounds.southwest.lat, route.bounds.southwest.lng),
                                new LatLng(route.bounds.northeast.lat, route.bounds.northeast.lng)
                        );
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                    } else {
                        Toast.makeText(this, "No route found", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | IOException |
                         com.google.maps.errors.ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Directions API error", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
        }
    }
}
