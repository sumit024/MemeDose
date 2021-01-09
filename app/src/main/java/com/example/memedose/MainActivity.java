package com.example.memedose;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
   private  ImageView imageView;
   private FloatingActionButton next, share,download;
    private ProgressBar progressBar;
     String currentUrl;
     private RequestQueue queue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.memeImageView);
        next=findViewById(R.id.next);
        share=findViewById(R.id.share);
        download=findViewById(R.id.download);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedDownload(currentUrl);

            }


        });
        progressBar=findViewById(R.id.progressBar);
        // Instantiate the RequestQueue
        //queue = Volley.newRequestQueue(this);
        queue=VolleySingleton.getInstance(this).getRequestQueue();
        loadMeme();

    }
    private void onClickedDownload(String url)
    {
//        Intent intent=new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        startActivity(intent);

        Uri uri= Uri.parse(url);


        DownloadManager downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);


        request.setTitle("Downloading your meme");
        request.setDescription("Fetching from reddit");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        File dir= new File(Environment.getExternalStorageDirectory()+"/Save Image/");
        if(!dir.exists())
        {
            dir.mkdir();
        }

        request.setDestinationInExternalPublicDir(String.valueOf(dir),System.currentTimeMillis()+".png");
        request.setMimeType("*/*");
        downloadManager.enqueue(request);

    }
    private void loadMeme()
    {
        progressBar.setVisibility(View.VISIBLE);


        String url =" https://meme-api.herokuapp.com/gimme";
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                try {
                    currentUrl =response.getString("url");
                    Glide.with(getApplicationContext()).load(currentUrl).placeholder(R.drawable.wait).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(GONE);
                            return false;
                        }
                    }).into(imageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Occurred!",Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(jsonObjectRequest);
    }

    public void share(View view) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Hey! Got a cool meme for you "+currentUrl);
        startActivity(Intent.createChooser(intent,"Share"));

    }

    public void next(View view) {
        loadMeme();
    }

    public void download(View view) {



    }
}