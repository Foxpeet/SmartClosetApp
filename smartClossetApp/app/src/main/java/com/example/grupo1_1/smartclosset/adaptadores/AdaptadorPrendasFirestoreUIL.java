package com.example.grupo1_1.smartclosset.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.grupo1_1.smartclosset.databinding.ElementoListaLandingBinding;
import com.example.grupo1_1.smartclosset.pojos.Prenda;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorPrendasFirestoreUIL extends
        FirestoreRecyclerAdapter<Prenda, AdaptadorPrendasLanding.ViewHolder> {

    protected View.OnClickListener onClickListener;
    protected Context context;
    public AdaptadorPrendasFirestoreUIL(
            @NonNull FirestoreRecyclerOptions<Prenda> options, Context context){
        super(options);
        this.context = context;
    }

    @Override
    public AdaptadorPrendasLanding.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        ElementoListaLandingBinding v = ElementoListaLandingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        v.getRoot().setOnClickListener(onClickListener);
        return new AdaptadorPrendasLanding.ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorPrendasLanding
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
