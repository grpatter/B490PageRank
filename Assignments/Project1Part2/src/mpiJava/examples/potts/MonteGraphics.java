import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class MonteGraphics extends Canvas //implements  Runnable  
{ 
    //private Thread thread;	

  public static int spinSize = 8;
  public static int spinColr = 2;
  public static int distribute = 0;

  private Color[] spin_colors = {Color.red, Color.green,
				 Color.magenta, Color.blue,
				 Color.orange, Color.yellow,
				 Color.cyan, Color.black,
				 Color.pink, Color.white,
				 Color.gray, Color.lightGray}; 

    /*
  public void start() {
    if(thread == null){
      thread = new Thread(this);
      thread.start();
    }
  }
 

  public void stop() {
    if(thread != null){
      thread.stop();
      thread = null;
    }
  }
 
  public void run() {
 
    while(thread != null){
      repaint();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // Do nothing
      };
    }
  }
  
    */



  public MonteGraphics() 
  {    
  }

  public void init() {
    //setBackground(Color.blue);

  }

	       
   public void paint(Graphics g) {
     int i, j, pi, pj;
     int gap = 6, xgap=0, ygap=0;
     int ioffset = 38, joffset = 32;
     int xoffset = 0,  yoffset = 0;

     for(i = 0; i < spinColr; i++) {
       g.setColor(spin_colors[i]);
       g.fillOval(i*18+28, 8, 10, 10);
     }

     int gsize = Integer.valueOf
	(MetroInputs.lsizeChoice.getSelectedItem()).intValue();
     int iloc = gsize, jloc = gsize;


     int xdim = gsize / CommVars.procdim[0];
     int ydim = gsize / CommVars.procdim[1];
     /*
     g.setColor(Color.orange);
     g.fillRect(ioffset, joffset,
		iloc*spinSize+(CommVars.procdim[0]-1)*gap+3,
		jloc*spinSize+(CommVars.procdim[1]-1)*gap+3 );
     */

     for(pi=0; pi<CommVars.procdim[0]; pi++) {
       xgap = pi * gap;
       xoffset = pi * xdim;
       for(pj=0; pj<CommVars.procdim[1]; pj++) {
	 ygap = pj * gap;
	 yoffset = pj * ydim;
	 g.setColor(Color.darkGray);
	 g.fill3DRect(ioffset+xoffset*spinSize+xgap, 
		      joffset+yoffset*spinSize+ygap, 
		      xdim*spinSize+3, ydim*spinSize+3, true);     
	 for(i = 0; i < xdim; i++) {
           for(j = 0; j < ydim; j++) {
	     g.setColor(spin_colors[CommVars.global_spin[i+xoffset][j+yoffset]]);
	     g.fillOval(i*spinSize+ioffset+xoffset*spinSize+xgap+1, 
			j*spinSize+joffset+yoffset*spinSize+ygap+1, 
			spinSize, spinSize);
	   }
	 }
       }
     }


     Font fc = new Font("Courier", Font.PLAIN, 14);
     FontMetrics fmc =  g.getFontMetrics(fc);

     g.setFont(fc);

     String iterSt = "Iteration : ";
     String enerSt = "Ave. Energy        : ";
     String magSt  = "Ave. Magnetization : ";
     String heatSt = "Specific Heat : ";
     String susSt  = "Susceptibility: ";

     int x1 = iloc * spinSize + ioffset+30;
     g.setColor(Color.blue);
     g.drawString(iterSt +
		  String.valueOf(CommVars.iter),
		  x1, 40);

     g.setColor(Color.black);
     //g.drawString(enerSt, x1, 80);
     g.drawString(enerSt +
		  String.valueOf((float)CommVars.tmpavenergy[0]), 
		  x1, 70);
     g.drawString(magSt + 
		  String.valueOf((float)CommVars.tmpavspin[0]), 
		  x1, 95);
     /*
     g.drawString(heatSt + 
		  String.valueOf((float)CommVars.tmpavspheat[0]), 
		  x1, 120);
     g.drawString(susSt + 
		  String.valueOf((float)CommVars.tmpavsuss[0]), 
		  x1, 145);
     */

   }

  /*
  public void draw(int size) { 
    spinSize = size;
    repaint();
  }
  */

  private Image offScreenImage;
  private Dimension offScreenSize;
  private Graphics offScreenGraphics;

  public final synchronized void update (Graphics g) {
    Dimension d = getSize();

    if((offScreenImage == null) || (d.width != offScreenSize.width) ||  
       (d.height != offScreenSize.height)) {
      offScreenImage = createImage(d.width, d.height);
      offScreenSize = d;
      offScreenGraphics = offScreenImage.getGraphics();
    }
    offScreenGraphics.clearRect(0, 0, d.width, d.height);
    paint(offScreenGraphics);
    g.drawImage(offScreenImage, 0, 0, null);
    
  }

} 






