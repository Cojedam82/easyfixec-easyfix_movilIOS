package com.easyfixapp.easyfix.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Categoria;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusquedaFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Categoria> categorias;
    private RecyclerAdapter adapter;


    public BusquedaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.busqueda_fragment, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.busqueda_recyclerview);
        categorias = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerAdapter(getActivity().getApplication(),categorias);
        recyclerView.setAdapter(adapter);


        for (int i=0;i<10;i++){
            Categoria categoria = new Categoria();
            categoria.setImagen("https://www.shareicon.net/download/2015/10/18/657833_penguin_512x512.png");
            categoria.setNombre("Computación");
            categorias.add(categoria);
        }

        adapter.notifyDataSetChanged();




        return rootview;
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<Holder>{

        private ArrayList<Categoria> categorias;
        private Context context;
        public RecyclerAdapter(Context context,ArrayList<Categoria> categorias) {
            this.categorias = categorias;
            this.context = context;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_busqueda,parent,false);
            return new Holder(inflatedView);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Categoria categoria = categorias.get(position);

            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                {
                    exception.printStackTrace();
                }
            });

            builder.build()
                    .load(categoria.getImagen())
                    .placeholder(android.R.drawable.sym_call_incoming)
                    .error(android.R.drawable.sym_call_outgoing)
                    .into(holder.button);
            holder.nombre.setText(categoria.getNombre());

        }

        @Override
        public int getItemCount() {
            return categorias.size();
        }


    }

    public  static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView button;
        private TextView nombre;

        public Holder(View itemView) {
            super(itemView);
            button = (ImageView) itemView.findViewById(R.id.item_busqueda_button);
            nombre = (TextView) itemView.findViewById(R.id.item_busqueda_texto);
        }

        @Override
        public void onClick(View v) {
            Log.d("click",v.toString());
        }
    }

}
