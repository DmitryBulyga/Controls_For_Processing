package iolimp;
import processing.core.*;
import processing.event.KeyEvent;
import processing.event.MouseEvent;







/**
 *
 * @author Dmitry Bulyga
 */








public class TextBox implements PConstants
{
  
  class Cursor
  {
    
    TextBox textBox;
    float x, y1, y2; // coordinates of cursor
    int position; // position of cursor, number of symbol, which is next to the cursor
    Cursor(float newY1, float newY2, int newPosition, TextBox newTextBox)
    {
      
      textBox = newTextBox;
      this.x = textBox.x + textBox.paddingleft;
      y1 = newY1;
      y2 = newY2;
      position = newPosition;
      
    }
    void draw()
    {
      this.x = textBox.x + textBox.paddingleft + (position - textBox.textStartPoint)*textBox.charWidth;
      if (((int)(textBox.parent.millis()) % 1000 > 500) && textBox.isFocused) 
          textBox.parent.line(x, y1, x, y2); // cursor is drawn every 500 ms if the textbox is focused
    }
  }
  
  
  
  
  
  private class ConvertException extends Exception{} 
  // class of exception, which is thrown if convertion from String tp int or float is failed
  
  
  
  
  
  
  PApplet parent;
  private float x, y; // coordinates of textbox
  private float w, h; // width and height of textbox
  private float charWidth; // width of one symbol, is needed for correct drawing of cursor
  private float fontSize; 
  private float paddingleft, paddingtop; // distances between borders and printed text
  private int charCount; // max count of chars in textbox with width = w
  private int timer; // a timer to control keyboard triggering
  private int textStartPoint; // number of symbol from which the string is shown
  private int selectionStartPoint, selectionCount;
  private final int timeout = 150; // timeout of keyboard triggering
  private PFont font; 
  private String stext; // value of textbox
  private boolean isFocused; 
  private boolean isShiftPressed = false; 
  private boolean isSelecting = false;
  private Cursor textCursor;
  
  
  
  
  
  
  
  
  public TextBox(float newX, float newY, float newW, float newH, PApplet parent)
  {
      
    this.parent = parent;  
    
    x = newX;
    y = newY;
    w = newW;
    h = newH;
    
    // paddings, fontsize and other values are counted automatically,
    // because they are in linear dependence with height of textbox
    
    paddingtop = h*0.2F; 
    paddingleft = paddingtop;
    textStartPoint = 0;
    textCursor = new Cursor(y + paddingtop, y + h - paddingtop, 0, this);
    selectionStartPoint = 0;
    selectionCount = 0;
    
    stext = "";
    fontSize = (h - 2*paddingtop) / 0.65F; 
    font = parent.createFont("RobotoMono-Thin.ttf", fontSize); // monospased font is used for simplicity
    charWidth = h / 1.8F;
    isFocused = false;
    charCount = (int)((w - paddingleft-charWidth/2)/charWidth);
    timer = parent.millis(); // setting up timer
    parent.registerMethod("keyEvent", this);
    parent.registerMethod("mouseEvent", this);
    parent.registerMethod("draw", this);
  }
  
  
  
  private void handleLetters(KeyEvent e)
  { 
          int len = stext.length();
          
          // insert char into the place of the cursor
          if(textCursor.position!=len) // if the cursor isn't in the end of the string
          {
  
                char[] buf = stext.toCharArray();
                stext="";
                
                for (int i=0; i < textCursor.position; i++)
                   stext += buf[i];
                
                stext += e.getKey();
                
                for (int i = textCursor.position; i < len; i++)
                   stext += buf[i];
              
          }
          else // if the cursor is in the end of the string
            stext += e.getKey(); // just push back the char
          

          timer = parent.millis(); // reset timer
          
          textCursor.position++; 
          
          if (stext.length() > charCount) //if textbox is overflown we change x-coordinate of cursor
              textStartPoint++;
  }
  
  private void handleBackSpace()
  {
         // the char on the left of cursor is to remove
         // it is assigned a value '\0'
         // then new string is formed of all the symbols except '\0'
         
         char[] buf = stext.toCharArray();
         int len=stext.length();
         stext="";
         
         
        buf[textCursor.position-1] = '\0';



        for (int i = 0;i < len;i++) 
            if (buf[i]!= '\0') 
                stext += buf[i];

        if (stext.length() > charCount - 1&&textStartPoint>0)  
            textStartPoint--;
        textCursor.position--;
            
         
         
         
         
  }
  
