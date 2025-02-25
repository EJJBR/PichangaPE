package com.example.pichangape;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pichangape.adapters.EstadisticasAdapter;
import com.example.pichangape.adapters.ReservasAdapter;
import com.example.pichangape.models.Reserva;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Reserva> listaReservas;
    private ReservasAdapter reservasAdapter;
    private String id_cancha;
    private Spinner spinnerEstado; // Spinner para elegir el estado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos la lista y el adaptador
        listaReservas = new ArrayList<>();
        reservasAdapter = new ReservasAdapter(this, listaReservas);
        recyclerView.setAdapter(reservasAdapter);

        // Recuperar el id_cancha enviado desde la actividad anterior
        id_cancha = getIntent().getStringExtra("id_cancha");
        if (id_cancha == null || id_cancha.isEmpty()) {
            Toast.makeText(this, "ID de cancha no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar y configurar el Spinner para filtrar las reservas por estado
        spinnerEstado = findViewById(R.id.spinnerEstado);

        // Opciones del Spinner (puedes agregar "Todos" para restaurar la lista completa)
        String[] estados = {"Todos", "Pendiente", "Alquilada", "Cancelado"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstados);

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String estadoSeleccionado = parent.getItemAtPosition(position).toString();
                reservasAdapter.filterByEstado(estadoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Opcional: No hacer nada
            }
        });

        // Llamada para obtener las reservas desde la API
        fetchReservas();
    }

    private void fetchReservas() {
        String urlReservas = "https://0fc85979-d67a-4869-aace-ff2b7e7fd9b4-00-csq92nfutubh.worf.replit.dev/reservaciones.php";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando reservaciones...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, urlReservas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        List<Reserva> reservasCargadas = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject reservaObj = jsonArray.getJSONObject(i);
                                int idReserva = reservaObj.getInt("id_reserva");
                                String fechaInicio = reservaObj.getString("fecha_inicio");
                                String horaInicio = reservaObj.getString("hora_inicio");
                                String horaFin = reservaObj.getString("hora_fin");
                                String estadoReserva = reservaObj.getString("estado_reserva");

                                Reserva reserva = new Reserva(idReserva, fechaInicio, horaInicio, horaFin, estadoReserva);
                                reservasCargadas.add(reserva);
                            }
                            // Actualizamos el adaptador con la lista completa de reservas
                            reservasAdapter.updateList(reservasCargadas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReservacionesActivity.this, "Error al procesar datos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReservacionesActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cancha", id_cancha);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}
