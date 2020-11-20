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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;

public class CameraActivity extends AppCompatActivity {

   Button btnCaptureImage_s;
   Button btnCaptureImage_n;
   Button startanalysis;
   Button taketest;
   ImageView imageDisplay1;
   ImageView imageDisplay2;
   Bitmap bitmap1;
   Bitmap bitmap2;

   TextView resultDisplay;

   TextView greeting;
   TextView buttoncount;
   int num_s = 0;
   int num_n = 0;
   int total_img = 0;
   int w;
   int h;

   Mat database = new Mat();

   int[][] database_s;
   int[][] database_n;

   Bitmap after;
   Bitmap test_image;
   int[] test_flat_img;




   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_camera);

       btnCaptureImage_s = (Button) findViewById(R.id.smilebtn);
       btnCaptureImage_n = (Button) findViewById(R.id.nobtn);
       imageDisplay1 = (ImageView) findViewById(R.id.displayImageView1);
       imageDisplay2 = (ImageView) findViewById(R.id.displayImageView2);
       startanalysis = (Button) findViewById(R.id.analysisBtn);
       resultDisplay = (TextView) findViewById(R.id.result);
       greeting = (TextView) findViewById(R.id.HETitle);
       buttoncount = (TextView) findViewById(R.id.buttoncount);

       taketest = (Button) findViewById(R.id.test);

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
           }
       });

       startanalysis.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (bitmap1 != null) {

                   boolean result = analysis();
                   after = getAfter();
                   imageDisplay1.setImageBitmap(after);
                   if (result) {
                       resultDisplay.setText("You are smiling.");
                   } else {
                       resultDisplay.setText("You should smile more!");
                   }
               } else {
                   resultDisplay.setText("Please take a pic first.");
               }
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
           Bitmap grey1 = toGrayscale(bitmap1);

           add_to_data(grey1, 0);
           num_s++;
       }
       if (requestCode==1){
           bitmap2 = (Bitmap) data.getExtras().get("data");

           imageDisplay2.setImageBitmap(bitmap2);
           Bitmap grey2 = toGrayscale(bitmap2);
           add_to_data(grey2, 1);
           num_n++;
       }
       if (requestCode==2){
           test_image = (Bitmap) data.getExtras().get("data");
           Bitmap grey3 = toGrayscale(test_image);
           add_to_data(grey3, 2);

       }

   }

   boolean analysis() {
       boolean found = true;
       Mat eigenvector = testPCACompute();


       return found;
   }

   public Bitmap getAfter() {

       return after;
   }


   public Mat testPCACompute() {
       database = new Mat(num_s+num_n, w*h, CvType.CV_32S){
           {
               for (int i = 0 ; i < num_s; i++){
                   database.put(i,0, database_s[i]);}
               for (int i = 0 ; i < num_n; i++){
                   database.put(i,0, database_n[i]);}
           }
       };
       Mat mean = new Mat();
       Mat vectors = new Mat();

       Core.PCACompute(database, mean, vectors);

//        Mat test_img_m = new Mat();
//        test_img_m.put(0,0,test_flat_img);
//
//        Mat compressed = new Mat();
//        compressed.create(test_img_m.rows(), num_s+num_n, test_img_m.type());
//        Mat reconstructed = new Mat();
//        for( int i = 0; i < test_img_m.rows(); i++ )
//        {
//            Mat coeffs = compressed.row(i);
//            Mat vec = Mat(test_img_m.row(i), coeffs, reconstructed);
//            // compress the vector, the result will be stored
//            // in the i-th row of the output matrix
//            pca.project(vec, coeffs);
//            // and then reconstruct it
//            pca.backProject(coeffs, reconstructed);
//            // and measure the error
//            printf("%d. diff = %g\n", i, norm(vec, reconstructed, NORM_L2));
//        }
       return vectors;

   }


   public void add_to_data(Bitmap bitmap, int t){
       if (t==0){

           int[] coverImageIntArray1D = new int[w * h];
           bitmap.getPixels(coverImageIntArray1D, 0, w, 0, 0, w, h);
           Log.d("TAG", "convert to 1d len: "+ Integer.toString(coverImageIntArray1D.length));
           int length;
           if (database_s==null){
               database_s = new int[][] {coverImageIntArray1D};
           }
           else {
               length = database_s.length;
               int[][] newArray = Arrays.copyOf(database_s, length+1);
               newArray[length] = coverImageIntArray1D;
               database_s = newArray;
           }

           Log.d("TAG", "add to database_s: "+ Integer.toString(database_s.length));

       } else if (t==1){
           int[] coverImageIntArray1D = new int[w * h];
           bitmap.getPixels(coverImageIntArray1D, 0, w, 0, 0, w, h);
           Log.d("TAG", "convert to 1d len: "+ Integer.toString(coverImageIntArray1D.length));
           int length;
           if (database_n==null){
               database_n = new int[][] {coverImageIntArray1D};
           }
           else {
               length = database_n.length;
               int[][] newArray = Arrays.copyOf(database_n, length+1);
               newArray[length] = coverImageIntArray1D;
               database_n = newArray;
           }

           Log.d("TAG", "add to database_s: "+ Integer.toString(database_n.length));
       }
       else if (t==2) {
           int[] coverImageIntArray1D = new int[w * h];
           bitmap.getPixels(coverImageIntArray1D, 0, w, 0, 0, w, h);
           Log.d("TAG", "convert test img to 1d len: "+ Integer.toString(coverImageIntArray1D.length));
           int length;
           test_flat_img = coverImageIntArray1D;

       }
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



}

