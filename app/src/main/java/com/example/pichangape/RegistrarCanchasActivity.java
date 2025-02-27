package com.example.pichangape;

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
import com.example.pichangape.database.login;

import java.util.HashMap;
import java.util.Map;

public class RegistrarCanchasActivity extends AppCompatActivity {

    String nombre;
    String apellido;
    private static final String TAG = "RegistrarCanchasActivity";
    private EditText lblNombreCancha, lblArea, lblDireccion, lblHorasDisponibles,
            lblFechasDisponibles, lblCostoPorHora;
    private Spinner spinnerCategoria;
    private Button btnRegresar, btnRegistrar;
    private String categoriaSeleccionada;
    private RequestQueue requestQueue;
    private String idDueno; // Variable para almacenar el ID del dueño

    // URL del script PHP en tu servidor
    private static final String URL_REGISTRAR_CANCHA = "https://1ef4fe96-f665-43f1-b822-9a6a386ace94-00-eod5c4wo3wtn.kirk.replit.dev/agregar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_canchas);

        // Inicializar RequestQueue de Volley
        requestQueue = Volley.newRequestQueue(this);

        // Obtener el ID del dueño del Intent
        obtenerIdDueno();

        // Inicializar vistas
        inicializarVistas();

        // Configurar el Spinner de categorías
        configurarSpinnerCategorias();

        // Configurar los botones
        configurarBotones();
        //Cargando los datos de nombre y apeliidop
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        //Probando si llego
        Toast.makeText(this, "Bienvenido: " + nombre+" "+apellido, Toast.LENGTH_LONG).show();
    }

    private void obtenerIdDueno() {
        // Obtener el ID del dueño que se pasó desde la actividad BienvenidaActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_cliente")) {
            idDueno = intent.getStringExtra("id_cliente");
            Log.d(TAG, "ID del dueño obtenido: " + idDueno);
        } else {
            Log.e(TAG, "No se encontró el ID del dueño en el Intent");
            Toast.makeText(this, "Error: No se pudo obtener la información del usuario", Toast.LENGTH_LONG).show();
            // Si no hay ID, regresamos a la actividad anterior
            finish();
        }
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
            // Pasar de vuelta el ID del dueño para mantener la sesión
            intent.putExtra("id_cliente", idDueno);


            intent.putExtra("nombre", nombre);

            intent.putExtra("apellido", apellido);

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
        // Verificar que tenemos el ID del dueño antes de continuar
        if (idDueno == null || idDueno.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario", Toast.LENGTH_SHORT).show();
            return;
        }

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

                // Usar el ID dinámico del dueño en lugar del ID fijo "2"
                params.put("id_dueno", idDueno);
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
