package tecsup.integrador.gamarraapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;

import layout.LoginComercianteFragment;
import layout.LoginUsuarioFragment;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.adapter.SectionsPageAdapter;
import tecsup.integrador.gamarraapp.helper.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SessionManager session;

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    Animation downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // session manager
        session = new SessionManager(getApplicationContext());

        // islogged remember
        if(sharedPreferences.getBoolean("islogged", false)){
            goComerciante();
        }

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            goUser();
        }

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        mViewPager.setAnimation(downtoup);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setAnimation(downtoup);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new LoginUsuarioFragment(), "Usuario");
        adapter.addFragment(new LoginComercianteFragment(), "Comerciante");

        viewPager.setAdapter(adapter);
    }

    /**
     * Go Login
     */

    private  void goUser(){
        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
    }

    private  void goComerciante(){
        Intent intent = new Intent(LoginActivity.this, UserComercianteActivity.class);
        startActivity(intent);
        finish();
    }

}
