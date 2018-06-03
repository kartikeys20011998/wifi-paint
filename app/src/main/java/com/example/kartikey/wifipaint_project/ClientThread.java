package com.example.kartikey.wifipaint_project;

import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class ClientThread implements Runnable
{
    private static final String TAG="Client Thread :";

    //indexes for substrings
    int b,x,y,pc,ea;

    //DATA VARIABLES
    String sdata="CLIENT SIDE SENDING THE DATA";
    Drawing_view drawingView;


    //CODE FOR DATA SENDING (CLIENT)

    //VARIABLES TO BE PREPARED
    float brushSize2,touchX,touchY;
    int paintColor;
    int eventAction,erase2;

    Data_collector data;
    DatagramSocket socket=null;

    //COUNTING PACKET VARIABLES
    int sendCount=1;
    int recieveCount=0;

    //Extracting the screen resolution
    int width,height;

    //DATA SENDING VARIABLE (BYTE ARRAY)
   // byte[] sendData=new byte[1024];
    byte[] recieveData=new byte[1024];

     String data_items[]=new String[5];

//  HOST ADDRESS AND PORT NUMBER
    InetAddress hostAddress;
    int mport=0;

    public ClientThread(InetAddress hostaddress,int port,Drawing_view drawing_view,int w,int h) {
        hostAddress=hostaddress;
        mport=port;
         drawingView=drawing_view;
         height=h;
         width=w;
    }

    @Override
    public void run() {
       //Confirming the host address and port number
        if(hostAddress!=null && mport!=0)
        {
            //starting an infinite loop to give out non-stop packets
            while(true)
            {
                try{
                    if(socket==null) {
                        socket = new DatagramSocket(mport);
                        socket.setSoTimeout(1000);
                    }
                }
                catch (IOException e)
                {
                    if(e.getMessage()==null)
                        Log.e("Set Socket","UNKNOWN MESSAGE");
                    else
                        Log.e("Set Socket",e.getMessage());
                }

                //READY TO SEND THE DATA
                //THIS BLOOCK IS AN ATTEMPT OF SENDING A PACKAGE
               //SEND PART

               try
               {
                   //TAKING DRAWING DATA INTO THE DRAWING VARIABLES
                   Data_collector data=new Data_collector();
                    Log.d(TAG,"data object created");
                   data.brushSize2=drawingView.brushSize;
                   data.touchX2=drawingView.touchX;
                   data.touchY2=drawingView.touchY;

                   /*Display display = getWindowManager().getDefaultDisplay();
                   Point size = new Point();
                   display.getSize(size);
                   int maxX = size.x;
                   int maxY = size.y;*/

                   data.paintColor=drawingView.getPaintColor();
                   data.eventAction=drawingView.event_action;
                   if(drawingView.erase)
                       data.erase2=1;
                   else
                       data.erase2=0;
                   data.width=width;
                   data.height=height;

                   ByteArrayOutputStream bos=new ByteArrayOutputStream();
                   ObjectOutput out=new ObjectOutputStream(bos);

                   out.writeObject(data);//Objects you want to serialize has to be marked Serializable.
                   //converting data into byte array
                   //1.sendData=(String.valueOf(brushSize2)+"\n"+String.valueOf(touchX)+"\n"+String.valueOf(touchY)+"\n"+paintColor+"\n"+String.valueOf(eventAction)+"\n"/*erase2+"\n"*/+String.valueOf(width)+"\n"+String.valueOf(height)+"\n").getBytes();
                  byte sendData[]=bos.toByteArray();

                   //2.sendData=(String.valueOf(brushSize2)+"\n"+String.valueOf(touchX)+"\n"+String.valueOf(touchY)+"\n"+paintColor+"\n"+String.valueOf(eventAction)+"\n"+erase2+"\n").getBytes();
                       sendCount++;//increasing sendCount to keep track of number of packets sent
                   //UDP packet created using the data,length and destination info
                   DatagramPacket packet=new DatagramPacket(sendData,sendData.length,hostAddress,mport);
                   socket.send(packet);
                   System.out.println("CLIENT PACKET SENT");
                   System.out.println("PACKETS SENT FROM CLIENT SIDE : "+sendCount);
               }
               catch (IOException exec)
               {
                   if(exec.getMessage()==null)
                       Log.d(TAG,"UNKNOWN MESSAGE : LIKELY TIMEOUT ");
                   else
                       Log.d(TAG,exec.getStackTrace().toString());
                   break;
               }

               //RECIEVING THE PACKET
                try
                {
                  DatagramPacket packet=new DatagramPacket(recieveData,recieveData.length);

                  socket.receive(packet);
                    byte rdata[]=packet.getData();//extracting data from the packet
                    //=new String(packet.getData(),0,packet.getLength());
                  //increasing the recieveCount to count the number of data packets recieved
                    recieveCount++;
                    ByteArrayInputStream bis=new ByteArrayInputStream(rdata);
                    ObjectInput obj=new ObjectInputStream(bis);
                    data=(Data_collector)obj.readObject();
                    if(data==null)
                        System.out.println("DATA OBJECT RECIEVED on Client Side is EMPTY");
                    System.out.println("PACKETS RECIEVED ON CLIENT SIDE : "+recieveCount);

                }
                catch( ClassNotFoundException exec)
                {
                    if(exec.getMessage()==null)
                        Log.e(TAG,"CLASS NOT FOUND EXCEPTION");
                    else
                        Log.e(TAG,exec.getMessage());
                }
                catch (IOException exec)
                {
                    if(exec.getMessage()==null)
                        Log.e("Set Socket","UNKNOWN MESSAGE:PACKET NOT RECIEVED ");
                    else
                        Log.e("Set Socket",exec.getMessage());
                    continue;
                }


            }

        }
    }


    public float getTouchX2()
    {
        return data.touchX2;
    }
    public float getTouchY2()
    {
        return data.touchY2;
    }

    public float getBrushSize2()
    {
        return data.brushSize2;
    }

    public int getPaintColor2()
    {
        return data.paintColor;
    }

    public int getEventAction2()
    {
        return data.eventAction;
    }

    public int getWidth2()
    {
        return data.width;
    }

    public int getHeight2()
    {
        return data.height;
    }

  /*  public String getSdata() {
        return sdata;
    }

    public String getRdata() {
        return rdata;
    }*/
}
