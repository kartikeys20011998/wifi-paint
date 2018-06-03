package com.example.kartikey.wifipaint_project;

import android.graphics.Path;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Kartikey on 26-07-2017.
 */

public class ServerThread implements Runnable
{

    //character array to store the recieved data
    char[] paintdata;

    //DATA RECIEVING OBJECT
      Data_collector data;

    //lower indexes for substrings
    int b,x,y,pc,ea,w,h;

    DatagramSocket socket=null; // socket for connection

    Drawing_view drawingView;

    String sData="SERVER SIDE SENDING DATA";
    //String rData="NOTHING RECIEVED ON SERVER SIDE";

  private final String TAG="Server Thread :";
   char[] val2;

    int sendCount=1;
    int recieveCount=0;

    InetAddress clientAddress=null;

    byte[] sendData=new byte[1024];
    byte[] recieveData=new byte[1024];

    //EXTRACTING THE SCREEN RESOLUTION
    int width,height;

    boolean gotpacket=false;

    int mport=0;

    public ServerThread(int port,Drawing_view drawing_view,int w,int h) {
      mport = port;
       drawingView=drawing_view;
       width=w;
        height=h;
    }

    @Override
    public void run() {
        //CONFIRMING THE PORT NUMBER
            //infinite loop to recieve the packet
            while(true)
            {
                 //OPENING THE SOCKET
                try{
                    if(socket==null) {
                        socket = new DatagramSocket(mport);
                        socket.setSoTimeout(10000);
                    }

                }
                catch (IOException exec2)
                {
                    if(exec2.getMessage()==null)
                        System.out.println("SOCKET CREATION EXCEPTION OCCURRED : UNKNOWN MESSAGE ");
                    else
                        System.out.println("SOCKET CREATION EXCEPTION OCCURRED : "+exec2.getMessage());;
                }

                //RECIVING PART OF SERVER
                //SERVER CANT SEND THE UNTIL IT RECIEVES THE PACKET
                //creating an empty packet


                // try catch for recieving the packet
                try{
                    //PACKET RECIEVED CODE
                    DatagramPacket packet=new DatagramPacket(recieveData,recieveData.length);
                    System.out.println("WAITING FOR PACKET");
                    socket.receive(packet);
                    byte rData[]=packet.getData();
                    //converting datapacket  into string(DONT CONVERT TO STRING NOT NEEDED,USE SERIALISATION)
                    //rData=new String(packet.getData(),0,packet.getLength());

                 /*   DataInputStream din = new DataInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(),packet.getLength()));
                    x=din.readInt();
                    rData=din.readLine();*/
                    ByteArrayInputStream bis=new ByteArrayInputStream(rData);
                    ObjectInput in=new ObjectInputStream(bis);
                    data=(Data_collector)in.readObject();
                    System.out.println("1.PACKET RECIEVED");
                    if(data==null)
                        Log.d(TAG,"data object recieved is null");
                    System.out.println("2.PACKET RECIEVED");
                    recieveCount++;// counting the number of packets
                    System.out.println("PACKETS RECIEVED ON SERVER SIDE : "+recieveCount);
                    if(clientAddress==null)
                      clientAddress=packet.getAddress();
                }
                catch(ClassNotFoundException c)
                {
                    System.out.println("CLASS NOT FOUND EXCEPTION :"+c.getMessage());
                }
                catch (IOException exec2)
                {
                    if(exec2.getMessage()==null) {
                        System.out.println("EXCEPTION OCCURRED : UNKNOWN MESSAGE ");
                        Log.d(TAG,exec2.toString());
                    }
                    else {
                        System.out.println("EXCEPTION OCCURRED : " + exec2.getMessage());
                        Log.d(TAG,exec2.toString());
                    }
                    continue;
                }

                //try-catch for sending  the packets

                try
                {
                    Data_collector data=new Data_collector();
                    data.brushSize2=drawingView.brushSize;
                    data.touchX2=drawingView.touchX;
                    data.touchY2=drawingView.touchY;
                    if(drawingView.erase)
                        data.erase2=1;
                    else
                        data.erase2=0;
                    data.eventAction=drawingView.event_action;
                    data.paintColor=drawingView.getPaintColor();
                    data.width=width;
                    data.height=height;
                    //SERIALISING THE DATA_COLLECTOR OBJECT
                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                    ObjectOutput out=new ObjectOutputStream(bos);
                    out.writeObject(data); //writing data object to the ObjectOutput Object


                    //checking client addrss is known to us
                    if(clientAddress!=null) {
                        //converting the data into an byte array
                        //sendData = (sData +"\n"+sendCount+"\n").getBytes();
                        byte b[]=bos.toByteArray();
                        sendCount++;//counting the number of packets send
                        //storing the data into a UDP packet
                        DatagramPacket packet = new DatagramPacket(b,b.length, clientAddress, mport);
                         socket.send(packet);
                        System.out.println("SERVER PACKET SENT ");
                        System.out.println("PACKETS SENT FROM SERVER SIDE : "+sendCount);
                    }
                }
                catch (IOException exec2)
                {
                    if(exec2.getMessage()==null)
                        System.out.println("EXCEPTION OCCURRED : UNKNOWN MESSAGE ");
                    else
                        System.out.println("SERVER SENDING EXCEPTION OCCURRED : "+exec2.getMessage());;
                }
            }


        }

