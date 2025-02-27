package com.example.pichangape;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pichangape.adapters.EstadisticasAdapter;
import com.example.pichangape.adapters.ReservasAdapter;
import com.example.pichangape.models.Reserva;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Reserva> listaReservas;
    private ReservasAdapter reservasAdapter;
    private String id_cancha;
    private Spinner spinnerEstado;
    private Button btnDownloadPDF; // Botón para descargar el PDF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaReservas = new ArrayList<>();
        reservasAdapter = new ReservasAdapter(this, listaReservas);
        recyclerView.setAdapter(reservasAdapter);

        // Recuperar el id_cancha enviado desde la actividad anterior
        id_cancha = getIntent().getStringExtra("id_cancha");
        if (id_cancha == null || id_cancha.isEmpty()) {
            Toast.makeText(this, "ID de cancha no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configuración del Spinner para filtrar las reservas por estado
        spinnerEstado = findViewById(R.id.spinnerEstado);
        String[] estados = {"Todos", "Pendiente", "Alquilada", "Cancelado"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstados);
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String estadoSeleccionado = parent.getItemAtPosition(position).toString();
                reservasAdapter.filterByEstado(estadoSeleccionado);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Opcional: No hacer nada
            }
        });

        // Configuración del botón para descargar el PDF
        btnDownloadPDF = findViewById(R.id.btnDownloadPDF);
        btnDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descargarPDF();
            }
        });

        // Llamada para obtener las reservas desde la API
        fetchReservas();
    }

    // Método para obtener las reservas (ya existente)
    private void fetchReservas() {
        String urlReservas = "https://739c9dc3-0789-44cf-b9b3-0a433b602be3-00-g7yu9uuhed8k.worf.replit.dev/reservaciones.php";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando reservaciones...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, urlReservas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        List<Reserva> reservasCargadas = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject reservaObj = jsonArray.getJSONObject(i);
                                int idReserva = reservaObj.getInt("id_reserva");
                                String fechaInicio = reservaObj.getString("fecha_inicio");
                                String horaInicio = reservaObj.getString("hora_inicio");
                                String horaFin = reservaObj.getString("hora_fin");
                                String estadoReserva = reservaObj.getString("estado_reserva");

                                Reserva reserva = new Reserva(idReserva, fechaInicio, horaInicio, horaFin, estadoReserva);
                                reservasCargadas.add(reserva);
                            }
                            reservasAdapter.updateList(reservasCargadas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReservacionesActivity.this, "Error al procesar datos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReservacionesActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cancha", id_cancha);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Método para descargar los datos de la cancha en PDF utilizando la API
    private void descargarPDF() {
        String urlAPI = "https://739c9dc3-0789-44cf-b9b3-0a433b602be3-00-g7yu9uuhed8k.worf.replit.dev/reporte.php?id_cancha=" + id_cancha;
        StringRequest request = new StringRequest(Request.Method.GET, urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject cancha = jsonObject.getJSONObject("cancha");
                            JSONArray reservas = jsonObject.getJSONArray("reservas");
                            generarPDF(cancha, reservas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReservacionesActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReservacionesActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void generarPDF(JSONObject cancha, JSONArray reservas) {
        // Crear documento PDF y definir la página
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Configuración de márgenes y posición inicial
        int margin = 40;
        int pageWidth = pageInfo.getPageWidth();
        int currentY = 50;
        int lineSpacing = 22; // Aumenta la separación vertical entre líneas

        // Configuración de pinturas para encabezados y texto
        Paint headerPaint = new Paint();
        headerPaint.setTextSize(20f);
        headerPaint.setFakeBoldText(true);
        headerPaint.setTextAlign(Paint.Align.CENTER);

        Paint subHeaderPaint = new Paint();
        subHeaderPaint.setTextSize(16f);
        subHeaderPaint.setFakeBoldText(true);

        Paint labelPaint = new Paint();
        labelPaint.setTextSize(12f);
        labelPaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setTextSize(12f);

        // Encabezado principal
        String titulo = "Reporte de Cancha";
        canvas.drawText(titulo, pageWidth / 2, currentY, headerPaint);
        currentY += 30;

        // Fecha y línea separadora
        String fechaActual = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        canvas.drawText("Fecha: " + fechaActual, margin, currentY, textPaint);
        currentY += 15;
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, textPaint);
        currentY += 25;

        // Sección: Datos de la Cancha (presentados en dos columnas: etiqueta - valor)
        canvas.drawText("Datos de la Cancha", margin, currentY, subHeaderPaint);
        currentY += 25;
        int labelX = margin;
        int valueX = margin + 140; // Espacio suficiente para las etiquetas
        try {
            canvas.drawText("Nombre:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("nombre_cancha"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Dirección:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("direccion"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Precio/Hora:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("precio_por_hora"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Tipo:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("tipoCancha"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            // Abreviamos "Horas Disponibles" para mayor orden
            canvas.drawText("Horas disponibles:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("horasDisponibles"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Fechas Abiertas:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("fechas_abiertas"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Estado:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("estado_cancha"), valueX, currentY, textPaint);
            currentY += lineSpacing + 5;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, textPaint);
        currentY += 25;

        // Sección: Datos del Dueño (presentados de forma similar)
        canvas.drawText("Datos del Dueño", margin, currentY, subHeaderPaint);
        currentY += 25;
        try {
            canvas.drawText("Nombre:", labelX, currentY, labelPaint);
            String nombreDueno = cancha.getString("nombre_dueno") + " " + cancha.getString("apellido_dueno");
            canvas.drawText(nombreDueno, valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Celular:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("celular_dueno"), valueX, currentY, textPaint);
            currentY += lineSpacing;

            canvas.drawText("Correo:", labelX, currentY, labelPaint);
            canvas.drawText(cancha.getString("correo_dueno"), valueX, currentY, textPaint);
            currentY += lineSpacing + 5;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, textPaint);
        currentY += 25;

        // Sección: Reservas (presentadas en formato de tabla con columnas fijas)
        canvas.drawText("Reservas", margin, currentY, subHeaderPaint);
        currentY += 25;
        try {
            if (reservas.length() == 0) {
                canvas.drawText("No se encontraron reservas.", margin, currentY, textPaint);
                currentY += lineSpacing;
            } else {
                // Definir posiciones fijas para las columnas de la tabla
                int colN = margin;                      // Columna para el número (ancho: 30 px)
                int colInicio = colN + 30;                // Columna "Inicio" (ancho: 120 px)
                int colFin = colInicio + 120;             // Columna "Fin" (ancho: 120 px)
                int colPrecio = colFin + 120;             // Columna "Precio" (ancho: 100 px)
                int colEstado = colPrecio + 100;          // Columna "Estado" (hasta el final)

                // Encabezados de la tabla
                canvas.drawText("N°", colN, currentY, labelPaint);
                canvas.drawText("Inicio", colInicio, currentY, labelPaint);
                canvas.drawText("Fin", colFin, currentY, labelPaint);
                canvas.drawText("Precio", colPrecio, currentY, labelPaint);
                canvas.drawText("Estado", colEstado, currentY, labelPaint);
                currentY += lineSpacing;

                // Línea divisoria para la tabla
                canvas.drawLine(margin, currentY, pageWidth - margin, currentY, textPaint);
                currentY += 10;

                // Listado de reservas
                for (int i = 0; i < reservas.length(); i++) {
                    JSONObject reserva = reservas.getJSONObject(i);
                    String inicio = reserva.getString("fecha_hora_inicio");
                    String fin = reserva.getString("fecha_hora_fin");
                    String precioTotal = reserva.getString("precio_total");
                    String estadoRes = reserva.getString("estado_reserva");

                    canvas.drawText(String.valueOf(i + 1), colN, currentY, textPaint);
                    canvas.drawText(inicio, colInicio, currentY, textPaint);
                    canvas.drawText(fin, colFin, currentY, textPaint);
                    canvas.drawText(precioTotal, colPrecio, currentY, textPaint);
                    canvas.drawText(estadoRes, colEstado, currentY, textPaint);
                    currentY += lineSpacing;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Finalizar la página y guardar el documento
        pdfDocument.finishPage(page);
        File pdfFile = new File(getExternalFilesDir(null), "DatosCancha.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF generado: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            verPDF(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }




    // Método para abrir el PDF utilizando FileProvider
    private void verPDF(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                pdfFile
        );
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay una aplicación para ver PDF instalada", Toast.LENGTH_SHORT).show();
        }
    }
}
