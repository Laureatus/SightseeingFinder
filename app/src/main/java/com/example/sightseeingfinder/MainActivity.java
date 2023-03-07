package com.example.sightseeingfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.Distance;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import android.location.Location;

import java.util.ArrayList;

public class MainActivity extends Activity implements LocationListener {
    private MapView mMapView;
    private MyLocationNewOverlay mMyLocationOverlay;
    private LocationManager locationManager;

    private double longitude;
    private double latitude;

    private int firstTime = 0;

    Location locationA = new Location("pointA");
    Location locationB = new Location("pointB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisiere die Konfiguration von osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // Setze das Layout und die MapView
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapview);

        final TextView distance = (TextView) findViewById(R.id.distance);


        // Setze die Map-Konfiguration
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setMultiTouchControls(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lon = lastKnownLocation.getLongitude();
                GeoPoint startPoint = new GeoPoint(lat, lon);
                IMapController mapController = mMapView.getController();
                mapController.setZoom(15.0);
                mapController.setCenter(startPoint);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
            mMyLocationOverlay = new MyLocationNewOverlay(mMapView);
            mMapView.getOverlays().add(mMyLocationOverlay);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        firstTime = 0;
        //mMyLocationOverlay.enableMyLocation();
    }

    // Idee für die Logik der for loop wurde aus chatGPT übernommen.
    public void calculateDistance(Location location) {
        ArrayList<SightseeingLocation> sightseeingLocations = new ArrayList<>();
        sightseeingLocations.add(new SightseeingLocation(8.5399872, 47.3770818, "Alfred Escher Denkmal"));
        sightseeingLocations.add(new SightseeingLocation(8.6286014, 47.2491651, "Halbinsel AU"));
        sightseeingLocations.add(new SightseeingLocation(10.5420327, 47.3795071, "Test 2"));
        double minDistance = Double.MAX_VALUE;
        SightseeingLocation closestSightseeingLocation = null;

        Location locationA = new Location("pointA");
        locationA.setLatitude(location.getLatitude());
        locationA.setLongitude(location.getLongitude());

        for (SightseeingLocation sightseeingLocation : sightseeingLocations) {
            Location locationB = new Location("pointB");
            locationB.setLatitude(sightseeingLocation.getLatitude());
            locationB.setLongitude(sightseeingLocation.getLongitude());

            float[] results = new float[1];
            Location.distanceBetween(locationA.getLatitude(), locationA.getLongitude(),
                    locationB.getLatitude(), locationB.getLongitude(), results);
            double distance = results[0];

            if (distance < minDistance) {
                minDistance = distance;
                closestSightseeingLocation = sightseeingLocation;
            }
        }

        final TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Double.toString(minDistance));
        GeoPoint sightseeing = new GeoPoint(closestSightseeingLocation.getLatitude(),closestSightseeingLocation.getLongitude());
        Marker sightseeingMarker = new Marker(mMapView);
        sightseeingMarker.setPosition(sightseeing);
        sightseeingMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        sightseeingMarker.setTitle(closestSightseeingLocation.getName());
        mMapView.getOverlays().add(sightseeingMarker);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMyLocationOverlay.disableMyLocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        calculateDistance(location);
    }
}