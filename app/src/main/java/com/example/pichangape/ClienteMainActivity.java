package com.example.pichangape;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
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
import com.example.pichangape.adapters.CanchaClienteAdapter;
import com.example.pichangape.adapters.ReservasAdapter;
import com.example.pichangape.models.Cancha;
import com.example.pichangape.models.Reserva;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteMainActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private MaterialButton btnCanchas, btnMisReservas;
    private RecyclerView recyclerView;
    private CanchaClienteAdapter canchaAdapter;
    private ReservasAdapter reservasAdapter;
    private List<Cancha> canchaList;
    private List<Reserva> reservasList;
    private String idCliente, nombre, apellido;
    private SearchView svFiltro;

    private String urlCanchas = ApiConfig.BASE_URL + "CMostrarTodasCanchas.php";
    private String urlReservas = ApiConfig.BASE_URL + "CListarReservasCliente.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_main);

        idCliente = getIntent().getStringExtra("id_cliente");
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");

        tvBienvenida = findViewById(R.id.tvBienvenidaCliente);
        btnCanchas = findViewById(R.id.btnVerCanchas);
        btnMisReservas = findViewById(R.id.btnMisReservas);
        recyclerView = findViewById(R.id.rvClientePrincipal);
        svFiltro = findViewById(R.id.svFiltroCliente);

        tvBienvenida.setText("¡Te damos la bienvenida, " + nombre + " " + apellido + "!");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Inicializar listas
        canchaList = new ArrayList<>();
        reservasList = new ArrayList<>();
        
        // Inicializar adaptadores
        canchaAdapter = new CanchaClienteAdapter(canchaList, idCliente, nombre, apellido);
        reservasAdapter = new ReservasAdapter(this, reservasList);

        // Por defecto mostramos canchas
        mostrarCanchas();

        btnCanchas.setOnClickListener(v -> mostrarCanchas());
        btnMisReservas.setOnClickListener(v -> mostrarMisReservas());

        svFiltro.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(recyclerView.getAdapter() instanceof CanchaClienteAdapter) {
                    canchaAdapter.getFilter().filter(query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(recyclerView.getAdapter() instanceof CanchaClienteAdapter) {
                    canchaAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    private void mostrarCanchas() {
        // UI
        btnCanchas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5252")));
        btnCanchas.setTextColor(Color.WHITE);
        btnMisReservas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#f0f0f0")));
        btnMisReservas.setTextColor(Color.parseColor("#424242"));
        svFiltro.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(canchaAdapter);
        obtenerTodasLasCanchas();
    }

    private void mostrarMisReservas() {
        // UI
        btnMisReservas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5252")));
        btnMisReservas.setTextColor(Color.WHITE);
        btnCanchas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#f0f0f0")));
        btnCanchas.setTextColor(Color.parseColor("#424242"));
        svFiltro.setVisibility(View.GONE);

        recyclerView.setAdapter(reservasAdapter);
        obtenerMisReservas();
    }

    private void obtenerTodasLasCanchas() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCanchas,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray canchasArray = jsonResponse.getJSONArray("canchas");
                        List<Cancha> nuevaLista = new ArrayList<>();
                        for (int i = 0; i < canchasArray.length(); i++) {
                            JSONObject obj = canchasArray.getJSONObject(i);
                            nuevaLista.add(new Cancha(
                                    obj.getString("id_cancha"),
                                    obj.getString("nombre"),
                                    obj.getString("direccion"),
                                    (float) obj.getDouble("precio_por_hora"),
                                    obj.optString("numYape", ""),
                                    obj.optString("numTransfer", ""),
                                    obj.optString("horasDisponibles", ""),
                                    obj.optString("fechas_abiertas", "")
                            ));
                        }
                        canchaAdapter.actualizarDatos(nuevaLista);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        );
        queue.add(stringRequest);
    }

    private void obtenerMisReservas() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando tus reservas...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, urlReservas,
                response -> {
                    progressDialog.dismiss();
                    List<Reserva> nuevasReservas = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            nuevasReservas.add(new Reserva(
                                    obj.getInt("id_reserva"),
                                    obj.getString("fecha_inicio"),
                                    obj.getString("hora_inicio"),
                                    obj.getString("hora_fin"),
                                    obj.getString("estado_reserva")
                            ));
                        }
                        reservasAdapter.updateList(nuevasReservas);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Sin reservas aún", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cliente", idCliente);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
