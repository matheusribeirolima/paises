package com.example.musico.paises;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class PaisDAO extends SQLiteOpenHelper {
    //Constantes para auxilio no controle de versões
    private static final int VERSAO = 1;
    private static final String TABELA = "Pais";
    private static final String DATABASE = "Paises";
    //Constante para log no logcat
    private static final String TAG = "CADASTRO_PAIS";
    private ListView listView;
    private Context context;

    public PaisDAO(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    public PaisDAO(Context context, ListView listView) {
        super(context, DATABASE, null, VERSAO);
        this.context = context;
        this.listView = listView;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Definição do comando DDL a ser executado
        String ddl = "CREATE TABLE " + TABELA + " (" + "id INTEGER PRIMARY KEY, " + "shortname TEXT, longname TEXT, callingCode TEXT, flag BLOB, data TEXT)";
        //Execução do comando no SQLite
        db.execSQL(ddl);
    }

    //Método responsável pela atualização das estruturas das tabelas
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Definição do comando para destruir a tabela País
        String sql = "DROP TABLE IF EXISTS " + "TABELA";
        //Execução do comando de destruição
        db.execSQL(sql);
        //Chamada ao metodo de construção da base de dados
        onCreate(db);
    }

    public void cadastrar(Pais pais){
        //Objeto para armazenar os valores dos campos
        ContentValues values = new ContentValues();
        //Definição de valores dos campos da tabela
        values.put("id", pais.getId());
        values.put("shortname", pais.getShortname());
        values.put("longname", pais.getLongname());
        values.put("callingCode", pais.getCallingCode());
        if (pais.getFlag() != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pais.getFlag().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put("flag", byteArray);
        }
        values.put("data",pais.getData());
        //Inserir dados do País no DB
        getWritableDatabase().insert(TABELA, null, values);
    }

    public ArrayList<Pais> listar(){
        //Definição da coleção de paises
        ArrayList<Pais> lista = new ArrayList();
        //Definição da instrução SQL
        String sql = "SELECT * FROM Pais ORDER BY shortname";
        //Objeto que recebe os registros do banco de dados
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        try{
            while(cursor.moveToNext()){
                //Criando uma nova referencia para País
                Pais pais = new Pais();
                //Carregar atributos de País com dados do BD
                pais.setId(cursor.getInt(0));
                pais.setShortname(cursor.getString(1));
                pais.setLongname(cursor.getString(2));
                pais.setCallingCode(cursor.getString(3));
                if (cursor.getBlob(4) != null){
                    Bitmap bmp = BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).length);
                    pais.setFlag(bmp);
                }
                pais.setData(cursor.getString(5));
                //Adicionar novo país na lista
                lista.add(pais);
            }
        } catch(SQLException e){
            Log.i(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return lista;
    }

    public String verificar(int id){
        //Definição da instrução SQL
        String sql = "SELECT data FROM Pais WHERE id = "+id;
        //Objeto que recebe os registros do banco de dados
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        try{
            if( cursor != null && cursor.moveToFirst()){
                return cursor.getString(cursor.getColumnIndex("data"));
            }
        } catch(SQLException e){
            Log.i(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return null;
    }

    public void deletar(Pais pais){
        //Definição de array de parâmetros
        String[] args = {pais.getId().toString()};
        //Exclusão do país
        getWritableDatabase().delete(TABELA, "id=?", args);
    }

    public void alterar(Pais pais){
        //Objeto para armazenar os valores dos campos
        ContentValues values = new ContentValues();
        //Definição de valores dos campos da tabela
        values.put("id", pais.getId());
        values.put("shortname", pais.getShortname());
        values.put("longname", pais.getLongname());
        values.put("callingCode", pais.getCallingCode());
        if (pais.getFlag() != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pais.getFlag().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put("flag", byteArray);
        }
        values.put("data",pais.getData());
        //Coleção de valores de parametros SQL para a clausula WHERE
        String[] args = {pais.getId().toString()};
        //Altera dados do musico no DB
        getWritableDatabase().update(TABELA, values, "id=?", args);
    }
}
