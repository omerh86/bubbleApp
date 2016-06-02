package com.example.admin.bubbleapp;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;

import at.markushi.ui.CircleButton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    TextView breathTv;
    String stringFromServer = "";
    ToneGenerator toneG;
    com.michaldrabik.tapbarmenulib.TapBarMenu tapBarMenu;
    private Handler handler;
    at.markushi.ui.CircleButton b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new LongOperation().execute(null, null, null);
            }
        }, 3000);
        tapBarMenu = (com.michaldrabik.tapbarmenulib.TapBarMenu) findViewById(R.id.tapBarMenu);
        tapBarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapBarMenu.toggle();
            }
        });
        //breathTv = (TextView) findViewById(R.id.breathTv);
        //Button reaload = (Button) findViewById(R.id.reload);
//        reaload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new LongOperation().execute(null, null, null);
//
//            }
//        });

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Room A", "Room B", "Room C", "Room D");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Log.d("spinner", "pressed " + item);
                Data data = Data.getinstance();
                if (position > 0) {
                    data.room = null;
                } else {
                    data.room = item;
                }
            }
        });

        b = (CircleButton) findViewById(R.id.signBtn);
        b.setAnimationProgress(30);
        b.setPressed(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public String httpReq(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        Log.d("ma", "" + response.toString());
        return response.body().string();
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String s = "nothing";
            try {
                s = httpReq("http://91.228.127.197:3000/check");

            } catch (IOException e) {
                Log.d("ma", "" + e.toString());
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new LongOperation().execute(null, null, null);
                }
            }, 3000);
            return s;

        }

        @Override
        protected void onPostExecute(String result) {
            //breathTv.setText(result);
            // b.setAnimationProgress(30);
            b.setPressed(!b.isPressed());
            if (!stringFromServer.equals(result)) {
                stringFromServer = result;
                if (result.equals("false")) {
                    b.setColor(Color.RED);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 1000);
                    Toast.makeText(getBaseContext(), "false", Toast.LENGTH_SHORT).show();
                } else {
                    b.setColor(Color.GREEN);
                    Toast.makeText(getBaseContext(), "true", Toast.LENGTH_SHORT).show();
                }
            }


        }

    }

}
