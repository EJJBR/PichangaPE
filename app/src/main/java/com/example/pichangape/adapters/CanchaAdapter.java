package com.example.pichangape.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichangape.R;
import com.example.pichangape.models.Cancha;

import java.util.List;

public class CanchaAdapter extends RecyclerView.Adapter<CanchaAdapter.ViewHolder> {
    private List<Cancha> canchaList;

    public CanchaAdapter(List<Cancha> canchaList) {
        this.canchaList = canchaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cancha_mostrada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cancha cancha = canchaList.get(position);
        holder.tvNombre.setText(cancha.getNombre());
        holder.tvUbicacion.setText(cancha.getUbicacion());
        holder.tvPrecioHora.setText(String.valueOf(cancha.getPrecioHora()));
    }

    @Override
    public int getItemCount() {
        return canchaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion, tvPrecioHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvPrecioHora = itemView.findViewById(R.id.precioHoraCancha);
        }
    }
}
