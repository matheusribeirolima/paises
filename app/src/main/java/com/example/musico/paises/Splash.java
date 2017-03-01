package com.example.musico.paises;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity implements Runnable {//activity com thread

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(this, 2000);//atribui um delay de 2 segundos para a execução do run
    }

    public void run(){
        startActivity(new Intent(this, MainActivity.class));//chama outra activity
        finish();//finaliza a splash
    }
}
