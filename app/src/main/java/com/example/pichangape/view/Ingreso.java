package com.example.pichangape.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pichangape.R;

public class Ingreso extends AppCompatActivity {

    private TextView tvBienvenida;
    private String idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingreso);

        // Inicializar el TextView
        tvBienvenida = findViewById(R.id.tvBienvenida);

        // Recuperar los datos enviados desde el login
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        idCliente = getIntent().getStringExtra("id_cliente");

        // Verificar si los datos fueron recibidos
        if (idCliente == null || idCliente.isEmpty()) {
            Toast.makeText(this, "ID de cliente no recibido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar el mensaje de bienvenida en el TextView
        tvBienvenida.setText("¡Bienvenido, " + nombre + " " + apellido + "!");

        // Configurar insets para evitar que la UI se sobreponga con las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}