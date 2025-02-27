package com.example.pichangape;


import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerificarComprobanteActivity extends AppCompatActivity {

    private int idReserva;
    private PhotoView ivComprobante;
    private ProgressBar progressBar;
    private boolean isFullScreen = false;

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

        // Configurar listener para alternar modo pantalla completa al pulsar la imagen
        ivComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullScreen();
            }
        });

        // Configurar listeners para los botones
        MaterialButton btnRechazar = findViewById(R.id.btnRechazar);
        MaterialButton btnAprobar = findViewById(R.id.btnAprobar);

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoReserva("cancelado");
            }
        });

        btnAprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoReserva("pagado");
            }
        });

        fetchComprobante();
    }

    /**
     * Alterna el modo pantalla completa para la imagen.
     */
    private void toggleFullScreen() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ivComprobante.getLayoutParams();
        View buttonContainer = findViewById(R.id.buttonContainer);

        if (!isFullScreen) {
            // Expandir la imagen a pantalla completa
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ivComprobante.setLayoutParams(params);
            if (buttonContainer != null) {
                buttonContainer.setVisibility(View.GONE);
            }
            isFullScreen = true;
        } else {
            // Volver al tamaño original
            params.height = dpToPx(200);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ivComprobante.setLayoutParams(params);
            if (buttonContainer != null) {
                buttonContainer.setVisibility(View.VISIBLE);
            }
            isFullScreen = false;
        }
    }

    /**
     * Convierte dp a píxeles.
     */
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Obtiene y muestra el comprobante.
     */
    private void fetchComprobante() {
        // URL de la API para obtener el voucher
        String urlComprobante = "https://739c9dc3-0789-44cf-b9b3-0a433b602be3-00-g7yu9uuhed8k.worf.replit.dev/obtener_voucher.php";

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
                            // Se espera que la API retorne la URL de la imagen en "image_url"
                            String imageUrl = jsonObject.getString("image_url");
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

    /**
     * Envía el nuevo estado a la API para actualizar la reserva.
     * Si la actualización es exitosa, se redirige a la pantalla de BienvenidaActivity.
     * @param nuevoEstado "cancelado" o "pagado"
     */
    private void actualizarEstadoReserva(final String nuevoEstado) {
        // URL de la API para actualizar el estado
        String urlActualizarEstado = "https://739c9dc3-0789-44cf-b9b3-0a433b602be3-00-g7yu9uuhed8k.worf.replit.dev/actualizar_estado_reserva.php";

        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, urlActualizarEstado,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("error")) {
                                Toast.makeText(VerificarComprobanteActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                            } else if (jsonObject.has("success")) {
                                Toast.makeText(VerificarComprobanteActivity.this, jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                                // Deshabilitar botones para evitar cambios posteriores
                                findViewById(R.id.btnRechazar).setEnabled(false);
                                findViewById(R.id.btnAprobar).setEnabled(false);

                                // Se espera que la respuesta incluya id_cliente, y opcionalmente nombre y apellido
                                String id_cliente = jsonObject.optString("id_cliente", "");
                                String nombre = jsonObject.optString("nombre", "");
                                String apellido = jsonObject.optString("apellido", "");
                                if (id_cliente.isEmpty()) {
                                    Toast.makeText(VerificarComprobanteActivity.this, "No se obtuvo id_cliente", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                // Navegar a la pantalla de BienvenidaActivity
                                Intent intent = new Intent(VerificarComprobanteActivity.this, BienvenidaActivity.class);
                                intent.putExtra("id_cliente", id_cliente);
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("apellido", apellido);
                                startActivity(intent);
                                finish(); // Cerrar esta actividad
                            }
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
                // Enviar los parámetros id_reserva y estado a la API
                Map<String, String> params = new HashMap<>();
                params.put("id_reserva", String.valueOf(idReserva));
                params.put("estado", nuevoEstado);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
