package com.example.pichangape;

import android.app.ProgressDialog;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);

        recyclerView = findViewById(R.id.recyclerReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        fetchReservas();
    }

    private void fetchReservas() {
        // URL de la API que devuelve las reservaciones para la cancha
        String urlReservas = "https://1fe8107b-4bc6-4865-9bbd-dbd93570a5ba-00-z75lvfccgfim.worf.replit.dev/reservaciones.php";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando reservaciones...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, urlReservas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            // Se asume que la respuesta es un arreglo JSON
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject reservaObj = jsonArray.getJSONObject(i);

                                // Obtener el id_reserva y demás datos
                                int idReserva = reservaObj.getInt("id_reserva");
                                String fechaInicio = reservaObj.getString("fecha_inicio");
                                String horaInicio = reservaObj.getString("hora_inicio");
                                String horaFin = reservaObj.getString("hora_fin");
                                String estadoReserva = reservaObj.getString("estado_reserva");

                                // Crear objeto Reserva usando el nuevo constructor
                                Reserva reserva = new Reserva(idReserva, fechaInicio, horaInicio, horaFin, estadoReserva);
                                listaReservas.add(reserva);
                            }
                            reservasAdapter.notifyDataSetChanged();
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
                // Enviar el id_cancha a la API
                Map<String, String> params = new HashMap<>();
                params.put("id_cancha", id_cancha);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
