package tecsup.integrador.gamarraapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.Categoria2;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    LinearLayout a11,a12;
    Animation uptowndown, downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //btncont = (Button)findViewById(R.id.button);
        a11 = (LinearLayout) findViewById(R.id.a11);
        a12 = (LinearLayout) findViewById(R.id.a12);
        uptowndown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        a11.setAnimation(uptowndown);
        a12.setAnimation(downtoup);

        addCategorias();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000); // 2 segundos
    }

    private void addCategorias(){

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Categoria>> call = service.getCategoriaTienda();
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {
                        List<Categoria> categoriasTienda = response.body();
                        Log.d(TAG, "categoriasTienda: " + categoriasTienda);

                        for(Categoria categoria: categoriasTienda){
                            //CategoriaRepository.delete(categoria.getId());
                            CategoriaRepository.createCategoriasTienda(categoria.getId(), categoria.getNombre());

                        }

                        List<Categoria> categorias = CategoriaRepository.listCategoriasTienda();
                        Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Call<List<Categoria>> call2 = service.getCategoriaProducto();
        call2.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {
                        List<Categoria> categoriasProducto = response.body();
                        Log.d(TAG, "categoriasProducto: " + categoriasProducto);

                        for(Categoria categoria: categoriasProducto){
                            //CategoriaRepository.delete(categoria.getId());
                            CategoriaRepository.createCategoriasProducto(categoria.getId(), categoria.getNombre());

                        }

                        List<Categoria2> categorias = CategoriaRepository.listCategoriasProducto();
                        Log.d(TAG, "categoriasProductoORM: " + categorias.toString());

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
