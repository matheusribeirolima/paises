package com.example.musico.paises;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class VisitadosFragment extends Fragment {
    private Context context;
    private ListView listView;
    private PaisesArrayAdapter adapter;
    private PaisDAO pais;
    private FloatingActionButton atualizar;
    private ArrayList<Pais> excluir = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitados, container, false);
        context = getActivity().getApplicationContext();//obtem o contexto para o fragment
        listView = (ListView) view.findViewById(R.id.listVisitados);
        adapter = new PaisesArrayAdapter(context,new ArrayList<Pais>());//atribui o contexto e um array vazio para o adapter
        listView.setAdapter(adapter);//atribui o adapter a listView
        pais = new PaisDAO(context);//inicia a conexão com o DB
        adapter.addAll(pais.listar());//preenche o adapter com os países do DB
        atualizar = (FloatingActionButton) view.findViewById(R.id.atualizar);
        atualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//trata o evento de click no botão de atualizar
                adapter.clear();//limpa o adapter
                adapter.addAll(pais.listar());//o preenche novamente
                Toast.makeText(context, "Lista atualizada!", Toast.LENGTH_SHORT).show();
            }
        });
        pais.close();//fecha a conexão

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//trata o click em um item da listView
                Intent intent = new Intent(getActivity(),DetalhesActivity.class);//chama outra activity
                Pais clickado = (Pais)parent.getItemAtPosition(position);//pega o país clickado
                Bundle bundle = new Bundle();//obtem e armazena num bundle os dados do país
                bundle.putInt("id",clickado.getId());
                if (clickado.getFlag() != null){//caso haja a bandeira nos dados obtidos do WS
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    clickado.getFlag().compress(Bitmap.CompressFormat.PNG, 100, stream);//obtem a bandeira e a insere no stream
                    byte[] byteArray = stream.toByteArray();//converte a imagem para um array de bytes
                    bundle.putByteArray("imagem",byteArray);//inicia a activity
                }
                bundle.putString("nomecurto",clickado.getShortname());
                bundle.putString("nomelongo",clickado.getLongname());
                bundle.putString("callingCode",clickado.getCallingCode());
                intent.putExtras(bundle);//adiciona os dados obtidos do país e os envia para a outra activity
                getActivity().startActivity(intent);
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);//modifica a listView para ser de múltipla escolha
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {// Capture ListView item click
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {//quando um item é selecionado ou não
                if (checked){
                    excluir.add((Pais)listView.getItemAtPosition(position));//adiciona o pais clicado ao array pra excluir
                    if (excluir.size()>1){
                        mode.setTitle(excluir.size()+" países selecionados");
                    } else {
                        mode.setTitle("1 país selecionado");
                    }
                } else {//se o item foi desmarcado
                    excluir.remove(listView.getItemAtPosition(position));//remove da lista de exclusão
                    mode.setTitle(excluir.size()+" países selecionados");
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {//ao iniciar a seleção dos países
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_main, menu);//infla o menu de exclusão
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {//quando seleciona uma ação aos itens clickados
                switch (item.getItemId()){
                    case R.id.delete_id://para caso o item clicado for a lixeira
                        PaisDAO dao = new PaisDAO(context);
                        for(Pais pais : excluir){//percorre os países a serem excluidos
                            dao.deletar(pais);//apaga do banco
                            adapter.remove(pais);//remove da lista
                            adapter.notifyDataSetChanged();//notifica que a lista foi alterada
                        }
                        Toast.makeText(context, excluir.size()+" países removidos", Toast.LENGTH_SHORT).show();
                        excluir.clear();//limpa o array dos países a serem excluidos
                        dao.close();//fecha a conexão com o DB
                        mode.finish();//finaliza o menu de exclusão
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                excluir.clear();
            }
        });

        return view;
    }
}
