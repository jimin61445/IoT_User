package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    boolean floor =  true;
    private FirebaseFirestore db;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ImageView directionImageView;
    private TextView location;

    private BroadcastReceiver azimuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.myapplication.AZIMUTH_UPDATE")) {
                float azimuth = intent.getFloatExtra("azimuth", 0f);

            }
        }
    };
    private int cL;
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

    private Button navigationButton;

    private int spinChcek=0;
    private String result;
    private int startCh;
    private int endCh;
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

        check Che = new check();

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String current = (String) parent.getItemAtPosition(position);
                endCh=Che.checkMain(current);
                Log.d("ench", String.valueOf(endCh));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn1.setOnClickListener(t);
        btn2.setOnClickListener(u);
//        btn3.setOnClickListener(c);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        Accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Magnet = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pt = (ImageView) findViewById(R.id.pointer);
        tv =(TextView)findViewById(R.id.textView1);
        lt =(TextView)findViewById(R.id.textView2);

        navigationButton = findViewById(R.id.navigationButton);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 네비게이션 액티비티로 전환
//                Intent intent = new Intent(MainActivity.this, Navigation.class);
//                startActivity(intent);
                getCurrentFingerprint();

            }
        });

    }

    View.OnClickListener t =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            spinChcek=1;
            astar.fVal=0;
            int check =0;
            int Flo;
            int Roo;
            int FloT;
            int RooT;
            MyView mv = (MyView) findViewById(R.id.printV);
            MyView mv2  = (MyView) findViewById(R.id.printB);
            mv.setFloor(4);
            mv2.setFloor(5);
            Flo=(int)startCh/100;
            Roo=(int)startCh%100;
            FloT=(int)endCh/100;
            RooT=(int)endCh%100;
            Log.d("Log",Flo+" "+Roo);
            Log.d("Log",FloT+" "+RooT);
            if(Flo==4){
                astar.start=astar.n[Roo];
            }
            else if(Flo==5){
                astar.start = astar.m[Roo];
            }
            else{
                check = 1;
                Toast.makeText(getApplicationContext(),"현재위치를 찾아주세요",Toast.LENGTH_SHORT).show();
            }
            if(FloT==4){
                astar.end = astar.n[RooT];
            }
            else if(FloT==5){
                astar.end = astar.m[RooT];
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
            updateDirectionImage();

        }
    };
    View.OnClickListener u= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            astar.fVal=0;
            lt.setText(" "+Math.ceil(astar.fVal));
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

        // BroadcastReceiver 등록
        IntentFilter filter = new IntentFilter("com.example.myapplication.AZIMUTH_UPDATE");
        registerReceiver(azimuthReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener((SensorEventListener) this, Accel);
        sm.unregisterListener((SensorEventListener) this,Magnet);

        // BroadcastReceiver 해제
        unregisterReceiver(azimuthReceiver);
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
        if(AccelSet&&MagnetSet){
            SensorManager.getRotationMatrix(mR,null,LastAccel,LastMagnet);
            SensorManager.getOrientation(mR,mOrientation);

            float azimuth = (float) Math.toDegrees(mOrientation[0]);
            tv.setText(" "+azimuth);

            // azimuth 값을 Broadcast로 전달
            Intent azimuthIntent = new Intent("com.example.myapplication.AZIMUTH_UPDATE");
            azimuthIntent.putExtra("azimuth", azimuth);
            sendBroadcast(azimuthIntent);
            RotateAnimation RA = new RotateAnimation(currentDegree, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            RA.setDuration(250);
            RA.setFillAfter(true);
//            pt.startAnimation(RA);
            currentDegree = -azimuth;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){}

    private void getData(double[] features, String[] feat) {
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("classTest");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String ssid,bssid,rssi;
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<Double> dataList = new ArrayList<>();
                        List<String> databaseLocations = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {

                            // 문서의 필드에서 RSSI 값을 가져와서 벡터로 변환

                            ArrayList<Object> RSSIHashMap = (ArrayList<Object>) documentSnapshot.getData().get("RSSI");
                            if(RSSIHashMap!=null&&!RSSIHashMap.isEmpty()) {
                                double sum = 0;
                                for (int i = 0; i < RSSIHashMap.size(); i++) {
                                    sum = 0;

                                    HashMap<String, String> map = (HashMap<String, String>) RSSIHashMap.get(i);
                                    ssid = map.get("ssid");
                                    bssid = map.get("bssid");
                                    rssi = map.get("rssi");
                                    for (int j = 0; j < 3; j++) {
                                        if (feat[j].equals(bssid)) {
                                            sum = sum + Math.abs(features[j] - Integer.parseInt(rssi));
                                            Log.d("LOGLOGLOG", documentSnapshot.getId() + " " + features[j] + " " + rssi + " " + j);
                                        }
                                        else{
                                            sum=sum+1;
                                        }
                                    }
                                }
                                Log.d("LOGLOGLOG", documentSnapshot.getId() + " "+sum);
                                dataList.add(sum / 3);
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
                        fingerprintMatching(dataList, databaseLocations);

                    }
                } else {
                    // 데이터 가져오기 실패
                    Exception exception = task.getException();
                    Toast.makeText(getApplicationContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // 2. 현재 위치의 Wi-Fi rssi 값을 핑거프린트 특징값으로 변환
    private void getCurrentFingerprint() {

        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // 위치 권한이 있는 경우, Wi-Fi 신호 측정값 가져오기
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        double[] features = new double[0];
        String[] feat = new String[0];
        if (wifiManager != null) {

            wifiManager.startScan(); // Wi-Fi 스캔 시작
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults != null && !scanResults.isEmpty()) {

                int numFeatures = scanResults.size();
                Log.d("LOG", String.valueOf(numFeatures));
                int one = 0;
                int two = 0;
                int three = 0;
                int oneTemp = -100;
                int twoTemp = -110;
                int threeTemp = -120;

                features = new double[3];
                feat = new String[3];

                for (int i = 0; i < numFeatures; i++) {
                    ScanResult scanResult = scanResults.get(i);
                    Log.d("LOG", scanResult.BSSID);
                    if (scanResult.level > threeTemp) {
                        if (scanResult.level > twoTemp) {
                            if (scanResult.level > oneTemp) {
                                if (oneTemp != -100) {
                                    three = two;
                                    two = one;
                                    threeTemp = twoTemp;
                                    twoTemp = oneTemp;
                                }
                                one = i;
                                oneTemp = scanResult.level;

                            } else {
                                if (twoTemp != -100) {
                                    three = two;
                                    threeTemp = twoTemp;
                                }
                                two = i;
                                twoTemp = scanResult.level;
                            }
                        } else {
                            three = i;
                            threeTemp = scanResult.level;
                        }
                    }

                }
                features[0] = oneTemp;
                features[1] = twoTemp;
                features[2] = threeTemp;
                // RSSI 값 저장
                String rssiTemp = null;
                String li;
                for (int p = 0; p < 3; p++) {
                    if (p == 0) {
                        li = scanResults.get(one).BSSID;
                    } else if (p == 1) {
                        li = scanResults.get(two).BSSID;
                    } else {
                        li = scanResults.get(three).BSSID;
                    }
                    feat[p] = li;
                }

            }
        }
        getData(features, feat);
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
    private void fingerprintMatching(List<Double> databaseFingerprints, List<String> databaseLocations) {
        // 유클리디안 거리
        List<Double> distances = new ArrayList<>();
        Double minSum= Double.valueOf(10000);
        int min=0;
        for(int i=0;i<databaseFingerprints.size();i++){
            Log.d("Min", String.valueOf(databaseFingerprints.get(i)));
            if(minSum>databaseFingerprints.get(i)){
                minSum=databaseFingerprints.get(i);
                min=i;
            }
        }
        this.result = databaseLocations.get(min);




        // knn
//        int k = 3;
//        List<Integer> indices = findNearestNeighbors(distances, k);
//
//        // 가장 가까운 이웃 중에서 가장 많이 등장한 위치
//        Map<String, Integer> locationCount = new HashMap<>();
//        for (int i : indices) {
//            String location = databaseLocations.get(i);
//            int count = locationCount.getOrDefault(location, 0);
//            locationCount.put(location, count + 1);
//        }
//
//        // 가장 많이 등장한 위치 출력
//        String mostFrequentLocation = null;
////        int maxCount = 0;
////        for (Map.Entry<String, Integer> entry : locationCount.entrySet()) {
////            String location = entry.getKey();
////            int count = entry.getValue();
////            if (count > maxCount) {
////                mostFrequentLocation = location;
////                maxCount = count;
////            }
////        }
        Log.d("Fingerprint Matching", result);
        check Che = new check();
        startCh=Che.checkMain(this.result);
        Log.d("cg", String.valueOf(startCh));
        Toast.makeText(getApplicationContext(),"You are location is "+ result,Toast.LENGTH_SHORT).show();
    }

    // 유클리디안 거리 계산
//    private double computeEuclideanDistance(RealVector v1, Double v2) {
//        double[] array1 = v1.toArray();
//        double[] array2 = v2.toArray();
//
////        if (array1.length != array2.length) {
////            throw new IllegalArgumentException("벡터의 길이가 일치하지 않습니다.");
////        }
//
//        double sum = 0.0;
//        for (int i = 0; i < array1.length; i++) {
//            double diff = array1[i] - array2[i];
//            sum += diff * diff;
//        }
//
//        return Math.sqrt(sum);
//    }

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
    public void updateDirectionImage() {
        if (pt != null) {

            // 현재 위치 출력
            //String locationText = "Latitude: " + latitude + ", Longitude: " + longitude;
            //location.setText(locationText);


            // 방향에 따라 이미지 변경


            // 다음 경로 노드를 바라보는 회전 각도 계산
//            float rotationAngle = targetDirection - azimuth;

            // 현재 위치와 다음 노드의 좌표
            float currentX = (float) astar.start.getX();
            float currentY = (float) astar.start.getY(); // 현재 위치의 Y 좌표
            float nextX = (float) astar.next.getX(); // 다음 노드의 X 좌표
            float nextY = (float) astar.next.getY(); // 다음 노드의 Y 좌표
//
//            // 현재 위치와 다음 노드 사이의 방향 벡터 계산
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
            Log.d("angle", String.valueOf(angleDegrees));
            if (angleDegrees > 45 && angleDegrees < 135) {
                pt.setImageResource(R.drawable.right);
            } else if (angleDegrees > 135 && angleDegrees < 225) {
                pt.setImageResource(R.drawable.down);
            } else if (angleDegrees > 225 && angleDegrees < 315) {
                pt.setImageResource(R.drawable.left);
            } else {
                pt.setImageResource(R.drawable.up);
            }

//            // 이미지 회전 애니메이션 생성
//            RotateAnimation rotationAnimation = new RotateAnimation(0f, rotationAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            rotationAnimation.setDuration(250);
//            rotationAnimation.setFillAfter(true);
//
//            // 이미지뷰에 애니메이션 적용
//            directionImageView.startAnimation(rotationAnimation);
        }
    }
}