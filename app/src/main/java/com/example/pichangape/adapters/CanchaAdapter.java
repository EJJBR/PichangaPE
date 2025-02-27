package com.example.pichangape.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichangape.R;
import com.example.pichangape.models.Cancha;

import java.util.ArrayList;
import java.util.List;

public class CanchaAdapter extends RecyclerView.Adapter<CanchaAdapter.ViewHolder> implements Filterable {

    private List<Cancha> canchaList;          // Lista que se muestra
    private List<Cancha> canchaListFull;      // Copia completa para filtrar

    public CanchaAdapter(List<Cancha> canchaList) {
        this.canchaList = new ArrayList<>(canchaList);
        // Crear una copia de la lista original para filtrar
        this.canchaListFull = new ArrayList<>(canchaList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancha_mostrada, parent, false);
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

    @Override
    public Filter getFilter() {
        return canchaFilter;
    }

    private Filter canchaFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Cancha> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(canchaListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Cancha cancha : canchaListFull) {
                    // Filtrar por nombre o ubicación
                    if (cancha.getNombre().toLowerCase().contains(filterPattern) ||
                            cancha.getUbicacion().toLowerCase().contains(filterPattern)) {
                        filteredList.add(cancha);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            canchaList.clear();
            canchaList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Método para actualizar ambas listas cuando se obtienen nuevos datos
    public void actualizarDatos(List<Cancha> nuevaLista) {
        canchaList.clear();
        canchaList.addAll(nuevaLista);
        canchaListFull.clear();
        canchaListFull.addAll(nuevaLista);
        notifyDataSetChanged();
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
