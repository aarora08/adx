package io.blacknode.adx;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Navigation View
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    // Fragments
    private MapFragment mapFragment;
    private UserProfile userFragment;
    private CameraFragment cameraFragment;

    private boolean mLoginCheck;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav= (BottomNavigationView) findViewById (R.id.bottomNavigationView);

        mapFragment = new MapFragment();
        userFragment = new UserProfile();
        cameraFragment = new CameraFragment();

        setFragment(cameraFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_map:
                        mMainNav.setItemBackgroundResource((R.color.colorPrimary));
                        setFragment(mapFragment);

                        return true;

                    case R.id.nav_camera:
                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        if(mapFragment.isVisible()){
                            mapFragment.onDestroy();
                        }
                        setFragment(cameraFragment);
                        return true;

                    case R.id.nav_user:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        if(mapFragment.isVisible()){
                            mapFragment.onDestroy();
                        }

                        setFragment(userFragment);
                        return true;

                    default:
                        return false;
                }
            }




    });

}


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();

}


}
