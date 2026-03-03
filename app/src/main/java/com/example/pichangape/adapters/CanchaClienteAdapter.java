package com.example.pichangape.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichangape.HacerReservaActivity;
import com.example.pichangape.R;
import com.example.pichangape.models.Cancha;

import java.util.ArrayList;
import java.util.List;

public class CanchaClienteAdapter extends RecyclerView.Adapter<CanchaClienteAdapter.ViewHolder> implements Filterable {

    private List<Cancha> canchaList;
    private List<Cancha> canchaListFull;
    private String idCliente, nombre, apellido; // Datos del cliente actual

    public CanchaClienteAdapter(List<Cancha> canchaList, String idCliente, String nombre, String apellido) {
        this.canchaList = new ArrayList<>(canchaList);
        this.canchaListFull = new ArrayList<>(canchaList);
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancha_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cancha cancha = canchaList.get(position);
        holder.tvNombre.setText(cancha.getNombre());
        holder.tvUbicacion.setText(cancha.getUbicacion());
        holder.tvPrecio.setText("$" + cancha.getPrecioHora() + " / hora");

        holder.btnReservar.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, HacerReservaActivity.class);
            
            // Pasar datos de la cancha
            intent.putExtra("id_cancha", cancha.getIdCancha());
            intent.putExtra("nombre_cancha", cancha.getNombre());
            intent.putExtra("direccion", cancha.getUbicacion());
            intent.putExtra("precio", cancha.getPrecioHora());
            
            // PASAR LOS NUEVOS DATOS DE PAGO Y DISPONIBILIDAD
            intent.putExtra("numYape", cancha.getNumYape());
            intent.putExtra("numTransfer", cancha.getNumTransfer());
            intent.putExtra("horasDisponibles", cancha.getHorasDisponibles());
            intent.putExtra("fechas_abiertas", cancha.getFechasAbiertas());
            
            // Pasar datos del cliente actual para la reserva
            intent.putExtra("id_cliente_reservador", idCliente);
            intent.putExtra("nombre_cliente", nombre);
            intent.putExtra("apellido_cliente", apellido);
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return canchaList.size();
    }

    public void actualizarDatos(List<Cancha> nuevaLista) {
        canchaList.clear();
        canchaList.addAll(nuevaLista);
        canchaListFull.clear();
        canchaListFull.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Cancha> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(canchaListFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (Cancha item : canchaListFull) {
                    if (item.getNombre().toLowerCase().contains(pattern) ||
                        item.getUbicacion().toLowerCase().contains(pattern)) {
                        filteredList.add(item);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion, tvPrecio;
        ImageButton btnReservar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCanchaCliente);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacionCanchaCliente);
            tvPrecio = itemView.findViewById(R.id.tvPrecioCanchaCliente);
            btnReservar = itemView.findViewById(R.id.btnReservarCancha);
        }
    }
}