  private void handleBackSpaceForSelection(){
      
        char[] buf = stext.toCharArray();
        int len=stext.length();
        stext="";




        int realStartPoint=0;
        int realCount=0;
        if (selectionCount>0){
            realStartPoint = selectionStartPoint;
            realCount = selectionCount;

        }

        if (selectionCount<0){
            realStartPoint = selectionStartPoint + selectionCount;
            realCount = -selectionCount;

        }
       if (realCount<=textStartPoint)
            textStartPoint-=realCount;
       else textStartPoint=0;
       textCursor.position = realStartPoint;
       for (int i = 0; i < realStartPoint; i++)
           stext+=buf[i];
       for (int i = realStartPoint+realCount; i<len; i++)
           stext+=buf[i];
       selectionCount = 0;
       isFocused = true;
  }
  
  private void handleLeftArrow()
  {
      if (textCursor.position > 0)
      {
          
            if(textCursor.position==textStartPoint)             
                textStartPoint--;
            textCursor.position--;
            
      }
  }
  
  private void handleRightArrow()
  {
      if (textCursor.position < stext.length())
      {
            
             if(textCursor.position==textStartPoint+charCount)
                 textStartPoint++;
             textCursor.position++;
          
          
      }
  }
  
  private void handleShift(KeyEvent e)
  {
      if (e.getKey()>='A' && e.getKey()<='Z')  //if user pressed SHIFT and a char
        isShiftPressed = true;
      else
        isShiftPressed=false;
  }
  
  
  

  
  private void printText()   // this method prints part of textbox value, which should be printed
  {  
  
    if (stext.length() <= charCount)  //if textbox isn't overflown
        parent.text(stext, x + paddingleft, y + h - paddingtop);
     
    else                //if textbox is overflown
    {
       char[] buf = stext.toCharArray();
       String visibleString = "";
       for (int i = textStartPoint; i < textStartPoint + charCount; i++)
            visibleString += buf[i];
       parent.text(visibleString, x + paddingleft, y + h - paddingtop);
     }
     
     
  }
  
  private void drawSelection()
  {
      if (selectionCount!=0)
      { 
          int startInBox = selectionStartPoint - textStartPoint;
          float rectx = this.x + paddingleft + startInBox * charWidth;
          float recty = this.y + 2;
          float rectw = charWidth * selectionCount;
          float recth = this.h - 4;
          parent.rect(rectx, recty, rectw, recth);
      }
          
  }
  
  private int getCursorPosition(MouseEvent e)
  {
      if(stext.length()<charCount && e.getX()>x + paddingleft + charWidth * stext.length()) // if user clicked on whight field
                
                return stext.length();
      
      else  //if user clicked on text
      {
          
        if (e.getX()>x+paddingleft+charWidth*charCount) 
            return charCount + textStartPoint;
        else 
            return parent.round((parent.mouseX - x - paddingleft) / charWidth) + textStartPoint;
      }
      
  }
  
  public void keyEvent(KeyEvent e) // this method handles keyboard
  {
    System.out.println(textCursor.position + " " + textStartPoint + " " + selectionStartPoint + " " + selectionCount);
    // we check keyboard taping only if textbox is focused and if time between pressings is more than timeout
    // !isShiftPressed is to avoid double-printing capital letters (smth like "AA", "BB")
    
    if (e.getKeyCode()==8 && selectionCount!=0){
        handleBackSpaceForSelection();
        System.out.println(textCursor.position + " " + textStartPoint + " " + selectionStartPoint + " " + selectionCount);
        return;
    }
    if(isFocused && parent.millis() - timer >= timeout && !isShiftPressed)
    {
        
                parent.keyPressed = false;  // we have to turn this flag off manually
      
                if (e.getKeyCode()>=48||e.getKeyCode()==32) // if user prints a letter
                    handleLetters(e);
      
      
                else if(e.getKeyCode()==8 && textCursor.position!=0) //if user pressed BACKSPACE
                  handleBackSpace();
                
    
                else if(e.getKeyCode()==10)                    //if user pressed Enter
                   //handle Enter
                   isFocused = false; 
      
                else if(e.getKeyCode()==37)
                    handleLeftArrow();
                
                else if(e.getKeyCode()==39)
                    handleRightArrow();
      
                timer = parent.millis(); //reset timer
    }
    
    handleShift(e);
  }
  
