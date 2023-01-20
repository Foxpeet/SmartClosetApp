package com.example.grupo1_1.smartclosset.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.grupo1_1.smartclosset.databinding.ElementoListaConjuntoBinding;
import com.example.grupo1_1.smartclosset.pojos.Conjunto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorConjuntosFirestoreUI extends
        FirestoreRecyclerAdapter<Conjunto, AdaptadorConjuntos.ViewHolder> {

    protected View.OnClickListener onClickListener;
    protected Context context;
    public AdaptadorConjuntosFirestoreUI(
            @NonNull FirestoreRecyclerOptions<Conjunto> options, Context context){
        super(options);
        this.context = context;
    }

    @Override
    public AdaptadorConjuntos.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        ElementoListaConjuntoBinding v = ElementoListaConjuntoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        v.getRoot().setOnClickListener(onClickListener);
        return new AdaptadorConjuntos.ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorConjuntos.ViewHolder holder, int position, @NonNull Conjunto conjunto) {
        holder.personaliza(conjunto);
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
