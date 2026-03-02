package com.example.pichangape.database;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pichangape.ApiConfig;
import com.example.pichangape.BienvenidaActivity;
import com.example.pichangape.ClienteMainActivity;
import com.example.pichangape.R;
import com.example.pichangape.RegistroClienteActivity;
import com.example.pichangape.models.ConexionDuenio;
import com.example.pichangape.view.Ingreso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class login extends AppCompatActivity {
    EditText txtUsuario, pswContrasenia;
    String strUsuario, strContrasenia;
    String url = ApiConfig.BASE_URL + "CLogin.php";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        txtUsuario = findViewById(R.id.txtUser);
        pswContrasenia = findViewById(R.id.pswContraseña);

        Button btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguearse();
            }
        });

        // VINCULACIÓN DEL BOTÓN DE REGISTRO
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, RegistroClienteActivity.class);
                startActivity(intent);
            }
        });
    }
    public void loguearse() {
        if (txtUsuario.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese su usuario", Toast.LENGTH_LONG).show();
            return;
        }
        if (pswContrasenia.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verificando credenciales...");
        progressDialog.show();

        strUsuario = txtUsuario.getText().toString().trim();
        strContrasenia = pswContrasenia.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("error")) {
                                Toast.makeText(login.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                            } else {
                                String rol = jsonObject.getString("rol");
                                String id_cliente = jsonObject.getString("id_cliente");
                                String nombre = jsonObject.getString("nombre");
                                String apellido = jsonObject.getString("apellido");

                                if (rol.equals("dueño")) {
                                    // Redirigir a Panel de Dueño
                                    Toast.makeText(login.this, "Bienvenido " + nombre + " " + apellido, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(login.this, Ingreso.class);
                                    intent.putExtra("nombre", nombre);
                                    intent.putExtra("apellido", apellido);
                                    intent.putExtra("id_cliente", id_cliente);
                                    startActivity(intent);

                                } else if (rol.equals("cliente")) {
                                    // REDIRIGIR A PANEL DE CLIENTE (Nueva Pantalla)
                                    Toast.makeText(login.this, "Bienvenido " + nombre, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(login.this, ClienteMainActivity.class);
                                    intent.putExtra("nombre", nombre);
                                    intent.putExtra("apellido", apellido);
                                    intent.putExtra("id_cliente", id_cliente);
                                    startActivity(intent);
                                    
                                } else {
                                    Toast.makeText(login.this, "Rol no reconocido.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(login.this, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(login.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", strUsuario);
                params.put("contraseña", strContrasenia);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}