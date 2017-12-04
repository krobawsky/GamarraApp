package tecsup.integrador.gamarraapp.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import layout.OfertasFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.ScrollingGaleriaActivity;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> implements Filterable {

    private static final String TAG = ProductosAdapter.class.getSimpleName();

    private List<Producto> productos;

    private Filter fRecords;

    private OfertasFragment fragment;

    public ProductosAdapter(OfertasFragment fragment){
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
        }
    }

    @Override
    public ProductosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.ViewHolder viewHolder, final int position) {

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

                store.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ApiService service = ApiServiceGenerator.createService(ApiService.class);
                        Call<Tienda> call = service.showTienda(Integer.parseInt(producto.getTienda_id()));
                        call.enqueue(new Callback<Tienda>() {
                            @Override
                            public void onResponse(Call<Tienda> call, Response<Tienda> response) {
                                try {

                                    int statusCode = response.code();
                                    Log.d(TAG, "HTTP status code: " + statusCode);

                                    if (response.isSuccessful()) {

                                        final Tienda tienda = response.body();
                                        Log.d(TAG, "tienda: " + tienda);

                                        Intent intent = new Intent(fragment.getActivity(), ScrollingGaleriaActivity.class);
                                        intent.putExtra("ID", tienda.getId());
                                        intent.putExtra("nombre", tienda.getNombre());
                                        intent.putExtra("telefono", tienda.getTelefono());
                                        intent.putExtra("ubicacion", tienda.getUbicacion());
                                        intent.putExtra("puesto", tienda.getPuesto());
                                        intent.putExtra("latitud", tienda.getLatitud());
                                        intent.putExtra("longitud", tienda.getLongitud());
                                        intent.putExtra("encargado_id", tienda.getComerciante_id());
                                        fragment.startActivity(intent);

                                    } else {
                                        Log.e(TAG, "onError: " + response.errorBody().string());
                                        throw new Exception("Error en el servicio");
                                    }

                                } catch (Throwable t) {
                                    try {
                                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                                        //Toast.makeText(fragment.getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                    }catch (Throwable x){}
                                }
                            }

                            @Override
                            public void onFailure(Call<Tienda> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                //Toast.makeText(fragment.getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDialog.cancel();
                    }
                });

                productDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.productos.size();
    }

    @Override
    public Filter getFilter() {
        if(fRecords == null) {
            fRecords=new ProductosAdapter.RecordFilter();
        }
        return fRecords;
    }

    //filter class
    private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            //Implement filter logic
            // if edittext is null return the actual list
            if (constraint == null || constraint.length() == 0) {
                //No need for filter
                results.values = productos;
                results.count = productos.size();

            } else {
                //Need Filter
                // it matches the text  entered in the edittext and set the data in adapter list
                ArrayList<Producto> fRecords = new ArrayList<Producto>();

                for (Producto producto : productos) {
                    if (producto.getNombre().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                        fRecords.add(producto);
                    }
                }
                results.values = fRecords;
                results.count = fRecords.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {

            //it set the data from filter to adapter list and refresh the recyclerview adapter
            productos = (ArrayList<Producto>) results.values;
            notifyDataSetChanged();
        }
    }
}
