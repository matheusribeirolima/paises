package com.example.musico.paises;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListarAsync extends AsyncTask<URL, Pais, String> {
    private PaisesArrayAdapter adapter;
    private Context context;
    private View view;
    private ProgressBar progressBar;
    private int count;

    public ListarAsync(ListView listView, View view) {//recebe a listview que vai preencher e a view da activity que o chamou
        this.adapter = (PaisesArrayAdapter) listView.getAdapter();
        this.context = view.getContext();
        this.view = view;
    }

    @Override
    protected void onPreExecute() {//antes de executar a thread configura e exibe a progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setMax(250);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(URL... urls) {//executando a thread, ela recebe uma URL como parâmetro
        InputStream is = null;
        HttpURLConnection conexao;
        try{
            conexao = (HttpURLConnection) urls[0].openConnection();//configura os parametros do httpurlconnection para conexão
            conexao.setReadTimeout(10000);
            conexao.setConnectTimeout(15000);
            conexao.setRequestMethod("GET");
            conexao.setDoInput(true);
            conexao.connect();//abre a conexão
            if (conexao.getResponseCode() == HttpURLConnection.HTTP_OK){//verifica se a conexão deu certo
                try{
                    is = conexao.getInputStream();
                    JSONArray arrayJson = new JSONArray(converte(is));//array de objetos JSON para recebimento da resposta convertida do WS
                    for(int i=0;i<arrayJson.length();i++){
                        Pais pais = new Pais();
                        JSONObject objectArray = arrayJson.getJSONObject(i);//cria um objeto json para cada termo obtido no WS
                        pais.setId(Integer.parseInt(objectArray.getString("id")));
                        pais.setShortname(objectArray.getString("shortname"));
                        pais.setLongname(objectArray.getString("longname"));
                        pais.setCallingCode(objectArray.getString("callingCode"));
                        URL url = new URL("http://sslapidev.mypush.com.br/world/countries/"+pais.getId()+"/flag");
                        pais.setFlag(BitmapFactory.decodeStream(url.openStream()));//decodificação dos dados do WS para bitmap
                        publishProgress(pais);//Diz a thread pra executar conforme os dados chegam
                    }
                } catch(JSONException | IOException e){
                    e.printStackTrace();
                } finally {//sempre fecha a conexão
                    if (is != null) {
                        try {
                            conexao.disconnect();
                            is.close();
                            return "Todos os dados foram recuperados com sucesso!";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Sem acesso ao Web Service!";
    }

    public String converte(InputStream is) throws IOException {//lê toda a resposta do WS e a converte em String
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    protected void onProgressUpdate(Pais... values) {//executa conforme for sendo atualizada
        adapter.add(values[0]);//adiciona o país passado ao array adapter passado por parâmetro
        count++;
        progressBar.setProgress(count);//atualiza a barra de progresso
    }

    @Override
    protected void onPostExecute(String s) {//quando termina a execução da thread
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);//esconde a barra de progresso
    }
}
