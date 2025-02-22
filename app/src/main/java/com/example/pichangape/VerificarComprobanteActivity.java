package com.example.pichangape;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerificarComprobanteActivity extends AppCompatActivity {

    private int idReserva;
    private ImageView ivComprobante;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_comprobante);

        ivComprobante = findViewById(R.id.ivComprobante);
        progressBar = findViewById(R.id.progressBar);

        // Obtener el idReserva del Intent
        idReserva = getIntent().getIntExtra("id_reserva", -1);
        if (idReserva == -1) {
            Toast.makeText(this, "No se recibió el ID de la reserva", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchComprobante();
    }

    private void fetchComprobante() {
        // URL del API (asegúrate de reemplazarlo por la URL real de tu servidor)
        String urlComprobante = "https://1fe8107b-4bc6-4865-9bbd-dbd93570a5ba-00-z75lvfccgfim.worf.replit.dev/obtener_voucher.php";

        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, urlComprobante,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("error")) {
                                Toast.makeText(VerificarComprobanteActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                                return;
                            }
                            // Se asume que la API retorna la URL de la imagen en la propiedad "image_url"
                            String imageUrl = jsonObject.getString("image_url");
                            // Usar Glide para cargar la imagen en el ImageView
                            Glide.with(VerificarComprobanteActivity.this)
                                    .load(imageUrl)
                                    .into(ivComprobante);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VerificarComprobanteActivity.this, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(VerificarComprobanteActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Enviar el idReserva a la API
                Map<String, String> params = new HashMap<>();
                params.put("id_reserva", String.valueOf(idReserva));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
