package tecsup.integrador.gamarraapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Categoria2;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;

public class RegisterProductoActivity extends AppCompatActivity {

    private static final String TAG = RegisterProductoActivity.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private MaterialSpinner spinnerCategoria;

    private EditText nombreInput;
    private EditText precioInput;
    private EditText detallesInput;

    private ImageButton btnBack;
    private Button btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_producto);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        nombreInput = (EditText) findViewById(R.id.nombre_input);
        precioInput = (EditText) findViewById(R.id.precio_input);
        detallesInput = (EditText) findViewById(R.id.detalles_input);
        spinnerCategoria = (MaterialSpinner) findViewById(R.id.spinner);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnBack = (ImageButton) findViewById(R.id.btnBack);

        //Spinner
        spinner();

        // Next Button Click event
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String nombre = nombreInput.getText().toString().trim();
                final String precio = precioInput.getText().toString().trim();
                final String detalles = detallesInput.getText().toString().trim();
                final String categoria = spinnerCategoria.getSelectedItem().toString().trim();

                if (!nombre.isEmpty() && !precio.isEmpty() && !categoria.isEmpty()) {

                    if(!categoria.equalsIgnoreCase("Escoja una categoría")){

                        Intent i = new Intent(RegisterProductoActivity.this, RegisterProImagenActivity.class);
                        i.putExtra("nombre", nombre);
                        i.putExtra("precio", precio);
                        i.putExtra("detalles", detalles);
                        i.putExtra("categoria", categoria);
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Escoja una categoría válida para continuar.", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Nombre y precio son requeridos para continuar.", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
            }
        });


        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Carga la lista de categorias de producto
     */

    public void spinner(){

        //Lista de categorias
        final List<String> values = new ArrayList<>();

        final List<Categoria2> categorias = CategoriaRepository.listCategoriasProducto();

        for (Categoria2 categoria : categorias) {
            values.add(categoria.getNombre());
        }

        ArrayAdapter dataAdapter = new ArrayAdapter(getApplication(), android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(dataAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String item = parent.getItemAtPosition(pos).toString();

                for (Categoria2 categoria : categorias) {

                    if(categoria.getNombre().equalsIgnoreCase(item)){
                        Toast.makeText(parent.getContext(), "Haz seleccionado: " + item, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                for (Categoria2 categoria : categorias) {

                    if(!categoria.getNombre().equalsIgnoreCase(item)){
                        //Toast.makeText(parent.getContext(), "", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
