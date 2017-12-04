package tecsup.integrador.gamarraapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import layout.MapFragment;
import layout.PerfilFragment;
import layout.OfertasFragment;
import layout.TiendasFragment;
import retrofit2.Call;
import retrofit2.Callback;
import tecsup.integrador.gamarraapp.R;

import tecsup.integrador.gamarraapp.services.MyJobService;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;
import tecsup.integrador.gamarraapp.servicios.ResponseMessage;

public class UserActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = UserActivity.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private String usuario_id;

    private ApiService service;

    private ImageView photoImageView;
    private TextView txtName;
    private TextView txtEmail;

    //Datos
    private String user_id;
    private String email;
    private String name;
    private String photo = "0";

    private GoogleApiClient googleApiClient;

    private ProfileTracker profileTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_usuario);

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

        MapFragment mapFragment = new MapFragment();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.content, mapFragment);
        transaction2.commit();

        //Con esto generamos el usuario en el header del menu
        View hView = navigationView.getHeaderView(0);
        photoImageView = (ImageView) hView.findViewById(R.id.imageView);
        txtName = (TextView) hView.findViewById(R.id.name);
        txtEmail = (TextView) hView.findViewById(R.id.email);

        initialize();
    }

    public void initialize(){

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Facebook
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    displayProfileInfo(currentProfile);
                }
            }

            private void displayProfileInfo(Profile currentProfile) {
            }
        };

        if (AccessToken.getCurrentAccessToken() == null) {

        } else {
            requestEmail(AccessToken.getCurrentAccessToken());

            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                displayProfileInfo(profile);
            } else {
                Profile.fetchProfileForCurrentAccessToken();
            }
        }

        // FirebaseJobDispatcher configuration
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(UserActivity.this));
        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(MyJobService.class)
                        .setTag("MyJobService")
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(1, 5)) // Cada 5 a 30 segundos
                        .build()
        );
    }

    //<------------------------------ Google -------------------------------------->//

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            user_id = account.getId();
            email = account.getEmail();
            name = account.getDisplayName();

            txtEmail.setText(email);
            txtName.setText(name);
            if(account.getPhotoUrl() != null){
                Glide.with(getApplicationContext()).load(account.getPhotoUrl()).into(photoImageView);
                photo = account.getPhotoUrl().toString();
            }

            Log.d(TAG, "user_id: " + user_id);

            newUser(user_id, email, name);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //<------------------------------------- Facebook ---------------------->

    private void requestEmail(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (response.getError() != null) {
                    Toast.makeText(getApplicationContext(), response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    email = object.getString("email");
                    setEmail(email);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setEmail(String email) {
        txtEmail.setText(email);
    }

    private void displayProfileInfo(Profile profile) {

        user_id = profile.getId();
        name = profile.getName();
        photo = profile.getProfilePictureUri(100, 100).toString();

        txtName.setText(name);
        Glide.with(getApplicationContext())
                .load(photo)
                .into(photoImageView);

        Log.d(TAG, "user_id: " + user_id);

        newUser(user_id, email, name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        profileTracker.stopTracking();
    }

    //<----------------------------- Verificar Login ---------------------->

    public void newUser(final String id, final String email, final String name){

        String userid = sharedPreferences.getString("user_id", "123");
        Log.d(TAG, "user_id: " + user_id);

        if (userid.equalsIgnoreCase(id)){
            //Toast.makeText(UserActivity.this, "Bienvenido "+name, Toast.LENGTH_LONG).show();

        } else {
            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("islogged", true).commit();
            editor.putString("user_id", user_id).commit();

            service = ApiServiceGenerator.createService(ApiService.class);
            Call<ResponseMessage> call = service.createUser(id, email, name);

            call.enqueue(new Callback<ResponseMessage>() {
                @Override
                public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                    try {

                        int statusCode = response.code();
                        Log.d(TAG, "HTTP status code: " + statusCode);

                        if (response.isSuccessful()) {

                            ResponseMessage responseMessage = response.body();
                            Log.d(TAG, "responseMessage: " + responseMessage);

                        } else {
                            Log.e(TAG, "onError: " + response.errorBody().string());
                            throw new Exception("Error en el servicio");
                        }

                    } catch (Throwable t) {
                        try {
                            Log.e(TAG, "onThrowable: " + t.toString(), t);
                            //Toast.makeText(UserActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Throwable x) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    //Toast.makeText(UserActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
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
        getMenuInflater().inflate(R.menu.home_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_us) {
            Dialog productDialog = new Dialog(UserActivity.this);
            productDialog.setContentView(R.layout.alert_nosotros);
            productDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_map) {
            MapFragment mapFragment = new MapFragment();
            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
            transaction2.replace(R.id.content, mapFragment);
            transaction2.commit();
        } else if (id == R.id.navigation_store) {
            TiendasFragment storeFragment = new TiendasFragment();
            FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
            transaction3.replace(R.id.content, storeFragment);
            transaction3.commit();
        } else if (id == R.id.navigation_ofertas) {
            OfertasFragment productsFragment = new OfertasFragment();
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            transaction1.replace(R.id.content, productsFragment);
            transaction1.commit();
        } else if (id == R.id.nav_perfil) {
            PerfilFragment perfilFragment = new PerfilFragment();
            FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
            transaction4.replace(R.id.content, perfilFragment);

            Bundle args = new Bundle();
            args.putString("photo", photo);
            args.putString("name", name);
            args.putString("email", email);
            perfilFragment.setArguments(args);

            transaction4.commit();
        }else if (id == R.id.nav_logut) {

            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        goLogInScreen();
                    }
                }
            });

            LoginManager.getInstance().logOut();
            goLogInScreen();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //<------------------------------ Cerrar sesión -------------------------------------->//

    private void goLogInScreen() {

        // remove from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("islogged", false).commit();
        editor.putString("user_id", null).commit();

        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
