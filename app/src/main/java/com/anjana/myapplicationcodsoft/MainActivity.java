package com.anjana.myapplicationcodsoft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private boolean isFlashlightOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.btn);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = getCameraIdWithFlash(cameraManager);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (cameraId==null) {
            btn.setEnabled(false);
            Toast.makeText(this, "Your device does not have a flashlight.", Toast.LENGTH_SHORT).show();
            return;
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlashlight();
            }
        });
    }

    private String getCameraIdWithFlash(CameraManager cameraManager) throws CameraAccessException {
        for (String cameraId : cameraManager.getCameraIdList()) {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (hasFlash != null && hasFlash) {
                return cameraId;
            }
        }
        return null;
    }

    private void toggleFlashlight() {
        if(cameraManager==null){
            Toast.makeText(this, "Your device doesn't have a flashlight.", Toast.LENGTH_SHORT).show();
        }
        try {
            if (isFlashlightOn) {
                //Turn On the flashlight
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
                btn.setText("Turn On");
            } else {
                // Turn off the flashlight
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
                btn.setText("Turn Off");
            }
        }catch (CameraAccessException e){
            e.printStackTrace();
            Toast.makeText(this, "Failed to access flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() { //Flashlight is turned off when Paused
        super.onPause();
        if (isFlashlightOn) {
            toggleFlashlight();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager= null;
        cameraId=null;
    }
}