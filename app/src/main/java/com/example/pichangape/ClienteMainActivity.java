package com.example.pichangape;

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
import com.example.pichangape.models.Cancha;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClienteMainActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private MaterialButton btnCanchas, btnMisReservas;
    private RecyclerView recyclerView;
    private CanchaClienteAdapter adapter;
    private List<Cancha> canchaList;
    private String idCliente, nombre, apellido;
    private SearchView svFiltro;

    // URL para obtener todas las canchas (reutilizamos la de mostrar canchas pero el PHP debería permitir traer todas)
    // O crearemos una nueva si es necesario. Por ahora usamos la base.
    private String urlCanchas = ApiConfig.BASE_URL + "CMostrarTodasCanchas.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_main);

        // Recuperar datos del intent
        idCliente = getIntent().getStringExtra("id_cliente");
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");

        // Inicializar vistas
        tvBienvenida = findViewById(R.id.tvBienvenidaCliente);
        btnCanchas = findViewById(R.id.btnVerCanchas);
        btnMisReservas = findViewById(R.id.btnMisReservas);
        recyclerView = findViewById(R.id.rvClientePrincipal);
        svFiltro = findViewById(R.id.svFiltroCliente);

        tvBienvenida.setText("¡Te damos la bienvenida, " + nombre + " " + apellido + "!");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        canchaList = new ArrayList<>();
        adapter = new CanchaClienteAdapter(canchaList);
        recyclerView.setAdapter(adapter);

        // Configurar botones de navegación
        btnCanchas.setOnClickListener(v -> mostrarCanchas());
        btnMisReservas.setOnClickListener(v -> mostrarMisReservas());

        // Configurar buscador
        svFiltro.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Cargar canchas por defecto
        obtenerTodasLasCanchas();
    }

    private void mostrarCanchas() {
        btnCanchas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5252")));
        btnCanchas.setTextColor(Color.WHITE);
        btnMisReservas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#f0f0f0")));
        btnMisReservas.setTextColor(Color.parseColor("#424242"));
        
        obtenerTodasLasCanchas();
    }

    private void mostrarMisReservas() {
        btnMisReservas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5252")));
        btnMisReservas.setTextColor(Color.WHITE);
        btnCanchas.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#f0f0f0")));
        btnCanchas.setTextColor(Color.parseColor("#424242"));
        
        Toast.makeText(this, "Próximamente: Lista de tus reservas", Toast.LENGTH_SHORT).show();
    }

    private void obtenerTodasLasCanchas() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlCanchas,
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
                                    (float) obj.getDouble("precio_por_hora")
                            ));
                        }
                        adapter.actualizarDatos(nuevaLista);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ClienteMainActivity.this, "Error al procesar canchas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ClienteMainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );
        queue.add(stringRequest);
    }
}
