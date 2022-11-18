package es.upv.epsg.iot_g1_1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import es.upv.epsg.iot_g1_1.databinding.ElementoListaBinding;

public class AdaptadorPrendas extends RecyclerView.Adapter<AdaptadorPrendas.ViewHolder> {

    ArrayList<Prenda> listaPrendas;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorPrendas(ArrayList<Prenda> listaPrendas, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaPrendas = listaPrendas;
    }

    @Override
    public AdaptadorPrendas.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.elemento_lista, null);
        return new AdaptadorPrendas.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final AdaptadorPrendas.ViewHolder holder, final int position) {
        holder.personaliza(listaPrendas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaPrendas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public ImageView foto;

        public ViewHolder(ElementoListaBinding itemView) {
            super(itemView.getRoot());
            nombre = itemView.nombre;
            foto = itemView.foto;

        }

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            foto = itemView.findViewById(R.id.foto);
        }

        public void personaliza(final Prenda prenda) {
            nombre.setText(prenda.getNombrePrenda());

            //foto con url para probar el sistema de urls, no funciona, lo mirare
            //foto.setImageURI(Uri.parse("https://freesvg.org/img/1667815134wings-simple-clip-art.png"));

            if(prenda.getTipo().equals("parte de arriba")){
                foto.setImageResource(R.drawable.camiseta);
                return;
            }
            if(prenda.getTipo().equals("parte de abajo")){
                foto.setImageResource(R.drawable.pantalones);
                return;
            }
            if(prenda.getTipo().equals("accesorios")){
                foto.setImageResource(R.drawable.bufanda);
            }
        }
    }
}