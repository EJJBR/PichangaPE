package com.example.pichangape.view;

import android.os.Bundle;
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
import com.example.pichangape.R;
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
    private String url = "https://1ef4fe96-f665-43f1-b822-9a6a386ace94-00-eod5c4wo3wtn.kirk.replit.dev/CMostrarCancha.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingreso);

        // Inicializar vistas
        tvBienvenida = findViewById(R.id.tvBienvenida);
        recyclerView = findViewById(R.id.tblMostrarCanchas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista y adaptador
        canchaList = new ArrayList<>();
        canchaAdapter = new CanchaAdapter(canchaList);
        recyclerView.setAdapter(canchaAdapter);

        // Obtener datos del intent
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        idCliente = getIntent().getStringExtra("id_cliente");

        if (idCliente == null || idCliente.isEmpty()) {
            Toast.makeText(this, "ID de cliente no recibido", Toast.LENGTH_SHORT).show();
            return;
        }

        tvBienvenida.setText("¡Bienvenido, " + nombre + " " + apellido + "!");

        // Configurar insets para evitar superposición con la barra del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Llamar a la función para obtener canchas
        obtenerCanchas(idCliente);
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
                            canchaList.clear(); // Limpiar la lista antes de agregar nuevas canchas

                            for (int i = 0; i < canchasArray.length(); i++) {
                                JSONObject canchaObj = canchasArray.getJSONObject(i);
                                Cancha cancha = new Cancha(
                                        canchaObj.getString("id_cancha"),
                                        canchaObj.getString("nombre"),
                                        canchaObj.getString("direccion"),
                                        (float) canchaObj.optDouble("precio_por_hora", 0.0)
                                );
                                canchaList.add(cancha);
                            }

                            // Notificar al adaptador que los datos han cambiado
                            canchaAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Ingreso.this, "Error procesando JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
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
}