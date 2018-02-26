package io.blacknode.adx;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private MapView mapView;
    private MapboxMap map;
    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private final int permsRequestCode = 200;
    private PermissionsManager permissionsManager;
    private FloatingActionButton floatingActionButton;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Mapbox.getInstance(this.getActivity(), getString(R.string.access_token));
        locationEngine = new LocationSource(getContext());
        locationEngine.activate();
        mapView = getView().findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {


            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;


                // Adding the map style
                map.setStyleUrl("mapbox://styles/aarora08/cj4rmee4d85c52qlghrb0zlpu");


            }

        });
        floatingActionButton = getView().findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps();
                }
            }
        });

    }

    //gps
    private void toggleGps() {


            enableLocation(true);

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
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                    locationEngine.removeLocationEngineListener(this);
                }
            }

        };
        locationEngine.addLocationEngineListener(locationEngineListener);
        floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);


        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }




    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }




    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        new Thread(new Runnable() {
            public void run() {


                android.app.Fragment fragment = getActivity().getFragmentManager()
                        .findFragmentByTag("MapFragment");
                if (null != fragment) {
                    android.app.FragmentTransaction ft = getActivity()
                            .getFragmentManager().beginTransaction();
                    ft.remove(fragment);
                    ft.commit();

                }
            }
        }).start();

        }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
//        new Thread(new Runnable() {
//            public void run() {
//
//
//               android.app.Fragment fragment = getActivity().getFragmentManager()
//                        .findFragmentById(R.id.nav_map);
//                if (null != fragment) {
//                    android.app.FragmentTransaction ft = getActivity()
//                            .getFragmentManager().beginTransaction();
//                    ft.remove(fragment);
//                    ft.commit();
//
//                }
//            }
//        }).start();

    }








}
