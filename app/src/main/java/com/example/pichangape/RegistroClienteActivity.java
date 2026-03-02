package com.example.pichangape;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroClienteActivity extends AppCompatActivity {

    private EditText txtNombre, txtApellido, txtCelular, txtCorreo, txtDocumento, txtUsuario, txtPassword;
    private Spinner spinnerTipoDoc, spinnerDia, spinnerMes, spinnerAnio;
    private Button btnRegistrarse, btnCancelar;

    // URL del endpoint centralizada
    private final String URL_REGISTRO = ApiConfig.BASE_URL + "CRegistroCliente.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cliente);

        inicializarVistas();
        configurarSpinners();
        configurarBotones();
    }

    private void inicializarVistas() {
        txtNombre = findViewById(R.id.txtRegNombre);
        txtApellido = findViewById(R.id.txtRegApellido);
        txtCelular = findViewById(R.id.txtRegCelular);
        txtCorreo = findViewById(R.id.txtRegCorreo);
        txtDocumento = findViewById(R.id.txtRegDocumento);
        txtUsuario = findViewById(R.id.txtRegUsuario);
        txtPassword = findViewById(R.id.txtRegPassword);

        spinnerTipoDoc = findViewById(R.id.spinnerTipoDoc);
        spinnerDia = findViewById(R.id.spinnerRegDia);
        spinnerMes = findViewById(R.id.spinnerRegMes);
        spinnerAnio = findViewById(R.id.spinnerRegAnio);

        btnRegistrarse = findViewById(R.id.btnFinalizarRegistro);
        btnCancelar = findViewById(R.id.btnCancelarRegistro);
    }

    private void configurarSpinners() {
        // Spinner Tipo Documento
        ArrayAdapter<CharSequence> adapterTipoDoc = ArrayAdapter.createFromResource(this,
                R.array.tipos_documento, android.R.layout.simple_spinner_item);
        adapterTipoDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDoc.setAdapter(adapterTipoDoc);

        // Spinners Fecha de Nacimiento
        List<String> dias = new ArrayList<>();
        for (int i = 1; i <= 31; i++) dias.add(String.format("%02d", i));

        List<String> meses = new ArrayList<>();
        for (int i = 1; i <= 12; i++) meses.add(String.format("%02d", i));

        List<String> anios = new ArrayList<>();
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anioActual; i >= anioActual - 100; i--) anios.add(String.valueOf(i));

        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        ArrayAdapter<String> adapterMeses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meses);
        ArrayAdapter<String> adapterAnios = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, anios);

        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAnios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDia.setAdapter(adapterDias);
        spinnerMes.setAdapter(adapterMeses);
        spinnerAnio.setAdapter(adapterAnios);
    }

    private void configurarBotones() {
        btnCancelar.setOnClickListener(v -> finish());

        btnRegistrarse.setOnClickListener(v -> {
            if (validarCampos()) {
                ejecutarRegistro();
            }
        });
    }

    private boolean validarCampos() {
        if (txtNombre.getText().toString().trim().isEmpty() ||
            txtApellido.getText().toString().trim().isEmpty() ||
            txtCelular.getText().toString().trim().isEmpty() ||
            txtCorreo.getText().toString().trim().isEmpty() ||
            txtDocumento.getText().toString().trim().isEmpty() ||
            txtUsuario.getText().toString().trim().isEmpty() ||
            txtPassword.getText().toString().trim().isEmpty()) {
            
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void ejecutarRegistro() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTRO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("success")) {
                                Toast.makeText(RegistroClienteActivity.this, jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                                finish(); // Regresa al Login
                            } else if (jsonObject.has("error")) {
                                Toast.makeText(RegistroClienteActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistroClienteActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroClienteActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", txtNombre.getText().toString().trim());
                params.put("apellido", txtApellido.getText().toString().trim());
                params.put("numeroCel", txtCelular.getText().toString().trim());
                params.put("correo", txtCorreo.getText().toString().trim());
                params.put("tipoDoc", spinnerTipoDoc.getSelectedItem().toString());
                params.put("documento", txtDocumento.getText().toString().trim());
                params.put("fechaNac", spinnerAnio.getSelectedItem().toString() + "-" +
                                      spinnerMes.getSelectedItem().toString() + "-" +
                                      spinnerDia.getSelectedItem().toString());
                params.put("usuario", txtUsuario.getText().toString().trim());
                params.put("password", txtPassword.getText().toString().trim());
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
