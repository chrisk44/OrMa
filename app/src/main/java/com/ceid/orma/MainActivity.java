package com.ceid.orma;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    public boolean onOptionsItemSelected(MenuItem item){
        //if handled return true
        switch(item.getItemId()){
            case R.id.settings_icon:
                //Go to settings

                return true;

            case R.id.done_icon:
                // currentOrderFragment.onDoneClick(this) will return whether the order was sent

                return true;

            case R.id.notifications_icon:
                // Reveal notifications

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
