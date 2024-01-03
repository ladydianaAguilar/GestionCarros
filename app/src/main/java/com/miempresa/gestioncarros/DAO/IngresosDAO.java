package com.miempresa.gestioncarros.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.miempresa.gestioncarros.Entidades.Ingresos;
import com.miempresa.gestioncarros.Entidades.Motivo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IngresosDAO {
    SQLiteDatabase db;
    Database_Helper helper;

    public IngresosDAO(Context contexto){
        helper = new Database_Helper(contexto);
    }

    // Método insertar Ingresos
    public String insertarIngresos(Ingresos obj){
        db = helper.getWritableDatabase();
        ContentValues g = new ContentValues();
        g.put("placa", obj.getPlaca());

        // Formatear la fecha antes de insertarla
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputFormat.parse(obj.getFechaingreso());
            // Ajustar el formato de salida al utilizar outputFormat
            String fechaFormateada = outputFormat.format(date);
            g.put("fechaingreso", fechaFormateada);
        } catch (ParseException e) {
            e.printStackTrace();
            // Maneja la excepción de formato de fecha aquí si es necesario
        }

        g.put("monto", obj.getMonto());
        db.insert("ingresos", null, g);
        db.close();
        return "Ingreso registrado exitosamente";
    }

    public String insertarMotivo(Motivo obj){
        db = helper.getWritableDatabase();
        ContentValues m = new ContentValues();
        m.put("placa", obj.getPlaca());

        // Formatear la fecha antes de insertarla
        SimpleDateFormat inputFormatMotivo = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dateMotivo = inputFormatMotivo.parse(obj.getFechamotivo());
            String fechaMotivoFormateada = outputFormat.format(dateMotivo);
            m.put("fechamotivo", fechaMotivoFormateada);
        } catch (ParseException e) {
            e.printStackTrace();
            // Maneja la excepción de formato de fecha aquí si es necesario
        }

        m.put("motivo", obj.getMotivo());
        db.insert("motivo", null, m);
        db.close();
        return "Motivo registrado exitosamente";
    }

    // verifica fecha en tabla ingresos
    public boolean existeIngresoParaFecha(String xplaca, String xfecha) {
        db = helper.getReadableDatabase();

        String[] columns = {"placa"};
        String selection = "placa = ? AND fechaingreso = ?";
        String[] selectionArgs = {xplaca, xfecha};

        Cursor cursor = db.query("ingresos", columns, selection, selectionArgs, null, null, null);

        boolean existeIngreso = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return existeIngreso;
    }

    // verifica fecha en tabla motivo
    public boolean existeMotivoParaFecha(String xplaca, String xfecha) {
        db = helper.getReadableDatabase();

        String[] columns = {"placa"};
        String selection = "placa = ? AND fechamotivo = ?";
        String[] selectionArgs = {xplaca, xfecha};

        Cursor cursor = db.query("motivo", columns, selection, selectionArgs, null, null, null);

        boolean existeMotivo = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return existeMotivo;
    }

    // también se utiliza en GastosActivity para validar el registro del Gasto
    public boolean existeMesAño(String xplaca, Integer xmes, Integer xaño){
        db = helper.getReadableDatabase();
        String[] columns = {"placa"};
        String selection = "placa = ? and mesreg = ? and añoreg = ?";
        String[] selectionArgs = {xplaca, String.valueOf(xmes), String.valueOf(xaño)};
        Cursor cursor = db.query("saldos", columns, selection, selectionArgs, null, null, null);
        boolean existeMesAño = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return existeMesAño;
    }

    // Llamar getAllPlacas de Gastos DAO

    // Obtener Cuenta según placa seleccionada
    public int obtenerMonto(String xplaca){
        db = helper.getReadableDatabase();
        String query = "select cuenta from carros WHERE placa ='"  + xplaca + "'";

        Cursor c1 = db.rawQuery(query, null);
        int montoEncontrado = 0;
        if (c1.moveToFirst()) {
            int col = c1.getColumnIndex("cuenta");
            montoEncontrado = c1.getInt(col);
        }
        c1.close();
        db.close();
        return montoEncontrado;
    }

    public void actualizarMontoIngresos(Double xmontoNuevo, String xplaca, String xfecha){
        db = helper.getWritableDatabase();
        // monto nuevo va a ser lo insertado en edtMonto
        String query = "update ingresos set monto=" + xmontoNuevo + " where placa='" + xplaca + "'AND fechaingreso='" + xfecha +"'";
        db.execSQL(query);
        db.close();
    }

    public void actualizarMonto2(String xplaca, Double xmonto, Integer xmes, Integer xaño, String xfecha){
        db = helper.getWritableDatabase();
        Double montoviejo = obtenerMontoViejo(xplaca, xfecha, db);
        Double diferencia = xmonto - montoviejo;
        String query = "UPDATE saldos SET montototal = montototal + " + diferencia + ", saldo = saldo +" + diferencia + " WHERE (placa = '" + xplaca + "' AND mesreg = " + xmes + " AND añoreg = " + xaño + ")";
        db.execSQL(query);
        db.close();
    }

    private Double obtenerMontoViejo(String xplaca, String xfecha, SQLiteDatabase db){
        Double montoviejo = 0.0;
        String query = "SELECT monto FROM ingresos WHERE placa='" + xplaca + "' AND fechaingreso='" + xfecha + "'";
        Cursor c1 = db.rawQuery(query, null);

        if(c1.moveToFirst()){
            int col = c1.getColumnIndex("monto");
            montoviejo = c1.getDouble(col);
        }
        c1.close();
        // db.close();
        return montoviejo;
    }

    public void actualizarMonto(String xplaca, Double xmonto, Integer xmes, Integer xaño){
        db = helper.getWritableDatabase();
        String query = "UPDATE saldos SET montototal = montototal + " + xmonto + ", saldo = saldo +" + xmonto + "  WHERE (placa = '" + xplaca + "' AND mesreg = " + xmes + " AND añoreg = " + xaño + ")";
        db.execSQL(query);
        db.close();
    }

    // Si no existe el monto con el mes y año, CREARLO en saldos:
    public void crearMonto(String xplaca, Double xmonto, Integer xmes, Integer xaño){
        db = helper.getWritableDatabase();
        String query = "INSERT INTO saldos(placa, mesreg, añoreg, montototal, saldo) VALUES ('" + xplaca + "'," + String.format("%02d", xmes) + ", " + xaño + ", " + xmonto + ", " + xmonto + ")";
        db.execSQL(query);
        db.close();
    }
}


