package com.zowee.measuresize;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class BorderDetectActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private CameraBridgeViewBase javaCameraViev;

    private Mat mRgba;
    private Mat mGray;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            Log.i("linlian", "mLoaderCallback.onManagerConnected status=" + status);
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                    javaCameraViev.enableView();
                    javaCameraViev.setOnTouchListener(BorderDetectActivity.this);
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    public BorderDetectActivity() {
        Log.i("linlian", "new BorderDetectActivity()");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        javaCameraViev = findViewById(R.id.camera_view);
        javaCameraViev.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraViev.setCvCameraViewListener(BorderDetectActivity.this);
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
        mGray = new Mat();

    }

    @Override
    public void onCameraViewStopped() {
        //收尾 mat 释放
        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        Mat cannyMat = new Mat();
        Mat blur = new Mat();

        //模糊化后，减少一些干扰点
        Imgproc.blur(mGray, blur, new Size(7, 7));
        //边缘检测
        Imgproc.Canny(blur, cannyMat, 50, 150, 3, false);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Scalar color = new Scalar(250, 0, 0);

        contours = filterContours(contours);

        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(mRgba, contours, i, color, 2, 8, hierarchy, 0, new Point());
        }

        return mRgba;
    }

    private List<MatOfPoint> filterContours(List<MatOfPoint> contours) {

        List<MatOfPoint> newContours = new ArrayList<>();
        Log.i("linlian"," filterContours: original size ="+contours.size());
        for (int i =0;i<contours.size();i++){
            MatOfPoint matOfPoint = contours.get(i);
            double area = Imgproc.contourArea(matOfPoint);
            Log.i("linlian"," i: "+i+" area ="+area);

            if(area<100){//面积过滤,太小的过滤掉
                //contours.remove(i);
                continue;
            }

            newContours.add(matOfPoint);

        }

        Log.i("linlian"," filterContours: after filter size ="+newContours.size());
        return newContours;
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
        if (javaCameraViev != null) {
            javaCameraViev.disableView();
            javaCameraViev.setOnTouchListener(null);

        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }
}
