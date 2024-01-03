package com.miempresa.gestioncarros;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.miempresa.gestioncarros.DAO.GastosDAO;
import com.miempresa.gestioncarros.DAO.IngresosDAO;
import com.miempresa.gestioncarros.Entidades.Gastos;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;


public class GastosActivity extends AppCompatActivity implements View.OnClickListener {
    List<String> articulosList;
    Spinner spnplacasgasto, spnarticulo;
    EditText edtfechagasto, edtcosto, edtnuevoarti ;
    TextView tvsaldo, tvprueba;
    Button btncalgasto, btnnuevogasto, btnguardargasto;

    private int dia, mes, año; //calendar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);
        //
        spnplacasgasto = (Spinner) findViewById(R.id.spnPlacasGasto);
        edtfechagasto = (EditText) findViewById(R.id.edtFechagasto);
        btncalgasto = (Button) findViewById(R.id.btnCalgasto);
        tvsaldo = (TextView) findViewById(R.id.tvSaldo);
        tvprueba = (TextView) findViewById(R.id.tvpru);
        spnarticulo = (Spinner) findViewById(R.id.spnarticulo);
        edtcosto = (EditText) findViewById(R.id.edtCosto);
        btnguardargasto = (Button) findViewById(R.id.btnGuardarGasto);
        btnnuevogasto = (Button) findViewById(R.id.btnNuevog);
        edtnuevoarti= (EditText) findViewById(R.id.edtOtroArti);
        //
        llenarSpinnerPlacas();
        llenarSpinnerArticulos();
        edtfechagasto.setText(obtenerFechaActual());
        //
        spnplacasgasto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                GastosDAO dao = new GastosDAO(getApplicationContext());
                // Extrae mes y año de la fechagasto
                String fechaGasto = edtfechagasto.getText().toString();
                String[] partesFecha = fechaGasto.split("/");

