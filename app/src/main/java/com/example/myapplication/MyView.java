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
        Paint pt = new Paint();
        pt.setColor(Color.RED);
        pt.setStrokeWidth(3);
        pt.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        if(astar.list.size()!=0){

            if (this.floor == astar.list.peek().getFloor()) {
                    path.moveTo((float) (astar.list.peek().getX() * this.getWidth() / 45.3), (float) (this.getHeight() - astar.list.peek().getY() * this.getHeight() / 96.7));
            }

            while (!astar.list.isEmpty()) {
                if(this.floor==astar.start.getFloor()){
                        canvas.drawCircle((float) (astar.start.getX() * this.getWidth() / 45.3), (float) (this.getHeight() - astar.start.getY() * this.getHeight() / 96.7), 10, pt);
                }
                if(this.floor==astar.end.getFloor()){
                        canvas.drawCircle((float) (astar.end.getX() * this.getWidth() / 45.3), (float) (this.getHeight() - astar.end.getY() * this.getHeight() / 96.7),10,pt);
                }

                if(this.floor!=astar.list.peek().getFloor()){
                    break;
                }
                path.lineTo((float) (astar.list.peek().getX() * this.getWidth() / 45.3), (float) (this.getHeight() - astar.list.peek().getY() * this.getHeight() / 96.7));

                astar.list.pop();
            }
        }


        canvas.drawPath(path,paint);
    }
    public void setFloor(int floor){
        this.floor=floor;
    }
}
