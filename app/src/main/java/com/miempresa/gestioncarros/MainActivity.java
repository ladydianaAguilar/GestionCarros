package com.miempresa.gestioncarros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnnuevoingreso, btnnuevogasto, btnconsultagasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        btnnuevoingreso = (Button) findViewById(R.id.btnNuevoIngreso);
        btnnuevogasto = (Button) findViewById(R.id.btnNuevoGasto);
        btnconsultagasto = (Button) findViewById(R.id.btnConsultarGasto);
        //
        btnnuevoingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),IngresosActivity.class);
                startActivity(i);
            }
        });
        //
        btnnuevogasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent g = new Intent(getApplicationContext(), GastosActivity.class);
                startActivity(g);
            }
        });
        //
        btnconsultagasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(getApplicationContext(), ListarGastosActivity.class);
                startActivity(c);
            }
        });
    }

}