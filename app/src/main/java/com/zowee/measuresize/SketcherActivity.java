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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.FILLED;
import static org.opencv.core.Core.FONT_ITALIC;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * 在camera preview上绘图测试
 */
public class SketcherActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

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
                    javaCameraViev.setOnTouchListener(SketcherActivity.this);
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    public SketcherActivity() {
        Log.i("linlian", "new MainActivity()");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        javaCameraViev = findViewById(R.id.camera_view);
        javaCameraViev.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraViev.setCvCameraViewListener(SketcherActivity.this);
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
        Log.i("linlian", "onCameraViewStarted width =" + width + " height=" + height);
        w = width / 2;
    }

    @Override
    public void onCameraViewStopped() {
        //收尾 mat 释放
        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        drawAtom(mRgba);

        drawRock(mRgba);

        double fontScaleFromHeight = Imgproc.getFontScaleFromHeight(FONT_ITALIC, 30);
        Imgproc.putText(mRgba, "draw text", new Point(w / 2, w/4), FONT_ITALIC, fontScaleFromHeight,
                new Scalar(255, 0, 0),8);

        return mRgba;
    }


    private int w = 100;

    private void drawAtom(Mat atom_image) {

        // Mat atom_image = new Mat(w, w, CV_8UC3);
        //Mat rock_image = new Mat(w, w, CV_8UC3);

        drawEllipse(atom_image, 90);
        drawEllipse(atom_image, 0);
        drawEllipse(atom_image, 45);
        drawEllipse(atom_image, -45);
        Imgproc.circle(atom_image, new Point(w / 2, w / 2), w / 32, new Scalar(255, 0, 0), FILLED, Imgproc.LINE_8, 0);

    }

    private void drawEllipse(Mat img, int angle) {
        int thickness = 2;
        int lineType = 8;
        Imgproc.ellipse(img,
                new Point(w / 2, w / 2),
                new Size(w / 4, w / 16), angle,
                0.0,
                360.0,
                new Scalar(255, 255, 0),
                thickness,
                lineType,
                0);
    }

    private void drawRock(Mat rock_image) {
        myPolygon(rock_image);
        Imgproc.rectangle(rock_image, new Point(0, 7 * w / 8), new Point(w, w),
                new Scalar(0, 255, 255), FILLED, Imgproc.LINE_8, 0);
        myLine(rock_image, new Point(0, 15 * w / 16), new Point(w, 15 * w / 16));
        myLine(rock_image, new Point(w / 4, 7 * w / 8), new Point(w / 4, w));
        myLine(rock_image, new Point(w / 2, 7 * w / 8), new Point(w / 2, w));
        myLine(rock_image, new Point(3 * w / 4, 7 * w / 8), new Point(3 * w / 4, w));


    }


    private void myPolygon(Mat img) {//无法绘制多边形
        List<MatOfPoint> listPoints = new ArrayList<>();

        listPoints.add(new MatOfPoint(new Point(w / 4, 7 * w / 8)));
        listPoints.add(new MatOfPoint(new Point(3 * w / 4, 7 * w / 8)));
        listPoints.add(new MatOfPoint(new Point(3 * w / 4, 13 * w / 16)));
        listPoints.add(new MatOfPoint(new Point(11 * w / 16, 13 * w / 16)));
        listPoints.add(new MatOfPoint(new Point(19 * w / 32, 3 * w / 8)));
        listPoints.add(new MatOfPoint(new Point(3 * w / 4, 3 * w / 8)));
        listPoints.add(new MatOfPoint(new Point(3 * w / 4, w / 8)));
        listPoints.add(new MatOfPoint(new Point(26 * w / 40, w / 8)));
        listPoints.add(new MatOfPoint(new Point(26 * w / 40, w / 4)));
        listPoints.add(new MatOfPoint(new Point(22 * w / 40, w / 4)));
        listPoints.add(new MatOfPoint(new Point(22 * w / 40, w / 8)));
        listPoints.add(new MatOfPoint(new Point(18 * w / 40, w / 8)));
        listPoints.add(new MatOfPoint(new Point(18 * w / 40, w / 4)));
        listPoints.add(new MatOfPoint(new Point(14 * w / 40, w / 4)));
        listPoints.add(new MatOfPoint(new Point(14 * w / 40, w / 8)));
        listPoints.add(new MatOfPoint(new Point(w / 4, w / 8)));
        listPoints.add(new MatOfPoint(new Point(w / 4, 3 * w / 8)));
        listPoints.add(new MatOfPoint(new Point(13 * w / 32, 3 * w / 8)));
        listPoints.add(new MatOfPoint(new Point(5 * w / 16, 13 * w / 16)));
        listPoints.add(new MatOfPoint(new Point(w / 4, 13 * w / 16)));


        Imgproc.fillPoly(img, listPoints, new Scalar(255, 255, 255),
                Imgproc.LINE_8, 0, new Point(w / 2, w / 2));
    }

    private void myLine(Mat img, Point start, Point end) {//黑色分割线
        Imgproc.line(img, start, end, new Scalar(0, 0, 0), 2, Imgproc.LINE_8, 0);
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
