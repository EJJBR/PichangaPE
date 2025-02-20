package com.example.pichangape;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
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
import com.example.pichangape.models.CanchaEstadistica;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BienvenidaActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private RecyclerView rvEstadisticas;
    private List<CanchaEstadistica> listaEstadisticas;
    private EstadisticasAdapter adapter;
    private String id_cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        tvBienvenida = findViewById(R.id.tvBienvenida);
        rvEstadisticas = findViewById(R.id.rvEstadisticas);

        // Recuperar los extras enviados desde la actividad de login
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        id_cliente = getIntent().getStringExtra("id_cliente");

        // Verificar que se haya recibido id_cliente
        if(id_cliente == null || id_cliente.isEmpty()){
            Toast.makeText(this, "ID de cliente no recibido", Toast.LENGTH_SHORT).show();
            // Puedes optar por finalizar la actividad o asignar un valor por defecto para pruebas
            // finish();
            return;
        }

        // Actualizar el TextView con el mensaje de bienvenida
        tvBienvenida.setText("¡Te damos la bienvenida, " + nombre + " " + apellido + "!");

        // Configurar el RecyclerView
        rvEstadisticas.setLayoutManager(new LinearLayoutManager(this));
        listaEstadisticas = new ArrayList<>();
        adapter = new EstadisticasAdapter(this, listaEstadisticas);
        rvEstadisticas.setAdapter(adapter);

        // Llamar a la API para obtener las estadísticas de las canchas
        fetchEstadisticas();
    }

    private void fetchEstadisticas() {
        // URL de la API que retorna las estadísticas filtradas por id_cliente
        String urlEstadisticas = "https://1fe8107b-4bc6-4865-9bbd-dbd93570a5ba-00-z75lvfccgfim.worf.replit.dev/estadisticas_Canchas.php";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando estadísticas...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, urlEstadisticas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            // Se asume que la respuesta es un arreglo JSON
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String idCancha = obj.getString("id_cancha");
                                String nombreCancha = obj.getString("nombre");
                                double ganancias = obj.getDouble("ganancias");
                                int totalReservas = obj.getInt("total_reservas");
                                int totalReservasPagadas = obj.getInt("total_reservas_pagadas");

                                CanchaEstadistica estadistica = new CanchaEstadistica(idCancha, nombreCancha, ganancias, totalReservas, totalReservasPagadas);
                                listaEstadisticas.add(estadistica);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BienvenidaActivity.this, "Error al procesar los datos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(BienvenidaActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Enviar el id_cliente para filtrar las canchas del usuario
                Map<String, String> params = new HashMap<>();
                params.put("id_cliente", id_cliente);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
