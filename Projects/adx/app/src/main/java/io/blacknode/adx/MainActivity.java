package io.blacknode.adx;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "authlogin";
    // Navigation View
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    //button
    private Button mLogin;
    // Fragments
    private MapFragment mapFragment;
    private UserProfile userFragment;
    private CameraFragment cameraFragment;

    //private boolean mLoginCheck;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);



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
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Toast.makeText(MainActivity.this,"Log In!",Toast.LENGTH_SHORT).show();

                    // User is signed in
                    Intent i = new Intent(MainActivity.this, LoginPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                // ...
            }
        };
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this,"Log In!",Toast.LENGTH_SHORT).show();

            // User is signed in
            Intent i = new Intent(this, LoginPage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else{
            Toast.makeText(this,"Logged In!",Toast.LENGTH_SHORT).show();
            mLogin =  findViewById(R.id.login);
            mLogin.setText(R.string.logout);
        }

}


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();

}
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
