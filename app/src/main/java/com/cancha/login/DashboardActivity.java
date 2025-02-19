package com.cancha.login;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvClienteInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvClienteInfo = findViewById(R.id.tvClienteInfo);

        // Obtener el idCliente pasado desde LoginActivity
        int idCliente = getIntent().getIntExtra("ID_CLIENTE", -1);

        // Mostrarlo en pantalla
        tvClienteInfo.setText("ID del Cliente: " + idCliente);
    }
}
