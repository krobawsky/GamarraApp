package tecsup.integrador.gamarraapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Usuario;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class LoginUserActivity extends AppCompatActivity {

    private static final String TAG = LoginUserActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;

    private Button loginBtn;
    private Button linkBtn;

    private EditText inputEmail;
    private EditText inputPassword;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        inputEmail = (EditText) findViewById(R.id.userTxt);
        inputPassword = (EditText) findViewById(R.id.passTxt);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        linkBtn = (Button) findViewById(R.id.btnLink);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // username remember
        String username = sharedPreferences.getString("username", null);
        if(username != null){
            inputEmail.setText(username);
            inputPassword.requestFocus();
        }

        // Login button Click Event
        loginBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    pDialog.setMessage("Ingresando ...");
                    showDialog();

                    checkLogin(email, password);

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Campos incompletos!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        linkBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterUserActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Verificar el login
     * */
    private void checkLogin(final String email, final String password) {

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
                        hideDialog();


                        for (Usuario usuario : usuarios) {

                            if (usuario.getEmail().equalsIgnoreCase(email) && usuario.getPassword().equalsIgnoreCase(convertMd5(password))) {

                                String name = usuario.getNombre();
                                Toast.makeText(getApplication(), "Bienvenido "+ name, Toast.LENGTH_SHORT).show();

                                // Go to Dashboard
                                goDashboard(String.valueOf(usuario.getId()), email);
                                return;
                            }
                        }

                        for (Usuario usuario : usuarios) {
                            if (!usuario.getEmail().equalsIgnoreCase(email) && !usuario.getPassword().equalsIgnoreCase(convertMd5(password))) {
                                Toast.makeText(LoginUserActivity.this, "Datos incorrectos.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(LoginUserActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                hideDialog();
                Toast.makeText(LoginUserActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Convertir la contraseña
     */

    public static String convertMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * Go Login
     */

    private  void goDashboard(String id, String username){

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("islogged", true).commit();
        editor.putString("username", username).commit();
        editor.putString("id", id).commit();

        Intent intent = new Intent(LoginUserActivity.this, UserComercianteActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Progress Dialog
     */

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
