package com.example.pichangape;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleReservaActivity extends AppCompatActivity {

    private int idReserva;
    private TextView tvFecha, tvHoraInicio, tvHoraFin, tvNombre, tvApellido, tvCelular, tvEstado;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reserva);

        // Referenciar los elementos del layout
        tvFecha = findViewById(R.id.tvFecha);
        tvHoraInicio = findViewById(R.id.tvHoraInicio);
        tvHoraFin = findViewById(R.id.tvHoraFin);
        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);
        tvCelular = findViewById(R.id.tvCelular);
        tvEstado = findViewById(R.id.tvEstado);
        progressBar = findViewById(R.id.progressBar);

        // Recuperar el id_reserva enviado desde el adaptador
        idReserva = getIntent().getIntExtra("id_reserva", -1);
        if (idReserva == -1) {
            Toast.makeText(this, "No se recibió el ID de la reserva", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Llamar al método para obtener los detalles de la reserva
        fetchDetalleReserva();
    }

    private void fetchDetalleReserva() {
        // URL de la API que devuelve los detalles de la reserva
        String urlDetalle = "https://1fe8107b-4bc6-4865-9bbd-dbd93570a5ba-00-z75lvfccgfim.worf.replit.dev/reservaciones_clientes.php"; // Reemplaza con la URL real

        // Mostrar el ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, urlDetalle,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Ocultar el ProgressBar
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // Si la API retorna un error, se muestra un mensaje
                            if (jsonObject.has("error")) {
                                Toast.makeText(DetalleReservaActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Obtener los datos de la respuesta JSON
                            String fecha = jsonObject.getString("fecha");
                            String horaInicio = jsonObject.getString("hora_inicio");
                            String horaFin = jsonObject.getString("hora_fin");
                            String nombreReservador = jsonObject.getString("nombre_reservador");
                            String apellidoReservador = jsonObject.getString("apellido_reservador");
                            String celular = jsonObject.getString("celular");
                            String estadoReserva = jsonObject.getString("estado_reserva");

                            // Asignar los datos a los TextViews
                            tvFecha.setText("Fecha: " + fecha);
                            tvHoraInicio.setText("Inicio: " + horaInicio);
                            tvHoraFin.setText("Fin: " + horaFin);
                            tvNombre.setText("Nombre: " + nombreReservador);
                            tvApellido.setText("Apellido: " + apellidoReservador);
                            tvCelular.setText("Celular: " + celular);
                            tvEstado.setText("Estado: " + estadoReserva);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetalleReservaActivity.this, "Error al procesar los datos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(DetalleReservaActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Enviar el id_reserva a la API
                Map<String, String> params = new HashMap<>();
                params.put("id_reserva", String.valueOf(idReserva));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
