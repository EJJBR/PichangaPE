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
import com.example.pichangape.view.Ingreso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarCanchasActivity extends AppCompatActivity {

    String nombre;
    String apellido;
    private static final String TAG = "RegistrarCanchasActivity";
    private EditText lblNombreCancha, lblDireccion, lblHorasDisponibles,
            lblFechasDisponibles, lblCostoPorHora;
    private Spinner spinnerCategoria, spinnerHoraInicio, spinnerHoraFin;
    private Spinner spinnerDiaInicio, spinnerMesInicio, spinnerDiaFin, spinnerMesFin;
    private Button btnRegresar, btnRegistrar;
    private String categoriaSeleccionada;
    private RequestQueue requestQueue;
    private String idDueno;
    
    // Variables para el modo edición
    private String idCanchaEdicion = null;
    private boolean esModoEdicion = false;

    private static final String URL_REGISTRAR_CANCHA = ApiConfig.BASE_URL + "agregar.php";
    // Nota: La URL de actualizar se usará después
    private static final String URL_ACTUALIZAR_CANCHA = ApiConfig.BASE_URL + "actualizar_cancha.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_canchas);

        requestQueue = Volley.newRequestQueue(this);

        // Obtener datos del dueño
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        obtenerIdDueno();

        inicializarVistas();
        configurarSpinnerCategorias();
        configurarSpinnersHoras();
        configurarSpinnersFechas();
        configurarBotones();

        // COMPROBAR SI ESTAMOS EN MODO EDICIÓN
        verificarModoEdicion();
    }

    private void verificarModoEdicion() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_cancha")) {
            esModoEdicion = true;
            idCanchaEdicion = intent.getStringExtra("id_cancha");
            
            // Cambiar textos de la interfaz
            btnRegistrar.setText("Actualizar");
            
            // Llenar campos con los datos recibidos
            lblNombreCancha.setText(intent.getStringExtra("nombre_cancha"));
            lblDireccion.setText(intent.getStringExtra("direccion"));
            lblCostoPorHora.setText(intent.getStringExtra("precio"));
            
            // Nota: Para los spinners de horas y fechas, se quedarían con valores por defecto
            // o podrías parsear los strings si fuera necesario.
            
            Log.d(TAG, "Modo edición activado para cancha ID: " + idCanchaEdicion);
        }
    }

    private void obtenerIdDueno() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_cliente")) {
            idDueno = intent.getStringExtra("id_cliente");
        } else {
            // Si no hay ID de cliente (caso de venir desde el botón editar), 
            // el ID del dueño debería ser recuperado de una sesión global o base de datos.
            // Por ahora, asumiremos que se mantiene la navegación.
        }
    }

    private void inicializarVistas() {
        lblNombreCancha = findViewById(R.id.lblNombreCancha);
        lblDireccion = findViewById(R.id.lblDireccion);
        lblHorasDisponibles = findViewById(R.id.lblhorasdisponibles);
        lblFechasDisponibles = findViewById(R.id.lblfechasdisponibles);
        lblCostoPorHora = findViewById(R.id.lblCostoporhora);
        spinnerCategoria = findViewById(R.id.spinner_categorias);
        spinnerHoraInicio = findViewById(R.id.spinner_hora_inicio);
        spinnerHoraFin = findViewById(R.id.spinner_hora_fin);

        spinnerDiaInicio = findViewById(R.id.spinner_dia_inicio);
        spinnerMesInicio = findViewById(R.id.spinner_mes_inicio);
        spinnerDiaFin = findViewById(R.id.spinner_dia_fin);
        spinnerMesFin = findViewById(R.id.spinner_mes_fin);

        btnRegresar = findViewById(R.id.btnRegresar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
    }

    private void configurarSpinnersHoras() {
        List<String> horas = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            horas.add(String.format("%02d:00", i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, horas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerHoraInicio.setAdapter(adapter);
        spinnerHoraFin.setAdapter(adapter);

        spinnerHoraInicio.setSelection(8);
        spinnerHoraFin.setSelection(22);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTextoHoras();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerHoraInicio.setOnItemSelectedListener(listener);
        spinnerHoraFin.setOnItemSelectedListener(listener);
        actualizarTextoHoras();
    }

    private void actualizarTextoHoras() {
        String inicio = spinnerHoraInicio.getSelectedItem().toString();
        String fin = spinnerHoraFin.getSelectedItem().toString();
        lblHorasDisponibles.setText(inicio + "-" + fin);
    }

    private void configurarSpinnersFechas() {
        List<String> dias = new ArrayList<>();
        for (int i = 1; i <= 31; i++) dias.add(String.format("%02d", i));

        List<String> meses = new ArrayList<>();
        for (int i = 1; i <= 12; i++) meses.add(String.format("%02d", i));

        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        ArrayAdapter<String> adapterMeses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meses);

        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDiaInicio.setAdapter(adapterDias);
        spinnerMesInicio.setAdapter(adapterMeses);
        spinnerDiaFin.setAdapter(adapterDias);
        spinnerMesFin.setAdapter(adapterMeses);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTextoFechas();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerDiaInicio.setOnItemSelectedListener(listener);
        spinnerMesInicio.setOnItemSelectedListener(listener);
        spinnerDiaFin.setOnItemSelectedListener(listener);
        spinnerMesFin.setOnItemSelectedListener(listener);

        actualizarTextoFechas();
    }

    private void actualizarTextoFechas() {
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        String fechaI = anioActual + "-" +
                        spinnerMesInicio.getSelectedItem().toString() + "-" +
                        spinnerDiaInicio.getSelectedItem().toString();
        String fechaF = anioActual + "-" +
                        spinnerMesFin.getSelectedItem().toString() + "-" +
                        spinnerDiaFin.getSelectedItem().toString();
        lblFechasDisponibles.setText(fechaI + ", " + fechaF);
    }

    private void configurarSpinnerCategorias() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarBotones() {
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarCanchasActivity.this, Ingreso.class);
            intent.putExtra("id_cliente", idDueno);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            startActivity(intent);
            finish();
        });

        btnRegistrar.setOnClickListener(v -> {
            if (validarCampos()) {
                if (esModoEdicion) {
                    actualizarCancha();
                } else {
                    registrarCancha();
                }
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

        if (spinnerHoraInicio.getSelectedItemPosition() >= spinnerHoraFin.getSelectedItemPosition()) {
            Toast.makeText(this, "La hora de inicio debe ser menor a la hora de fin", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Double.parseDouble(lblCostoPorHora.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El costo por hora debe ser un número válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void registrarCancha() {
        if (idDueno == null || idDueno.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Registrando cancha...", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRAR_CANCHA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Respuesta del servidor: " + response);
                        if (response.trim().equals("success")) {
                            Toast.makeText(RegistrarCanchasActivity.this, "Cancha registrada exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                        } else {
                            Toast.makeText(RegistrarCanchasActivity.this, "Error: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrarCanchasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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

        requestQueue.add(stringRequest);
    }

    private void actualizarCancha() {
        Toast.makeText(this, "Actualizando cancha...", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACTUALIZAR_CANCHA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            Toast.makeText(RegistrarCanchasActivity.this, "Cancha actualizada exitosamente", Toast.LENGTH_SHORT).show();
                            // Al terminar, volvemos a la lista
                            btnRegresar.performClick();
                        } else {
                            Toast.makeText(RegistrarCanchasActivity.this, "Error: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrarCanchasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cancha", idCanchaEdicion); // Enviamos el ID para saber cuál actualizar
                params.put("nombre", lblNombreCancha.getText().toString().trim());
                params.put("direccion", lblDireccion.getText().toString().trim());
                params.put("precio_por_hora", lblCostoPorHora.getText().toString().trim());
                params.put("tipoCancha", categoriaSeleccionada.toLowerCase());
                params.put("horasDisponibles", lblHorasDisponibles.getText().toString().trim());
                params.put("fechas_abiertas", lblFechasDisponibles.getText().toString().trim());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void limpiarCampos() {
        lblNombreCancha.setText("");
        lblDireccion.setText("");
        lblCostoPorHora.setText("");
        spinnerCategoria.setSelection(0);
        spinnerHoraInicio.setSelection(8);
        spinnerHoraFin.setSelection(22);
        
        spinnerDiaInicio.setSelection(0);
        spinnerMesInicio.setSelection(0);
        spinnerDiaFin.setSelection(0);
        spinnerMesFin.setSelection(0);
        
        actualizarTextoHoras();
        actualizarTextoFechas();
    }
}
