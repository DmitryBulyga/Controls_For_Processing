import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import iolimp.TextBox; 

import gui_for_processing.*; 
import iolimp.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_171203b extends PApplet {



TextBox t;

public void setup(){
  background(200);
  
  t = new TextBox(10, 10, 300, 40, this);
 
}

public void draw(){
  
}
  public void settings() {  size(400, 400); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_171203b" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
