package by.dagonwat.beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import by.dagonwat.beacon.*;

public class MainActivity extends AppCompatActivity
{
    TextView txt;
    String size = "";
    ArrayList<String> beaconAddress;
    ArrayList<String> beaconName;
    ArrayList<String> beaconPicSize;

    //ip address and socket from which we will get info
    public static final String SERVER_NUM = "178.124.203.226";
    public static final int SOCKET_NUM = 10000;

    public final static String BROADCAST_ACTION = "by.dagonwat.serviceback";
    public final static String SIZE = "size";
    public final static String SERVER = "server";
    public final static String SOCKET = "socket";

    BroadcastReceiver br;

    //decode the only string server gives us into arrays
    private void decode(String a)
    {
        String[] parts = a.split("-");

        for (int i = 0; i < parts.length; i += 3)
        {
            beaconAddress.add(parts[i]);
            beaconName.add(parts[i + 1]);
            beaconPicSize.add(parts[i + 2]);
        }
    }

    //blit the image of beacon
    public void decodeImage(byte[] a)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(a, 0, a.length);
        ImageView image = (ImageView) findViewById(R.id.pic);
        image.setImageBitmap(bmp);
    }

    //start looking for beacons near
    public void startDiscovery(View view)
    {
        //Timer tim = new Timer();
        //TimerTask bthh = new MyTimerTask();
        //tim.schedule(bthh, 200, 500);
    }

    public void askPicture(int num)
    {
        Intent intent = new Intent(this, GetImageService.class);
        intent.putExtra(SIZE, beaconPicSize.get(num));
        intent.putExtra(SERVER, SERVER_NUM);
        intent.putExtra(SOCKET, SOCKET_NUM);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.text);

        br = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                decodeImage(intent.getByteArrayExtra("result"));
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);

        //new GetInformation().execute("");
    }

    public class GetInformation extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String fserver = "";
            try
            {
                Socket fromserver = new Socket(SERVER_NUM, SOCKET_NUM);
                PrintWriter out = new
                        PrintWriter(fromserver.getOutputStream(),true);
                BufferedReader in  = new BufferedReader(new
                        InputStreamReader(fromserver.getInputStream()));
                fserver = in.readLine();
            }
            catch (IOException e)
            {
                Thread.interrupted();
            }

            return fserver;
        }

        @Override
        protected void onPostExecute(String result)
        {
            decode(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}


