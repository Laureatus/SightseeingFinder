package com.example.sightseeingfinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {
    private MapView mMapView;
    private MyLocationNewOverlay mMyLocationOverlay;
    private LocationManager locationManager;

    private double longitude;
    private double latitude;

    private int count = 0;

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
        mMyLocationOverlay = new MyLocationNewOverlay(mMapView);

        final TextView distance = (TextView) findViewById(R.id.distance);
        final Button compassButton = (Button) findViewById(R.id.compassButton);

        compassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CompassActivity.class);
                startActivity(intent);
            }
        });

        // Setze die Map-Konfiguration
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setMultiTouchControls(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lon = lastKnownLocation.getLongitude();
                GeoPoint startPoint = new GeoPoint(lat, lon);
                IMapController mapController = mMapView.getController();
                mapController.setZoom(15.0);
                mapController.setCenter(startPoint);
                mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
            mMapView.getOverlays().add(mMyLocationOverlay);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMyLocationOverlay.enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
        }


    }

    // Idee für die Logik der for loop wurde aus chatGPT übernommen.
    public void calculateDistance(Location location) {
        ArrayList<SightseeingLocation> sightseeingLocations = new ArrayList<>();
        sightseeingLocations.add(new SightseeingLocation(8.5399872, 47.3770818, "Alfred Escher Denkmal"));
        sightseeingLocations.add(new SightseeingLocation(8.548926043622066, 47.371008917607476,"Kunsthaus Zürich"));
        sightseeingLocations.add(new SightseeingLocation(8.541044269554044, 47.36977609848296, "Fraumünster"));
        sightseeingLocations.add(new SightseeingLocation(8.541602169059097, 47.3665795805176, "Bürkliplatz"));
        sightseeingLocations.add(new SightseeingLocation(8.538682692837556, 47.36967811652621, "Paradeplatz"));
        sightseeingLocations.add(new SightseeingLocation(8.544032540729392, 47.37020933844361, "Grossmünster"));
        sightseeingLocations.add(new SightseeingLocation(8.540788599999999, 47.3730337, "Lindenhof Zürich"));
        sightseeingLocations.add(new SightseeingLocation(8.543721099999999, 47.3727049, "Niederdorf"));
        sightseeingLocations.add(new SightseeingLocation(8.5745307, 47.3870227, "Zoo Zürich"));
        sightseeingLocations.add(new SightseeingLocation(8.560222099999999, 47.3593613, "Botanischer Garten der Universität Zürich"));
        sightseeingLocations.add(new SightseeingLocation(8.5405492, 47.3790558, "Schweizerisches Nationalmuseum"));
        sightseeingLocations.add(new SightseeingLocation(8.5629379, 47.3847827, "Aussichtspunkt Zürich"));
        sightseeingLocations.add(new SightseeingLocation(8.5305524, 47.3578898, "Rieterpark"));
        sightseeingLocations.add(new SightseeingLocation(8.541932099999999, 47.37352279999999, "Täufergedenkplatte"));
        sightseeingLocations.add(new SightseeingLocation(8.5368608, 47.36348189999999, "Bürkli-Statue"));
        sightseeingLocations.add(new SightseeingLocation(8.542102999999997, 47.3698163, "Hans Waldmann Statue von Hermann Haller"));
        sightseeingLocations.add(new SightseeingLocation(8.5432963, 47.3694813, "Zwingli Denkmal"));
        sightseeingLocations.add(new SightseeingLocation(8.5366988, 47.3496853, "Saffa Insel"));
        double minDistance = Double.MAX_VALUE;
        SightseeingLocation closestSightseeingLocation = null;

        Location locationA = new Location("pointA");
        locationA.setLatitude(location.getLatitude());
        locationA.setLongitude(location.getLongitude());

        for (SightseeingLocation sightseeingLocation : sightseeingLocations) {
            GeoPoint sightseeing = new GeoPoint(sightseeingLocation.getLatitude(),sightseeingLocation.getLongitude());
            Marker sightseeingMarker = new Marker(mMapView);
            sightseeingMarker.setPosition(sightseeing);
            sightseeingMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            sightseeingMarker.setTitle(sightseeingLocation.getName());
            mMapView.getOverlays().add(sightseeingMarker);

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
        distance.setText(Double.toString(Math.round(minDistance*100.0)/100.0));
        GeoPoint sightseeing = new GeoPoint(closestSightseeingLocation.getLatitude(),closestSightseeingLocation.getLongitude());
        List<GeoPoint> points = new ArrayList<>();
        points.add(sightseeing);
        points.add(new GeoPoint(location.getLatitude(),location.getLongitude()));
        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(Color.RED);
        line.setWidth(5f);
        mMapView.getOverlayManager().add(line);
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMyLocationOverlay.disableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        count++;
        GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        IMapController mapController = mMapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(currentLocation);
        calculateDistance(location);
    }
}