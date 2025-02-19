package com.example.pichangape.database;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pichangape.R;
import com.example.pichangape.models.ConexionDuenio;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class login extends AppCompatActivity {
    EditText txtUsuario, pswContrasenia;
    String strUsuario, strContrasenia;
    String url = "https://1fe8107b-4bc6-4865-9bbd-dbd93570a5ba-00-z75lvfccgfim.worf.replit.dev/CLogin.php";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        txtUsuario = findViewById(R.id.txtUser);
        pswContrasenia = findViewById(R.id.pswContraseña);

        Button btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguearse();
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
                                if (rol.equals("dueño")) {
                                    // Crear objeto Dueño con los datos del JSON
                                    ConexionDuenio duenio = new ConexionDuenio(
                                            jsonObject.getString("id_cliente"),
                                            jsonObject.getString("nombre"),
                                            jsonObject.getString("apellido"),
                                            jsonObject.getString("numeroCel"),
                                            jsonObject.getString("correo"),
                                            jsonObject.getString("documento"),
                                            jsonObject.getString("tipoDoc"),
                                            jsonObject.getString("fechaNac"),
                                            jsonObject.getString("usuario"),
                                            jsonObject.getString("password"),
                                            jsonObject.getString("rol"),
                                            jsonObject.getString("numYape"),
                                            jsonObject.getString("numTransfer")
                                    );
                                    Toast.makeText(login.this,"bienvenido "+duenio.getNombre()+" "+duenio.getApellido(),Toast.LENGTH_LONG).show();
                                    // Iniciar la nueva actividad y pasar el objeto Dueño
                                    //Intent intent = new Intent(login.this, BienvenidaActivity.class);
                                    //intent.putExtra("dueño", dueño);
                                    //startActivity(intent);
                                } else {
                                    Toast.makeText(login.this, "Acceso denegado. No eres dueño.", Toast.LENGTH_LONG).show();
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