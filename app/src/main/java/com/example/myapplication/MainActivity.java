package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    boolean floor =  true;

    private ImageView pt;
    private TextView tv;
    private TextView lt;
    private SensorManager sm;
    private Sensor Accel;
    private Sensor Magnet;
    private float[] LastAccel = new float[3];
    private float[] LastMagnet = new float[3];
    private boolean AccelSet = false;
    private boolean MagnetSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float currentDegree = 0f;

    EditText ets ;
    EditText ete ;
    EditText eto ;
    EditText eti ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init.init();
        Button btn1 = (Button)findViewById(R.id.btn1);
        Button btn2 = (Button)findViewById(R.id.btn2);
//        Button btn3 = (Button)findViewById(R.id.btn3);
         ets = (EditText)findViewById(R.id.ets);
         ete = (EditText)findViewById(R.id.ete);
        eto = (EditText)findViewById(R.id.eto);
        eti =(EditText)findViewById(R.id.eti);

        btn1.setOnClickListener(t);
        btn2.setOnClickListener(u);
//        btn3.setOnClickListener(c);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        Accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Magnet = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pt = (ImageView) findViewById(R.id.pointer);
        tv =(TextView)findViewById(R.id.textView1);
        lt =(TextView)findViewById(R.id.textView2);
    }

    View.OnClickListener t =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int check =0;
            MyView mv = (MyView) findViewById(R.id.printV);
            MyView mv2  = (MyView) findViewById(R.id.printB);
            mv.setFloor(4);
            mv2.setFloor(5);
            if(Integer.parseInt(ets.getText().toString())==4){
                int a = Integer.parseInt(ete.getText().toString());
                astar.start = astar.n[a];
            }
            else if(Integer.parseInt(ets.getText().toString())==5){
                int a = Integer.parseInt(ete.getText().toString());
                astar.start = astar.m[a];
            }
            else{
                check = 1;
                Toast.makeText(getApplicationContext(),"해당 층은 지원하지 않습니다",Toast.LENGTH_SHORT).show();
            }
            if(Integer.parseInt(eti.getText().toString())==4){
                int b = Integer.parseInt(eto.getText().toString());
                astar.end = astar.n[b];
            }
            else if(Integer.parseInt(eti.getText().toString())==5){
                int b = Integer.parseInt(eto.getText().toString());
                astar.end = astar.m[b];
            }
            else{
                check = 1;
                Toast.makeText(getApplicationContext(),"해당 층은 지원하지 않습니다",Toast.LENGTH_SHORT).show();
            }
            if(astar.start==astar.end){
                Toast.makeText(getApplicationContext(),"같은 위치입니다.",Toast.LENGTH_SHORT).show();
            }
            if(check==0) {
                astar.astarMain();
                lt.setText(" "+Math.ceil(astar.fVal));

                if (astar.start.getFloor() == 4) {
                    mv.invalidate();
                    mv2.invalidate();
                } else {
                    mv2.invalidate();
                    mv.invalidate();
                }
            }
        }
    };
    View.OnClickListener u= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MyView mv = (MyView) findViewById(R.id.printV);

            MyView mv2  = (MyView) findViewById(R.id.printB);
            mv.invalidate();
            mv2.invalidate();
        }
    };
    @Override
    protected void onResume(){
        super.onResume();
        sm.registerListener((SensorEventListener) this,Accel,SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener((SensorEventListener) this,Magnet,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener((SensorEventListener) this, Accel);
        sm.unregisterListener((SensorEventListener) this,Magnet);
    }
    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor == Accel){
            System.arraycopy(event.values,0,LastAccel,0,event.values.length);
            AccelSet=true;
        }
        else if(event.sensor==Magnet){
            System.arraycopy(event.values,0,LastMagnet,0,event.values.length);
            MagnetSet=true;
        }
        if(AccelSet==true&&MagnetSet==true){
            SensorManager.getRotationMatrix(mR,null,LastAccel,LastMagnet);
            SensorManager.getOrientation(mR,mOrientation);

            float azimuth = (float) Math.toDegrees(mOrientation[0]);
            tv.setText(" "+azimuth);

            RotateAnimation RA =  new RotateAnimation(currentDegree,-azimuth, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

            RA.setDuration(250);
            RA.setFillAfter(true);
            pt.startAnimation(RA);
            currentDegree= -azimuth;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){}

}