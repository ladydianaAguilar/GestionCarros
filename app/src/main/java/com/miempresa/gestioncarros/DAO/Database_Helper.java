package com.miempresa.gestioncarros.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database_Helper extends SQLiteOpenHelper {

    //definir variable para creacion de tabla
    String qcarros = "create table if not exists carros (placa text not null primary key, cuenta real not null)";
    String qarticulos = "create table if not exists articulos (id integer primary key autoincrement, articulo text not null, costo real not null)";

    String qsaldos = "create table if not exists saldos (placa text not null, mesreg integer not null, añoreg integer not null, montototal real not null, saldo real not null)";

    String qgastos = "create table if not exists gastos (idgasto integer not null primary key autoincrement, placa text not null, fechagasto text , saldogasto real , " +
            "articulo text, costo real)";
    String qingresos = "create table if not exists ingresos(placa text, fechaingreso text, monto real)";
    String qmotivo="create table if not exists motivo (placa text, fechamotivo text, motivo text)";

    //constructor
    public Database_Helper(@Nullable Context context) {
        super(context, "BDGESTIONCARROS.db", null, 1);
    }

    //metodos necesarios crea/actualiza
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(qcarros);
        db.execSQL("insert into carros (placa, cuenta) values ('A1E738', 200)");
        db.execSQL("insert into carros (placa, cuenta) values ('AAK145', 200)");
        db.execSQL("insert into carros (placa, cuenta) values ('AKZ618', 140)");
        //
        db.execSQL(qarticulos);
        db.execSQL("insert into articulos (id,articulo, costo) values (1,'Cambio de Aceite', 220)");
        //
        db.execSQL(qsaldos);//llenado por gastos e ingresos
        //
        db.execSQL(qgastos);//gastos resta a saldos
        //
        db.execSQL(qingresos); //ingresos suma a saldos
        //
        db.execSQL(qmotivo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
