package tecsup.integrador.gamarraapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import layout.MapFragment;
import layout.MisOfertasFragment;
import layout.PerfilFragment;
import layout.MiTiendaFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Usuario;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class UserComercianteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = UserComercianteActivity.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private String usuario_id;

    private ImageView menuImg;
    private TextView menuName;
    private TextView menuEmail;

    //Datos
    private String email;
    private String name;
    private String dni;
    private String photoUrl = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_comerciante);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //el header del menu-------------------------------
        View hView = navigationView.getHeaderView(0);
        menuName = (TextView) hView.findViewById(R.id.menuName);
        menuEmail = (TextView) hView.findViewById(R.id.menuEmail);
        menuImg = (ImageView) hView.findViewById(R.id.menuImg);

        initialize();
    }

    //<----------------------------- Initialize ---------------------->

    public void initialize(){

        //Fragment
        String tienda_id = sharedPreferences.getString("tienda_id", null);
        Log.d(TAG, "tienda_id: " + tienda_id);

        if( tienda_id != null ){
            MisOfertasFragment misOfertasFragment = new MisOfertasFragment();
            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
            transaction2.replace(R.id.content, misOfertasFragment);
            transaction2.commit();

        } else {
            MiTiendaFragment tiendaFragment = new MiTiendaFragment();
            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
            transaction2.replace(R.id.content, tiendaFragment);
            transaction2.commit();

        }

        //id de usuario SharedPreferences
        usuario_id = sharedPreferences.getString("id", null);
        Log.d(TAG, "usuario_id: " + usuario_id);

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Usuario>> call = service.getUsuarios();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Usuario> usuarios = response.body();
                        Log.d(TAG, "usuarios: " + usuarios);

                        for (Usuario usuario : usuarios) {
                            if (usuario_id.equalsIgnoreCase(String.valueOf(usuario.getId()))){

                                name = usuario.getNombre();
                                email = usuario.getEmail();
                                dni = usuario.getDni();
                                photoUrl = ApiService.API_BASE_URL + "/perfiles/" + usuario.getImg();

                                menuName.setText(usuario.getNombre());
                                menuEmail.setText(usuario.getEmail());
                                Picasso.with(UserComercianteActivity.this).load(photoUrl).resize(72, 72)
                                        .centerCrop().into(menuImg);
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(UserComercianteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(UserComercianteActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    //<----------------------------- Nav Drawer ---------------------->

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_comerciante, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_us) {
            Dialog productDialog = new Dialog(UserComercianteActivity.this);
            productDialog.setContentView(R.layout.alert_nosotros);
            productDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_map) {
            MapFragment mapFragment = new MapFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, mapFragment);
            transaction.commit();

        } else if (id == R.id.navigation_store) {
            MiTiendaFragment tiendaFragment = new MiTiendaFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, tiendaFragment);
            transaction.commit();

        } else if (id == R.id.navigation_ofertas) {
            MisOfertasFragment misOfertasFragment = new MisOfertasFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, misOfertasFragment);
            transaction.commit();

        } else if (id == R.id.nav_perfil) {
            PerfilFragment perfilFragment = new PerfilFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, perfilFragment);

            Bundle args = new Bundle();
            args.putString("photo", photoUrl);
            args.putString("name", name);
            args.putString("email", email);
            args.putString("dni", dni);
            perfilFragment.setArguments(args);

            transaction.commit();
        } else if (id == R.id.nav_logut) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {

        // remove from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("islogged", false).commit();
        editor.putString("tienda_id", null).commit();
        editor.putString("id", null).commit();

        // Launching the login activity
        Intent intent = new Intent(UserComercianteActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