    public String getsData() {
        return sData;
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

//    public String getrData() { return rData; }

    /*
    public String getClientMsg() {

        char[] val = new char[60];

     try {
         char[] msg = rData.toCharArray();
         int i = 0;
         while (msg[i]!='\n') {
             val[i]=msg[i];
             i++;
         }
         val[i]='\n';
         val2=new char[i];
         i=0;
         while(val[i]!='\n')
         {
             val2[i]=val[i];
             i++;
         }
         System.out.println(String.valueOf(val2));
     }
     catch(Exception e)
     {
         System.out.println("CLIENT MSG EXCEPTION OCCURRED : "+e);
     }
        return String.valueOf(val2);
    }

    public String getClientCount()
    {

        char[] val=new char[64];
        int i=0,j=0;
        try {
            char[] msg=rData.toCharArray();
            while (msg[i] != '\n') {
                i++;
            }
            i++;
            while (msg[i] != '\n') {
                val[j] = msg[i];
                i++;
                j++;
            }
        }
        catch(Exception e)
        {
            System.out.println("CLIENT COUNT EXCEPTION OCCURRED : "+e);
        }
        return String.valueOf(val);
    }
*/
/*
    //EXTRACTING THE BRUSH SIZE
    public float getBrushSize()
    {
        char[] brushSize=new char[8];
        int i=0,j=0;

        try
        {
       //     paintdata=rData.toCharArray();
            while(paintdata[i] != '\n' )
            {
                brushSize[j]=paintdata[i];
                i++;
                j++;
            }
            i++;
            b=i;
            System.out.println("BRUSH SiZE : "+Float.parseFloat(String.valueOf(brushSize)));
        }
        catch(Exception e)
        {
            System.out.println("BRUSH SIZE EXCEPTION OCCURRED:"+e);
        }

        return Float.parseFloat(String.valueOf(brushSize));
    }  */

  /*  //EXTRACTING THE  X-COORDINATE
    public float getTouchX()
    {
        int i=b,j=0;
//        float x2;
        char[] touchX=new char[64];
        try {
     //       paintdata=rData.toCharArray();
      //      while (paintdata[i] != '\n') {
        //        i++;
          //  }
           // i++;
            while (paintdata[i] != '\n') {
                touchX[j] = paintdata[i];
                i++;
                j++;
            }
            i++;
            x = i;
            System.out.println("X-CORD:"+Float.parseFloat(String.valueOf(touchX)));
        }
        catch (Exception e)
        {
            System.out.println("X-CORDINATE EXCEPTION OCCURRED: "+e);
        }

        return Float.parseFloat(String.valueOf(touchX));
    }*/
 /*
    //EXTRACTING THE  Y-COORDINATE
    public float getTouchY()
    {
        int i=x,j=0;
   //     float y2;
        char[] touchY=new char[64];
        try {
            paintdata=rData.toCharArray();
            while (paintdata[i] != '\n') {
                touchY[j] = paintdata[i];
                i++;
                j++;
            }
            i++;
            y = i;
            System.out.println("y_CORD:"+ Float.parseFloat(String.valueOf(touchY)));
        }
        catch (Exception e)
        {
            System.out.println("Y-CORDINATE EXCEPTION OCCURRED : "+e);
        }
        return Float.parseFloat(String.valueOf(touchY));
    }  */
/*
    //EXTRACTING THE  PAINT-COLOR
    public int getPaintColor()
    {
        int i=y,j=0,paint_col=0;
        char[] paintColor=new char[64];
        try {
            paintdata=rData.toCharArray();
            while (paintdata[i] != '\n') {
                paintColor[j] = paintdata[i];
                i++;
                j++;
            }
            i++;
            pc = i;
            paint_col=(int)Float.parseFloat(String.valueOf(paintColor));
            System.out.println("PAINT_COLOR : "+paint_col);
        }
        catch (Exception e)
        {
            System.out.println("PAINT-COLOR EXCEPTION OCCURRED : "+e);
        }
        return paint_col;
     } */
/*
    //EXTRACTING THE  EVENT-ACTION
    public int getEventAction()
        {
        int i=pc,j=0,event_action=0;
        char[]eventAction=new char[64];
        try
        {
        paintdata=rData.toCharArray();
        while(paintdata[i]!='\n'){
        eventAction[j]=paintdata[i];
        i++;
        j++;
        }
        i++;
        w=i;
        event_action=(int)Float.parseFloat(String.valueOf(eventAction));
        System.out.println("EVENT-ACTION : "+event_action);
        }
        catch(Exception e)
        {
        System.out.println("EVENT-ACTION EXCEPTION OCCURRED : "+e);
        }
        return event_action;
        }  */
        /*
    //EXTRACTING THE  SEND_COUNT
    public int getErasureStatus()
    {
        int i=w,j=0; //Exchange w with previous respective lower index for the substring
        char[] bool=new char[20];
        try {
            paintdata=rData.toCharArray();
            while(paintdata[i] != '\n')
            {
                bool[j] = paintdata[i];
                i++;
                j++;
            }
            i++;
            //w=i;
        }
        catch (Exception e)
        {
            System.out.println("getERASURE STATUS EXCEPTION OCCURRED : "+e);
        }

        int a=(int)Float.parseFloat(String.valueOf(bool));
        return a;
    } */
/*
    public int getWidth()
    {
        int i=w,j=0;
        char[] bool2=new char[20];
        try
        {
            paintdata=rData.toCharArray();
            while(paintdata[i] != '\n' )
            {
                bool2[j]=paintdata[i];
                i++;
                j++;
            }
            i++;
            h=i;
            System.out.println((int)Float.parseFloat(String.valueOf(bool2)));
        }
        catch(Exception e)
        {
            System.out.println("ServerThread.getWidth() EXCEPTION OCCURRED : "+e);
        }
        return (int)Float.parseFloat(String.valueOf(bool2));
    } */
/*
    public int getHeight()
    {
        int  i=h,j=0;
        char[] bool2=new char[100];
        try
        {
            paintdata=rData.toCharArray();
            while(paintdata[i] != '\n')
            {
                bool2[j]=paintdata[i];
                i++;
                j++;
            }
            i++;
            System.out.println((int)Float.parseFloat(String.valueOf(bool2)));
        }
        catch(Exception e)
        {
            System.out.println("ServerThread.getHeight() EXCEPTION OCCURRED : "+e);
        }
        return (int)Float.parseFloat(String.valueOf(bool2));
    }
    */


}


