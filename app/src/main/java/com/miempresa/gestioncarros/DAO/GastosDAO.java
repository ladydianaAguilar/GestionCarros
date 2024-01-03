package com.miempresa.gestioncarros.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.miempresa.gestioncarros.Entidades.Gastos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GastosDAO {

    //variables globales
    SQLiteDatabase db;
    Database_Helper helper;
    public String queryc;

    public GastosDAO(Context contexto) {
        helper = new Database_Helper(contexto);
    }

    //método inserta en tabla
    public String insertaGastos(Gastos obj) {
        db = helper.getWritableDatabase();
        ContentValues g = new ContentValues();
        g.put("placa", obj.getPlaca());

        // Formatear la fecha antes de insertarla
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputFormat.parse(obj.getFecha());

            // Ajustar el formato de salida al utilizar outputFormat
            String fechaFormateada = outputFormat.format(date);

            g.put("fechagasto", fechaFormateada);
        } catch (ParseException e) {
            e.printStackTrace();
            // Maneja la excepción de formato de fecha aquí si es necesario
        }

        g.put("saldogasto", obj.getSaldo());
        g.put("articulo", obj.getArticulo());
        g.put("costo", obj.getCosto());

        db.insert("gastos", null, g);
        db.close();
        return "Gasto registrado exitosamente";
    }


    // Método para obtener placas
    public List<String> getAllPlacas() {
        List<String> placasList = new ArrayList<>();
        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT placa FROM carros", null);
        int columnIndex = cursor.getColumnIndex("placa");

        if (columnIndex >= 0 && cursor.moveToFirst()) {
            do {
                String placa = cursor.getString(columnIndex);
                placasList.add(placa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return placasList;
    }

    public List<Gastos> mostrarArtCosto(String xplaca, int xmes, int xaño) {
        List<Gastos> listGastos = new ArrayList<>();

        queryc = "SELECT articulo, costo FROM gastos WHERE placa='" + xplaca + "' and substr(fechagasto, 4, 2) ='" + String.format("%02d", xmes) + "' and substr(fechagasto, 7, 4) = '" + xaño + "' ";
        System.out.println("Consulta SQL: " + queryc);

        db = helper.getReadableDatabase();
        Cursor c1 = db.rawQuery(queryc, null);

        int rowCount = c1.getCount();
        System.out.println("Número de filas devueltas: " + rowCount);

        if (rowCount > 0) {
            while (c1.moveToNext()) {
                Gastos gasto = new Gastos();
                gasto.setArticulo(c1.getString(0));
                gasto.setCosto(c1.getDouble(1));

                System.out.println("Articulo: " + gasto.getArticulo() + ", Costo: " + gasto.getCosto());

                listGastos.add(gasto);
            }
        } else {
            System.out.println("No se encontraron resultados.");
        }

        c1.close();
        db.close();
        return listGastos;
    }

    public List<String> getAllArticulos() {
        List<String> articulosList = new ArrayList<>();
        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT articulo FROM articulos ORDER BY id", null);
        int columnIndex = cursor.getColumnIndex("articulo");

        if (columnIndex >= 0 && cursor.moveToFirst()) {
            do {
                String articulo = cursor.getString(columnIndex);
                articulosList.add(articulo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return articulosList;
    }

    // Método para obtener meses
    public List<String> getAllMeses() {
        List<String> mesesList = new ArrayList<>();
        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT mesreg FROM saldos", null);
        int columnIndex = cursor.getColumnIndex("mesreg");

        if (columnIndex >= 0 && cursor.moveToFirst()) {
            do {
                String mes = cursor.getString(columnIndex);
                mesesList.add(mes);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return mesesList;
    }

    // Método para obtener años sin repetir DISTINCT
    public List<String> getAllAños() {
        List<String> añosList = new ArrayList<>();
        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT añoreg FROM saldos", null);
        int columnIndex = cursor.getColumnIndex("añoreg");

        if (columnIndex >= 0 && cursor.moveToFirst()) {
            do {
                String año = cursor.getString(columnIndex);
                añosList.add(año);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return añosList;
    }

   //Agrega nuevo articulo
   public void agregarNuevoArticulo(String nvoarti, double nvocosto){
       db = helper.getWritableDatabase();
       ContentValues values = new ContentValues();
       values.put("articulo", nvoarti);
       values.put("costo", nvocosto );
       //
       db.insert("articulos", null, values);
       db.close();
   }

    // obtiene saldo segun placa seleccionada
    public double obtenerSaldo(String xplaca, Integer xmes, Integer xaño){
        db = helper.getReadableDatabase();
        //
        String query = "select saldo from saldos WHERE placa ='"+xplaca+"' AND mesreg= "+String.format("%02d", xmes)+" AND añoreg="+xaño+"";
        Cursor c1 = db.rawQuery(query, null);
        double saldoEncontrado = 0.0;
        if (c1.moveToFirst()) {
            int col = c1.getColumnIndex("saldo");
            saldoEncontrado = c1.getDouble(col);
        }
        //
        c1.close();
        db.close();


        return saldoEncontrado;
    }

    //MODIFICAAAAAR SENTENCIA - debe restar en saldo y montototal
    //no actualiza porque mesreg esta como = 1, 2 ....y al comparar con mes de Gastos es 01, 02
    public void actualizarSaldo(String xplaca, Double xcosto, Integer xmes, Integer xaño){
        db = helper.getWritableDatabase();
        String query = "UPDATE saldos SET saldo = saldo - " + xcosto + " WHERE (placa = '" + xplaca + "' AND mesreg = " + String.format("%02d", xmes) + " AND añoreg = " + xaño + ")";
        db.execSQL(query);
        db.close();
    }

    public double obtenerCosto(String xarti){
        db = helper.getReadableDatabase();
        String query = "select costo from articulos where articulo='"+ xarti+"'";

        Cursor c1 = db.rawQuery(query, null);
        double costoEncontrado = 0.0;
        if (c1.moveToFirst()) {
            int col = c1.getColumnIndex("costo");
            costoEncontrado = c1.getDouble(col);
        }
        //
        c1.close();
        db.close();
        return costoEncontrado;
    }

    public double sumcosto(String xplaca, int xmes, int xaño){
        db = helper.getReadableDatabase();

        String query = "select sum(costo) as total_costo from gastos where placa='" + xplaca + "' and substr(fechagasto, 4, 2) ='" + String.format("%02d", xmes) + "' and substr(fechagasto, 7, 4) = '" + xaño + "' ";
        Cursor c1 = db.rawQuery(query, null);

        double sumcosto = 0.0;
        if (c1.moveToFirst()) {
            int col = c1.getColumnIndex("total_costo");
            sumcosto = c1.getDouble(col);
        }
        //
        c1.close();
        db.close();
        return sumcosto;
    }

    public double obtenerMontoTotal(String xplaca, Integer xmes, Integer xaño){
        db = helper.getReadableDatabase();
        String query = "select montototal from saldos WHERE placa ='"  + xplaca + "'AND mesreg = " + xmes + " AND añoreg = " + xaño + "";

        Cursor c1 = db.rawQuery(query, null);
        double montoEncontrado = 0.0;
        if (c1.moveToFirst()) {
            int col = c1.getColumnIndex("montototal");
            montoEncontrado = c1.getDouble(col);
        }
        //
        c1.close();
        db.close();
        return montoEncontrado;
    }


}//fin
