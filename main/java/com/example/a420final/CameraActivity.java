package com.example.a420final;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.ColorMatrixColorFilter;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.core.Core.norm;

import java.lang.Math;

public class CameraActivity extends AppCompatActivity {


    Button btnCaptureImage_s;
    Button btnCaptureImage_n;
    Button startanalysis;
    Button taketest;
    Button model;

    Button nextpage;
    ImageView imageDisplay1;
    ImageView imageDisplay2;
    ImageView testimage;
    Bitmap bitmap1;
    Bitmap bitmap2;
    Mat mat_s = new Mat();
    Mat mat_n = new Mat();

    TextView resultDisplay;
    TextView dis;

    TextView greeting;
    TextView buttoncount;
    int num_s = 0;
    int num_n = 0;
    int total_img = 0;
    int w;
    int h;
    int knn = 1; // this is the default knn k value

    Mat database = new Mat();
    Mat eigenvectors = new Mat();
    Mat mean = new Mat();
    Mat weights = new Mat();
    double theta;

//    int[][] database_s;
//    int[][] database_n;

    Bitmap eigenface1;
    Bitmap eigenface2;
    Bitmap bitmap3;
    Mat test_image_flat;
    int result;
    double smallest_dis;
//    int[] test_flat_img;

    double weight_diff;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);




        btnCaptureImage_s = (Button) findViewById(R.id.smilebtn);
        btnCaptureImage_n = (Button) findViewById(R.id.nobtn);
        model = (Button) findViewById(R.id.model);
        imageDisplay1 = (ImageView) findViewById(R.id.displayImageView1);
        imageDisplay2 = (ImageView) findViewById(R.id.displayImageView2);
        startanalysis = (Button) findViewById(R.id.analysisBtn);
        resultDisplay = (TextView) findViewById(R.id.result);
        greeting = (TextView) findViewById(R.id.HETitle);
        buttoncount = (TextView) findViewById(R.id.buttoncount);
        testimage = (ImageView) findViewById(R.id.testimage);
        dis = (TextView) findViewById(R.id.dis);

        taketest = (Button) findViewById(R.id.test);
        nextpage = (Button) findViewById(R.id.nextpage);

        greeting.setText("Hi " + MainActivity.name + ", please take some selfies to build the model.");

        btnCaptureImage_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_img++;
                num_s ++;
                buttoncount.setText("You have taken " + String.valueOf(total_img) + "image." + "\n" +
                        String.valueOf(num_s) + " images are smiling."+ String.valueOf(num_n) + " images are not smiling.");

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);

            }
        });


//        @Override
//        public void onSaveInstanceState(Bundle savedInstanceState) {
//            super.onSaveInstanceState(savedInstanceState);
//            // Save UI state changes to the savedInstanceState.
//            // This bundle will be passed to onCreate if the process is
//            // killed and restarted.
//            savedInstanceState.putBoolean("MyBoolean", true);
//            savedInstanceState.putDouble("myDouble", 1.9);
//            savedInstanceState.putInt("MyInt", 1);
//            savedInstanceState.putString("MyString", "Welcome back to Android");
//            // etc.
//        }

        btnCaptureImage_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_img++;
                num_n ++;
                buttoncount.setText("You have taken " + String.valueOf(total_img) + "image." + "\n" +
                        String.valueOf(num_s) + " images are smiling."+ String.valueOf(num_n) + " images are not smiling.");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);

            }
        });

        taketest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_img++;

                buttoncount.setText("You have taken the test image.");

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 2);
                Log.d("TAG", Integer.toString(result));
            }
        });

        startanalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat test_weights = get_test_weights(eigenvectors, test_image_flat);

                result = find_nearest_neighbour(test_weights, weights, num_s, num_n , knn);
                if (num_s+num_n<4){
                    theta = 1900;
                } else{
                    theta = 2400;
                }
                if (result==0 & smallest_dis > theta) {
                    result = 2;
                }
                if (result==1 & smallest_dis > theta) {
                    result = 3;
                }


                if (result == 0){
                    resultDisplay.setText("Your face is classified as class A (smiling).");
                } else if (result==1){
                    resultDisplay.setText("Your face is classified as class B (Not smiling).");
                } else if (result==2){
                    resultDisplay.setText("You might not be the user in the model or your test image is too off. \n But I guess you are smilling. \n For better result, please generate the modle first");
                } else{
                    resultDisplay.setText("You might not be the user in the model or your test image is too off. \n But I guess you are not smilling. \n For better result, please generate the modle first");
                }
            }
        });

        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap1 != null) {
                    boolean m = false;

                    generateModel();
                    if (eigenvectors!=null){
                        m = true;
                        eigenface1 = getEigenface1();
                        imageDisplay1.setImageBitmap(eigenface1);
                        eigenface2 = getEigenface2();
                        imageDisplay2.setImageBitmap(eigenface2);
                    }


                    if (!m) {
                        resultDisplay.setText("There is something wrong with the model.");
                    } else{
                        resultDisplay.setText("Model is generated. Eigenfaces are shown. Get ready to test!");
                    }
                } else {
                    resultDisplay.setText("Please take some pic first.");
                }
            }
        });

        nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(CameraActivity.this, AnalysisActivity.class);
                next.putExtra("BitmapImage", (Parcelable) bitmap3);
                Log.d("TAG", Integer.toString(result));
                next.putExtra("result", result);
                startActivity(next);
