package com.example.pichangape.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pichangape.R;
import com.example.pichangape.models.Reserva;
import java.util.List;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ViewHolder> {

    private Context context;
    private List<Reserva> listaReservas;

    public ReservasAdapter(Context context, List<Reserva> listaReservas) {
        this.context = context;
        this.listaReservas = listaReservas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de la tarjeta (por ejemplo, card_reserva.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.card_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Reserva reserva = listaReservas.get(position);
        // Asigna los datos de la reserva a los TextViews del layout
        holder.tvFecha.setText("Fecha: " + reserva.getFechaInicio());
        holder.tvHoraInicio.setText("Inicio: " + reserva.getHoraInicio());
        holder.tvHoraFin.setText("Fin: " + reserva.getHoraFin());
        holder.tvEstado.setText("Estado: " + reserva.getEstadoReserva());

        // Listener para detectar el clic en la tarjeta
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ejemplo: Mostrar un Toast con el id de la reserva
                Toast.makeText(context, "Reserva ID: " + reserva.getIdReserva(), Toast.LENGTH_SHORT).show();

                // Aquí puedes iniciar una actividad o llamar a la API
                // pasando reserva.getIdReserva() para obtener los detalles de esa reserva en particular.
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
}
