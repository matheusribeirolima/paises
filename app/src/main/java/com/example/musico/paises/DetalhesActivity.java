package com.example.musico.paises;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class DetalhesActivity extends AppCompatActivity {

    private Bitmap bmp;
    private Bundle args;
    private FloatingActionButton salvar;
    private DatePicker date;
    private Switch visitado;
    private PaisDAO dao;
    private TextView visitou;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);//atribuindo layout
        args = getIntent().getExtras();//obtendo os dados passados à activity quando foi criada
        if (args != null){
            setTitle(args.getString("nomecurto"));//atribuindo o título da activity
            if (args.getByteArray("imagem") != null){//verifica se a imagem foi recebida e a atribui
                byte[] byteArray = args.getByteArray("imagem");
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ((ImageView) findViewById(R.id.flag)).setImageBitmap(bmp);
            }//atribui os outros elementos
            ((TextView) findViewById(R.id.shortname)).setText(args.getString("nomecurto"));
            ((TextView) findViewById(R.id.longname)).setText(args.getString("nomelongo"));
            ((TextView) findViewById(R.id.callingCode)).setText(args.getString("callingCode"));
        }
        salvar = (FloatingActionButton) findViewById(R.id.salvar);
        date = (DatePicker) findViewById(R.id.datePicker);
        visitado = (Switch) findViewById(R.id.switch_visitado);
        visitou = (TextView) findViewById(R.id.text_visitado);
        dao = new PaisDAO(this);//Criação do objeto DAO - inicio da conexão com o DB
        if (dao.verificar(args.getInt("id")) != null){
            visitado.setChecked(true);
            visitou.setText("SIM");
            String[] data = dao.verificar(args.getInt("id")).split("/");
            date.updateDate(Integer.parseInt(data[2]),Integer.parseInt(data[1]),Integer.parseInt(data[0]));
        } else {
            visitou.setText("NAO");
            visitado.setChecked(false);
            salvar.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            (findViewById(R.id.text_data)).setVisibility(View.GONE);
        }
        dao.close();//Encerramento da conexão com o DB
        salvar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//Tratando evento de click no botão de salvar
                PaisDAO dao = new PaisDAO(getApplicationContext());
                String data = date.getDayOfMonth()+"/"+date.getMonth()+"/"+date.getYear();//obtem data do gadget e salva como string
                Pais pais = new Pais(args.getInt("id"),args.getString("nomecurto"),args.getString("nomelongo"),args.getString("callingCode"),bmp,data);//cria objeto para salvar no DB
                if (dao.verificar(args.getInt("id")) != null){//Se ja houver o objeto no banco, altera-o
                    dao.alterar(pais);
                    Toast.makeText(DetalhesActivity.this, "Pais alterado!", Toast.LENGTH_SHORT).show();
                } else {//Caso contrario, o salva
                    dao.cadastrar(pais);
                    Toast.makeText(DetalhesActivity.this, "País cadastrado!", Toast.LENGTH_SHORT).show();
                }
                dao.close();//Encerramento da conexão com o DB
                finish();//Encerramento da activity
            }
        });

        visitado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {//Tratamento do evento de mudança no switch
                if(isChecked){//se ativado, deixa visivel o botao de salvar e o widget da data
                    visitou.setText("SIM");
                    salvar.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    (findViewById(R.id.text_data)).setVisibility(View.VISIBLE);
                }else{//caso contrario os oculta
                    salvar.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                    (findViewById(R.id.text_data)).setVisibility(View.GONE);
                }
            }
        });

        if (getSupportActionBar() != null){//verifica a disponibilidade da barra de ações e atribui o elemento da seta
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//tratamento do evento de clicar na seta de voltar a hierarquia das activitys
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
