package com.example.grupo1_1.smartclosset.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.grupo1_1.smartclosset.databinding.ElementoListaBinding;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorPrendasFirestoreUI extends
        FirestoreRecyclerAdapter<Prenda, AdaptadorPrendas.ViewHolder> {

    protected View.OnClickListener onClickListener;
    protected Context context;
    public AdaptadorPrendasFirestoreUI(
            @NonNull FirestoreRecyclerOptions<Prenda> options, Context context){
        super(options);
        this.context = context;
    }

    @Override
    public AdaptadorPrendas.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        ElementoListaBinding v = ElementoListaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        v.getRoot().setOnClickListener(onClickListener);
        return new AdaptadorPrendas.ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorPrendas
            .ViewHolder holder, int position, @NonNull Prenda prenda) {
        holder.personaliza(prenda);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }

    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()) {
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
    }
}
