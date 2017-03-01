package com.example.musico.paises;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//atribui a toolbar à activity

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());//cria o pager adapter
        adapter.addFragments(new PaisesFragment(), "Países");//adiciona os Fragments com seus respectivos nomes
        adapter.addFragments(new VisitadosFragment(), "Visitados");
        viewPager.setAdapter(adapter);//atribui o adapter ao ViewPager
        tabLayout.setupWithViewPager(viewPager);//atribui o ViewPager à tab layout
    }
}
