package com.example.kartikey.wifipaint_project;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View.OnClickListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.EmptyStackException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    //PAINT REFERNCES
    private Drawing_view drawView;
    private float smallBrush,mediumBrush,largeBrush;
    private ImageButton currPaint,drawBtn,eraseBtn,newBtn,saveBtn;

  //WIFI CONNECTIVITY REFERENCES
    float s;

 //runOnUiThread Variables
    float brushSize2,touchX,touchY;
    int paintColor; //paint Color
    int eventAction,erase;

 //Extracting the screen resolution
    int w,h,w2,h2;
    float res_ration; //RESOLUTION Ratio

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
        setContentView(R.layout.activity_main);
        //Display object to be created for sending resolution of current phone to other
        Display d=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //PAINT SETUP
        smallBrush=getResources().getInteger(R.integer.small_size);
        mediumBrush=getResources().getInteger(R.integer.medium_size);
        largeBrush=getResources().getInteger(R.integer.large_size);

        drawView=(Drawing_view)findViewById(R.id.drawing);
        drawView.setBrushSize(mediumBrush);
        drawBtn=(ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        eraseBtn=(ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn=(ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn=(ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        LinearLayout paintColors=(LinearLayout)findViewById(R.id.paint_colors);
        currPaint=(ImageButton)paintColors.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


        //CURRENT DEVICE RESOLUTION TO BE SEND
        w2=d.getWidth();
        h2=d.getHeight();
        drawView.w=w2;
        drawView.h=h2;

        //WIFI CONNECTIVITY(DATA TRANSFER)

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
                serverThread=new ServerThread(port,drawView,w2,h2);  //NEED TO PASS DRAW VIEW OBJECT
                new Thread(serverThread).start();
            }
            else
            {
                clientThread=new ClientThread(hostAddress,port,drawView,w2,h2);  //NEED TO PASS DRAW VIEW OBJECT
                new Thread(clientThread).start();
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
                             try
                             {
                                 brushSize2 =serverThread.getBrushSize2();
                                 touchX=serverThread.getTouchX2();
                                 touchY=serverThread.getTouchY2();
                                 paintColor=serverThread.getPaintColor2();
                                 eventAction=serverThread.getEventAction2();
                                 w=serverThread.getWidth2();
                                 h=serverThread.getHeight2();
                                 System.out.println("brushSize2 in Main: "+brushSize2);
                                 System.out.println("paintColor in Main: "+paintColor);
                                 System.out.println("eventAction in Main: "+eventAction);
                                 System.out.println("w in Main: "+w);
                                 System.out.println("y in Main: "+h);
                                 System.out.println("touchX2 in Main: "+touchX);
                                 System.out.println("touchY2 in Main: "+touchY);

                                 /*erase=serverThread.getErasureStatus();
                                 System.out.println("Value of Erase"+erase);
                                 if(erase==1) {
                                  Toast.makeText(getApplicationContext(),"ERASE SET TO TRUE ",Toast.LENGTH_LONG).show();
                                     drawView.setErase2(true);
                                 }
                                 else {
                                     Toast.makeText(getApplicationContext(),"ERASE ELSE EXECUTED",Toast.LENGTH_LONG).show();
                                     drawView.setErase2(false);
                                 }*/
                                 System.out.println("SERVER TASK STARTED");
                                 drawView.setupDrawing2(serverThread.getPaintColor2(),serverThread.getBrushSize2());
                                 drawView.onOtherTouchEvent(serverThread.getEventAction2(),serverThread.getTouchX2(),serverThread.getTouchY2(),w,h);
                             }
                             catch (Exception e)
                             {
                                 System.out.println("SERVER runOnUiTHread EXCEPTION : "+e);
                             }
                        }
                    });

                }
                else if(!host)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // p1TextView.setText("CLIENT : "+clientThread.getSdata());
                                //p2TextView.setText("SERVER : "+clientThread.getRdata());
                                brushSize2=clientThread.getBrushSize2();
                                touchX=clientThread.getTouchX2();
                                touchY=clientThread.getTouchY2();
                                paintColor=clientThread.getPaintColor2();
                                 eventAction=clientThread.getEventAction2();
                                w=clientThread.getWidth2();
                                h=clientThread.getHeight2();
                                System.out.println("brushSize2 in Main: "+brushSize2);
                                System.out.println("paintColor in Main: "+paintColor);
                                System.out.println("eventAction in Main: "+eventAction);
                                System.out.println("w in Main: "+w);
                                System.out.println("y in Main: "+h);
                                System.out.println("touchX2 in Main: "+touchX);
                                System.out.println("touchY2 in Main: "+touchY);
                                drawView.setupDrawing2(paintColor,brushSize2);
                                drawView.onOtherTouchEvent(eventAction,touchX,touchY,w,h);
                                System.out.println("CLIENT TASK STARTED");
                            }
                            catch(Exception e)
                            {
                                System.out.println("CLIENT runOnUiThread Exception : "+e);
                            }

                        }
                    });
                }
            }
        };
        mtimer.schedule(mtimerTask,10,10);
        super.onResume();
    }

    public void paintClicked(View v)
    {
        if(v!=currPaint)
        {
            ImageButton imgView=(ImageButton)v;
            String color=v.getTag().toString();
            drawView.setErase(false);
            drawView.setBrushSize(drawView.getLastBrushSize());
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)v;

        }
    }
    @Override
    public void onClick(View view)
    {
        // for drawing button
        if(view.getId()== R.id.draw_btn) {
            Toast.makeText(getApplicationContext(), "BRUSH BUTTON CLICKED", Toast.LENGTH_LONG).show();
            try {
                final Dialog brushDialog = new Dialog(MainActivity.this);
                brushDialog.setTitle(" BRUSH SIZE ");
                brushDialog.setContentView(R.layout.brush_size);
                brushDialog.show();
                ImageButton smallButton = (ImageButton)brushDialog.findViewById(R.id.small_brush);

                smallButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(false);
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton medBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                medBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(false);
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton lrgBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                lrgBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(false);
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
            }
            catch (Exception e)
            {
                System.err.println("EXCEPTION OCCURRED : "+e);
            }
        }
        else // ERASURE BUTTON
            if(view.getId()==R.id.erase_btn)
            {
                final Dialog erase_dialog=new Dialog(MainActivity.this);
                erase_dialog.setTitle("ERASER SIZE");
                erase_dialog.setContentView(R.layout.brush_size);
                erase_dialog.show();
                ImageButton smallBtn=(ImageButton)erase_dialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       drawView.setErase(true);
                       drawView.setBrushSize(smallBrush);
                       erase_dialog.dismiss();
                    }
                });
                final ImageButton medBtn=(ImageButton)erase_dialog.findViewById(R.id.medium_brush);
                medBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        erase_dialog.dismiss();
                    }
                });
                ImageButton lrgBtn=(ImageButton)erase_dialog.findViewById(R.id.large_brush);
                lrgBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        erase_dialog.dismiss();
                    }
                });
            }
            else
                if(view.getId()==R.id.new_btn)
                {
                   final AlertDialog.Builder new_dialog=new AlertDialog.Builder(MainActivity.this);
                    new_dialog.setTitle(" NEW CANVAS ");
                    new_dialog.setMessage("DO YOU WANT TO START NEW DRAWING (you will lose the current drawing)");
                    new_dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                         drawView.startNew();
                         dialog.dismiss();
                        }
                    });
                    new_dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    new_dialog.show();
                }
            else
                {
                  AlertDialog.Builder save_dialog=new AlertDialog.Builder(MainActivity.this);
                  save_dialog.setTitle("SAVE DRAWING");
                  save_dialog.setMessage("SAVE DRAWING TO GALLERY");
                  save_dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                       drawView.setDrawingCacheEnabled(true);
                       String img_url= MediaStore.Images.Media.insertImage(getContentResolver(),drawView.getDrawingCache(), UUID.randomUUID().toString(),"DRAWING");
                       if(img_url != null) {
                           Toast.makeText(getApplicationContext(), "IMAGE SAVED SUCCESSFULLY ", Toast.LENGTH_LONG).show();
                           drawView.destroyDrawingCache();
                       }
                        else
                            Toast.makeText(getApplicationContext(),"OOps! image cant be saved ",Toast.LENGTH_LONG).show();

                      }
                  });
                  save_dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                      }
                  });
                    save_dialog.show();
                }
    }


}
