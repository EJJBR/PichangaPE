package com.cancha.login;

import android.content.Intent;               // <-- Importante para abrir otra actividad
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsuario, editTextContrasena;
    private Button btnLogin;
    private final String URL_API =
            "https://92074086-7f51-47e8-857b-74e2a557e3b5-00-umq26f6wasig.janeway.replit.dev/index.php?action=login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Referenciar vistas
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        btnLogin = findViewById(R.id.btnLogin);

        // 2. Configurar botón de login
        btnLogin.setOnClickListener(view -> {
            String usuario = editTextUsuario.getText().toString().trim();
            String contrasena = editTextContrasena.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Ingresa usuario y contraseña",
                        Toast.LENGTH_SHORT).show();
            } else {
                // 3. Llamar al método que hace la petición de login
                loginUser(usuario, contrasena);
            }
        });
    }

    /**
     * Envía las credenciales al servidor usando Volley
     */
    private void loginUser(String usuario, String contrasena) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario", usuario);    // Ajustar a lo que espera tu API
            jsonBody.put("password", contrasena); // Ajustar a lo que espera tu API
        } catch (JSONException e) {
            showToast("Error al crear la solicitud");
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                jsonBody,
                this::handleResponse,
                this::handleError
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Maneja la respuesta exitosa del servidor
     */
    private void handleResponse(JSONObject response) {
        try {
            if (response.has("success")) {
                // 1. Mostrar mensaje de éxito
                showToast(response.getString("success"));

                // 2. Obtener el idCliente de la respuesta (ajusta la key si tu API difiere)
                int idCliente = response.getInt("id_cliente");

                // 3. Abrir DashboardActivity y pasar idCliente
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("ID_CLIENTE", idCliente);
                startActivity(intent);

                // 4. (Opcional) Cerrar LoginActivity para que no regrese con el botón atrás
                finish();

            } else if (response.has("error")) {
                // Manejo de errores devueltos por la API
                String error = response.getString("error");
                if (error.equals("Usuario no encontrado")) {
                    showToast("El usuario ingresado no existe.");
                } else if (error.equals("Contraseña incorrecta")) {
                    showToast("La contraseña es incorrecta.");
                } else {
                    showToast(error);
                }
            }
        } catch (JSONException e) {
            showToast("Error al procesar la respuesta del servidor");
            e.printStackTrace();
        }
    }

    /**
     * Maneja el error de la petición (por ejemplo, no hay conexión)
     */
    private void handleError(VolleyError error) {
        showToast("Error de conexión con el servidor");
        error.printStackTrace();
    }

    /**
     * Muestra un mensaje rápido al usuario
     */
    private void showToast(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
