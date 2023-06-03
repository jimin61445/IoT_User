package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Navigation extends AppCompatActivity {
    private ImageView directionImageView;

    private BroadcastReceiver azimuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.myapplication.AZIMUTH_UPDATE")) {
                float azimuth = intent.getFloatExtra("azimuth", 0f);
                // 여기서 Navigation 액티비티에서 azimuth 값을 활용하는 작업 수행
                updateDirectionImage(azimuth);
            }
        }
    };

    public Navigation() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        directionImageView = findViewById(R.id.directionImageView);

        Toast myToast = Toast.makeText(this.getApplicationContext(),"Navigation Start", Toast.LENGTH_LONG);
        myToast.show();
    }

    public void updateDirectionImage(float azimuth) {
        if (directionImageView != null) {
            // 방향에 따라 이미지 변경
            if (azimuth > 45 && azimuth < 135) {
                directionImageView.setImageResource(R.drawable.right);
            } else if (azimuth > 135 && azimuth < 225) {
                directionImageView.setImageResource(R.drawable.down);
            } else if (azimuth > 225 && azimuth < 315) {
                directionImageView.setImageResource(R.drawable.left);
            } else {
                directionImageView.setImageResource(R.drawable.up);
            }

            // 다음 경로 노드를 바라보는 회전 각도 계산
            float rotationAngle = targetDirection - azimuth;

            // 현재 위치와 다음 노드의 좌표
            float currentX = getCurrentX(); // 현재 위치의 X 좌표
            float currentY = getCurrentY(); // 현재 위치의 Y 좌표
            float nextX = nextNode.getX(); // 다음 노드의 X 좌표
            float nextY = nextNode.getY(); // 다음 노드의 Y 좌표

            // 현재 위치와 다음 노드 사이의 방향 벡터 계산
            float directionX = nextX - currentX;
            float directionY = nextY - currentY;

            // 방향 벡터의 각도 계산 (라디안)
            float angleRadians = (float) Math.atan2(directionY, directionX);

            // 라디안 값을 도 단위로 변환
            float angleDegrees = (float) Math.toDegrees(angleRadians);

            // 각도 범위 조정 (0도에서 360도 사이)
            if (angleDegrees < 0) {
                angleDegrees += 360;
            }

            // 이미지 회전 애니메이션 생성
            RotateAnimation rotationAnimation = new RotateAnimation(0f, rotationAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotationAnimation.setDuration(250);
            rotationAnimation.setFillAfter(true);

            // 이미지뷰에 애니메이션 적용
            directionImageView.startAnimation(rotationAnimation);
        }
    }
}

