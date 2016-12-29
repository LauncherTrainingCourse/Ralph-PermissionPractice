package com.example.permissiontest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ralph_Chao on 2016/12/23.
 */

public class ShowNearPhoto extends AppCompatActivity {

    ArrayList<String> imagePath = new ArrayList<>();
    LocationManager locationManager;
    private Double longitude;
    private Double latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_photo_layout);
        File externalStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PermissionTest");

        if (!externalStorage.exists() || externalStorage.listFiles().length < 1) {
            Toast.makeText(getApplicationContext(), "Album Empty", Toast.LENGTH_SHORT).show();
        } else {
            getLocation();
            File [] photoList = externalStorage.listFiles();
            Arrays.sort(photoList);
            for (File f : photoList) {
                if (f.isFile()) {
                    String imageName = f.getName();
                    String path = externalStorage.getAbsolutePath() + "/" + imageName;
                    if (checkDistance(path))
                        imagePath.add(path);
                }
            }
            Log.d("num", Integer.toString(externalStorage.listFiles().length));

            GridAdapter gridAdapter = new GridAdapter(this, imagePath);
            GridView gridView = (GridView) findViewById(R.id.near_gridview);
            gridView.setAdapter(gridAdapter);
        }
    }

    private Boolean checkDistance(String path) {
        double distance = 0;
        try {
            ExifInterface exi = new ExifInterface(path);
            String lat_data = exi.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lon_data = exi.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            double photo_lat = convertToDegree(lat_data);
            double photo_lon = convertToDegree(lon_data);
            Log.d("Photo Name", path);
            Log.d("lat_current", Double.toString(latitude));
            Log.d("lon_current", Double.toString(longitude));
            Log.d("lat_photo", Double.toString(photo_lat));
            Log.d("lon_photo", Double.toString(photo_lon));

            Location locationC = new Location("");
            locationC.setLatitude(latitude);
            locationC.setLongitude(longitude);
            Location locationP = new Location("");
            locationP.setLatitude(photo_lat);
            locationP.setLongitude(photo_lon);
            distance = locationC.distanceTo(locationP);
            Log.d("Distance", Double.toString(distance));

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (distance < 50)
            return true;

        return false;
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            Location location;

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            }
            else {
                Log.d("Error", "Can't get WIFI");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.d("Location", Double.toString(longitude) + "  " + Double.toString(latitude));
        }
    }

    private double convertToDegree(String input) {
        Double result;
        String[] DMS = input.split(",", 3);
        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double D = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double M = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double S = S0 / S1;

        result = D + (M / 60) + (S / 3600);

        return result;
    }
}
