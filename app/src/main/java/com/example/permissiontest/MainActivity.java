package com.example.permissiontest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {
    static final int PIC_FROM_CAMERA = 1;
    private String currentPhotoPath = null;
    private ImageView imageView;
    LocationManager locationManager;
    private Double longitude;
    private Double latitude;
    private Boolean storagePermit;
    private Boolean checkLocation;
    private Boolean checkCamera;
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storagePermit = (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        checkLocation = (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        checkCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!storagePermit)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        File storage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PermissionTest");
        if (!storage.exists()) {
            if (!storage.mkdir()) {
                Log.d("Erroe", "Can't create picture directory");
            }
        }

        Fresco.initialize(this);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);
        ImageButton button1 = (ImageButton) findViewById(R.id.button);
        button1.setAnimation(myAnim);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                if (!storagePermit)
                    Toast.makeText(getApplication(), "Please give the permission for read storage", Toast.LENGTH_SHORT).show();
                else
                    openGallery();
            }
        });

        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        button2.setAnimation(myAnim);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                if (!storagePermit)
                    Toast.makeText(getApplication(), "Please give the permission for read storage", Toast.LENGTH_SHORT).show();
                else if (!checkLocation)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                else
                    showNearPhoto();
            }
        });

        ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        button3.setAnimation(myAnim);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                if (!storagePermit)
                    Toast.makeText(getApplication(), "Please give the permission for read storage", Toast.LENGTH_SHORT).show();
                else if (!checkCamera && !checkLocation)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                else if (!checkCamera)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 4);
                else if (!checkLocation)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
                else
                    takePhoto();
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grandResults) {

        switch (requestCode) {
            case 1: {
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storagePermit = true;
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Warning")
                                .setMessage("If you don't approve this permission.\nYou can't use all function")
                                .setPositiveButton("OK!!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                })
                                .setNegativeButton("NOOOOO!!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        storagePermit = false;
                                    }
                                }).show();
                    }
                    Log.d("Permission", "Denied");
                }
                break;
            }
            case 2:
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocation = true;
                    showNearPhoto();
                }
                break;

            case 3:
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED && grandResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                else if(grandResults[0] == PackageManager.PERMISSION_GRANTED)
                    checkCamera = true;
                else if(grandResults[1] == PackageManager.PERMISSION_GRANTED)
                    checkLocation = true;
                break;

            case 4:
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCamera = true;
                    takePhoto();
                }
                break;
            case 5:
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocation = true;
                    takePhoto();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permission, grandResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PIC_FROM_CAMERA: {
                    File f = new File(currentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);
                    setPic();
                    setGPS();
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() {
        String dateStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageName = "JEPG_" + dateStamp;
        File storage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PermissionTest");
        File image = new File(storage.getPath() + File.separator + imageName + ".jpg");
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int width = bmOptions.outWidth;
        int height = bmOptions.outHeight;
        int layout_width = imageView.getWidth();
        int layout_height = imageView.getHeight();
        int scale = min(width/layout_width, height/layout_height);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bm);
    }

    private void openGallery() {
        Intent intent = new Intent(this, ReadGallery.class);
        startActivity(intent);
    }

    private void showNearPhoto() {
        Intent intent = new Intent(this, ShowNearPhoto.class);
        startActivity(intent);
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
            Toast.makeText(this, longitude + "  " + latitude, Toast.LENGTH_SHORT).show();
        }
    }

    private void setGPS() {
        getLocation();
        try {
            ExifInterface exif = new ExifInterface(currentPhotoPath);
            int num1Lat = (int) Math.floor(latitude);
            int num2Lat = (int) Math.floor((latitude - num1Lat) * 60);
            double num3Lat = (latitude - ((double) num1Lat + ((double) num2Lat / 60))) * 3600000;

            int num1Lon = (int) Math.floor(longitude);
            int num2Lon = (int) Math.floor((longitude - num1Lon) * 60);
            double num3Lon = (longitude - ((double) num1Lon + ((double) num2Lon / 60))) * 3600000;

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat + "/1," + num2Lat + "/1," + num3Lat + "/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon + "/1," + num2Lon + "/1," + num3Lon + "/1000");

            if (latitude > 0)
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            else
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");

            if (longitude > 0)
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            else
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            exif.saveAttributes();
            Log.d("GPS Info", Double.toString(latitude) + "  " + Double.toString(longitude));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
