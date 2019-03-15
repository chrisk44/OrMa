package com.ceid.orma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    boolean inMain;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);
        inMain = true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!inMain){
                setContentView(R.layout.test_main);
                inMain = true;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void changeView(View asd){
        switch(asd.getId()){
            case R.id.to_login_btn:
                setContentView(R.layout.login_layout);
                break;

            case R.id.to_main_btn:
                setContentView(R.layout.activity_main);
                break;

            case R.id.to_overview_btn:
                setContentView(R.layout.overview_layout);

                /*findViewById(R.id.overview_view).setOnTouchListener(new View.OnTouchListener(){
                    int lastAction;
                    float dX, dY;
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN:
                                dX = v.getX() - event.getRawX();
                                dY = v.getY() - event.getRawY();
                                lastAction = MotionEvent.ACTION_DOWN;
                                break;

                            case MotionEvent.ACTION_MOVE:
                                v.setY(event.getRawY() + dY);
                                v.setX(event.getRawX() + dX);
                                lastAction = MotionEvent.ACTION_MOVE;
                                v.refreshDrawableState();
                                break;

                            case MotionEvent.ACTION_UP:
                                if (lastAction == MotionEvent.ACTION_DOWN)
                                    Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                return false;
                        }
                        return true;
                    }
                });*/
                break;

            case R.id.to_order_btn:
                setContentView(R.layout.order_layout);
                break;
        }
        inMain = false;
    }
}
