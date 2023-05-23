package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MyView extends View{
    public int floor;
    public MyView(Context context) {
        super(context);
    }
    public MyView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        if(astar.list.size()!=0){

            if (this.floor == astar.list.peek().getFloor()) {
                if (this.floor == 4) {

                    path.moveTo((float) (astar.list.peek().getX() * findViewById(R.id.printV).getWidth() / 45.3), (float) (findViewById(R.id.printV).getHeight() - astar.list.peek().getY() * findViewById(R.id.printV).getHeight() / 96.7));
                }
                else{

                    path.moveTo((float) (astar.list.peek().getX() * findViewById(R.id.printB).getWidth() / 45.3), (float) (findViewById(R.id.printB).getHeight() - astar.list.peek().getY() * findViewById(R.id.printB).getHeight() / 96.7));
                }

            }

            while (!astar.list.isEmpty()) {


                if(this.floor!=astar.list.peek().getFloor()){
                    break;
                }
                if (floor == 4) {
                    path.lineTo((float) (astar.list.peek().getX() * findViewById(R.id.printV).getWidth() / 45.3), (float) (findViewById(R.id.printV).getHeight() - astar.list.peek().getY() * findViewById(R.id.printV).getHeight() / 96.7));
                }
                else{
                    path.lineTo((float) (astar.list.peek().getX() * findViewById(R.id.printB).getWidth() / 45.3), (float) (findViewById(R.id.printB).getHeight() - astar.list.peek().getY() * findViewById(R.id.printB).getHeight() / 96.7));
                }
                astar.list.pop();
            }
        }


        canvas.drawPath(path,paint);
    }
    public void setFloor(int floor){
        this.floor=floor;
    }
}
