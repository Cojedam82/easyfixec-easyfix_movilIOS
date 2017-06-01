package com.easyfixapp.easyfix.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Categoria;
import com.easyfixapp.easyfix.models.Servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaFragment extends Fragment {

    private ListView listView;
    private AgendaAdapter adapter;
    private ArrayList<Servicio> servicios;

    public AgendaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.agenda_fragment, container, false);
        listView = (ListView) rootview.findViewById(R.id.agenda_lista);

        servicios = new ArrayList<>();
        adapter = new AgendaAdapter(getActivity().getApplicationContext(),servicios);
        listView.setAdapter(adapter);

        for (int i=0;i<5;i++){
            Servicio s = new Servicio();
            s.setNombre("Elvis Tomalá");
            Categoria c = new Categoria();
            c.setNombre("Electricidad - Lavadora");
            s.setCategoria(c);
            s.setFecha("Jueves 14");
            s.setHora("15:00");
            servicios.add(s);
        }
        adapter.notifyDataSetChanged();



        return rootview;
    }

    private class AgendaAdapter extends ArrayAdapter<Servicio>{
        private ArrayList<Servicio> servicios;
        private Context context;

        public AgendaAdapter(Context context, ArrayList<Servicio> objects) {
            super(context, 0,objects);
            this.servicios=objects;
            this.context=context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //super.getView(position, convertView, parent);

            convertView = LayoutInflater.from(context).inflate(R.layout.item_agenda,parent,false);

            Servicio servicio = servicios.get(position);

            ImageView foto = (ImageView) convertView.findViewById(R.id.item_agenda_foto);
            TextView nombre = (TextView)convertView.findViewById(R.id.item_agenda_nombre);
            TextView categoria = (TextView)convertView.findViewById(R.id.item_agenda_categoria);
            TextView fecha = (TextView)convertView.findViewById(R.id.item_agenda_fecha);
            TextView hora = (TextView) convertView.findViewById(R.id.item_agenda_hora);

            nombre.setText(servicio.getNombre());
            categoria.setText(servicio.getCategoria().getNombre());
            fecha.setText(servicio.getFecha());
            hora.setText(servicio.getHora());

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            categoria.setTextColor(color);

            return convertView;
        }
    }
}
