package com.ceid.orma;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity{
    static Random r = new Random();
    View mainFragmentView;
    Menu toolbarMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragmentView = findViewById(R.id.main_fragment_view);

        // Load default fragment (Overview)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_view, new OverviewFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        toolbarMenu = menu;
        getMenuInflater().inflate(R.menu.toolbar, menu);

        // Disable done button
        menu.findItem(R.id.done_icon).setVisible(false);

        return true;
    }
}
