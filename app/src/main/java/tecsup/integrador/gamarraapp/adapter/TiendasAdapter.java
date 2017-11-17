package tecsup.integrador.gamarraapp.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import layout.StoreFragment;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.DetailActivity;
import tecsup.integrador.gamarraapp.models.Tienda;

public class TiendasAdapter extends RecyclerView.Adapter<TiendasAdapter.ViewHolder> implements Filterable {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private List<Tienda> tiendas;
    public ArrayList<Tienda> filterList;

    private StoreFragment fragment;

    public TiendasAdapter(StoreFragment fragment){
        this.tiendas = new ArrayList<>();
        this.fragment = fragment;
    }

    public void setTiendas(List<Tienda> tiendas){
        this.tiendas = tiendas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nombreText;
        public TextView telefonoText;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreText = (TextView) itemView.findViewById(R.id.nombre_text);
            telefonoText = (TextView) itemView.findViewById(R.id.telefono_text);
        }
    }

    @Override
    public TiendasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tienda, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TiendasAdapter.ViewHolder viewHolder, int position) {

        final Tienda tienda = this.tiendas.get(position);

        viewHolder.nombreText.setText(tienda.getNombre());
        viewHolder.telefonoText.setText(tienda.getTelefono());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragment.getActivity(), DetailActivity.class);
                intent.putExtra("ID", tienda.getId());
                fragment.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.tiendas.size();
    }

    private Filter fRecords;

    @Override
    public Filter getFilter() {
        if(fRecords == null) {
            fRecords=new RecordFilter();
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
                results.values = tiendas;
                results.count = tiendas.size();

            } else {
                //Need Filter
                // it matches the text  entered in the edittext and set the data in adapter list
                ArrayList<Tienda> fRecords = new ArrayList<Tienda>();

                for (Tienda tienda : tiendas) {
                    if (tienda.getNombre().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                        fRecords.add(tienda);
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
            tiendas = (ArrayList<Tienda>) results.values;
            notifyDataSetChanged();
        }
    }
}
