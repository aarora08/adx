package io.blacknode.adx;


import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements
        SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener{

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraviewOn = false;
    private AugmentedPOI mPoi;

    private double mAzimuthReal = 0;
    private double mAzimuthTeoretical = 0;
    private static double AZIMUTH_ACCURACY = 5;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;

    private MyCurrentAzimuth myCurrentAzimuth;
//    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;
    ImageView pointerIcon;

    private LocationEngine myCurrentLocation;
    private LocationEngineListener locationEngineListener;

//    private DatabaseReference mRef;
//    private GeoFire mGeoFire;

    private ImageButton switchView;
    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
        setupLayout();
        setAugmentedRealityPoint();



    }




    private void setAugmentedRealityPoint() {
        mPoi = new AugmentedPOI(
                "NYIT",
                "EGGC ",
                40.7427,
                -74.0036
        );
    }

    public double calculateTeoreticalAzimuth() {
        double dX = mPoi.getPoiLatitude() - mMyLatitude;
        double dY = mPoi.getPoiLongitude() - mMyLongitude;

        double phiAngle;
        double tanPhi;
        double azimuth = 0;

        tanPhi = Math.abs(dY / dX);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        if (dX > 0 && dY > 0) { // I quater
            return azimuth = phiAngle;
        } else if (dX < 0 && dY > 0) { // II
            return azimuth = 180 - phiAngle;
        } else if (dX < 0 && dY < 0) { // III
            return azimuth = 180 + phiAngle;
        } else if (dX > 0 && dY < 0) { // IV
            return azimuth = 360 - phiAngle;
        }

        return phiAngle;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        double minAngle = azimuth - AZIMUTH_ACCURACY;
        double maxAngle = azimuth + AZIMUTH_ACCURACY;
        List<Double> minMax = new ArrayList<Double>();

        if (minAngle < 0)
            minAngle += 360;

        if (maxAngle >= 360)
            maxAngle -= 360;

        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);

        return minMax;
    }

    private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
        if (minAngle > maxAngle) {
            if (isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
                return true;
        } else {
            if (azimuth > minAngle && azimuth < maxAngle)
                return true;
        }
        return false;
    }

    private void updateDescription() {
        descriptionTextView.setText(mPoi.getPoiName() + " azimuthTeoretical "
                + mAzimuthTeoretical + " azimuthReal " + mAzimuthReal + " latitude "
                + mMyLatitude + " longitude " + mMyLongitude);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        mAzimuthTeoretical = calculateTeoreticalAzimuth();
        Toast.makeText(getActivity(),"latitude: "+location.getLatitude()+" longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        updateDescription();
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
        mAzimuthReal = azimuthChangedTo;
        mAzimuthTeoretical = calculateTeoreticalAzimuth();

        pointerIcon = (ImageView) getView().findViewById(R.id.icon);

        double minAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(0);
        double maxAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(1);

        if (isBetween(minAngle, maxAngle, mAzimuthReal)) {
            pointerIcon.setVisibility(View.VISIBLE);
        } else {
            pointerIcon.setVisibility(View.INVISIBLE);
        }

        updateDescription();
    }

    @Override
    public void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.deactivate();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.activate();
    }

    private void enableLocation(boolean enabled) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = myCurrentLocation.getLastLocation();
        if (lastLocation != null) {

        }

        locationEngineListener = new LocationEngineListener() {
            @Override
            public void onConnected() {
                // No action needed here.
            }

            @Override
            public void onLocationChanged(Location location) {

                if (location != null) {
                    // Move the map camera to where the user location is and then remove the
                    // listener so the camera isn't constantly updating when the user location
                    // changes. When the user disables and then enables the location again, this
                    // listener is registered again and will adjust the camera once again.
                    myCurrentLocation.removeLocationEngineListener(this);
                }
            }

        };
        myCurrentLocation.addLocationEngineListener(locationEngineListener);



        // Enable or disable the location layer on the map
    }

    private void setupListeners() {
        
        myCurrentLocation = new LocationSource(getContext());
        myCurrentLocation.activate();

//        myCurrentLocation = new MyCurrentLocation(this);
//        myCurrentLocation.buildGoogleApiClient(getActivity());
//        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this,getActivity().getApplicationContext());
        myCurrentAzimuth.start();
    }

    private void setupLayout() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //     int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        descriptionTextView = getView().findViewById(R.id.cameraTextView);

        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView =  getView().findViewById(R.id.cameraview);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (isCameraviewOn) {
            mCamera.stopPreview();
            isCameraviewOn = false;
        }

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                isCameraviewOn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isCameraviewOn = false;
    }

}


