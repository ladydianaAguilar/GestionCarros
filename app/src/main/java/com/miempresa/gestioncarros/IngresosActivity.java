package com.miempresa.gestioncarros;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.miempresa.gestioncarros.DAO.GastosDAO;
import com.miempresa.gestioncarros.DAO.IngresosDAO;
import com.miempresa.gestioncarros.Entidades.Ingresos;
import com.miempresa.gestioncarros.Entidades.Motivo;
import com.miempresa.gestioncarros.databinding.ActivityIngresosBinding;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class IngresosActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spnplacas;
    private Button btncalendar, btnguarda;
    private EditText edtfechaing, edtmontoing;
    private CheckBox chktrabajo;
    private TextInputEditText tietmotivo;
    private TextInputLayout tilmotivo;
    private int dia, mes, año;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresos);
        //
        spnplacas = (Spinner) findViewById(R.id.spnPlacas);
        btncalendar = (Button) findViewById(R.id.btncalendar);
        edtfechaing = (EditText) findViewById(R.id.edtFechaing);
        edtmontoing = (EditText) findViewById(R.id.edtMontoing);
        btnguarda = (Button) findViewById(R.id.btnGuardar);
        chktrabajo = (CheckBox) findViewById(R.id.chkTrabajo);
        tietmotivo = (TextInputEditText) findViewById(R.id.tietMotivo);
        tilmotivo = (TextInputLayout) findViewById(R.id.tilMotivo);
        //
        llenarSpinnerPlacas();
        edtfechaing.setText(obtenerFechaActual());
        //
        btncalendar.setOnClickListener(this);
        spnplacas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                IngresosDAO dao = new IngresosDAO(getApplicationContext());
                switch (pos){
                    case 1: edtmontoing.setText(String.valueOf(dao.obtenerMonto("A1E738")));break;
                    case 2: edtmontoing.setText(String.valueOf(dao.obtenerMonto("AAK145")));break;
                    case 3: edtmontoing.setText(String.valueOf(dao.obtenerMonto("AKZ618")));break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnguarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarIngreso();
            }
        });
        chktrabajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chktrabajo.isChecked()) {
                    tilmotivo.setVisibility(View.VISIBLE);
                    edtmontoing.setText("0");
                    edtmontoing.setEnabled(false);
                }else{
                    tilmotivo.setVisibility(View.GONE);
                    edtmontoing.setEnabled(true);
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void grabarIngreso() {
        IngresosDAO dao = new IngresosDAO(this);
        //definir en variables
        String spinnerplaca = spnplacas.getSelectedItem().toString();
        String montoingreso = edtmontoing.getText().toString();
        String fechaIngreso = edtfechaing.getText().toString(); //aqui fecha: 2/3/2023 cuando se desea comparar convertirlo en 02/03/2023
        Log.d("DEBUG", fechaIngreso);
        String[] partesFecha = fechaIngreso.split("/");
        int mesIngreso = Integer.parseInt(partesFecha[1]);
        int añoIngreso = Integer.parseInt(partesFecha[2]);

        //FORMATEO DE LA fechaingreso (para la comparación)
        fechaIngreso = String.format("%02d/%02d/%04d",
                Integer.parseInt(fechaIngreso.split("/")[0]),
                Integer.parseInt(fechaIngreso.split("/")[1]),
                Integer.parseInt(fechaIngreso.split("/")[2]));

        // Valida placa
        if (spnplacas.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona una placa", Toast.LENGTH_SHORT).show();
            return;
        }
        //Validar si ya se registro en Ingresos, si ya se marco chekbox no permitir registrar en esa misma fecha
        //Si chkbox 'No trabajo' está activado:

        // fechaingreso toma este valor: String fechaIngreso = edtfechaing.getText().toString();
        if (chktrabajo.isChecked()) {
            if (dao.existeIngresoParaFecha(spinnerplaca, fechaIngreso)) {
                Log.d("DEBUG", "Existe");
                Toast.makeText(this, "Ya existe registro", Toast.LENGTH_SHORT).show();
                return;
            }
            if(tietmotivo.getText().toString().isEmpty()){
                tilmotivo.setError("Ingrese motivo");
                return;
            }
            //Ingresar Motivo
            Motivo obj = new Motivo(
                    spinnerplaca,
                    fechaIngreso,
                    tietmotivo.getText().toString()
            );
            String ms = dao.insertarMotivo(obj);
            Toast.makeText(this, ms, Toast.LENGTH_SHORT).show();
            limpiarCampos();

        } else {
            if (dao.existeMotivoParaFecha(spinnerplaca, fechaIngreso)) {
                Toast.makeText(this, "Ya existe registro", Toast.LENGTH_SHORT).show();
                return;
            }
                if (TextUtils.isEmpty(montoingreso) || Double.parseDouble(montoingreso) == 0) {
                    Toast.makeText(this, "Ingresa el monto", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Valida placa
                if (spnplacas.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Selecciona una placa", Toast.LENGTH_SHORT).show();
                    return;
                }
                //valida fecha

                if (dao.existeIngresoParaFecha(spinnerplaca, fechaIngreso)) {
                    //actualizar montoTotal y saldo de tabla Saldos
                    dao.actualizarMonto2(spinnerplaca,
                            Double.valueOf(montoingreso), mesIngreso, añoIngreso, fechaIngreso);
                    //actualizar monto de tabla Ingresos
                    dao.actualizarMontoIngresos(Double.valueOf(montoingreso), spinnerplaca, fechaIngreso);
                    Toast.makeText(this, "Se ha actualizado el monto", Toast.LENGTH_SHORT).show();
                    return;
                } //else{
                    Ingresos obj = new Ingresos(
                            spinnerplaca,
                            fechaIngreso,
                            Double.parseDouble(montoingreso)
                    );
                    String ms = dao.insertarIngresos(obj);

                    if (dao.existeMesAño(spinnerplaca, mesIngreso, añoIngreso)) {
                        //si existe MesAño ACTUALIZA
                        dao.actualizarMonto(spinnerplaca,
                                Double.parseDouble(montoingreso),
                                mesIngreso,
                                añoIngreso);
                    } else {
                        //sino CREA nuevo registro en TABLA SALDOS
                        dao.crearMonto(spinnerplaca, Double.valueOf(montoingreso), mesIngreso, añoIngreso);
                    }
                    //fin de actualizar monto
                    dao = null;
                    //Ventana Confirmacion
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Ingresos");
                    builder.setMessage(ms);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            limpiarCampos();
                        }
                    });
                    builder.create().show();
                //} //fin del sino del validar ingreso para fecha

            //}//fin else de si existemotivofecha
        }
    }
    private void limpiarCampos() {
        spnplacas.setSelection(0);
        edtfechaing.setText(obtenerFechaActual());
        edtmontoing.setText("0");
        edtmontoing.setEnabled(true);
        chktrabajo.setChecked(false);
        tietmotivo.setText("");
        tilmotivo.setVisibility(View.GONE);
    }
    private void llenarSpinnerPlacas(){
        GastosDAO gastosDAO = new GastosDAO(this);

        // Obtener la lista de placas
        List<String> placasList = gastosDAO.getAllPlacas();

        placasList.add(0, "Seleccionar Placa");

        // ArrayAdapter y establecerlo en el Spinner
        ArrayAdapter<String> placasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, placasList);
        placasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnplacas.setAdapter(placasAdapter);
    } //fin llenarSpinner

    //CALENDAR
    @Override
    public void onClick(View v) {
        if(v == btncalendar){
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            año = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    edtfechaing.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            }
            ,año,mes,dia);

            datePickerDialog.show();
        }
    } //fin date
    public String obtenerFechaActual() {
        // Obtener la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date fecha = new Date();
        return dateFormat.format(fecha);
    }

} //fin todoo