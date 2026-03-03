package com.example.pichangape;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HacerReservaActivity extends AppCompatActivity {

    private TextView tvNombre, tvDireccion, tvPrecio, tvYape, tvTransfer, tvTotal;
    private Spinner spinnerDia, spinnerMes, spinnerHoraInicio, spinnerHoraFin;
    private ImageView ivPreview;
    private Button btnSeleccionar, btnConfirmar;

    private String idCancha, idCliente, precioHoraStr, horasDisponibles, fechasAbiertas;
    private double precioHora, totalCalculado = 0;
    private Bitmap bitmapVoucher;

    // Variables para el rango de fechas
    private int mesInicioPermitido, mesFinPermitido, diaInicioPermitido, diaFinPermitido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hacer_reserva);

        recuperarDatos();
        inicializarVistas();
        configurarSpinnersDinamicos();
        configurarSeleccionImagen();

        btnConfirmar.setOnClickListener(v -> {
            if (validarDatos()) {
                subirImagenImgBB();
            }
        });
    }

    private void recuperarDatos() {
        Intent intent = getIntent();
        idCancha = intent.getStringExtra("id_cancha");
        idCliente = intent.getStringExtra("id_cliente_reservador");
        precioHoraStr = intent.getStringExtra("precio");
        precioHora = Double.parseDouble(precioHoraStr);
        horasDisponibles = intent.getStringExtra("horasDisponibles");
        fechasAbiertas = intent.getStringExtra("fechas_abiertas");
    }

    private void inicializarVistas() {
        tvNombre = findViewById(R.id.tvReservaNombreCancha);
        tvDireccion = findViewById(R.id.tvReservaDireccion);
        tvPrecio = findViewById(R.id.tvReservaPrecioHora);
        tvYape = findViewById(R.id.tvYapeDuenio);
        tvTransfer = findViewById(R.id.tvTransferDuenio);
        tvTotal = findViewById(R.id.tvTotalReserva);
        ivPreview = findViewById(R.id.ivVoucherPreview);
        btnSeleccionar = findViewById(R.id.btnSeleccionarVoucher);
        btnConfirmar = findViewById(R.id.btnConfirmarReserva);

        tvNombre.setText(getIntent().getStringExtra("nombre_cancha"));
        tvDireccion.setText(getIntent().getStringExtra("direccion"));
        tvPrecio.setText("$" + precioHoraStr + " / hora");
        tvYape.setText("Yape: " + getIntent().getStringExtra("numYape"));
        tvTransfer.setText("Transferencia: " + getIntent().getStringExtra("numTransfer"));
    }

    private void configurarSpinnersDinamicos() {
        spinnerDia = findViewById(R.id.spinnerResDia);
        spinnerMes = findViewById(R.id.spinnerResMes);
        spinnerHoraInicio = findViewById(R.id.spinnerResHoraInicio);
        spinnerHoraFin = findViewById(R.id.spinnerResHoraFin);

        // --- 1. Lógica de Horas ---
        try {
            String[] partesH = horasDisponibles.split("-");
            int hI = Integer.parseInt(partesH[0].split(":")[0]);
            int hF = Integer.parseInt(partesH[1].split(":")[0]);
            List<String> horasPermitidas = new ArrayList<>();
            for (int i = hI; i <= hF; i++) horasPermitidas.add(String.format("%02d:00", i));
            ArrayAdapter<String> adapterH = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horasPermitidas);
            spinnerHoraInicio.setAdapter(adapterH);
            spinnerHoraFin.setAdapter(adapterH);
        } catch (Exception e) { Log.e("HacerReserva", "Error horas"); }

        // --- 2. Lógica de Fechas (CORREGIDA) ---
        try {
            if (fechasAbiertas.contains(",")) {
                String[] rangos = fechasAbiertas.split(",");
                String[] fInicio = rangos[0].trim().split("-");
                String[] fFin = rangos[rangos.length - 1].trim().split("-");

                mesInicioPermitido = Integer.parseInt(fInicio[1]);
                diaInicioPermitido = Integer.parseInt(fInicio[2]);
                mesFinPermitido = Integer.parseInt(fFin[1]);
                diaFinPermitido = Integer.parseInt(fFin[2]);

                List<String> mesesP = new ArrayList<>();
                for (int i = mesInicioPermitido; i <= mesFinPermitido; i++) mesesP.add(String.format("%02d", i));
                spinnerMes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mesesP));

                // Listener para actualizar los días según el mes seleccionado
                spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        actualizarSpinnerDias(Integer.parseInt(mesesP.get(position)));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                String[] fUnica = fechasAbiertas.trim().split("-");
                List<String> m = new ArrayList<>(); m.add(fUnica[1]);
                List<String> d = new ArrayList<>(); d.add(fUnica[2]);
                spinnerMes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, m));
                spinnerDia.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, d));
            }
        } catch (Exception e) { Log.e("HacerReserva", "Error fechas"); }

        AdapterView.OnItemSelectedListener listenerTotal = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { calcularTotal(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerHoraInicio.setOnItemSelectedListener(listenerTotal);
        spinnerHoraFin.setOnItemSelectedListener(listenerTotal);
    }

    private void actualizarSpinnerDias(int mesSeleccionado) {
        List<String> diasP = new ArrayList<>();
        int inicio = 1;
        int fin = 31; // Simplificado, idealmente usar Calendar para fin de mes

        if (mesSeleccionado == mesInicioPermitido) inicio = diaInicioPermitido;
        if (mesSeleccionado == mesFinPermitido) fin = diaFinPermitido;

        for (int i = inicio; i <= fin; i++) diasP.add(String.format("%02d", i));
        spinnerDia.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diasP));
    }

    private void calcularTotal() {
        String sInicio = (String) spinnerHoraInicio.getSelectedItem();
        String sFin = (String) spinnerHoraFin.getSelectedItem();
        if (sInicio != null && sFin != null) {
            int hI = Integer.parseInt(sInicio.split(":")[0]);
            int hF = Integer.parseInt(sFin.split(":")[0]);
            if (hF > hI) {
                totalCalculado = (hF - hI) * precioHora;
                tvTotal.setText(String.format("Total a pagar: $%.2f", totalCalculado));
            } else {
                totalCalculado = 0;
                tvTotal.setText("Total a pagar: $0.00");
            }
        }
    }

    private void configurarSeleccionImagen() {
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            bitmapVoucher = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            ivPreview.setImageBitmap(bitmapVoucher);
                            ivPreview.setVisibility(View.VISIBLE);
                        } catch (IOException e) { e.printStackTrace(); }
                    }
                }
        );
        btnSeleccionar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });
    }

    private boolean validarDatos() {
        if (bitmapVoucher == null) {
            Toast.makeText(this, "Por favor, suba la foto del voucher", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (totalCalculado <= 0) {
            Toast.makeText(this, "Seleccione un horario válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void subirImagenImgBB() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Subiendo comprobante...");
        pd.setCancelable(false);
        pd.show();

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.imgbb.com/1/upload?key=" + ApiConfig.IMGBB_API_KEY,
                response -> {
                    pd.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        String urlImagen = json.getJSONObject("data").getString("url");
                        registrarReservaEnBD(urlImagen);
                    } catch (JSONException e) { Toast.makeText(this, "Error ImgBB", Toast.LENGTH_SHORT).show(); }
                },
                error -> {
                    pd.dismiss();
                    Toast.makeText(this, "Fallo al subir", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", convertirBitmapBase64(bitmapVoucher));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private String convertirBitmapBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void registrarReservaEnBD(String urlVoucher) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Guardando reserva...");
        pd.show();

        String url = ApiConfig.BASE_URL + "CRegistrarReserva.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    pd.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("success")) {
                            Toast.makeText(this, json.getString("success"), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) { Toast.makeText(this, "Error de respuesta", Toast.LENGTH_SHORT).show(); }
                },
                error -> {
                    pd.dismiss();
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                int anio = Calendar.getInstance().get(Calendar.YEAR);
                String mes = spinnerMes.getSelectedItem().toString();
                String dia = spinnerDia.getSelectedItem().toString();
                String hInicio = spinnerHoraInicio.getSelectedItem().toString();
                String hFin = spinnerHoraFin.getSelectedItem().toString();

                params.put("id_reservador", idCliente);
                params.put("id_cancha", idCancha);
                params.put("fecha_hora_inicio", anio + "-" + mes + "-" + dia + " " + hInicio + ":00");
                params.put("fecha_hora_fin", anio + "-" + mes + "-" + dia + " " + hFin + ":00");
                params.put("precio_total", String.valueOf(totalCalculado));
                params.put("voucher_pago", urlVoucher);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
