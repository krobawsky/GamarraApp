package tecsup.integrador.gamarraapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import layout.ProductsFragment;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.ProductosActivity;
import tecsup.integrador.gamarraapp.activity.TiendaActivity;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.servicios.ApiService;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> implements Filterable {

    private static final String TAG = ProductosAdapter.class.getSimpleName();

    private List<Producto> productos;
    public ArrayList<Producto> filterList;

    private Filter fRecords;

    private ProductsFragment fragment;

    public ProductosAdapter(ProductsFragment fragment){
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
                        Intent intent = new Intent(fragment.getActivity(), TiendaActivity.class);
                        intent.putExtra("ID", Integer.parseInt(producto.getTienda_id()));
                        fragment.startActivity(intent);
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
