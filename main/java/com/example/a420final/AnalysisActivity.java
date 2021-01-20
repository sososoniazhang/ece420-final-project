package com.example.a420final;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnalysisActivity extends AppCompatActivity {
    ImageView imageView1;
    TextView result;
    EditText log;
    Button savelog;
    String filename = "saved_data";
    List<String> addArray = new ArrayList();
    Button showlog;
    TextView showmsg;
    String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        result = (TextView) findViewById(R.id.result);
        log = (EditText) findViewById(R.id.log);
        savelog = (Button) findViewById(R.id.savelog);
        showlog = (Button) findViewById(R.id.showlog);
        showmsg = (TextView) findViewById(R.id.showmsg);


        Intent intent = getIntent();
        Bitmap testimg = (Bitmap) intent.getParcelableExtra("BitmapImage");
        int test_result = intent.getIntExtra("result", 0);
        if (test_result == 0) {
            result.setText("Share the things that make you happy!");
        } else if (test_result == 1) {
            result.setText("You should smile more. Let me know how you feel right now.");
        }

        imageView1.setImageBitmap(testimg);


        savelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> addArray = new ArrayList();
                msg += log.getText().toString() + "          ";
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                msg += currentDate + "         ";

                msg += currentTime + "         ";
                msg += "\n";
                try {
                    FileOutputStream Fops = openFileOutput(filename, Context.MODE_PRIVATE);
                    Fops.write(msg.getBytes());
                    Fops.close();
                    Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
                    addArray.add(msg);

                    ((EditText) findViewById(R.id.log)).setText("");

                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        showlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> addArray = new ArrayList();
                showmsg.setText(msg);
                try {

                    Log.d("TAG", "reading the file");
                    String msg = "this is the msg";
                    InputStream inputreader = getAssets().open(filename);
                    Log.d("TAG", "buffer");
                    BufferedReader buffreader = new BufferedReader(new InputStreamReader(inputreader));
                    Log.d("TAG", "buffer");


                    boolean hasNextLine = true;
                    while (hasNextLine) {
                        String line = buffreader.readLine();
                        addArray.add(line);
                        msg += line;
                        Log.d("TAG", msg);
                        hasNextLine = line != null;
                    }


                    inputreader.close();
                    showmsg.setText(msg);
                    }
                    catch(java.io.FileNotFoundException e){

                    }catch(java.io.IOException e){

                    }

                }


            });

        }
}

