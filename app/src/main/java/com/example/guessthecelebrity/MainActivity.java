package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String>celebUrl=new ArrayList<String>();
    ArrayList<String>celebName=new ArrayList<String>();
    Button b0;
    Button b1;
    Button b2;
    Button b3;
    String[] s=new String[4];
    ImageView imageview;
    int choose=0,locationOfCorrectAnswer=0;

    public void chosen(View view)
    {
        Log.i(view.getTag().toString(),Integer.toString(locationOfCorrectAnswer));
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(getApplicationContext(),"Correct!!",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Wrong! The correct answer is "+celebName.get(choose),Toast.LENGTH_LONG).show();
        }
        getNextQuestion();
    }

    public class DownloadImage extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream in=urlConnection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class Downloadurl extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url=null;
            HttpURLConnection urlConnection=null;
            try
            {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1)
                {
                    char c=(char)data;
                    result+=c;
                    data=reader.read();
                }
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageview=(ImageView)findViewById(R.id.imageView);
        b0=(Button)findViewById(R.id.button0);
        b1=(Button)findViewById(R.id.button1);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        Downloadurl task=new Downloadurl();
        try {
            String result=task.execute("http://www.posh24.se/kandisar").get();
            String[] string=result.split("<div class=\"col-xs-12 col-sm-6 col-md-4\">");
            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(string[0]);
            while(m.find())
            {
                celebUrl.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(string[0]);
            while (m.find())
            {
                celebName.add(m.group(1));
            }
           getNextQuestion();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void getNextQuestion()
    {
        Random rand=new Random();
        choose=rand.nextInt(celebName.size());
        DownloadImage taskimage=new DownloadImage();
        Bitmap myBitmap= null;
        try {
            myBitmap = taskimage.execute(celebUrl.get(choose)).get();
            imageview.setImageBitmap(myBitmap);
            locationOfCorrectAnswer=rand.nextInt(4);
            for (int i=0;i<4;i++)
            {
                if(i==locationOfCorrectAnswer)
                {
                    s[i]=celebName.get(choose);
                }
                else
                {
                    int x;
                    x=rand.nextInt(celebName.size());
                    while(x==choose)
                    {
                        x=rand.nextInt(celebName.size());
                    }
                    s[i]=celebName.get(x);
                }
            }
            b0.setText(s[0]);
            b1.setText(s[1]);
            b2.setText(s[2]);
            b3.setText(s[3]);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
