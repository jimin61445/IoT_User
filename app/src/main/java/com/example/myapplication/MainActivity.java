package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Stack;


public class MainActivity extends AppCompatActivity {
    boolean floor =  true;

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
//    View.OnClickListener c = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            MyView mv1 = (MyView) findViewById(R.id.printV);
//            MyView mv2 = (MyView) findViewById(R.id.printB);
//            if(floor){
//                mv1.setVisibility(View.GONE);
//                mv2.setVisibility(View.VISIBLE);
//                floor=false;
//            }
//            else{
//                mv1.setVisibility(View.VISIBLE);
//                mv2.setVisibility(View.GONE);
//                floor=true;
//            }
//        }
//    };
}