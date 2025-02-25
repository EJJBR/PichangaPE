package com.example.pichangape.ui.theme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pichangape.BienvenidaActivity;
import com.example.pichangape.R;

import java.util.HashMap;
import java.util.Map;

public class RegistrarCanchasActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarCanchasActivity";
    private EditText lblNombreCancha, lblArea, lblDireccion, lblHorasDisponibles,
            lblFechasDisponibles, lblCostoPorHora;
    private Spinner spinnerCategoria;
    private Button btnRegresar, btnRegistrar;
    private String categoriaSeleccionada;
    private RequestQueue requestQueue;

    // URL del script PHP en tu servidor
    private static final String URL_REGISTRAR_CANCHA = "https://ed53badf-245e-4cc2-80f8-850ed4314fb2-00-3rcks3ldjl2v6.kirk.replit.dev/agregar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_canchas);

        // Inicializar RequestQueue de Volley
        requestQueue = Volley.newRequestQueue(this);

        // Inicializar vistas
        inicializarVistas();

        // Configurar el Spinner de categorías
        configurarSpinnerCategorias();

        // Configurar los botones
        configurarBotones();
    }

    private void inicializarVistas() {
        lblNombreCancha = findViewById(R.id.lblNombreCancha);
        lblArea = findViewById(R.id.lblArea);
        lblDireccion = findViewById(R.id.lblDireccion);
        lblHorasDisponibles = findViewById(R.id.lblhorasdisponibles);
        lblFechasDisponibles = findViewById(R.id.lblfechasdisponibles);
        lblCostoPorHora = findViewById(R.id.lblCostoporhora);
        spinnerCategoria = findViewById(R.id.spinner_categorias);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
    }

    private void configurarSpinnerCategorias() {
        // Adaptador para llenar el Spinner con los valores del array de strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        // Manejar la selección de un ítem
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void configurarBotones() {
        // Configurar botón Regresar
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarCanchasActivity.this, BienvenidaActivity.class);
            startActivity(intent);
            finish();
        });

        // Configurar botón Registrar
        btnRegistrar.setOnClickListener(v -> {
            if (validarCampos()) {
                registrarCancha();
            }
        });
    }

    private boolean validarCampos() {
        if (lblNombreCancha.getText().toString().trim().isEmpty() ||
                lblDireccion.getText().toString().trim().isEmpty() ||
                lblHorasDisponibles.getText().toString().trim().isEmpty() ||
                lblFechasDisponibles.getText().toString().trim().isEmpty() ||
                lblCostoPorHora.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar que el costo por hora sea un número válido
        try {
            Double.parseDouble(lblCostoPorHora.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El costo por hora debe ser un número válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registrarCancha() {
        // Mostrar un mensaje de carga
        Toast.makeText(this, "Registrando cancha...", Toast.LENGTH_SHORT).show();

        // Crear una solicitud de cadena para enviar al servidor PHP
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRAR_CANCHA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Respuesta del servidor: " + response);

                        if (response.trim().equals("success")) {
                            Toast.makeText(RegistrarCanchasActivity.this,
                                    "Cancha registrada exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                        } else {
                            Toast.makeText(RegistrarCanchasActivity.this,
                                    "Error al registrar la cancha: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error de Volley: " + error.toString());
                        Toast.makeText(RegistrarCanchasActivity.this,
                                "Error de conexión. Intente nuevamente.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Añadir parámetros a la solicitud
                params.put("id_dueno", "2"); // ID fijo para este ejemplo
                params.put("nombre", lblNombreCancha.getText().toString().trim());
                params.put("direccion", lblDireccion.getText().toString().trim());
                params.put("precio_por_hora", lblCostoPorHora.getText().toString().trim());
                params.put("tipoCancha", categoriaSeleccionada.toLowerCase());
                params.put("horasDisponibles", lblHorasDisponibles.getText().toString().trim());
                params.put("fechas_abiertas", lblFechasDisponibles.getText().toString().trim());
                params.put("estado", "activa");

                return params;
            }
        };

        // Añadir la solicitud a la cola
        requestQueue.add(stringRequest);
    }

    private void limpiarCampos() {
        lblNombreCancha.setText("");
        lblArea.setText("");
        lblDireccion.setText("");
        lblHorasDisponibles.setText("");
        lblFechasDisponibles.setText("");
        lblCostoPorHora.setText("");
        spinnerCategoria.setSelection(0);
    }
}