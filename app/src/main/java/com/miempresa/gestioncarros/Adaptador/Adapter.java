package com.miempresa.gestioncarros.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miempresa.gestioncarros.Entidades.Gastos;
import com.miempresa.gestioncarros.R;

import java.util.List;

public class Adapter extends ArrayAdapter<Gastos> {

    Context micontexto;
    int milayout;
    public List<Gastos> milista;
    public Adapter(@NonNull Context context,
                   int resource,
                   @NonNull List<Gastos> objects){
        super(context, resource, objects);  // Pasa la lista de objetos al constructor de la superclase
        micontexto = context;
        milayout = resource;
        milista = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null) {
            //configura la fila para el ListView (listadeGastos)
            LayoutInflater myinflate = LayoutInflater.from(micontexto);
            convertView = myinflate.inflate(milayout, null);
        }
        //accede a los controles de la fila
        TextView tvarticulo = convertView.findViewById(R.id.tvArticulo);
        TextView tvcosto = convertView.findViewById(R.id.tvCosto);

        //enviar datos del articulo y costo a los controles de la fila
        Gastos obj = milista.get(position);
        if(obj !=null) {
            tvarticulo.setText(obj.getArticulo());
            tvcosto.setText(String.valueOf(obj.getCosto()));
        }
        return convertView;
    }
}