                int mesGasto = Integer.parseInt(partesFecha[1]);
                int añoGasto = Integer.parseInt(partesFecha[2]);
                //POS inicia en 0
                switch (pos){
                    case 1: tvsaldo.setText(String.valueOf(dao.obtenerSaldo("A1E738",mesGasto,añoGasto)));break;
                    case 2: tvsaldo.setText(String.valueOf(dao.obtenerSaldo("AAK145",mesGasto,añoGasto)));break;
                    case 3: tvsaldo.setText(String.valueOf(dao.obtenerSaldo("AKZ618",mesGasto,añoGasto)));break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnarticulo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                GastosDAO dao = new GastosDAO(getApplicationContext());
                //

                if(pos == articulosList.size()-1){
                    edtnuevoarti.setVisibility(View.VISIBLE);
                    //
                    edtcosto.setText("0");
                }else{
                    edtnuevoarti.setVisibility(View.GONE);
                    String articuloSelec = articulosList.get(pos);
                    //obtiene costo segun articulo
                    edtcosto.setText(String.valueOf(dao.obtenerCosto(articuloSelec)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btncalgasto.setOnClickListener(this);
        btnguardargasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GastosDAO dao = new GastosDAO(getApplicationContext());
                grabarGasto();

            }
        });
        btnnuevogasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });
    }


    private void limpiarCampos() {
        edtfechagasto.setText(obtenerFechaActual());
        edtcosto.setText("0");
        edtnuevoarti.setText("");
        tvsaldo.setText("0");
        spnplacasgasto.setSelection(0);
        spnarticulo.setSelection(0);
        spnplacasgasto.requestFocus();
    }

    //metodo llenar spninner PLacas
    private void llenarSpinnerPlacas() {
        GastosDAO gastosDAO = new GastosDAO(this);

        // Obtener la lista de placas
        List<String> placasList = gastosDAO.getAllPlacas();

        placasList.add(0, "Seleccionar Placa");

        // ArrayAdapter y establecerlo en el Spinner
        ArrayAdapter<String> placasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, placasList);
        placasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnplacasgasto.setAdapter(placasAdapter);
    }

    private void llenarSpinnerArticulos() {
        GastosDAO gastosDAO = new GastosDAO(this);

        // Obtener la lista de articulos
        articulosList = gastosDAO.getAllArticulos();

        articulosList.add(0, "Seleccionar Articulo");
        articulosList.add("Otros");


        // ArrayAdapter y establecerlo en el Spinner
            ArrayAdapter<String> articulosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, articulosList);
            articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnarticulo.setAdapter(articulosAdapter);
    }

    private void grabarGasto() {
        GastosDAO dao = new GastosDAO(this);

        // Extrae mes y año de la fechagasto
        String fechaGasto = edtfechagasto.getText().toString();
        String[] partesFecha = fechaGasto.split("/");

        int mesGasto = Integer.parseInt(partesFecha[1]);
        int añoGasto = Integer.parseInt(partesFecha[2]);
        String nuevoArticulo;
        String nuevoCosto = edtcosto.getText().toString().trim();

        // Validaciones
        if (spnplacasgasto.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona una placa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(edtfechagasto.getText().toString())) {
            Toast.makeText(this, "Ingresa la fecha del gasto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spnarticulo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona un artículo", Toast.LENGTH_SHORT).show();
            return;
        }

        //VALIDAR SI GASTO INGRESADO DE MES Y AÑO EXISTE EN tabla SALDOS, SINO NO PERMITIR REGISTRAR gasto.
        IngresosDAO ingresodao = new IngresosDAO(this);
        if(ingresodao.existeMesAño(spnplacasgasto.getSelectedItem().toString(),mesGasto,añoGasto)) {

        // si "Otros" es seleccionado en spn
        if (spnarticulo.getSelectedItemPosition() == articulosList.size() - 1) {
            nuevoArticulo = edtnuevoarti.getText().toString().trim();
            if (!nuevoArticulo.isEmpty() && !nuevoCosto.isEmpty()) {
                if(!articulosList.contains(nuevoArticulo)) {
                    dao.agregarNuevoArticulo(nuevoArticulo, Double.parseDouble(nuevoCosto));
                    llenarSpinnerArticulos();
                }else{
                    Toast.makeText(this, "¡El artículo ya existe!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this, "Ingrese un articulo válido", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            nuevoArticulo = spnarticulo.getSelectedItem().toString();
        }

        if (TextUtils.isEmpty(nuevoCosto) || nuevoCosto.equals("0")) {
            Toast.makeText(this, "Ingresa un costo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String placaSeleccionada = spnplacasgasto.getSelectedItem().toString();
        double saldo = dao.obtenerSaldo(placaSeleccionada, mesGasto, añoGasto);
        double costoIngresado = Double.parseDouble(edtcosto.getText().toString());

        if (saldo <= 0 || costoIngresado > saldo) {
            Toast.makeText(this, "Saldo Insuficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        Gastos obj = new Gastos(
                    spnplacasgasto.getSelectedItem().toString(),
                    edtfechagasto.getText().toString(),
                    Double.parseDouble(tvsaldo.getText().toString()),
                    nuevoArticulo,
                    Double.parseDouble(edtcosto.getText().toString())
            );
            String mensaje = dao.insertaGastos(obj);

            // Llama al método actualizarSaldo después de insertar el gasto
            dao.actualizarSaldo(spnplacasgasto.getSelectedItem().toString(),
                    Double.parseDouble(edtcosto.getText().toString()),
                    mesGasto,
                    añoGasto);
            //
            dao = null;
            //
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Gastos");
            builder.setMessage(mensaje);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    limpiarCampos();

                }
            });
            builder.create().show();
        }else{
            Toast.makeText(this, "Aún no existen registros de ingresos para insertar el gasto", Toast.LENGTH_SHORT).show();
        }
    }

    //Calendario
    @Override
    public void onClick(View v) {
            if(v == btncalgasto){
                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                año = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtfechagasto.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }
                        ,año,mes,dia);

                datePickerDialog.show();
            }
    }
    public String obtenerFechaActual() {
        // Obtener la fecha actual con formato "YYYY/MM/DD"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date fecha = new Date();
        return dateFormat.format(fecha);
    }
}