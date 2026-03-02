package com.example.pichangape.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pichangape.ApiConfig;
import com.example.pichangape.BienvenidaActivity;
import com.example.pichangape.R;
import com.example.pichangape.RegistrarCanchasActivity;
import com.example.pichangape.adapters.CanchaAdapter;
import com.example.pichangape.models.Cancha;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ingreso extends AppCompatActivity {

    private TextView tvBienvenida;
    private RecyclerView recyclerView;
    private CanchaAdapter canchaAdapter;
    private List<Cancha> canchaList;
    private String idCliente;
    private String nombre;
    private String apellido;
    // URL centralizada con ApiConfig
    private String url = ApiConfig.BASE_URL + "CMostrarCancha.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingreso);

        // Obtener datos del intent al principio
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        idCliente = getIntent().getStringExtra("id_cliente");

        if (idCliente == null || idCliente.isEmpty()) {
            Toast.makeText(this, "ID de cliente no recibido", Toast.LENGTH_SHORT).show();
            // No terminamos la actividad para permitir depuración, pero podrías poner finish()
        }

        // Botón para ir a la actividad de ingresos
        Button btnIngresos = findViewById(R.id.btnIngresos);
        btnIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irVentanaIngresos();
            }
        });

        // Botón para registrar nuevas canchas
        ImageButton btnAgregarCancha = findViewById(R.id.btnAgregarCancha);
        btnAgregarCancha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irRegistrarCanchas();
            }
        });

        // Inicializar vistas
        tvBienvenida = findViewById(R.id.tvBienvenida);
        recyclerView = findViewById(R.id.tblMostrarCanchas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mostrar saludo
        if (nombre == null || nombre.equals("null") || nombre.isEmpty() ||
                apellido == null || apellido.equals("null") || apellido.isEmpty()) {
            tvBienvenida.setVisibility(View.GONE);
        } else {
            tvBienvenida.setText("¡Bienvenido, " + nombre + " " + apellido + "!");
        }

        // Inicializar lista y adaptador (PASANDO LOS DATOS DEL DUEÑO AL ADAPTADOR)
        canchaList = new ArrayList<>();
        canchaAdapter = new CanchaAdapter(canchaList, idCliente, nombre, apellido);
        recyclerView.setAdapter(canchaAdapter);

        // Inicializar SearchView
        SearchView svFiltro = findViewById(R.id.svFiltro);
        svFiltro.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                canchaAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                canchaAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Configurar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Llamar a la función para obtener canchas
        if (idCliente != null) {
            obtenerCanchas(idCliente);
        }
    }

    private void obtenerCanchas(String idDueno) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray canchasArray = jsonResponse.getJSONArray("canchas");
                            List<Cancha> nuevaLista = new ArrayList<>();
                            for (int i = 0; i < canchasArray.length(); i++) {
                                JSONObject canchaObj = canchasArray.getJSONObject(i);
                                Cancha cancha = new Cancha(
                                        canchaObj.getString("id_cancha"),
                                        canchaObj.getString("nombre"),
                                        canchaObj.getString("direccion"),
                                        (float) canchaObj.optDouble("precio_por_hora", 0.0)
                                );
                                nuevaLista.add(cancha);
                            }
                            canchaAdapter.actualizarDatos(nuevaLista);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Ingreso.this, "Error procesando JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Ingreso.this, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_dueno", idDueno);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void irVentanaIngresos() {
        Intent intent = new Intent(Ingreso.this, BienvenidaActivity.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("apellido", apellido);
        intent.putExtra("id_cliente", idCliente);
        startActivity(intent);
    }

    public void irRegistrarCanchas() {
        Intent intent = new Intent(Ingreso.this, RegistrarCanchasActivity.class);
        intent.putExtra("id_cliente", idCliente);
        intent.putExtra("nombre", nombre);
        intent.putExtra("apellido", apellido);
        startActivity(intent);
    }
}
