package com.zowee.measuresize;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase javaCameraViev;

    private Mat mRgba;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            Log.i("linlian", "mLoaderCallback.onManagerConnected status=" + status);
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                    javaCameraViev.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    public MainActivity() {
        Log.i("linlian", "new MainActivity()");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        javaCameraViev = findViewById(R.id.camera_view);
        javaCameraViev.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraViev.setCvCameraViewListener(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //初始化 mat创建

        mRgba = new Mat();

    }

    @Override
    public void onCameraViewStopped() {
        //收尾 mat 释放
        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();


        return mRgba;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraViev != null) {
            javaCameraViev.disableView();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (javaCameraViev != null)
            javaCameraViev.disableView();
    }

}
