package com.example.pichangape.adapters;

/**
 * EstadisticasAdapter es un adaptador personalizado para RecyclerView, encargado de mostrar
 * una lista de estadísticas de canchas en tarjetas dentro de la aplicación.
 *
 * Cada tarjeta contiene:
 * - Nombre de la cancha
 * - Ganancias obtenidas
 * - Total de reservas
 * - Total de reservas pagadas
 *
 * Este adaptador usa el layout "card_estadisticas.xml" para definir la apariencia de cada elemento.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pichangape.R;
import com.example.pichangape.models.CanchaEstadistica;
import com.example.pichangape.ReservacionesActivity; // Asegúrate de importar la actividad
import java.util.List;

public class EstadisticasAdapter extends RecyclerView.Adapter<EstadisticasAdapter.ViewHolder> {

    private Context context; // Contexto de la aplicación
    private List<CanchaEstadistica> estadisticasList; // Lista de estadísticas de canchas

    /**
     * Constructor que recibe el contexto y la lista de estadísticas de canchas
     * @param context Contexto de la aplicación
     * @param estadisticasList Lista de objetos CanchaEstadistica
     */
    public EstadisticasAdapter(Context context, List<CanchaEstadistica> estadisticasList) {
        this.context = context;
        this.estadisticasList = estadisticasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de la tarjeta (card_estadisticas.xml) para cada elemento de la lista
        View view = LayoutInflater.from(context).inflate(R.layout.card_estadisticas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtiene el objeto CanchaEstadistica de la lista en la posición actual
        CanchaEstadistica estadistica = estadisticasList.get(position);

        // Asigna los datos a los elementos de la tarjeta
        holder.tvCanchaNombre.setText(estadistica.getNombre());
        holder.tvGanancias.setText("Ganancias: $" + String.format("%.2f", estadistica.getGanancias()));
        holder.tvTotalReservas.setText("Total Reservas: " + estadistica.getTotalReservas());
        holder.tvTotalReservasPagadas.setText("Reservas Pagadas: " + estadistica.getTotalReservasPagadas());

        // Agregar listener para detectar el toque en la tarjeta
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir la actividad de reservaciones
                Intent intent = new Intent(context, ReservacionesActivity.class);
                // Enviar el id_cancha para filtrar las reservaciones en la nueva actividad
                intent.putExtra("id_cancha", estadistica.getIdCancha());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return estadisticasList.size(); // Retorna el número total de elementos en la lista
    }

    /**
     * ViewHolder es una clase interna que mantiene las referencias a las vistas
     * dentro de cada tarjeta para mejorar el rendimiento del RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCanchaNombre, tvGanancias, tvTotalReservas, tvTotalReservasPagadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referencias a las vistas de la tarjeta
            tvCanchaNombre = itemView.findViewById(R.id.tvCanchaNombre);
            tvGanancias = itemView.findViewById(R.id.tvGanancias);
            tvTotalReservas = itemView.findViewById(R.id.tvTotalReservas);
            tvTotalReservasPagadas = itemView.findViewById(R.id.tvTotalReservasPagadas);
        }
    }
}