  public void mouseEvent(MouseEvent e)      //this method handles mouse
  {
      System.out.println(textCursor.position + " " + textStartPoint + " " + selectionStartPoint + " " + selectionCount);
    if (e.getX()>=x && e.getX() <= x+w && e.getY() >= y && e.getY() <= y+h) 
    {  
        if (e.getAction()==MouseEvent.PRESS){
        
        
            isFocused = true;
            textCursor.position = getCursorPosition(e);
        
            selectionStartPoint = getCursorPosition(e);
            selectionCount = 0;
            isSelecting = true;
            
        }
    
        if ((e.getAction() == MouseEvent.DRAG  || e.getAction()==MouseEvent.RELEASE) && isSelecting)
            
            /*if (e.getX()< selectionStartPoint * charWidth + x + paddingleft){
                int newStartPoint = getCursorPosition(e);
                selectionCount += (selectionStartPoint-newStartPoint);
                selectionStartPoint = newStartPoint;
                
            }
            else{
                selectionCount = getCursorPosition(e) - selectionStartPoint;
            }*/
        
            selectionCount = getCursorPosition(e) - selectionStartPoint;
        
        
        if (e.getAction()==MouseEvent.RELEASE)
            isSelecting = false;
        
        
    } else {
        
        if (e.getAction()==MouseEvent.PRESS||e.getAction()==MouseEvent.RELEASE)   
            isFocused = false;
    
    
        if (e.getAction()==MouseEvent.DRAG){
            //if (parent.millis() - timer >= timeout)
            //    if(e.getX() > this.x)
            //        selection
        }
    
    }
    
    
    
    
  }
 
  
  
  public void draw()  // draws the textbox
  {    
  
    //drawing a white rectangle
    parent.fill(255); 
    parent.rect(x, y, w, h); 
    
    //drawing selection rectangle
    parent.fill(125);
    drawSelection();
    
    //printing text
    parent.fill(0);
    parent.textFont(font);
    printText();
    
    //drawing a cursor
    textCursor.draw();
  }
  
  
  // next methods return value in string or converted in char or a numeric type
  
  public String getValueString()
  {
    return stext; 
  }
  
  public char[] getValueCharArray()
  {
    return stext.toCharArray(); 
  }
  
  public int getValueInt() throws ConvertException
  {
    char[] buf = stext.toCharArray();
    boolean isBelowZero=false;
    int result=0;
   
    for (int i = 0; i<stext.length(); i++)
    {
     if (buf[i] >= 48 && buf[i] <= 57)
       result += ((int)(buf[i]) - 48) * (int)(parent.pow(10, stext.length() - i - 1));
     else if(i == 0 && buf[i] == '-')
       isBelowZero = true;
     else
       throw new ConvertException();
    }
   
   
    if (isBelowZero)
      return result*(-1);
    else
      return result;
     
     
  }
  
  public char getValueChar()
  {
    char[] buf = stext.toCharArray();
    if (stext=="") return '\0';
    return buf[0];
  }
  
  public float getValueFloat() throws ConvertException
  {
    
    char[] buf = stext.toCharArray(); 
    if(stext=="") return 0;
    float result = 0;
    boolean isBelowZero = false;
    int pointIndex=0;
    
    //looking for position of the point
    while(pointIndex < stext.length() && buf[pointIndex]!='.' && buf[pointIndex]!=',')
      pointIndex++;
      
    //parsing the integer part of number
    for (int i = 0; i<pointIndex;i++)
    {
      if (buf[i] >= 48 && buf[i] <= 57)
        result+=((int)(buf[i]) - 48) * (int)(parent.pow(10, pointIndex - i - 1));
      else if(i==0 && buf[i]=='-')
        isBelowZero=true;
      else
        throw new ConvertException();
    } 
    
    
    //parsing the fractional part of number
    for (int i = pointIndex + 1; i < stext.length(); i++)
    {
      if (buf[i] >= 48 && buf[i] <= 57)
        result += ((int)(buf[i]) - 48) * parent.pow(10, -(i - pointIndex));
      else
        throw new ConvertException();
    } 
        
        
        
    if(isBelowZero)
      return -result;
    else
      return result;
  }
  
  
  
  
}