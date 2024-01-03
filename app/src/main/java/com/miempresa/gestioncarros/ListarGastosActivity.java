package com.miempresa.gestioncarros;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.miempresa.gestioncarros.Adaptador.Adapter;
import com.miempresa.gestioncarros.DAO.GastosDAO;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListarGastosActivity extends AppCompatActivity {
    Button btnconsulta;
    Spinner spnplacacon, spnmescon, spnañocpn;
    ListView lvgasto;
    TextView tvtotal, tvingreso, tvneto, tvprueba;
    TableLayout tvresumen;
    LinearLayout lyborde;
    private Adapter adapter;
    String[] vMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Julio",
            "Junio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private int valorMes;

    void EnlazarSpinnerMeses(){
        // definimos un ArrayAdapter de String
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                vMeses
        );
        //  enlazar el adaptador con el spinner
        spnmescon.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_gastos);
        //
        spnplacacon = (Spinner) findViewById(R.id.spnPlacaConsulta);
        spnmescon = (Spinner) findViewById(R.id.spnMesConsulta);
        spnañocpn = (Spinner) findViewById(R.id.spnAñoConsulta);
        btnconsulta = (Button) findViewById(R.id.btnConsultar);
        tvtotal = (TextView) findViewById(R.id.tvTotal);
        tvingreso = (TextView) findViewById(R.id.tvIngreso);
        tvneto = (TextView) findViewById(R.id.tvNeto);
        tvprueba = (TextView) findViewById(R.id.tvprueba);
        lvgasto = (ListView) findViewById(R.id.lvGastos);
        tvresumen = (TableLayout) findViewById(R.id.tbResumen);
        lyborde = (LinearLayout) findViewById(R.id.layoutBorde);
        //
        llenarSpinnerPlacas();
        EnlazarSpinnerMeses();
        llenarSpinnerAños();

        // Obtener la fecha actual con formato "MM"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        Date fecha = new Date();
        String a = dateFormat.format(fecha);
        spnmescon.setSelection(Integer.parseInt(a)-1);
        //
        tvresumen.setVisibility(View.GONE);

        btnconsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llenarLista();

            }
        });
        spnmescon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    switch (pos) {
                        case 0:valorMes = 1;break;
                        case 1:valorMes = 2;break;
                        case 2:valorMes = 3;break;
                        case 3:valorMes = 4;break;
                        case 4:valorMes = 5;break;
                        case 5:valorMes = 6;break;
                        case 6:valorMes = 7;break;
                        case 7:valorMes = 8;break;
                        case 8:valorMes = 9;break;
                        case 9:valorMes = 10;break;
                        case 10:valorMes = 11;break;
                        case 11:valorMes = 12;break;
                    }
                }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //metodo llenar spninner PLacas
    private void llenarSpinnerPlacas() {
        GastosDAO gastosDAO = new GastosDAO(this);

        // Obtener la lista de placas
        List<String> placasList = gastosDAO.getAllPlacas();

        placasList.add(0, "Seleccionar Placa");

        // ArrayAdapter y establecerlo en el Spinner
        ArrayAdapter<String> placasAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, placasList);
        placasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnplacacon.setAdapter(placasAdapter);
    }
    private void llenarSpinnerAños() {
        GastosDAO gastosDAO = new GastosDAO(this);

        // Obtener la lista de placas
        List<String> añosList = gastosDAO.getAllAños();

        // ArrayAdapter y establecerlo en el Spinner
        ArrayAdapter<String> añosAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, añosList);
        añosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnañocpn.setAdapter(añosAdapter);
    }

    private void limpiar(){
        lvgasto.setAdapter(null);
        lyborde.setVisibility(View.GONE);
        tvresumen.setVisibility(View.GONE);
    }
    private void llenarLista() {
        // Validaciones
        if (spnplacacon.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona una placa", Toast.LENGTH_SHORT).show();
            limpiar();
        }else {
            String xplacac = spnplacacon.getSelectedItem().toString();
            //'valorMes' retorna numero de mes
            String xañoc = "";
            if (spnañocpn.getSelectedItem() != null) {
                xañoc = spnañocpn.getSelectedItem().toString();
            }

            GastosDAO dao = new GastosDAO(this);

            //Valida

            // Obtener la lista de articulos y costos
            adapter = new Adapter(this,
                    R.layout.fila_gasto,
                    dao.mostrarArtCosto(xplacac, valorMes, Integer.parseInt(xañoc))
            );
            Log.d("a:",String.valueOf(valorMes));
            if(!adapter.isEmpty()) {
                //tvprueba.setText(dao.queryc);
                lvgasto.setAdapter(adapter);
                calcular();
                lyborde.setVisibility(View.VISIBLE);
                tvresumen.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(this, "No existen registros", Toast.LENGTH_SHORT).show();
                limpiar();
            }
        }
    }
    private void calcular(){
        GastosDAO dao = new GastosDAO(this);

        String xplacac = spnplacacon.getSelectedItem().toString();
        String xañoc = spnañocpn.getSelectedItem().toString();

        //suma TOTAL de gastos
        String total = String.valueOf(dao.sumcosto(xplacac, valorMes, Integer.parseInt(xañoc)));
        tvtotal.setText(total);

        //INGRESO del Mes
        String ingreso = String.valueOf(dao.obtenerMontoTotal(xplacac, valorMes, Integer.parseInt(xañoc)));
        tvingreso.setText(ingreso);

        //NETO del Mes
        double neto = Double.parseDouble(ingreso) - Double.parseDouble(total);
        // si neto es negativo multi por -1
        if (neto < 0) {
            neto *= -1;
        }
        tvneto.setText(String.valueOf(neto));
    }
}