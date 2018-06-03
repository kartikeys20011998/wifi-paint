package com.example.kartikey.wifipaint_project;

import java.io.Serializable;

/**
 * Created by Kartikey on 02-04-2018.
 */

public class Data_collector implements Serializable{
    float brushSize2,touchX2,touchY2;
    int paintColor;
    int eventAction,erase2;
    //Extracting the screen resolution
    int width,height;

}