//                startActivity(new Intent(CameraActivity.this, AnalysisActivity.class));


        }
        });
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                File file = getOutputMediaFile();
//
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            bitmap1 = (Bitmap) data.getExtras().get("data");

            imageDisplay1.setImageBitmap(bitmap1);
            Mat mat1 = new Mat();
            Bitmap grey1 = toGrayscale(bitmap1);

//            Bitmap bmp32 = grey1.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(grey1, mat1);
            Size s = mat1.size();
            double rows = s.height;
            double cols = s.width;
            Log.d("TAG", "add s to database"+ Integer.toString((int)rows));
            Log.d("TAG", "add s to database"+ Integer.toString((int)cols));
            add_to_data(mat1, 0);

        }
        if (requestCode==1){
            bitmap2 = (Bitmap) data.getExtras().get("data");

            imageDisplay2.setImageBitmap(bitmap2);
            Bitmap grey2 = toGrayscale(bitmap2);
            Mat mat2 = new Mat();
            Utils.bitmapToMat(grey2, mat2);
            add_to_data(mat2, 1);


        }
        if (requestCode==2){
            bitmap3 = (Bitmap) data.getExtras().get("data");
            Bitmap grey3 = toGrayscale(bitmap3);
            testimage.setImageBitmap(bitmap3);
            test_image_flat = new Mat();
            Utils.bitmapToMat(grey3, test_image_flat);
            add_to_data(test_image_flat, 2);

        }

    }


    public void add_to_data(Mat mat, int t){
        if (t==0){
            mat_s.push_back(Flatten(mat));
            database.push_back(Flatten(mat));
            Size s = database.size();
            double rows = s.height;
            double cols = s.width;
            Log.d("TAG", "add s to database"+ Integer.toString((int)rows));
            Log.d("TAG", "add s to database"+ Integer.toString((int)cols));

        } else if (t==1){
            mat_n.push_back(Flatten(mat));
            database.push_back(Flatten(mat));
            Size s = database.size();
            double rows = s.height;
            double cols = s.width;
            Log.d("TAG", "add n to database"+ Integer.toString((int)rows));
            Log.d("TAG", "add n to database"+ Integer.toString((int)cols));

        }
        else if (t==2) {
            test_image_flat = Flatten(mat);

        }
    }



    public void generateModel() {

        Core.PCACompute(database, mean, eigenvectors);

//        System.out.println("Printing the database dump");
//        System.out.println(database.dump());
//
//        System.out.println("Printing the mean dump");
//        System.out.println(mean.dump());

//        Log.d("TAG", "mean row!!!!!!!!!!!!!!!!"+ Integer.toString((int)rows));
//        Log.d("TAG", "mean col!!!!!!!!!!!!!!!!"+ Integer.toString((int)cols));
        Size s = eigenvectors.size();
        double rows = s.height;
        double cols = s.width;
        Log.d("TAG", "pca row!!!!!!!!!!!!!!!!"+ Integer.toString((int)rows));
        Log.d("TAG", "pca col!!!!!!!!!!!!!!!!"+ Integer.toString((int)cols));
//        System.out.println("Printing the eigenv dump");
//        System.out.println(eigenvectors.dump());

        Mat weight_single = new Mat();
        Mat weights_total = new Mat();
        for (int i=0; i<(int)mat_s.size().height; i++){
            Mat smile = mat_s.row(i);

            Core.PCAProject(smile, mean, eigenvectors, weight_single);
            System.out.println("insert weights");
            System.out.println(weight_single.dump());
            System.out.println(weights.dump());
            weights_total.push_back(weight_single);
        }
        Mat weight_s = weight_single;


        for (int i=0; i<(int)mat_n.size().height; i++){

            Mat not = mat_n.row(i);

            Core.PCAProject(not, mean, eigenvectors, weight_single);
            weights_total.push_back(weight_single);
        }
        Mat weight_n = weight_single;


        weights = weights_total;
        Size s1 = weights.size();
        double rows1 = s1.height;
        double cols1 = s1.width;
        Log.d("TAG", "weights row!!!!!!!!!!!!!!!!"+ Integer.toString((int)rows1));
        Log.d("TAG", "weights col!!!!!!!!!!!!!!!!"+ Integer.toString((int)cols1));
        System.out.println("Printing the weight dump");
        System.out.println(weights.dump());

        weight_diff = find_dis(weight_n, weight_s);
        Toast.makeText(getBaseContext(), "Model created! " , Toast.LENGTH_SHORT).show();
        Log.d("TAG", Double.toString(weight_diff));




    }



    public Bitmap getEigenface1() {
        Mat first_eigenvector = eigenvectors.row(0);
        Bitmap eigenface = matrow_to_bitmap(first_eigenvector,  w,  h);

        return eigenface;
    }

    public Bitmap getEigenface2() {
        Mat first_eigenvector = eigenvectors.row(1);
        Bitmap eigenface = matrow_to_bitmap(first_eigenvector,  w,  h);

        return eigenface;
    }

    public Bitmap matrow_to_bitmap(Mat m, int w, int h) {
        Mat first_eigenvector = m;

//        Bitmap bitmap;
        Mat src = first_eigenvector.reshape(0,h);

        Mat dst_norm = new Mat();
        Mat dst_norm_scaled = new Mat();
        Core.normalize(src, dst_norm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(dst_norm, dst_norm_scaled);

        Bitmap bitmap = Bitmap.createBitmap(dst_norm_scaled.cols(), dst_norm_scaled.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst_norm_scaled, bitmap);

//        bitmap = Bitmap.createBitmap(w, h,Bitmap.Config.ARGB_8888);
//
//        Utils.matToBitmap(src, bitmap);

        Log.d("TAG", "eigenface w"+ Integer.toString(bitmap.getWidth()));
        Log.d("TAG", "eigenface h"+ Integer.toString(bitmap.getHeight()));


        return bitmap;
    }

    Mat get_test_weights(Mat m, Mat img)
    {
        Mat test_weights = new Mat();
        Core.PCAProject(img, mean, m, test_weights);
        System.out.println("Got test weights");
        System.out.println(test_weights.dump());
        return test_weights;
    }

    int find_nearest_neighbour(Mat test_w, Mat weights, int num_s, int num_n, int k)
    {

        double[] dist_list = new double[num_n+num_s];
        int[] rank_list = new int[k];


        for (int i=0; i<(int) num_s+num_n; i++){
            Mat r = weights.row(i);
            double d = find_dis(r,test_w);
            dist_list[i] = d;
        }
        System.out.println("dist_list");
        System.out.println(dist_list);

        int s = 0;
        int n = 0;
        for (int i=0; i<k; i++) {

            int ind = findsmallest_delete(dist_list, i);
            if (i==0){
                System.out.println("smallest dis");
                dis.setText("smallest dis is " + smallest_dis);

            }
            rank_list[i] = ind;
            if (ind==0){
                s+=1;
            } else{
                n+=1;
            }
        }
        System.out.println("s and n");
        System.out.println(s);
        System.out.println(n);

        if (s > n){
            return 0;
        }
        return 1;
    }

    int findsmallest_delete(double[] lst, int p){
        int ind = 0;
        double smallest = Double.POSITIVE_INFINITY;
        for (int i=0; i<lst.length; i++) {
            if (lst[i] < smallest) {
                smallest = lst[i];
                ind = i;
            }


        }
        if (p == 0) {
            smallest_dis = smallest;

        }


        lst[ind] = Double.POSITIVE_INFINITY;
        if (ind < num_s) {
            return 0;
        }
        else {
            return 1;
        }

    }

    double find_dis(Mat m1, Mat m2){
        double dist = norm(m1,m2);
        return dist;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {

        h = bmpOriginal.getHeight();
        w = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Mat Flatten(Mat img)
    {
        Mat mat = new Mat();
        Imgproc.cvtColor(img, mat, Imgproc.COLOR_RGB2GRAY);
        mat = mat.reshape(1,1);
        return mat;
    }



}

