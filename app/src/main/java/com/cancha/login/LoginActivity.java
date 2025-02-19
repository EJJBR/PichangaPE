package com.cancha.login;

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
    private final String URL_API = "https://92074086-7f51-47e8-857b-74e2a557e3b5-00-umq26f6wasig.janeway.replit.dev/index.php?action=login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String usuario = editTextUsuario.getText().toString().trim();
            String contrasena = editTextContrasena.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(usuario, contrasena);
            }
        });
    }

    private void loginUser(String usuario, String contrasena) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario", usuario);  // Cambio de "usuarioDueñoCan" a "usuario"
            jsonBody.put("password", contrasena);  // Cambio de "contraseñaDueñoCan" a "password"
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

    private void handleResponse(JSONObject response) {
        try {
            if (response.has("success")) {
                showToast(response.getString("success"));
                int idCliente = response.getInt("id_cliente");  // Cambio de "id_dueño" a "id_cliente"
                // Aquí puedes abrir la siguiente actividad y pasar idCliente
            } else if (response.has("error")) {
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

    private void handleError(VolleyError error) {
        showToast("Error de conexión con el servidor");
        error.printStackTrace();
    }

    private void showToast(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
