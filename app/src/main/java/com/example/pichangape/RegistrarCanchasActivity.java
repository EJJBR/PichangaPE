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
    private EditText lblNombreCancha, lblArea, lblDireccion, lblHorasDisponibles,
            lblFechasDisponibles, lblCostoPorHora;
    private Spinner spinnerCategoria, spinnerHoraInicio, spinnerHoraFin;
    private Spinner spinnerDiaInicio, spinnerMesInicio, spinnerDiaFin, spinnerMesFin;
    private Button btnRegresar, btnRegistrar;
    private String categoriaSeleccionada;
    private RequestQueue requestQueue;
    private String idDueno;

    private static final String URL_REGISTRAR_CANCHA = "https://1fe8107b-4bc6-4865-9bbd-dbd93570a5ba-00-z75lvfccgfim.worf.replit.dev/agregar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_canchas);

        requestQueue = Volley.newRequestQueue(this);

        // OBTENER DATOS PRIMERO
        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        obtenerIdDueno();

        inicializarVistas();
        configurarSpinnerCategorias();
        configurarSpinnersHoras();
        configurarSpinnersFechas();
        configurarBotones();
    }

    private void obtenerIdDueno() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id_cliente")) {
            idDueno = intent.getStringExtra("id_cliente");
            Log.d(TAG, "ID del dueño obtenido: " + idDueno);
        } else {
            Log.e(TAG, "No se encontró el ID del dueño en el Intent");
            Toast.makeText(this, "Error: No se pudo obtener la información del usuario", Toast.LENGTH_LONG).show();
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

    private void limpiarCampos() {
        lblNombreCancha.setText("");
        lblArea.setText("");
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
