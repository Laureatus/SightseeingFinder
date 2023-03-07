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
            GeoPoint sightseeing = new GeoPoint(47.3770818,8.5399872);
            Marker sightseeingMarker = new Marker(mMapView);
            sightseeingMarker.setPosition(sightseeing);
            sightseeingMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mMapView.getOverlays().add(sightseeingMarker);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        firstTime = 0;
        mMyLocationOverlay.enableMyLocation();
    }

    public void calculateDistance(Location location) {
        locationB.setLatitude(47.3770818);
        locationB.setLongitude(8.5399872);
        locationA.setLatitude(location.getLatitude());
        locationA.setLongitude(location.getLongitude());
        float distanceToLocation = locationA.distanceTo(locationB);
        final TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Float.toString(distanceToLocation));
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