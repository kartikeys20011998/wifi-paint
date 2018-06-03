package com.example.kartikey.wifipaint_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class DataTransferDisplay extends AppCompatActivity {

    TextView p1TextView;
    TextView p2TextView;
    TextView p3TextView;
    //Drawing_view drawingView;
    ClientThread clientThread;
    ServerThread serverThread;

    String hostaddress;
    InetAddress hostAddress;

    Timer mtimer;
    TimerTask mtimerTask;

    Intent intent;

    Boolean host;

    int port=8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer_display);
        p1TextView=(TextView)findViewById(R.id.p1TextView);
        p2TextView=(TextView)findViewById(R.id.p2TextView);
        p3TextView=(TextView)findViewById(R.id.p3TextView);

        intent=getIntent();
    if(intent.getBooleanExtra("Connected",false))
    {
       //getting the host address in string form
        hostaddress=intent.getStringExtra("HostAddress");

        try{
            //converting host address from string to InetAddress
            hostAddress=InetAddress.getByName(hostaddress);
        }
        catch(Exception e)
        {
            System.out.println("EXCEPTION OCCURRED :"+e);
        }
         //Determine whether the device is a host or a client
        host=intent.getBooleanExtra("Host",false);
        if(host)
        {
            //serverThread=new ServerThread(port,drawingView);
            //new Thread(serverThread).start();

          //  p1TextView.setText("SERVER  : "+serverThread.getsData());
        //    p2TextView.setText("CLIENT : "+serverThread.getrData());
        }
        else
        {
          //clientThread=new ClientThread(hostAddress,port,drawingView);
            //new Thread(clientThread).start();
  //          p1TextView.setText("CLIENT : "+clientThread.getSdata());
//            p2TextView.setText("SERVER : "+clientThread.getRdata());

        }
    }

    }

    @Override
    protected void onResume() {

        mtimer=new Timer();
        mtimerTask=new TimerTask() {
            @Override
            public void run() {
                //uses a side thread to update the UI outside of the timer
                if(host)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("SERVER TASK STARTED");

                            p1TextView.setText("SERVER  : "+serverThread.getsData());
                            //p2TextView.setText("CLIENT MSG : "+serverThread.getClientMsg());
                            //p3TextView.setText("CLIENT COUNT  : "+serverThread.getClientCount());
                            //p1TextView.setText("SERVER  : ")
                            //p2TextView.setText("CLIENT : "+serverThread.getClientMsg());
                        }
                    });

                }
                else if(!host)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         //  p1TextView.setText("CLIENT : "+clientThread.getSdata());
                          //  p2TextView.setText("SERVER MSG : "+clientThread.getServerMsg());
                          //  p3TextView.setText("SERVER COUNT  : "+clientThread.getServerCount());
                            System.out.println("CLIENT TASK STARTED");
                           // p1TextView.setText("CLIENT  : ");
                          // p2TextView.setText("SERVER  : "+clientThread.getRdata());
                        }
                    });
                }
            }
        };
        mtimer.schedule(mtimerTask,10,10);
        super.onResume();
    }
}
