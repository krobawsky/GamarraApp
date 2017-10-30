package tecsup.integrador.gamarraapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import layout.SearchFragment;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.DetailActivity;
import tecsup.integrador.gamarraapp.datos.Tienda;

public class TiendasAdapter extends RecyclerView.Adapter<TiendasAdapter.ViewHolder> {

    private List<Tienda> tiendas;

    private Context context;
    private Activity activity;
    private Tienda tienda;

    public TiendasAdapter(FragmentActivity activity){
        this.tiendas = new ArrayList<>();
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
    public void onBindViewHolder(TiendasAdapter.ViewHolder viewHolder, int position) {

        tienda = this.tiendas.get(position);

        viewHolder.nombreText.setText(tienda.getNombre());
        viewHolder.telefonoText.setText(tienda.getTelefono());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("ID", tienda.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.tiendas.size();
    }

}
