package com.ceid.orma;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity{
    static Random r = new Random();
    View mainFragmentView;
    Menu toolbarMenu;
    DrawerLayout mDrawerLayout;
    View mNotificationView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragmentView = findViewById(R.id.main_fragment_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNotificationView = findViewById(R.id.notification_view);

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
                Toast.makeText(this, "Go to settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.done_icon:
                // currentOrderFragment.onDoneClick(this) will return whether the order was sent
                Toast.makeText(this, "Submit this order", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.notifications_icon:
                // Reveal notifications
                mDrawerLayout.openDrawer(mNotificationView);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mDrawerLayout.isDrawerOpen(mNotificationView)){
                mDrawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
