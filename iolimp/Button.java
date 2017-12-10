package iolimp;

import processing.core.*;
import processing.event.*;

public class Button implements PConstants
{
    public float x, y; // coordinates of textbox
    public float w, h; // width and height of textbox
    public String text;
    public boolean isFocused; 
    public boolean isPressed;
    PApplet parent;
    public PFont f;
    public Button(float newX, float newY, String newText, PApplet newParent){  
        this.parent = newParent;
        x = newX;
        y = newY;
        w = 30;
        h = 30;
        text = newText;
        parent.registerMethod("draw", this);
        parent.registerMethod("mouseEvent", this);
        f = parent.createFont("Arial", 18);
    }
    public void draw(){
        
        if (parent.mouseX > x - w / 2 && parent.mouseX < x + w / 2 && parent.mouseY > y - h / 2 && parent.mouseY < y + h / 2)
        {
            parent.fill(151, 170, 183);
            parent.ellipse(x,y+2,w,h);
            parent.cursor(HAND);
            
            parent.textFont(f, 16);
            parent.fill(74, 83, 96);
            parent.text(text, x + w * 3 / 4, y + 10);
            isFocused = true;
        }
        else 
        {
            parent.fill(106, 116, 130);
            parent.ellipse(x,y+2,w,h);
            parent.fill(138, 148, 163);
            parent.ellipse(x,y,w,h);
            
            parent.textFont(f, 16);
            parent.fill(74, 83, 96);
            parent.text(text, x + w * 3 / 4, y + 10);
            parent.cursor(POINT);
            isFocused = false;
        }
    }
    public void mouseEvent(MouseEvent e)
    {
        
    }
}
