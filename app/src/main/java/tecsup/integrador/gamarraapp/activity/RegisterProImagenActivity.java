package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Categoria2;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;
import tecsup.integrador.gamarraapp.servicios.ResponseMessage;


public class RegisterProImagenActivity extends AppCompatActivity {

    private static final String TAG = RegisterProImagenActivity.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private ProgressDialog pDialog;

    private ImageView imgPreview;

    private ImageButton btnBack;

    private String nombreProducto, precioProducto, detallesProducto, categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pro_imagen);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        nombreProducto = getIntent().getExtras().getString("nombre", null);
        precioProducto = getIntent().getExtras().getString("precio", null);
        detallesProducto = getIntent().getExtras().getString("detalles", null);
        categoria = getIntent().getExtras().getString("categoria", null);

        Log.d(TAG, "Datos producto: " + nombreProducto+ ","+ precioProducto +", "+ detallesProducto +", "+categoria);

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Registrar Oferta
     */

    public void registrerOferta(View view) {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<ResponseMessage> call = null;

        if (mediaFileUri == null) {
            Toast.makeText(this, "Suba la imagen de la oferta.", Toast.LENGTH_SHORT).show();
            return;

        } else {

            pDialog.setMessage("Subiendo oferta ...");
            showDialog();

            // Si se incluye hacemos envió en multiparts
            File file = new File(getRealPathFromURI(mediaFileUri));
            Log.d(TAG, "File: " + file.getPath() + " - exists: " + file.exists());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photobmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

            // get tienda_id from SharedPreferences
            String tienda_id = sharedPreferences.getString("tienda_id", null);
            Log.d(TAG, "tienda_id: " + tienda_id);

            //Lista de categorias
            List<Categoria2> categorias = CategoriaRepository.listCategoriasProducto();
            String categoria_id = null;
            
            for (Categoria2 cat : categorias) {
                if (cat.getNombre().equalsIgnoreCase(categoria)){
                    categoria_id = String.valueOf(cat.getId());
                    Log.d(TAG, "categoria_id: " + categoria_id);
                }
            }
            RequestBody nombrePart = RequestBody.create(MultipartBody.FORM, nombreProducto);
            RequestBody precioPart = RequestBody.create(MultipartBody.FORM, precioProducto);
            RequestBody detallesPart = RequestBody.create(MultipartBody.FORM, detallesProducto);
            RequestBody tiendaIdPart = RequestBody.create(MultipartBody.FORM, tienda_id);
            RequestBody categoriaIdPart = RequestBody.create(MultipartBody.FORM, categoria_id);
            
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

            call = service.createProducto(nombrePart, precioPart, detallesPart, tiendaIdPart, categoriaIdPart, imagenPart);
        }

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    hideDialog();

                    if (response.isSuccessful()) {

                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(RegisterProImagenActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterProImagenActivity.this, UserComercianteActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Intent intent = new Intent(RegisterProImagenActivity.this, UserComercianteActivity.class);
                        startActivity(intent);
                        finish();
                        //Toast.makeText(RegisterProImagenActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                hideDialog();
                Intent intent = new Intent(RegisterProImagenActivity.this, UserComercianteActivity.class);
                startActivity(intent);
                finish();
                //Toast.makeText(RegisterProImagenActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private Uri mediaFileUri;
    private static final int PHOTO_SEND = 200;
    private static final int CAPTURE_IMAGE_REQUEST = 300;
    private Bitmap photobmp;

    /**
     * Galleyy handler
     */

    public void galleryPicture(View view){

        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
            return;
        }

        // Iniciando la vista de la galería
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                PHOTO_SEND);
    }

    /**
     * Camera handler
     */

    public void takePicture(View view) {
        try {

            if (!permissionsGranted()) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
                return;
            }

            // Creando el directorio de imágenes (si no existe)
            File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    throw new Exception("Failed to create directory");
                }
            }

            // Definiendo la ruta destino de la captura (Uri)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            mediaFileUri = Uri.fromFile(mediaFile);

            // Iniciando la captura
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Error en captura: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Cargar imagen
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            // Resultado en la captura de la foto
            if (resultCode == RESULT_OK) {
                try {
                    Log.d(TAG, "ResultCode: RESULT_OK");

                    photobmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaFileUri);

                    // Reducir la imagen a 800px solo si lo supera
                    photobmp = scaleBitmapDown(photobmp, 800);
                    //imgPreview.setImageBitmap(photobmp);

                    int width = 360;
                    int height = 360;
                    imgPreview.setImageBitmap(Bitmap.createScaledBitmap(photobmp, width , height , false));

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(this, "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "ResultCode: RESULT_CANCELED");
            } else {
                Log.d(TAG, "ResultCode: " + resultCode);
            }

        } else if(requestCode == PHOTO_SEND){
            if (resultCode == RESULT_OK) {

                mediaFileUri = data.getData();
                String aaa = getRealPathFromURI(mediaFileUri);
                photobmp = BitmapFactory.decodeFile(aaa);

                // Reducir la imagen a 800px solo si lo supera
                photobmp = scaleBitmapDown(photobmp, 800);
                //imgPreview.setImageBitmap(photobmp);

                int width = 360;
                int height = 360;
                imgPreview.setImageBitmap(Bitmap.createScaledBitmap(photobmp, width , height , false));

            }
        }
    }

    /**
     * Permissions handler
     */

    private static final int PERMISSIONS_REQUEST = 200;

    private static String[] PERMISSIONS_LIST = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean permissionsGranted() {
        for (String permission : PERMISSIONS_LIST) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.d(TAG, "" + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, PERMISSIONS_LIST[i] + " permiso rechazado!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(this, "Permisos concedidos, intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Redimensionar una imagen bitmap
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    /**
     * Obtiene la ruta del archivo de imagen en el dispositivo
     * */
    public String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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
