package tecsup.integrador.gamarraapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import layout.MisOfertasFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.activity.TiendaActivity;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;
import tecsup.integrador.gamarraapp.servicios.ResponseMessage;

public class ProductosAdapterComerciante extends RecyclerView.Adapter<ProductosAdapterComerciante.ViewHolder> {

    private static final String TAG = ProductosAdapter.class.getSimpleName();

    private List<Producto> productos;

    private Filter fRecords;

    private MisOfertasFragment fragment;

    public ProductosAdapterComerciante(MisOfertasFragment fragment){
        this.productos = new ArrayList<>();
        this.fragment = fragment;
    }

    public void setProductos(List<Producto> productos){
        this.productos = productos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView fotoImage;
        public TextView nombreText;
        public TextView precioText;
        public ImageButton menuButton;

        public ViewHolder(View itemView) {
            super(itemView);
            fotoImage = (ImageView) itemView.findViewById(R.id.foto_image);
            nombreText = (TextView) itemView.findViewById(R.id.nombre_text);
            precioText = (TextView) itemView.findViewById(R.id.precio_text);
            menuButton = (ImageButton) itemView.findViewById(R.id.menu_button);
        }
    }

    @Override
    public ProductosAdapterComerciante.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ProductosAdapterComerciante.ViewHolder viewHolder, final int position) {

        final Producto producto = this.productos.get(position);

        viewHolder.nombreText.setText(producto.getNombre());
        viewHolder.precioText.setText("S/. " + producto.getPrecio());

        String url = ApiService.API_BASE_URL + "/images/" + producto.getImagen();
        Picasso.with(viewHolder.itemView.getContext()).load(url).resize(52, 52)
                .centerCrop().into(viewHolder.fotoImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog productDialog = new Dialog(fragment.getActivity());
                productDialog.setContentView(R.layout.alert_producto);
                productDialog.setCancelable(false);

                ImageButton close = ( ImageButton ) productDialog.findViewById(R.id.close_btn);
                ImageButton store = ( ImageButton ) productDialog.findViewById(R.id.store_btn);
                ImageView productImg = ( ImageView ) productDialog.findViewById(R.id.image_product);
                TextView productName = ( TextView ) productDialog.findViewById(R.id.name_product);
                TextView productDetails = ( TextView ) productDialog.findViewById(R.id.details_product);
                TextView productPrice = ( TextView ) productDialog.findViewById(R.id.price_product);

                String url = ApiService.API_BASE_URL + "/images/" + producto.getImagen();
                Picasso.with(fragment.getActivity()).load(url).resize(320, 320)
                        .centerCrop().into(productImg);

                productName.setText(producto.getNombre());
                productDetails.setText(producto.getDescripcion());
                productPrice.setText("S/. "+ producto.getPrecio());

                store.setVisibility(View.GONE);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDialog.cancel();
                    }
                });

                productDialog.show();
            }
        });

        viewHolder.menuButton.setVisibility(View.VISIBLE);
        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove_button:

                                ApiService service = ApiServiceGenerator.createService(ApiService.class);

                                Call<ResponseMessage> call = service.destroyProducto(producto.getId());

                                call.enqueue(new Callback<ResponseMessage>() {
                                    @Override
                                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                        try {

                                            int statusCode = response.code();
                                            Log.d(TAG, "HTTP status code: " + statusCode);

                                            if (response.isSuccessful()) {

                                                ResponseMessage responseMessage = response.body();
                                                Log.d(TAG, "responseMessage: " + responseMessage);

                                                // Eliminar item del recyclerView y notificar cambios
                                                productos.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, productos.size());

                                                Toast.makeText(v.getContext(), responseMessage.getMessage(), Toast.LENGTH_LONG).show();

                                            } else {
                                                Log.e(TAG, "onError: " + response.errorBody().string());
                                                throw new Exception("Error en el servicio");
                                            }

                                        } catch (Throwable t) {
                                            try {
                                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                                Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                            }catch (Throwable x){}
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.toString());
                                        Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                });

                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.productos.size();
    }

}
