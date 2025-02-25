package com.example.pichangape.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichangape.DetalleReservaActivity;
import com.example.pichangape.R;
import com.example.pichangape.models.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ViewHolder> {

    private Context context;
    private List<Reserva> listaReservas;      // Lista que se muestra actualmente
    private List<Reserva> listaReservasFull;  // Copia completa de la lista

    public ReservasAdapter(Context context, List<Reserva> listaReservas) {
        this.context = context;
        this.listaReservas = listaReservas;
        // Inicialmente se guarda una copia de la lista
        this.listaReservasFull = new ArrayList<>(listaReservas);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Reserva reserva = listaReservas.get(position);
        holder.tvFecha.setText("Fecha: " + reserva.getFechaInicio());
        holder.tvHoraInicio.setText("Inicio: " + reserva.getHoraInicio());
        holder.tvHoraFin.setText("Fin: " + reserva.getHoraFin());
        holder.tvEstado.setText("Estado: " + reserva.getEstadoReserva());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetalleReservaActivity.class);
                intent.putExtra("id_reserva", reserva.getIdReserva());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaReservas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHoraInicio, tvHoraFin, tvEstado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHoraInicio = itemView.findViewById(R.id.tvHoraInicio);
            tvHoraFin = itemView.findViewById(R.id.tvHoraFin);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }

    // Método para actualizar la lista completa de reservas
    public void updateList(List<Reserva> nuevasReservas) {
        listaReservas.clear();
        listaReservas.addAll(nuevasReservas);
        listaReservasFull.clear();
        listaReservasFull.addAll(nuevasReservas);
        notifyDataSetChanged();
    }

    // Función para filtrar la lista de reservas según el estado seleccionado.
    public void filterByEstado(String estado) {
        listaReservas.clear();
        if (estado.equalsIgnoreCase("Todos")) {
            listaReservas.addAll(listaReservasFull);
        } else {
            for (Reserva reserva : listaReservasFull) {
                if (reserva.getEstadoReserva().equalsIgnoreCase(estado)) {
                    listaReservas.add(reserva);
                }
            }
        }
        notifyDataSetChanged();
    }
}
