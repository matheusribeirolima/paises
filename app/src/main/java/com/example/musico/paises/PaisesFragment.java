package com.example.musico.paises;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PaisesFragment extends Fragment {

    private Context context;
    private ListView listView;
    private ListarAsync listarAsync;
    private PaisesArrayAdapter adapter;
    private View view;
    private FloatingActionButton atualizar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_paises, container, false);
        context = getActivity().getApplicationContext();//obtem o contexto para o fragment
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new PaisesArrayAdapter(context,new ArrayList<Pais>());//atribui o contexto e um array vazio para o adapter
        listView.setAdapter(adapter);//atribui o adapter a listview
        atualizar = (FloatingActionButton) view.findViewById(R.id.atualizar_ws);
        preencherLista();//preenche a listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//trata o click em um item da listView
                Intent intent = new Intent(getActivity(),DetalhesActivity.class);//chama outra activity
                Pais clickado = (Pais) parent.getItemAtPosition(position);//pega o país clickado
                Bundle bundle = new Bundle();//obtem e armazena num bundle os dados do país
                bundle.putInt("id",clickado.getId());
                if (clickado.getFlag() != null){//caso haja a bandeira nos dados obtidos do WS
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    clickado.getFlag().compress(Bitmap.CompressFormat.PNG, 100, stream);//obtem a bandeira e a insere no stream
                    byte[] byteArray = stream.toByteArray();//converte a imagem para um array de bytes
                    bundle.putByteArray("imagem",byteArray);
                }
                bundle.putString("nomecurto",clickado.getShortname());
                bundle.putString("nomelongo",clickado.getLongname());
                bundle.putString("callingCode",clickado.getCallingCode());
                intent.putExtras(bundle);//adiciona os dados obtidos do país e os envia para a outra activity
                getActivity().startActivity(intent);//inicia a activity
            }
        });

        atualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//trata o click no botão de atualizar
                if (listarAsync.getStatus() == AsyncTask.Status.FINISHED){//se a thread tiver terminado de executar
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Lista atualizada!", Toast.LENGTH_SHORT).show();
                } else {//se não tiver terminado sua execução
                    Toast.makeText(context, "Espere, download em andamento!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void preencherLista(){//faz a conexão com o WS
        try {
            URL url = new URL("http://sslapidev.mypush.com.br/world/countries/active");
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {//verifica a conectividade do dispositivo
                listarAsync = new ListarAsync(listView,view);//cria a asynctask
                listarAsync.execute(url);//executa a asynctask
            } else {
                Toast.makeText(context, "Sem conexão com a Internet!", Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            Toast.makeText(context, "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
