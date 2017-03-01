package com.example.musico.paises;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PaisesArrayAdapter extends ArrayAdapter{//array adapter customizado
    private Context context;
    private ArrayList<Pais> objects;

    public PaisesArrayAdapter(Context context, ArrayList<Pais> objects) {
        super(context, 0, R.id.nomecurto, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//para cada item no array
        Pais pais = objects.get(position);//Pega o item de acordo com a posição
        if (convertView == null){
            //infla o layout para podermos preencher os dados
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview, parent, false);
        }
        //atravez do layout pego pelo LayoutInflater, pega-se cada id relacionado ao item e definimos as informações.
        ((TextView) convertView.findViewById(R.id.nomecurto)).setText(pais.getShortname());
        ((ImageView) convertView.findViewById(R.id.bandeira)).setImageBitmap(pais.getFlag());
        ImageView salvo = (ImageView) convertView.findViewById(R.id.salvo);
        PaisDAO dao = new PaisDAO(context);
        if (dao.verificar(pais.getId()) != null){//verifica se o país já está no DB e seta o ícone do item
            salvo.setVisibility(View.VISIBLE);
        } else {
            salvo.setVisibility(View.GONE);
        }
        dao.close();
        return convertView;
    }
}
