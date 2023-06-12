package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ml.distance.EuclideanDistance;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Navigation extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ImageView directionImageView;
    private TextView location;

    private BroadcastReceiver azimuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.myapplication.AZIMUTH_UPDATE")) {
                float azimuth = intent.getFloatExtra("azimuth", 0f);

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
        location = findViewById(R.id.location);

        Toast myToast = Toast.makeText(this.getApplicationContext(), "Navigation Start", Toast.LENGTH_LONG);
        myToast.show();

        // 데이터베이스의 핑거프린트 & 위치 정보 -> 현재 위치로 추정할 Wi-Fi 신호 측정값 -> 핑거프린트 매칭까지
        getData();
    }

    // 1. 데이터베이스의 핑거프린트 & 위치 정보
    private void getData() {
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("classrooms");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String ssid,bssid,rssi;
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<RealVector> dataList = new ArrayList<>();
                        List<String> databaseLocations = new ArrayList<>();

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {

                            // 문서의 필드에서 RSSI 값을 가져와서 벡터로 변환

                            ArrayList<Object> RSSIHashMap = (ArrayList<Object>) documentSnapshot.getData().get("RSSI");

                            if(RSSIHashMap!=null&&!RSSIHashMap.isEmpty()) {
                                double[] features = new double[RSSIHashMap.size() * 2];

                                for (int i = 0; i < RSSIHashMap.size(); i++) {
                                    Log.d("B",""+RSSIHashMap.size());
                                    HashMap<String, String> map = (HashMap<String, String>) RSSIHashMap.get(i);
                                    ssid = map.get("ssid");
                                    bssid = map.get("bssid");
                                    rssi = map.get("rssi");
                                    Log.d("log", ssid + bssid + rssi);
                                    String[] li = bssid.split(":");
                                    String rssiTemp = null;
                                    rssiTemp = String.valueOf((Integer.parseInt(li[0],16)));
                                    for (int j = 1; j < li.length; j++) {
                                        rssiTemp=rssiTemp+String.valueOf((Integer.parseInt(li[j],16)));
                                    }
                                    features[2 * i] = Double.parseDouble(rssi);
                                    Log.d("lod",rssiTemp);
                                    features[2 * i + 1] = Double.parseDouble(rssiTemp);
                                }
                                Log.d("a","AAA");
                                RealVector dataVector = new ArrayRealVector(features);
                                dataList.add(dataVector);
                                databaseLocations.add(documentSnapshot.getId());
                            }


//                            if (RSSIHashMap != null && !RSSIHashMap.isEmpty()) {
//                                List<List<String>> RSSI = new ArrayList<>();
//                                for (Object value : RSSIHashMap.values()) {
//                                    if (value instanceof List) {
//                                        RSSI.add((List<String>) value);
//                                    }
//                                }double[] features = new double[RSSI.size() * 2];
//
//                                double[] features = new double[RSSI.size() * 2];
//                                for (int i = 0; i < RSSI.size(); i++) {
//                                    List<String> element = RSSI.get(i);
//                                    if (element.size() > 1) {
//                                        String bssid = element.toString();
//
//                                        int rssi = Integer.parseInt(element.get(1));
//                                        features[2 * i] = rssi; // RSSI 값 저장
//                                        features[2 * i + 1] = Double.parseDouble(bssid.replace(":", "")); // BSSID 값 숫자로 변환해 저장
//                                    }
//                                }
//                                // 벡터 생성 및 리스트에 추가
//                                RealVector dataVector = new ArrayRealVector(features);
//                                dataList.add(dataVector);
//                                databaseLocations.add(documentSnapshot.getId()); // 문서 ID를 위치 정보로 사용
//                            }
                        }

                        // 3. 핑거프린트 매칭 실행
                        RealVector currentFingerprint = getCurrentFingerprint();
                        if (currentFingerprint != null) {
                            fingerprintMatching(dataList, databaseLocations, currentFingerprint);
                        } else {
                            // 현재 위치의 핑거프린트를 가져올 수 없음
                            Toast.makeText(Navigation.this, "Failed to get current fingerprint", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    // 데이터 가져오기 실패
                    Exception exception = task.getException();
                    Toast.makeText(Navigation.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // 2. 현재 위치의 Wi-Fi rssi 값을 핑거프린트 특징값으로 변환
    private RealVector getCurrentFingerprint() {

        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return null;
        }

        // 위치 권한이 있는 경우, Wi-Fi 신호 측정값 가져오기
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {

            wifiManager.startScan(); // Wi-Fi 스캔 시작
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults != null && !scanResults.isEmpty()) {

                int numFeatures = scanResults.size();
                double[] features = new double[2*numFeatures];
                for (int i = 0; i < numFeatures; i++) {
                    ScanResult scanResult = scanResults.get(i);
                    int rssi = scanResult.level;
                    String bssid = scanResult.BSSID;
                    features[2 * i] = rssi; // RSSI 값 저장
                    String[] li = bssid.split(":");
                    String rssiTemp = null;
                    rssiTemp = String.valueOf((Integer.parseInt(li[0],16)));
                    for (int j = 1; j < li.length; j++) {
                        rssiTemp=rssiTemp+String.valueOf((Integer.parseInt(li[j],16)));
                    }
                    features[2 * i + 1] = Double.parseDouble(rssiTemp);
                }
                return new ArrayRealVector(features);
            }
        }
        return null;
    }

    // 위치 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentFingerprint();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 3. 핑거프린트 매칭 (유클리디안 거리, knn 매칭 알고리즘)
    private void fingerprintMatching(List<RealVector> databaseFingerprints, List<String> databaseLocations, RealVector currentFingerprint) {
        // 유클리디안 거리
        List<Double> distances = new ArrayList<>();
        for (RealVector fingerprint : databaseFingerprints) {
            double distance = computeEuclideanDistance(currentFingerprint, fingerprint);
            distances.add(distance);
        }

        // knn
        int k = 3;
        List<Integer> indices = findNearestNeighbors(distances, k);

        // 가장 가까운 이웃 중에서 가장 많이 등장한 위치
        Map<String, Integer> locationCount = new HashMap<>();
        for (int i : indices) {
            String location = databaseLocations.get(i);
            int count = locationCount.getOrDefault(location, 0);
            locationCount.put(location, count + 1);
        }

        // 가장 많이 등장한 위치 출력
        String mostFrequentLocation = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : locationCount.entrySet()) {
            String location = entry.getKey();
            int count = entry.getValue();
            if (count > maxCount) {
                mostFrequentLocation = location;
                maxCount = count;
            }
        }

        String result = String.format("가장 유사한 위치: %s", mostFrequentLocation);
        Log.d("Fingerprint Matching", result);
    }

    // 유클리디안 거리 계산
    private double computeEuclideanDistance(RealVector v1, RealVector v2) {
        double[] array1 = v1.toArray();
        double[] array2 = v2.toArray();

//        if (array1.length != array2.length) {
//            throw new IllegalArgumentException("벡터의 길이가 일치하지 않습니다.");
//        }

        double sum = 0.0;
        for (int i = 0; i < array1.length; i++) {
            double diff = array1[i] - array2[i];
            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }

    // knn 계산
    private List<Integer> findNearestNeighbors(List<Double> distances, int k) {
        List<Integer> indices = new ArrayList<>();
        List<Double> sortedDistances = new ArrayList<>(distances);
        Collections.sort(sortedDistances); // distances 오름차순으로 정렬

        Set<Double> processedDistances = new HashSet<>(); // 처리된 거리 값 저장할 Set
        int count = 0;
        int i = 0;
        while (count < k && i < distances.size()) {
            double distance = sortedDistances.get(i);
            int index = distances.indexOf(distance);
            if (!processedDistances.contains(distance)) {
                indices.add(index);
                processedDistances.add(distance);
                count++;
            }
            i++;
        }

        return indices;
    }

    //가야할 방향, 방향벡터로 계산
    public void updateDirectionImage(float azimuth) {
        if (directionImageView != null) {

            // 현재 위치 출력
            //String locationText = "Latitude: " + latitude + ", Longitude: " + longitude;
            //location.setText(locationText);

            /*
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
            directionImageView.startAnimation(rotationAnimation); */
        }
    }
}

