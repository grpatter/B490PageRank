/**********************************************************************/
/* CFD.java                                                           */
/* Perallel version                                                   */
/* Written by Sang Lim                                                */
/* slim@npac.syr.edu                                                  */
/**********************************************************************/

/* I modify this code from sequential version of CFD which is written 
 * by Saleh Elmohamed. This code is using mpiJava and tested upto 3 
 * processors. However, it is designd running on n processors.
 * It will read initial data from datafile called "Tran.dat" by 
 * processor 0. Also, processor 0 will take care display part. 
 * Calculation will be done by all processors including processor 0.
 * Following documentaion is from original seqential version. You may 
 * find useful information here. 
*/

/**********************************************************************/
/* CFD.java                                                           */
/* A 2-D Inviscid Flow Simulation.                                    */
/* Saleh Elmohamed.                                                   */ 
/**********************************************************************/
/* a note to experienced object oriented programmers:                 */
/* Some parts of this code will look sloppy because I used inline     */
/* code for some things which could have been done more elegantly     */
/* using public methods.                                              */
/* This was done because inlining by hand increased the execution     */
/* speed by about 20%.                                                */
/**********************************************************************/
/*The simulation:
 *-----------
 *  The code simulates a 2-D inviscid flow through an axisymmetric 
 *  nozzle. The simulation yields countour plots of all flow variables, 
 *  including velocity components, pressure, mach number, density and 
 *  entropy, and temperature. The plots show the location of any shock
 *  wave that would reside in the nozzle. Also, the code finds the 
 *  steady state solution to the  2-D Euler equations 
 *  (see report and reference). 
 *
 *  It uses a 4-stage Runge-Kutta time-stepping algorithm and a finite
 *  volume centeral-difference technique to find the solution. 
 *  At each stage, the residual is multiplied by a different value. 
 *  Generally, this gives a bit more accurate results since you go
 *  over more than one stage.
 *  A numerical dissipation model is emplyed to dampen any spurious 
 *  oscillations and prevent the soultion from blowing up in the 
 *  presence of shock waves (Jameson et al, 81).  The dampening order 
 *  is set by:  (1) compute the even derivatives (i.e. 2nd and 4th), 
 *  (2) set the order proportional to this.
 *
 *  If the inlet and exit conditions are supersonic, the code requires
 *  about 1800 iterations to complete execution. However, if subsonic 
 *  boundary conditions are employed, the number of iterations 
 *  required jumps to about 6000, as new errors are introduced into
 *  the domain by these conditions (see Dang, 1997). 
 *  The flow is going from left to right across the nozzle. To see the
 *  flow through, a "tuft" key is added as well as a "grid" key. 
 *  The flow can be either "subsonic", "transonic", or "supersonic".
 *  The subsonic flow requires < 1, the transonic flow is partly sub 
 *  and partly super, and the supersonic requires a flow of greater 
 *  than and equal 1.
 *
 * References:
 ************
 *  1. "Numerical Solutions of the Euler Equations by Finite Volume 
 *     Methods Using Runge-Kutta Time-Stepping Schemes", A. Jameson,
 *     W. Schmidt, and E. Turkel, paper no: AIAA-81-1259, AIAA 14th
 *     Fluid and Plasma Dynamics Conference, June 23-35, 1981, 
 *     Palo Alto, Calif.
 *
 *  2. "Monograph in CFD", T. Q. Dang, Dept. of Mechnical, Aerospace 
 *     and Manufacturing Engineering. Syracuse Univ. 1997.
 *      
 ********************************************************************/
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;         /* Lets you read files */
import java.io.StreamTokenizer;     /* Lets you interprit what you read */
import java.net.*;                  /* Let you open a URL */
import java.io.IOException;         /* Deals with io exceptions */
import java.io.*;
import java.util.*;
import mpi.*;

public class CFD extends Frame implements Runnable,
					  ItemListener,
                                          ActionListener{

	  /* Declare "globals" */
  Checkbox drawGridBox, pauseBox, tuftsBox;
  Choice plotChoice, timeChoice, viewChoice;
  Button quit;
  Color ctable[];
  DataPanel dataPanel;
  DampingPanel sdampingPanel, fdampingPanel;
  MachffPanel machffPanel;
  String inname = null;
  StringBuffer buffer = new StringBuffer();
  Thread drawThread = null;

  double machffmin;
  double machffmax; 
  final double dampingMin = 0.1;
  final double dampingMax = 5.0;
  int me;
  int[] terminate = new int[1];
  double sTime,eTime;	

  public CFD() throws MPIException {
    me = MPI.COMM_WORLD.Rank();
    InputStream instream = null;
    Panel controlPanelSouth = new Panel();
    Panel controlPanelNorth = new Panel();
    Panel controlPanelEast = new Panel();

    inname = "Tran.dat";
    try {
        instream = new DataInputStream(new FileInputStream(inname));
        } catch (IOException e){
            System.out.println("File not opened properly\n"+e.toString() );
            System.exit(1);
          }  

    dataPanel = new DataPanel(instream);

    /* Make the control panel */
    controlPanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    controlPanelSouth.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    controlPanelEast.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

    controlPanelNorth.setBackground(Color.lightGray);
    controlPanelSouth.setBackground(Color.lightGray);
    controlPanelEast.setBackground(Color.lightGray);

    quit = new Button("QUIT");
    quit.addActionListener(this);
    
    plotChoice = new Choice();
    plotChoice.addItem("Contours");
    plotChoice.addItem("Line Plot");

    plotChoice.addItemListener(this);
    
    viewChoice = new Choice();
    viewChoice.addItem("Density");
    viewChoice.addItem("Pressure");
    viewChoice.addItem("Temperature");
    viewChoice.addItem("Mach Number");
    viewChoice.addItem("Axial Velocity");
    viewChoice.addItem("Normal Velocity");
    viewChoice.addItem("Total Pressure");

    viewChoice.addItemListener(this);

    drawGridBox = new Checkbox("Grid", null, false); 
    tuftsBox    = new Checkbox("Tufts", null, false);

    timeChoice  = new Choice();
    timeChoice.addItem("Local Timestep");
    timeChoice.addItem("Time Accurate");

    timeChoice.addItemListener(this);
    
    pauseBox = new Checkbox("Pause", null, false);
     
    pauseBox.setFont(new Font("Helvetica", Font.PLAIN, 15));
    drawGridBox.setFont(new Font("Helvetica", Font.PLAIN, 15));
    tuftsBox.setFont(new Font("Helvetica", Font.PLAIN, 15));
 
    drawGridBox.addItemListener(this);
    tuftsBox.addItemListener(this);
    pauseBox.addItemListener(this);
    /* Set limits on mach slider.          */
    /* These shifting limits are necessary */
    /* because there is a singularity      */
    /* in the boundary conditions when the */
    /* mach number == 1.0                  */
    if (dataPanel.machff < 0.9) {
      machffmin = 0.2;
      machffmax = 0.9;
    }
    else if (dataPanel.machff > 1.3) {
      machffmin = 1.3;
       machffmax = 2.5;
    }
    else {
      machffmin = 0.2;
      machffmax = 0.9;
    }

    machffPanel = new MachffPanel("Inlet Mach Number", machffmin, machffmax,
	  			  dataPanel.machff);
    sdampingPanel = new DampingPanel("2nd Order Damping",
				     dampingMin, dampingMax,
				     dataPanel.secondOrderDamping);
    fdampingPanel = new DampingPanel("4th Order Damping", dampingMin,
				     dampingMax,
				     dataPanel.fourthOrderDamping);
    
    /* Add the button panel */
    controlPanelSouth.setFont(new Font("Helvetica", Font.PLAIN, 15));
    dataPanel.setFont(new Font("Helvetica", Font.PLAIN, 15));
    Label label2 = new Label (" of");
    controlPanelNorth.add(plotChoice);
    controlPanelNorth.add(label2);
    controlPanelNorth.add(viewChoice);
    controlPanelNorth.add(drawGridBox);
    controlPanelNorth.add(tuftsBox);
    controlPanelNorth.add(pauseBox);
    controlPanelNorth.add(timeChoice);
    controlPanelSouth.add(machffPanel);
    controlPanelSouth.add(fdampingPanel);
    controlPanelSouth.add(sdampingPanel);
    controlPanelEast.add(quit);

    add("East", controlPanelEast);
    add("South", controlPanelSouth);
    add("North", controlPanelNorth);
    /* Add contour plot to main screen */
    add("Center", dataPanel);
//System.out.println("2 : "+controlPanelSouth.getBackground());
  }

  public void start() {
    terminate[0] = 1;
    if (drawThread == null) {
      drawThread = new Thread(this);
      /* Priority has to be turned down so numerics  */
      /* don't hose graphics */
      drawThread.setPriority(Thread.MIN_PRIORITY+2);
      drawThread.start();
    }
  }

  //throws MPIException
  public void run() {
    while (drawThread != null) {
      try {
	MPI.COMM_WORLD.Bcast(terminate,0,1,MPI.INT,0);
	if(terminate[0] == 0){
	  MPI.Finalize();
	  System.exit(0);
	}
	
	if (me == 0){
	  dataPanel.machff = machffPanel.sliderValue;
	  dataPanel.secondOrderDamping = sdampingPanel.sliderValue;
	  dataPanel.fourthOrderDamping = fdampingPanel.sliderValue;
	}
	dataPanel.doIteration();
	if (me == 0)
	  try {
	    drawThread.sleep(1000);
	  } catch (InterruptedException e) {
	    System.err.println("Execution interrupted by another thread.");
	    System.err.println(e.getMessage());
	  }
      }
      catch(MPIException e) {
      }
    }

  }

  
  public void stop() {
    if (me == 0){
      if (drawThread != null && drawThread.isAlive()) {
               drawThread.stop();
      }
      drawThread = null;
    }
  }

  public void actionPerformed(ActionEvent evt){
    String arg = evt.getActionCommand();
    if (arg.equals("QUIT")) terminate[0] = 0;
 //   System.exit(0);
  }
  public void itemStateChanged(ItemEvent e) { 
    if (e.getItemSelectable() instanceof Choice){
      dataPanel.nplot = plotChoice.getSelectedIndex();
      dataPanel.nview = viewChoice.getSelectedIndex();
      dataPanel.ntime = timeChoice.getSelectedIndex();
    }
    else if (e.getItemSelectable() instanceof Checkbox){
      dataPanel.setGridSwitch(drawGridBox.getState());
      dataPanel.setTuftsSwitch(tuftsBox.getState());
      if(pauseBox.getState() == true) {
	drawThread.suspend();
      }
      else
	drawThread.resume();
    }    
  }
  public static void main(String[] args) throws MPIException 
  {
    MPI.Init(args);
    int root = MPI.COMM_WORLD.Rank();
    
    CFD f = new CFD();

    f.setSize(1024,850);
    if(root == 0)
      f.show();

    f.start();
  }
}

/* The fundamental class is the data          */
/* This executes when you create a new object */
class datafield  {
  private double a[][];               /* Grid cell area */
  private double local_a[][];
  int n_bar; 

  double deltat[][];                  /* Timestep */
  double local_deltat[][];

  private double machff;              /* Inflow mach number */
  public double machfftarget;         /* Target inflow mach number */
  public double secondOrderDamping;
  public double fourthOrderDamping;
  public int ntime;

  private double opg[][], pg[][], pg1[][];   /* Pressure */
  private double local_opg[][], local_pg[][], local_pg1[][];

  private double sxi[][], seta[][];
  private double local_sxi[][], local_seta[][];

  private double tg[][], tg1[][];           /* Temperature */
  private double local_tg[][], local_tg1[][];

  private double xnode[][], ynode[][];       /* Storage of node coordinates */
  private double local_xnode[][], local_ynode[][];

  double xmin=Double.MAX_VALUE, xmax=Double.MIN_VALUE,
         local_xmin=Double.MAX_VALUE, local_xmax=Double.MIN_VALUE,
         ymin=Double.MAX_VALUE, ymax=Double.MIN_VALUE,
         local_ymin=Double.MAX_VALUE, local_ymax=Double.MIN_VALUE;
                                    /* Max and Min of node coordinates */
  double hold_MinMax[];
  double cff, uff, vff, pff, rhoff, tff, jplusff, jminusff;
                                    /* Far field values */
  double datamax, datamin;
  int iter,my_rank,np;
  int imax, jmax;                   /* Number of nodes in x and y direction */
  private Statevector d[][];        /* Damping coefficients */
  private Statevector local_d[][];

  private Statevector f[][], g[][]; /* Flux Vectors */
  private Statevector local_f[][], local_g[][];

  private Statevector r[][], ug1[][];
  private Statevector local_r[][];
  private Statevector local_ug1[][];
  private Statevector ug[][];      /* Storage of data */
  private Statevector local_ug[][];

  final double Cp = 1004.5;         /* specific heat, const pres. */
  final double Cv=717.5;            /* specific heat, const vol. */
  final double gamma=1.4;           /* Ratio of specific heats */
  final double rgas=287.0;          /* Gas Constant */
  final int keytextwidth=50;        /* Width of text field in color table */
  final int ncontour = 16;          /* Number of contours between min and max */
  final double fourthOrderNormalizer = 0.02; /* Damping coefficients */
  final double secondOrderNormalizer = 0.02;

  datafield(InputStream instream) throws IOException, MPIException {
    int i, j, n;                    /* Dummy counters */
    double scrap, scrap2;           /* Temporary storage */

                                    /* Convert the stream into 
                                       tokens (which helps you parse it) 
				     *-----------------*/
      Reader reader = new BufferedReader(new InputStreamReader(instream));
      StreamTokenizer intokens = new StreamTokenizer(reader);

    my_rank = MPI.COMM_WORLD.Rank();
    np = MPI.COMM_WORLD.Size();
    if(my_rank == 0){
      /* Read header */
      if (intokens.nextToken() == StreamTokenizer.TT_NUMBER)
        imax = (int) intokens.nval;
      else
        throw new IOException();

      if (intokens.nextToken() == StreamTokenizer.TT_NUMBER)
        jmax = (int) intokens.nval;
      else
        throw new IOException();

      if (intokens.nextToken() == StreamTokenizer.TT_NUMBER) {
        machfftarget = intokens.nval;
        machff = machfftarget;
      }
      else
        throw new IOException();

      if (intokens.nextToken() == StreamTokenizer.TT_NUMBER)
        iter = (int) intokens.nval;
      else
        throw new IOException();
    }
    
    double basic_ele[] = new double[5];
    if(my_rank == 0){
      basic_ele[0] = imax;
      basic_ele[1] = jmax;
      basic_ele[2] = machfftarget;
      basic_ele[3] = machff;
      basic_ele[4] = iter;
    }
   /* Send initial value to other processors */
    MPI.COMM_WORLD.Bcast(basic_ele,0,5,MPI.DOUBLE,0);

    imax = (int)basic_ele[0];
    jmax = (int)basic_ele[1];
    machfftarget = basic_ele[2];
    machff = basic_ele[3];
    iter = (int)basic_ele[4];

    n_bar = (imax -1)/np;
  
    deltat = new double[imax+1][jmax+2];
    local_deltat = new double[n_bar+2][jmax+2];

    opg = new double[imax+2][jmax+2];
    local_opg = new double[n_bar+2][jmax+2];

    pg = new double[imax+2][jmax+2];
    local_pg = new double[n_bar+2][jmax+2];

    pg1 = new double[imax+2][jmax+2];
    local_pg1 = new double[n_bar+2][jmax+2];

    sxi =new double[imax+2][jmax+2];;
    local_sxi = new double[n_bar+2][jmax+2];

    seta = new double[imax+2][jmax+2];;
    local_seta = new double[n_bar+2][jmax+2];

    tg = new double[imax+2][jmax+2];
    local_tg = new double[n_bar+2][jmax+2];

    tg1 = new double[imax+2][jmax+2];
    local_tg1 = new double[n_bar+2][jmax+2];

    ug = new Statevector[imax+2][jmax+2];
    local_ug = new Statevector[n_bar+2][jmax+2];

    a = new double[imax][jmax];
    local_a = new double[n_bar+2][jmax];

    d =  new Statevector[imax+2][jmax+2];
    local_d = new Statevector[n_bar+2][jmax+2];

    f =  new Statevector[imax+2][jmax+2];
    local_f = new Statevector[n_bar+2][jmax+2];

    g =  new Statevector[imax+2][jmax+2];
    local_g = new Statevector[n_bar+2][jmax+2];

    r =  new Statevector[imax+2][jmax+2];
    local_r = new Statevector[n_bar+2][jmax+2];

    ug1 =  new Statevector[imax+2][jmax+2];
    local_ug1 = new Statevector[n_bar+2][jmax+2];

    xnode = new double[imax][jmax];
    local_xnode = new double[n_bar+2][jmax];

    ynode = new double[imax][jmax];
    local_ynode = new double[n_bar+2][jmax];
    
    for (i = 0; i < n_bar+2 ; i++)
       for (j = 0; j < jmax+2 ; j++){
          local_ug1[i][j] = new Statevector();
          local_ug[i][j] = new Statevector();
          local_d[i][j] = new Statevector();
          local_f[i][j] = new Statevector();
          local_g[i][j] = new Statevector();
          local_r[i][j] = new Statevector();
       }

    int initUg = 0;
    for (i = 0; i < imax+2; ++i)
      for (j = 0; j < jmax+2; ++j) {
	d[i][j] =  new Statevector();
	f[i][j] =  new Statevector();
	g[i][j] =  new Statevector();
	r[i][j] = new Statevector();
	ug[i][j] = new Statevector();
	ug1[i][j] = new Statevector();
        initUg ++; 
      }

    /* Set farfield values (we use normalized units for everything */    
    cff = 1.0;
    vff = 0.0;
    pff = 1.0 / gamma;
    rhoff = 1.0;
    tff = pff / (rhoff * rgas);

    /* Now load the data */
    hold_MinMax = new double[4];
    if(my_rank == 0){
      for (i = 1; i < imax+1; i++)
        for (j = 1; j < jmax+1; ++j) {
	  switch(intokens.nextToken()) {
	  case StreamTokenizer.TT_NUMBER:
            xnode[i-1][j-1] = (double) intokens.nval;
	    xmax = Math.max(xmax, xnode[i-1][j-1]);
	    xmin = Math.min(xmin, xnode[i-1][j-1]);
	    if (intokens.nextToken() == StreamTokenizer.TT_NUMBER) {
	      ynode[i-1][j-1] = (double) intokens.nval;
	      ymax = Math.max(ymax, ynode[i-1][j-1]);
	      ymin = Math.min(ymin, ynode[i-1][j-1]);
	      if(intokens.nextToken() == StreamTokenizer.TT_NUMBER) {
	        ug[i][j].a =(double) intokens.nval;
	        if(intokens.nextToken() == StreamTokenizer.TT_NUMBER) {
		  ug[i][j].b =(double) intokens.nval;
		  if(intokens.nextToken() == StreamTokenizer.TT_NUMBER) {
		    ug[i][j].c =(double) intokens.nval;
		    if(intokens.nextToken() == StreamTokenizer.TT_NUMBER) {
		      ug[i][j].d =(double) intokens.nval;
		      scrap = ug[i][j].c/ug[i][j].a;
		      scrap2 = ug[i][j].b/ug[i][j].a;
		      tg[i][j] = ug[i][j].d/ug[i][j].a 
		        - (0.5 * (scrap*scrap + scrap2*scrap2));
		      tg[i][j] = tg[i][j] / Cv;
		      pg[i][j] = rgas * ug[i][j].a * tg[i][j];
		    }
		  }
	        }
	      }
	    }
	    break;
	  case StreamTokenizer.TT_EOF:
	    throw new IOException();
	  default:
	    throw new IOException();
	  }
        }
      }

/*
------------- send boundary elements to other processors ----------------
*/ 
    int initPg = 0;
    int initXY = 0;
    double[] global_array = new double[(imax+2)*(jmax+2)*16];
    if(my_rank == 0){
      for (i = 1; i <= imax; ++i) 
        for (j = 1; j <= jmax; ++j){ 
          global_array[initPg++] = pg[i][j];
          global_array[initPg++] = tg[i][j];
          global_array[initPg++] = ug[i][j].a;
          global_array[initPg++] = ug[i][j].b;
          global_array[initPg++] = ug[i][j].c;
          global_array[initPg++] = ug[i][j].d;
        }    
      global_array[initPg++] = xmin;
      global_array[initPg++] = xmax;
      global_array[initPg++] = ymin;
      global_array[initPg++] = ymax;

      for (i=0; i < imax;i++)
        for(j=0;j < jmax;j++){
          global_array[initPg++] = xnode[i][j];
          global_array[initPg++] = ynode[i][j];
        }
    }

    MPI.COMM_WORLD.Bcast(global_array,0,(imax+2)*(jmax+2)*16,MPI.DOUBLE,0);

    if (my_rank != 0){
      for (i = 1; i <= imax; ++i) 
        for (j = 1; j <= jmax; ++j){ 
          pg[i][j] = global_array[initPg++];  
          tg[i][j] = global_array[initPg++];
          ug[i][j].a = global_array[initPg++];
          ug[i][j].b = global_array[initPg++];
          ug[i][j].c = global_array[initPg++];
          ug[i][j].d = global_array[initPg++];
        }
      xmin = global_array[initPg++];
      xmax = global_array[initPg++];
      ymin = global_array[initPg++];
      ymax = global_array[initPg++];

      for (i=0; i < imax;i++)
        for(j=0;j < jmax;j++){
          xnode[i][j] = global_array[initPg++];
          ynode[i][j] = global_array[initPg++];
        }
    }

    int global = 0;
    for (i = 1; i <= n_bar; ++i) 
      for (j = 1; j <= jmax; ++j){
        global = my_rank * n_bar + i;
        local_pg[i][j] = pg[global][j];
        local_tg[i][j] = tg[global][j];
        local_ug[i][j].a = ug[global][j].a;
        local_ug[i][j].b = ug[global][j].b;
        local_ug[i][j].c = ug[global][j].c;
        local_ug[i][j].d = ug[global][j].d;
      }

    int lastPro = np - 1;
    int position = 0;
    if (my_rank == lastPro)
      position = imax;
    else 
      position = (n_bar+1)*(my_rank+1);

    int pgPosition = (n_bar+1)*my_rank-1;

    for (j = 1; j<=jmax;++j){
      local_pg[n_bar+1][j]   = pg[position][j];
      local_tg[n_bar+1][j]   = tg[position][j];
      local_ug[n_bar+1][j].a = ug[position][j].a;
      local_ug[n_bar+1][j].b = ug[position][j].b;
      local_ug[n_bar+1][j].c = ug[position][j].c;
      local_ug[n_bar+1][j].d = ug[position][j].d;
      if(my_rank != 0)
        local_pg[0][j] = pg[pgPosition][j]; 
    }

    global = 0;
    for (i = 0; i < n_bar; ++i) 
      for (j = 0; j < jmax; ++j){
        global = my_rank * n_bar + i;
        local_xnode[i][j] = xnode[global][j];
        local_ynode[i][j] = ynode[global][j];
      }

    if (my_rank == lastPro)
       position = imax - 1;
    else 
       position = n_bar * (my_rank + 1);

    for (i = 0;i<jmax ; i++){
      local_xnode[n_bar][i] = xnode[position][i];
      local_ynode[n_bar][i] = ynode[position][i];
    }    

    /* Calculate grid cell areas */

    for (i = 1; i <= n_bar; ++i)
      for (j = 1; j < jmax; ++j){
	local_a[i][j] = 0.5 * ((local_xnode[i][j] - local_xnode[i-1][j-1]) 
			    * (local_ynode[i-1][j] - local_ynode[i][j-1])-
		              (local_ynode[i][j] - local_ynode[i-1][j-1]) 
	                    * (local_xnode[i-1][j] - local_xnode[i][j-1]));
      }
    double send_a[] = new double[jmax];
    double recv_a[] = new double[jmax];
    int right = my_rank + 1;
    int left  = my_rank - 1;
    if (my_rank == 0)
      left = np -1;
    else if(my_rank == (np -1))
      right = 0;
       
    for(j=1;j<jmax;j++)
      send_a[j] = local_a[n_bar][j];

    MPI.COMM_WORLD.Sendrecv(send_a,0,jmax,MPI.DOUBLE,right,1,
		            recv_a,0,jmax,MPI.DOUBLE,left ,1);
    if(my_rank != 0)
      for(j=1;j<jmax;j++)
        local_a[0][j] = recv_a[j];

    for(j=1;j<jmax;j++)
      send_a[j] = local_a[1][j];

    MPI.COMM_WORLD.Sendrecv(send_a,0,jmax,MPI.DOUBLE,left ,1,
		            recv_a,0,jmax,MPI.DOUBLE,right,1);
    if (my_rank != (np-1))
      for(j=1;j<jmax;j++)
        local_a[n_bar+1][j] = recv_a[j];  
  }

  public void drawKey(Color ctable[], Graphics g, int xkeywidth, 
		      int ykeywidth, int xcanvaswidth, int ycanvaswidth) {
    int colorbandheight, colorbandwidth;
    int n;
    int yband;
    int xoffset, yoffset;
    FontMetrics keyFontMetrics;

    xoffset = xcanvaswidth - xkeywidth;
    yoffset = ycanvaswidth - ykeywidth;
    colorbandwidth = xcanvaswidth - keytextwidth - (xoffset+5)-20;
    colorbandheight = (ykeywidth-10) / (ctable.length-1) + 1;

    g.setColor(Color.black);
    g.fillRect(xoffset, yoffset, xkeywidth+20, ykeywidth);

    /* Draw the colors themselves */
    for (n = 0; n < ctable.length; ++n) {
      g.setColor(ctable[n]);
      yband =  ycanvaswidth - 5 -
	(int) (((double)(n+1)/(double) ctable.length) 
	       * (double)(ykeywidth-10));
      g.fillRect(xoffset+5, yband, colorbandwidth, colorbandheight);
    }

    /* Write in the text for the key */
    keyFontMetrics = g.getFontMetrics();
    keyFontMetrics.getHeight();
    g.setColor(Color.white);
    StringBuffer writeBuffer = new StringBuffer().append(datamin);
    writeBuffer.setLength(6);
    g.drawString(" "+writeBuffer, xoffset+5+colorbandwidth, ycanvaswidth - 5);
    StringBuffer writeBuffer2 = new StringBuffer().append(datamax);
    writeBuffer2.setLength(6);
    g.drawString(" "+writeBuffer2, xoffset+5+colorbandwidth, 
		 keyFontMetrics.getHeight() + 5);

    /* Draw outline frame */
    g.setColor(Color.white);
    g.drawRect(xoffset, yoffset, xkeywidth, ykeywidth);
  }

  void doIteration() throws MPIException {
    double error;
    double scrap;
    int i, j;
    /* Record the old pressure values */
    for (i = 1; i < imax; ++i)
      for (j = 1; j < jmax; ++j) {
	opg[i][j] = pg[i][j];
      }

    /* Set new far field mach number (if necessary) */
    if (machff != machfftarget) {
      if (Math.abs(machfftarget-machff) <= 0.02)
	machff = machfftarget;
      else {
	if (machfftarget > machff) {
	  machff += 0.02;
	}
	else
	  machff -= 0.02;
      }
    }

    calculateDummyCells(local_pg, local_tg, local_ug);


    calculateDeltaT();
    calculateDamping(local_pg, local_ug);

    /* Do the integration */
    /*...... Step 1 ......*/
    calculateF(local_pg, local_tg, local_ug);
    calculateG(local_pg, local_tg, local_ug);
    calculateR();

    for (i = 1; i <= n_bar; ++i)
     for(j = 1; j < jmax; ++j){
	local_ug1[i][j].a=
           local_ug[i][j].a-0.25*local_deltat[i][j]/local_a[i][j]
           *(local_r[i][j].a-local_d[i][j].a);

	local_ug1[i][j].b=
           local_ug[i][j].b-0.25*local_deltat[i][j]/local_a[i][j]
           *(local_r[i][j].b-local_d[i][j].b);

	local_ug1[i][j].c=
           local_ug[i][j].c-0.25*local_deltat[i][j]/local_a[i][j]
           *(local_r[i][j].c-local_d[i][j].c);

	local_ug1[i][j].d=
           local_ug[i][j].d-0.25*local_deltat[i][j]/local_a[i][j]
           *(local_r[i][j].d-local_d[i][j].d);
    }

    calculateStateVar(local_pg1, local_tg1, local_ug1);

    /*...... Step 2 ......*/
    calculateDummyCells(local_pg1,local_tg1, local_ug1);
    calculateF(local_pg1, local_tg1, local_ug1);
    calculateG(local_pg1, local_tg1, local_ug1);
    calculateR();

    for (i = 1; i <= n_bar; ++i)
      for (j = 1; j < jmax; ++j) {
	local_ug1[i][j].a=
	  local_ug[i][j].a-0.33333*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].a-local_d[i][j].a);

	local_ug1[i][j].b=
	  local_ug[i][j].b-0.33333*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].b-local_d[i][j].b);

	local_ug1[i][j].c=
	  local_ug[i][j].c-0.33333*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].c-local_d[i][j].c);

	local_ug1[i][j].d=
	  local_ug[i][j].d-0.33333*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].d-local_d[i][j].d);
      }

    calculateStateVar(local_pg1, local_tg1, local_ug1);
    
    /*...... Step 3 ......*/
    calculateDummyCells(local_pg1, local_tg1, local_ug1);
    calculateF(local_pg1, local_tg1, local_ug1);
    calculateG(local_pg1, local_tg1, local_ug1);
    calculateR();
    for (i = 1; i <= n_bar; ++i)
      for (j = 1; j < jmax; ++j) {
	local_ug1[i][j].a=
	  local_ug[i][j].a-0.5*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].a-local_d[i][j].a);

	local_ug1[i][j].b=
	  local_ug[i][j].b-0.5*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].b-local_d[i][j].b);
	local_ug1[i][j].c=
	  local_ug[i][j].c-0.5*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].c-local_d[i][j].c);
	local_ug1[i][j].d=
	  local_ug[i][j].d-0.5*local_deltat[i][j]/local_a[i][j]
          *(local_r[i][j].d-local_d[i][j].d);
      }
    calculateStateVar(local_pg1, local_tg1, local_ug1);
    
    /*..... Step 4 (final step) .....*/
    calculateDummyCells(local_pg1, local_tg1, local_ug1);
    calculateF(local_pg1, local_tg1, local_ug1);
    calculateG(local_pg1, local_tg1,local_ug1);
    calculateR(); 

    int initUg = 0;
    for (i = 1; i <= n_bar; ++i)
      for (j = 1; j < jmax; ++j) {
	local_ug[i][j].a -= local_deltat[i][j]/local_a[i][j]
                            *(local_r[i][j].a-local_d[i][j].a);

	local_ug[i][j].b -= local_deltat[i][j]/local_a[i][j]
                            *(local_r[i][j].b-local_d[i][j].b);

	local_ug[i][j].c -= local_deltat[i][j]/local_a[i][j]
                            *(local_r[i][j].c-local_d[i][j].c);

	local_ug[i][j].d -= local_deltat[i][j]/local_a[i][j]
                            *(local_r[i][j].d-local_d[i][j].d);
     } 

    calculateStateVar(local_pg, local_tg, local_ug);

    double one_dim_array[] = new double[(n_bar+1) * jmax * 6];
    int initDim = 0;
    for (i = 1; i <= n_bar+1; ++i) 
       for (j = 1; j <= jmax; ++j) {
         one_dim_array[initDim++] = local_ug[i][j].a;
         one_dim_array[initDim++] = local_ug[i][j].b;
         one_dim_array[initDim++] = local_ug[i][j].c;
         one_dim_array[initDim++] = local_ug[i][j].d;
         one_dim_array[initDim++] = local_pg[i][j];
         one_dim_array[initDim++] = local_tg[i][j];
       }
    double global_dim_array[] = new double[(imax +2)*(jmax + 2)*6];
    MPI.COMM_WORLD.Gather(one_dim_array,0,initDim,MPI.DOUBLE,
             global_dim_array,0,initDim,MPI.DOUBLE,0);

    MPI.COMM_WORLD.Bcast(global_dim_array,0,(imax+2)*(jmax+2)*6,MPI.DOUBLE,0);

    initDim = 0;
    int k = 1;
    int boundary = n_bar + 1;
    for (i = 1; i <= (n_bar+1)*np;++i){
      for (j = 1; j <= jmax; ++j)
        if (i != boundary){
           ug[k][j].a = global_dim_array[initDim++];
           ug[k][j].b = global_dim_array[initDim++];
           ug[k][j].c = global_dim_array[initDim++];
           ug[k][j].d = global_dim_array[initDim++];
           pg[k][j] = global_dim_array[initDim++];
           tg[k][j] = global_dim_array[initDim++];
        }
      if (i == boundary){
         initDim += jmax*6;
         k -= 1;
         boundary += boundary; 
      } 
      k++;
    }

    /* calculate RMS Pressure Error. */
    error = 0.0;
    for (i = 1; i < imax; ++i)
      for (j = 1; j < jmax; ++j) {
	scrap = pg[i][j] - opg[i][j];
	error += scrap*scrap;
      }
    error = Math.sqrt(error / (double)((imax-1) * (jmax-1)) );
//System.out.println("error : "+error); 
  }


  private void calculateStateVar(double localpg[][], double localtg[][],
			 Statevector localug[][])
    /* Calculates the new state values for Range-Kutta */
    /* Works for default values, 8/15 at 9:45 pm */
    {
      double temp, temp2;
      int i, j;

      for (i = 1; i <= n_bar; ++i) {
	for (j = 1; j < jmax; ++j) {
	  temp = localug[i][j].b;
	  temp2 = localug[i][j].c;
	  localtg[i][j] = localug[i][j].d/localug[i][j].a - 0.5 *
	    (temp*temp + temp2*temp2)/(localug[i][j].a*localug[i][j].a);

	  localtg[i][j] = localtg[i][j] / Cv;
	  localpg[i][j] = localug[i][j].a * rgas * localtg[i][j];
	}
      }
    }

  private void calculateR() {
    /* Works for default values, straight channel (all 0's) 8/11, 9:15 pm */
    
    double deltax, deltay;
    double temp;
    int i,j;
    Statevector scrap;
    
    for (i = 1; i <= n_bar; ++i) {
      for (j = 1; j < jmax; ++j) {
	
	/* Start by clearing R */
	local_r[i][j].a = 0.0;
	local_r[i][j].b = 0.0;
	local_r[i][j].c = 0.0;
	local_r[i][j].d = 0.0;
	
	/* East Face */
	deltay = (local_ynode[i][j] - local_ynode[i][j-1]);
	deltax = (local_xnode[i][j] - local_xnode[i][j-1]);
	temp = 0.5 * deltay;
	local_r[i][j].a += temp*(local_f[i][j].a + local_f[i+1][j].a);
	local_r[i][j].b += temp*(local_f[i][j].b + local_f[i+1][j].b);
	local_r[i][j].c += temp*(local_f[i][j].c + local_f[i+1][j].c);
	local_r[i][j].d += temp*(local_f[i][j].d + local_f[i+1][j].d);

	temp = -0.5*deltax;
	local_r[i][j].a += temp * (local_g[i][j].a+local_g[i+1][j].a);
	local_r[i][j].b += temp * (local_g[i][j].b+local_g[i+1][j].b);
	local_r[i][j].c += temp * (local_g[i][j].c+local_g[i+1][j].c);
	local_r[i][j].d += temp * (local_g[i][j].d+local_g[i+1][j].d);
	
	/* South Face */
	deltay = (local_ynode[i][j-1] - local_ynode[i-1][j-1]);  
	deltax = (local_xnode[i][j-1] - local_xnode[i-1][j-1]);

	temp = 0.5 * deltay;
	local_r[i][j].a  += temp*(local_f[i][j].a+local_f[i][j-1].a);
	local_r[i][j].b  += temp*(local_f[i][j].b+local_f[i][j-1].b);
	local_r[i][j].c  += temp*(local_f[i][j].c+local_f[i][j-1].c);
	local_r[i][j].d  += temp*(local_f[i][j].d+local_f[i][j-1].d);

	temp = -0.5*deltax;
	local_r[i][j].a += temp * (local_g[i][j].a+local_g[i][j-1].a);
	local_r[i][j].b += temp * (local_g[i][j].b+local_g[i][j-1].b);
	local_r[i][j].c += temp * (local_g[i][j].c+local_g[i][j-1].c);
	local_r[i][j].d += temp * (local_g[i][j].d+local_g[i][j-1].d);
	
	/* West Face */
	deltay = (local_ynode[i-1][j-1] - local_ynode[i-1][j]);
	deltax = (local_xnode[i-1][j-1] - local_xnode[i-1][j]);

	temp = 0.5 * deltay;
	local_r[i][j].a  += temp*(local_f[i][j].a+local_f[i-1][j].a);
	local_r[i][j].b  += temp*(local_f[i][j].b+local_f[i-1][j].b);
	local_r[i][j].c  += temp*(local_f[i][j].c+local_f[i-1][j].c);
	local_r[i][j].d  += temp*(local_f[i][j].d+local_f[i-1][j].d);

	temp = -0.5*deltax;
	local_r[i][j].a += temp * (local_g[i][j].a+local_g[i-1][j].a);
	local_r[i][j].b += temp * (local_g[i][j].b+local_g[i-1][j].b);
	local_r[i][j].c += temp * (local_g[i][j].c+local_g[i-1][j].c);
	local_r[i][j].d += temp * (local_g[i][j].d+local_g[i-1][j].d);
		
	/* North Face */
	deltay = (local_ynode[i-1][j] - local_ynode[i][j]);
	deltax = (local_xnode[i-1][j] - local_xnode[i][j]);

	temp = 0.5 * deltay;
	local_r[i][j].a  += temp*(local_f[i][j].a+local_f[i+1][j].a);
	local_r[i][j].b  += temp*(local_f[i][j].b+local_f[i+1][j].b);
	local_r[i][j].c  += temp*(local_f[i][j].c+local_f[i+1][j].c);
	local_r[i][j].d  += temp*(local_f[i][j].d+local_f[i+1][j].d);
	
	temp = -0.5*deltax;
	local_r[i][j].a += temp * (local_g[i][j].a+local_g[i][j+1].a);
	local_r[i][j].b += temp * (local_g[i][j].b+local_g[i][j+1].b);
	local_r[i][j].c += temp * (local_g[i][j].c+local_g[i][j+1].c);
	local_r[i][j].d += temp * (local_g[i][j].d+local_g[i][j+1].d);
      }
    }
  }

  private void calculateG(double localpg[][], double localtg[][],
		  Statevector localug[][]) throws MPIException {
    /* Works for default values 8/15, 5:15 pm */    
    double temp, temp2, temp3;
    double v;
    int i, j;
    
    for (i = 0; i <= n_bar + 1; ++i) {
      for (j = 0; j < jmax + 1; ++j) {
	v = localug[i][j].c / localug[i][j].a;
	local_g[i][j].a = localug[i][j].c;
	local_g[i][j].b = localug[i][j].b * v;
	local_g[i][j].c = localug[i][j].c*v + localpg[i][j];
	temp = localug[i][j].b * localug[i][j].b;
	temp2 = localug[i][j].c * localug[i][j].c;
	temp3 = localug[i][j].a * localug[i][j].a;
	local_g[i][j].d = localug[i][j].c * (Cp * localtg[i][j]+ 
		 (0.5 * (temp + temp2)/(temp3)));
      }
    }

    double[] send_FG  = new double[(jmax+1)*8];
    double[] recv_FG  = new double[(jmax+1)*8];
    int initFG;
    int right = my_rank + 1;
    int left  = my_rank - 1;
    if (my_rank == 0)
      left = np -1;
    else if(my_rank == (np -1))
      right = 0;

    initFG = 0;
    for(j=0;j<jmax+1;j++){
      send_FG[initFG++] = local_g[n_bar][j].a;
      send_FG[initFG++] = local_g[n_bar][j].b;
      send_FG[initFG++] = local_g[n_bar][j].c;
      send_FG[initFG++] = local_g[n_bar][j].d;
      send_FG[initFG++] = local_f[n_bar][j].a;
      send_FG[initFG++] = local_f[n_bar][j].b;
      send_FG[initFG++] = local_f[n_bar][j].c;
      send_FG[initFG++] = local_f[n_bar][j].d;
    }
    MPI.COMM_WORLD.Sendrecv(send_FG,0,(jmax+1)*8,MPI.DOUBLE,right,1,
		            recv_FG,0,(jmax+1)*8,MPI.DOUBLE,left ,1);
    initFG = 0;
    if (my_rank != 0)
      for(j=0;j<jmax+1;j++){
        local_g[0][j].a = recv_FG[initFG++];
        local_g[0][j].b = recv_FG[initFG++];
        local_g[0][j].c = recv_FG[initFG++];
        local_g[0][j].d = recv_FG[initFG++];
        local_f[0][j].a = recv_FG[initFG++];
        local_f[0][j].b = recv_FG[initFG++];
        local_f[0][j].c = recv_FG[initFG++];
        local_f[0][j].d = recv_FG[initFG++];
      }
    initFG = 0;
    for(j=0;j<jmax+1;j++){
      send_FG[initFG++] = local_g[1][j].a;
      send_FG[initFG++] = local_g[1][j].b;
      send_FG[initFG++] = local_g[1][j].c;
      send_FG[initFG++] = local_g[1][j].d;
      send_FG[initFG++] = local_f[1][j].a;
      send_FG[initFG++] = local_f[1][j].b;
      send_FG[initFG++] = local_f[1][j].c;
      send_FG[initFG++] = local_f[1][j].d;
     }     
    MPI.COMM_WORLD.Sendrecv(send_FG,0,(jmax+1)*8,MPI.DOUBLE,left ,1,
		            recv_FG,0,(jmax+1)*8,MPI.DOUBLE,right,1);
    initFG = 0;
    if (my_rank != (np-1))
      for(j=0;j<jmax+1;j++){
         local_g[n_bar+1][j].a = recv_FG[initFG++];
         local_g[n_bar+1][j].b = recv_FG[initFG++];
         local_g[n_bar+1][j].c = recv_FG[initFG++];
         local_g[n_bar+1][j].d = recv_FG[initFG++];
         local_f[n_bar+1][j].a = recv_FG[initFG++];
         local_f[n_bar+1][j].b = recv_FG[initFG++];
         local_f[n_bar+1][j].c = recv_FG[initFG++];
         local_f[n_bar+1][j].d = recv_FG[initFG++];
       }
  }

  private void calculateF(double localpg[][], double localtg[][], 
		  Statevector localug[][]) {
     /* Works for default values 8/15, 4:50 pm */
    {
      double u;
      double temp1, temp2, temp3;
      int i, j;
      
      for (i = 0; i <= n_bar+1; ++i) {
	for (j = 0; j < jmax + 1; ++j) {	  
	  u = localug[i][j].b/ localug[i][j].a;
	  local_f[i][j].a = localug[i][j].b;
	  local_f[i][j].b = localug[i][j].b *u + localpg[i][j];
	  local_f[i][j].c = localug[i][j].c * u;
	  temp1 = localug[i][j].b * localug[i][j].b;
	  temp2 = localug[i][j].c * localug[i][j].c;
	  temp3 = localug[i][j].a * localug[i][j].a;
	  local_f[i][j].d = localug[i][j].b * (Cp * localtg[i][j] + 
	      	 (0.5 * (temp1 + temp2)/(temp3)));
	}
      }
    }
  }

  private void calculateDamping(double localpg[][], Statevector localug[][]) throws MPIException {
      double adt, sbar;
      double nu2;
      double nu4;
      double tempdouble;
      int ascrap, i, j;
      Statevector temp = new Statevector();
      Statevector temp2 = new Statevector();
      Statevector scrap2 = new Statevector(), scrap4 = new Statevector();
      
      nu2 = secondOrderDamping * secondOrderNormalizer;
      nu4 = fourthOrderDamping * fourthOrderNormalizer;

      double send_pg[] = new double[jmax+1];
      double recv_pg[] = new double[jmax+1];
      int right = my_rank + 1;
      int left  = my_rank - 1;
      if (my_rank == 0)
        left = np -1;
      else if(my_rank == (np -1))
        right = 0;

      for(j=0;j<=jmax;j++)
           send_pg[j] = localpg[n_bar][j];

      MPI.COMM_WORLD.Sendrecv(send_pg,0,jmax+1,MPI.DOUBLE,right,1,
	  	              recv_pg,0,jmax+1,MPI.DOUBLE,left ,1);
      if(my_rank != 0)
        for(j=0;j<=jmax;j++)
          localpg[0][j] = recv_pg[j];

      for(j=0;j<=jmax;j++)
        send_pg[j] = localpg[1][j];

      MPI.COMM_WORLD.Sendrecv(send_pg,0,jmax+1,MPI.DOUBLE,left ,1,
		              recv_pg,0,jmax+1,MPI.DOUBLE,right,1);
      if (my_rank != (np-1))
        for(j=0;j<=jmax;j++)
          localpg[n_bar+1][j] = recv_pg[j];

      /* First do the pressure switches */
      /* Checked and works with defaults. */
      for (i = 1; i <= n_bar; ++i)
	for (j = 1; j < jmax; ++j){
	  local_sxi[i][j] = Math.abs(localpg[i+1][j] -
		2.0 * localpg[i][j] + localpg[i-1][j])/ localpg[i][j];
	  local_seta[i][j] = Math.abs(localpg[i][j+1] -
	       2.0 * localpg[i][j] + localpg[i][j-1]) / localpg[i][j];
	}

      double send_sxi[] = new double[jmax+1];
      double recv_sxi[] = new double[jmax+1];

      for(j=1;j<jmax;j++)
        send_sxi[j] = local_sxi[n_bar][j];

      MPI.COMM_WORLD.Sendrecv(send_sxi,0,jmax+1,MPI.DOUBLE,right,1,
	  	              recv_sxi,0,jmax+1,MPI.DOUBLE,left ,1);
      if(my_rank != 0)
        for(j=1;j<jmax;j++)
          local_sxi[0][j] = recv_sxi[j];

      for(j=1;j<jmax;j++)
        send_sxi[j] = local_sxi[1][j];

      MPI.COMM_WORLD.Sendrecv(send_sxi,0,jmax+1,MPI.DOUBLE,left ,1,
		              recv_sxi,0,jmax+1,MPI.DOUBLE,right,1);
      if (my_rank != (np-1))
        for(j=1;j<jmax;j++)
          local_sxi[n_bar+1][j] = recv_sxi[j];

      double send_ug[] = new double[jmax*8];
      double recv_ug[] = new double[jmax*8];      
      int initUG = 0;
      Statevector hold_n2[][] = new Statevector[2][jmax+2];

      initUG = 0;
      for(j=1;j<jmax;j++){
        send_ug[initUG++] = localug[n_bar][j].a;
        send_ug[initUG++] = localug[n_bar][j].b;
        send_ug[initUG++] = localug[n_bar][j].c;
        send_ug[initUG++] = localug[n_bar][j].d;
        send_ug[initUG++] = localug[n_bar-1][j].a;
        send_ug[initUG++] = localug[n_bar-1][j].b;
        send_ug[initUG++] = localug[n_bar-1][j].c;
        send_ug[initUG++] = localug[n_bar-1][j].d;
      }
      MPI.COMM_WORLD.Sendrecv(send_ug,0,jmax*8,MPI.DOUBLE,right,1,
	  	              recv_ug,0,jmax*8,MPI.DOUBLE,left ,1);
      initUG = 0;
      if(my_rank != 0)
        for(j=1;j<jmax;j++){
          localug[0][j].a = recv_ug[initUG++];
          localug[0][j].b = recv_ug[initUG++];
          localug[0][j].c = recv_ug[initUG++];
          localug[0][j].d = recv_ug[initUG++];

          hold_n2[0][j] = new Statevector();
          hold_n2[0][j].a = recv_ug[initUG++];
          hold_n2[0][j].b = recv_ug[initUG++];
          hold_n2[0][j].c = recv_ug[initUG++];
          hold_n2[0][j].d = recv_ug[initUG++];
        }

      initUG = 0;
      for(j=1;j<jmax;j++){
        send_ug[initUG++] = localug[1][j].a;
        send_ug[initUG++] = localug[1][j].b;
        send_ug[initUG++] = localug[1][j].c;
        send_ug[initUG++] = localug[1][j].d;
 
        send_ug[initUG++] = localug[2][j].a;
        send_ug[initUG++] = localug[2][j].b;
        send_ug[initUG++] = localug[2][j].c;
        send_ug[initUG++] = localug[2][j].d;
      }
      MPI.COMM_WORLD.Sendrecv(send_ug,0,jmax*8,MPI.DOUBLE,left,1,
	  	              recv_ug,0,jmax*8,MPI.DOUBLE,right,1);
      initUG = 0;
      if (my_rank != (np-1))
        for(j=1;j<jmax;j++){
          localug[n_bar+1][j].a = recv_ug[initUG++];
          localug[n_bar+1][j].b = recv_ug[initUG++];
          localug[n_bar+1][j].c = recv_ug[initUG++];
          localug[n_bar+1][j].d = recv_ug[initUG++];

          hold_n2[1][j] = new Statevector();
          hold_n2[1][j].a = recv_ug[initUG++];
          hold_n2[1][j].b = recv_ug[initUG++];
          hold_n2[1][j].c = recv_ug[initUG++];
          hold_n2[1][j].d = recv_ug[initUG++];
        }

      /* Then calculate the fluxes ..... */
      for (i = 1; i <= n_bar; ++i) {
	for (j = 1; j < jmax; ++j) {
	  
	  /* Clear values */
	  /* East Face */
	  if (i > 1 && i < n_bar) {
	    adt = (local_a[i][j] + local_a[i+1][j]) / 
                  (local_deltat[i][j] + local_deltat[i+1][j]);
	    sbar = (local_sxi[i+1][j] + local_sxi[i][j]) * 0.5;
	  }
	  else {
	    adt = local_a[i][j]/local_deltat[i][j];
	    sbar = local_sxi[i][j];
	  }

          if((my_rank < np-1 && i == n_bar)||(my_rank > 0 && i == 1)){
             adt = (local_a[i][j] + local_a[i+1][j]) / 
                   (local_deltat[i][j] + local_deltat[i+1][j]);
	     sbar = (local_sxi[i+1][j] + local_sxi[i][j]) * 0.5;
          }
	  tempdouble = nu2*sbar*adt;
	  scrap2.a = tempdouble * (localug[i+1][j].a-localug[i][j].a);
	  scrap2.b = tempdouble * (localug[i+1][j].b-localug[i][j].b);
	  scrap2.c = tempdouble * (localug[i+1][j].c-localug[i][j].c);
	  scrap2.d = tempdouble * (localug[i+1][j].d-localug[i][j].d);
	  if (i > 1 && i < n_bar) {
	    temp = localug[i+2][j].svect(localug[i-1][j]);
	    temp2.a = 3.0*(localug[i][j].a-localug[i+1][j].a);
	    temp2.b = 3.0*(localug[i][j].b-localug[i+1][j].b);
	    temp2.c = 3.0*(localug[i][j].c-localug[i+1][j].c);
	    temp2.d = 3.0*(localug[i][j].d-localug[i+1][j].d);

	    tempdouble = -nu4*adt;
	    scrap4.a = tempdouble*(temp.a+temp2.a);
	    scrap4.b = tempdouble*(temp.a+temp2.b);
	    scrap4.c = tempdouble*(temp.a+temp2.c);
	    scrap4.d = tempdouble*(temp.a+temp2.d);
	  }
	  else {
	    scrap4.a = 0.0;
	    scrap4.b = 0.0;
	    scrap4.c = 0.0;
	    scrap4.d = 0.0;
	  }

          if((my_rank < np-1 && i == n_bar)||(my_rank > 0 && i == 1)){
            if (my_rank < np-1 && i==n_bar)
	       temp = hold_n2[1][j].svect(localug[i-1][j]);
            else 
	       temp = localug[i+2][j].svect(localug[i-1][j]);
	    temp2.a = 3.0*(localug[i][j].a-localug[i+1][j].a);
	    temp2.b = 3.0*(localug[i][j].b-localug[i+1][j].b);
	    temp2.c = 3.0*(localug[i][j].c-localug[i+1][j].c);
	    temp2.d = 3.0*(localug[i][j].d-localug[i+1][j].d);

	    tempdouble = -nu4*adt;
	    scrap4.a = tempdouble*(temp.a+temp2.a);
	    scrap4.b = tempdouble*(temp.a+temp2.b);
	    scrap4.c = tempdouble*(temp.a+temp2.c);
	    scrap4.d = tempdouble*(temp.a+temp2.d);
	  }
	  temp.a = scrap2.a + scrap4.a;
	  temp.b = scrap2.b + scrap4.b;
	  temp.c = scrap2.c + scrap4.c;
	  temp.d = scrap2.d + scrap4.d;
	  local_d[i][j] = temp;

	  /* West Face */ 
	  if(i > 1 && i < n_bar) {
	    adt = (local_a[i][j] + local_a[i-1][j]) / 
                  (local_deltat[i][j] + local_deltat[i-1][j]);
	    sbar = (local_sxi[i][j] + local_sxi[i-1][j]) *0.5;
	  }
	  else {
	    adt = local_a[i][j]/local_deltat[i][j];
	    sbar = local_sxi[i][j];
	  }
          if((my_rank < np-1 && i == n_bar)||(my_rank > 0 && i == 1)){
	    adt = (local_a[i][j] + local_a[i-1][j]) / 
                  (local_deltat[i][j] + local_deltat[i-1][j]);
	    sbar = (local_sxi[i][j] + local_sxi[i-1][j]) *0.5;
	  }
	  tempdouble = -nu2*sbar*adt;
	  scrap2.a = tempdouble * (localug[i][j].a-localug[i-1][j].a);
	  scrap2.b = tempdouble * (localug[i][j].b-localug[i-1][j].b);
	  scrap2.c = tempdouble * (localug[i][j].c-localug[i-1][j].c);
	  scrap2.d = tempdouble * (localug[i][j].d-localug[i-1][j].d);
	  if (i > 1 && i < n_bar) {
	    temp = localug[i+1][j].svect(localug[i-2][j]);
	    temp2.a = 3.0*(localug[i-1][j].a-localug[i][j].a);
	    temp2.b = 3.0*(localug[i-1][j].b-localug[i][j].b);
	    temp2.c = 3.0*(localug[i-1][j].c-localug[i][j].c);
	    temp2.d = 3.0*(localug[i-1][j].d-localug[i][j].d);

	    tempdouble = nu4*adt;
	    scrap4.a = tempdouble*(temp.a+temp2.a);
	    scrap4.b = tempdouble*(temp.a+temp2.b);
	    scrap4.c = tempdouble*(temp.a+temp2.c);
	    scrap4.d = tempdouble*(temp.a+temp2.d);
	  }
	  else {
	    scrap4.a = 0.0;
	    scrap4.b = 0.0;
	    scrap4.c = 0.0;
	    scrap4.d = 0.0;
	  }
          if((my_rank < np-1 && i == n_bar)||(my_rank > 0 && i == 1)){
            if(my_rank > 0 && i==1)
               temp = localug[i+1][j].svect(hold_n2[0][j]);
            else
 	       temp = localug[i+1][j].svect(localug[i-2][j]);
	    temp2.a = 3.0*(localug[i-1][j].a-localug[i][j].a);
	    temp2.b = 3.0*(localug[i-1][j].b-localug[i][j].b);
	    temp2.c = 3.0*(localug[i-1][j].c-localug[i][j].c);
	    temp2.d = 3.0*(localug[i-1][j].d-localug[i][j].d);

	    tempdouble = nu4*adt;
	    scrap4.a = tempdouble*(temp.a+temp2.a);
	    scrap4.b = tempdouble*(temp.a+temp2.b);
	    scrap4.c = tempdouble*(temp.a+temp2.c);
	    scrap4.d = tempdouble*(temp.a+temp2.d);
	  }

	  local_d[i][j].a += scrap2.a + scrap4.a;
	  local_d[i][j].b += scrap2.b + scrap4.b;
	  local_d[i][j].c += scrap2.c + scrap4.c;
	  local_d[i][j].d += scrap2.d + scrap4.d;

	  /* North Face */
	  if (j > 1 && j < jmax-1) {
	    adt = (local_a[i][j] + local_a[i][j+1]) / 
                  (local_deltat[i][j] + local_deltat[i][j+1]);
	    sbar = (local_seta[i][j] + local_seta[i][j+1]) * 0.5;
	  }
	  else {
	    adt = local_a[i][j]/local_deltat[i][j];
	    sbar = local_seta[i][j];
	  }
	  tempdouble = nu2*sbar*adt;
	  scrap2.a = tempdouble * (localug[i][j+1].a-localug[i][j].a);
	  scrap2.b = tempdouble * (localug[i][j+1].b-localug[i][j].b);
	  scrap2.c = tempdouble * (localug[i][j+1].c-localug[i][j].c);
	  scrap2.d = tempdouble * (localug[i][j+1].d-localug[i][j].d);

	  if (j > 1 && j < jmax-1) {
	    temp = localug[i][j+2].svect(localug[i][j-1]);
	    temp2.a = 3.0*(localug[i][j].a-localug[i][j+1].a);
	    temp2.b = 3.0*(localug[i][j].b-localug[i][j+1].b);
	    temp2.c = 3.0*(localug[i][j].c-localug[i][j+1].c);
	    temp2.d = 3.0*(localug[i][j].d-localug[i][j+1].d);

	    tempdouble = -nu4*adt;
	    scrap4.a = tempdouble*(temp.a+temp2.a);
	    scrap4.b = tempdouble*(temp.a+temp2.b);
	    scrap4.c = tempdouble*(temp.a+temp2.c);
	    scrap4.d = tempdouble*(temp.a+temp2.d);
	  }
	  else {
	    scrap4.a = 0.0;
	    scrap4.b = 0.0;
	    scrap4.c = 0.0;
	    scrap4.d = 0.0;
	  }
	  local_d[i][j].a += scrap2.a + scrap4.a;
	  local_d[i][j].b += scrap2.b + scrap4.b;
	  local_d[i][j].c += scrap2.c + scrap4.c;
	  local_d[i][j].d += scrap2.d + scrap4.d;
	  
	  /* South Face */
	  if (j > 1 && j < jmax-1) {
	    adt = (local_a[i][j] + local_a[i][j-1]) / 
                  (local_deltat[i][j] + local_deltat[i][j-1]);
	    sbar = (local_seta[i][j] + local_seta[i][j-1]) * 0.5;
	  }
	  else {
	    adt = local_a[i][j]/local_deltat[i][j];
	    sbar = local_seta[i][j];
	  }
	  tempdouble = -nu2*sbar*adt;
	  scrap2.a = tempdouble * (localug[i][j].a-localug[i][j-1].a);
	  scrap2.b = tempdouble * (localug[i][j].b-localug[i][j-1].b);
	  scrap2.c = tempdouble * (localug[i][j].c-localug[i][j-1].c);
	  scrap2.d = tempdouble * (localug[i][j].d-localug[i][j-1].d);

	  if (j > 1 && j < jmax-1) {
	    temp = localug[i][j+1].svect(localug[i][j-2]);
	    temp2.a = 3.0*(localug[i][j-1].a-localug[i][j].a);
	    temp2.b = 3.0*(localug[i][j-1].b-localug[i][j].b);
	    temp2.c = 3.0*(localug[i][j-1].c-localug[i][j].c);
	    temp2.d = 3.0*(localug[i][j-1].d-localug[i][j].d);

	    tempdouble = nu4*adt;
	    scrap4.a = tempdouble*(temp.a+temp2.a);
	    scrap4.b = tempdouble*(temp.a+temp2.b);
	    scrap4.c = tempdouble*(temp.a+temp2.c);
	    scrap4.d = tempdouble*(temp.a+temp2.d);
	  }
	  else {
	    scrap4.a = 0.0;
	    scrap4.b = 0.0;
	    scrap4.c = 0.0;
	    scrap4.d = 0.0;
	  }
	  local_d[i][j].a += scrap2.a + scrap4.a;
	  local_d[i][j].b += scrap2.b + scrap4.b;
	  local_d[i][j].c += scrap2.c + scrap4.c;
	  local_d[i][j].d += scrap2.d + scrap4.d;
	}
      }
  }
  
  private void calculateDeltaT() throws MPIException {
    double xeta, yeta, xxi, yxi;              /* Local change in x and y */
    int i, j;
    double mint;
    double c, q, r;
    double safety_factor = 0.3;
    
    for (i = 1; i <= n_bar; ++i)
      for (j = 1; j < jmax; ++j) {
	xxi = (local_xnode[i][j] - local_xnode[i-1][j] 
	       + local_xnode[i][j-1] - local_xnode[i-1][j-1]) * 0.5;
	yxi = (local_ynode[i][j] - local_ynode[i-1][j] 
               + local_ynode[i][j-1] - local_ynode[i-1][j-1]) * 0.5;
	xeta = (local_xnode[i][j] - local_xnode[i][j-1] 
	       + local_xnode[i-1][j] - local_xnode[i-1][j-1]) * 0.5;
	yeta = (local_ynode[i][j] - local_ynode[i][j-1] 
	       + local_ynode[i-1][j] - local_ynode[i-1][j-1]) * 0.5;
	
	q = (yeta * local_ug[i][j].b - xeta * local_ug[i][j].c);
	r = (-yxi * local_ug[i][j].b + xxi * local_ug[i][j].c);
	c = Math.sqrt (gamma * rgas * local_tg[i][j]);

	local_deltat[i][j] = safety_factor * 2.8284 * local_a[i][j] /

	  ( (Math.abs(q) + Math.abs(r))/local_ug[i][j].a + c * 
	   Math.sqrt(xxi*xxi + yxi*yxi + xeta*xeta + yeta*yeta +
				  2.0 * Math.abs(xeta*xxi + yeta*yxi)));
      }

    /* If that's the user's choice, make it time accurate */
    if (ntime == 1) {
      mint = 100000.0;
      for (i = 1; i <= n_bar; ++i)
	for (j = 1; j < jmax; ++j)
	  if (local_deltat[i][j] < mint)
	    mint = local_deltat[i][j];
      
      for (i = 1; i <= n_bar; ++i)
	for (j = 1; j < jmax; ++j)
	  local_deltat[i][j] = mint;
    }

    double send_deltat[] = new double[jmax];
    double recv_deltat[] = new double[jmax];

    int right = my_rank + 1;
    int left  = my_rank - 1;
    if (my_rank == 0)
      left = np -1;
    else if(my_rank == (np -1))
      right = 0;

    for(j=1;j<jmax;j++)
      send_deltat[j] = local_deltat[n_bar][j];

    MPI.COMM_WORLD.Sendrecv(send_deltat,0,jmax,MPI.DOUBLE,right,1,
    	                    recv_deltat,0,jmax,MPI.DOUBLE,left ,1);
    if(my_rank != 0)
      for(j=1;j<jmax;j++)
        local_deltat[0][j] = recv_deltat[j];

    for(j=1;j<jmax;j++)
      send_deltat[j] = local_deltat[1][j];

    MPI.COMM_WORLD.Sendrecv(send_deltat,0,jmax,MPI.DOUBLE,left ,1,
		            recv_deltat,0,jmax,MPI.DOUBLE,right,1);
    if (my_rank != (np-1))
      for(j=1;j<jmax;j++)
        local_deltat[n_bar+1][j] = recv_deltat[j];
  }

  private void calculateDummyCells(double localpg[][],
				   double localtg[][], Statevector localug[][]) {
    double c;
    double jminus;
    double jplus;
    double s;
    double rho, temp, u, v;
    double scrap, scrap2;
    double theta;
    double uprime;
    int i, j;
    Vector2 norm = new Vector2();
    Vector2 tan = new Vector2();
    Vector2 u1 = new Vector2();

    uff = machff;
    jplusff = uff + 2.0 / (gamma - 1.0) * cff;
    jminusff = uff - 2.0 / (gamma - 1.0) * cff;

    for (i = 1; i <= n_bar; ++i) {
      /* Bottom wall boundary cells */
      /* Routine checked by brute force for initial conditions. */
      /* Routine checked by brute force for random conditions. */
      /* Construct tangent vectors */
      tan.ihat = local_xnode[i][0] - local_xnode[i-1][0];
      tan.jhat = local_ynode[i][0] - local_ynode[i-1][0];
      norm.ihat = - (local_ynode[i][0] - local_ynode[i-1][0]);
      norm.jhat = local_xnode[i][0] - local_xnode[i-1][0];
      
      scrap = tan.magnitude();
      tan.ihat = tan.ihat / scrap;
      tan.jhat = tan.jhat / scrap;
      scrap = norm.magnitude();
      norm.ihat = norm.ihat / scrap;
      norm.jhat = norm.jhat / scrap;
      
      /* Now set some state variables */
      rho = localug[i][1].a;
      localtg[i][0] = localtg[i][1];
      u1.ihat = localug[i][1].b / rho;
      u1.jhat = localug[i][1].c / rho;
      
      u = u1.dot(tan) + u1.dot(norm) * tan.jhat /norm.jhat;
      u = u / (tan.ihat - (norm.ihat * tan.jhat / norm.jhat));
      
      v = - (u1.dot(norm) + u * norm.ihat) / norm.jhat;
      
      /* And construct the new state vector */
      localug[i][0].a = localug[i][1].a;
      localug[i][0].b = rho * u;
      localug[i][0].c = rho * v;
      localug[i][0].d = rho * (Cv * localtg[i][0] + 0.5 * (u*u + v*v));
      localpg[i][0] = localpg[i][1];
      
      /* Top Wall Boundary Cells */
      /* Checked numerically for default conditions. */
      /* Construct normal and tangent vectors */
      /* This part checked and works; it produces the correct vectors */
      tan.ihat = local_xnode[i][jmax-1] - local_xnode[i-1][jmax-1];
      tan.jhat = local_ynode[i][jmax-1] - local_ynode[i-1][jmax-1];
      norm.ihat = local_ynode[i][jmax-1] - local_ynode[i-1][jmax-1];
      norm.jhat = -(local_xnode[i][jmax-1] - local_xnode[i-1][jmax-1]);
      
      scrap = tan.magnitude();
      tan.ihat = tan.ihat / scrap;
      tan.jhat = tan.jhat / scrap;
      scrap = norm.magnitude();
      norm.ihat = norm.ihat / scrap;
      norm.jhat = norm.jhat / scrap;
     
      /* Now set some state variables */
      rho = localug[i][jmax-1].a;
      temp = localtg[i][jmax-1];
      u1.ihat = localug[i][jmax-1].b / rho;
      u1.jhat = localug[i][jmax-1].c / rho;
     
      u = u1.dot(tan) + u1.dot(norm) * tan.jhat /norm.jhat;
      u = u / (tan.ihat - (norm.ihat * tan.jhat / norm.jhat));
     
      v = - (u1.dot(norm) + u * norm.ihat) / norm.jhat;
      
      /* And construct the new state vector */
      localug[i][jmax].a = localug[i][jmax-1].a;
      localug[i][jmax].b = rho * u;
      localug[i][jmax].c = rho * v;
      localug[i][jmax].d = rho * (Cv * temp + 0.5 * (u*u + v*v));
      localtg[i][jmax] = temp;
      localpg[i][jmax] = localpg[i][jmax-1];
    }

   for (j = 1; j < jmax; ++j) {
     /* Inlet Boundary Cells: unchecked */
     /* Construct the normal vector; */
     norm.ihat = local_ynode[0][j-1] - local_ynode[0][j];
     norm.jhat = local_xnode[0][j] - local_xnode[0][j-1];
     scrap = norm.magnitude();
     norm.ihat = norm.ihat / scrap;
     norm.jhat = norm.jhat / scrap;
     theta = Math.acos((local_ynode[0][j-1] - local_ynode[0][j]) / 
      Math.sqrt((local_xnode[0][j] - local_xnode[0][j-1])
                *(local_xnode[0][j] - local_xnode[0][j-1]) 
	       + (local_ynode[0][j-1] - local_ynode[0][j]) 
               * (local_ynode[0][j-1] - local_ynode[0][j])));
     
     u1.ihat = localug[1][j].b / localug[1][j].a;
     u1.jhat = localug[1][j].c / localug[1][j].a;
     uprime = u1.ihat * Math.cos(theta);
     c = Math.sqrt(gamma * rgas * localtg[1][j]);
     /* Supersonic inflow; works on the initial conditions */
     if (uprime < -c) {
       /* Use far field conditions */
       localug[0][j].a = rhoff;
       localug[0][j].b = rhoff * uff;
       localug[0][j].c = rhoff * vff;
       localug[0][j].d = rhoff * (Cv * tff + 0.5 * (uff*uff + vff*vff));
       localtg[0][j] = tff;
       localpg[0][j] = pff;
     }
     /* Subsonic inflow */
     /* This works on the initial conditions */
     else if(uprime < 0.0) {
       /* Calculate Riemann invarients here */
       jminus = u1.ihat - 2.0/(gamma-1.0) * c;
       s = Math.log(pff) - gamma * Math.log(rhoff);
       v = vff;
       
       u = (jplusff + jminus) / 2.0;
       scrap = (jplusff - u) * (gamma-1.0) * 0.5;
       localtg[0][j] = (1.0 / (gamma * rgas)) * scrap * scrap;
       localpg[0][j] = Math.exp(s) / Math.pow((rgas * localtg[0][j]), gamma);
       localpg[0][j] = Math.pow(localpg[0][j], 1.0 / (1.0 - gamma));
       
       /* And now: construct the new state vector */

       localug[0][j].a = localpg[0][j] / (rgas * localtg[0][j]);
       localug[0][j].b = localug[0][j].a * u;
       localug[0][j].c = localug[0][j].a * v;
       localug[0][j].d = localug[0][j].a * (Cv * tff + 0.5 * (u*u + v*v));
     }
     /* Other options: */
     /* We should throw an exception here. */
     else {
       System.err.println("You have outflow at the inlet, which is not allowed.");
     }
     /* Outlet Boundary Cells */
     /* Construct the normal vector; works. */
     norm.ihat = local_ynode[0][j] - local_ynode[0][j-1];
     norm.jhat = local_xnode[0][j-1] - local_xnode[0][j];
     scrap = norm.magnitude();
     norm.ihat = norm.ihat / scrap;
     norm.jhat = norm.jhat / scrap;
     scrap = local_xnode[0][j-1] - local_xnode[0][j];
     scrap2 = local_ynode[0][j] - local_ynode[0][j-1];
     theta = Math.acos((local_ynode[0][j] - local_ynode[0][j-1]) / 
		       Math.sqrt(scrap*scrap + scrap2*scrap2));
     u1.ihat = localug[n_bar][j].b / localug[n_bar][j].a;
     u1.jhat = localug[n_bar][j].c / localug[n_bar][j].a;
     uprime = u1.ihat * Math.cos(theta);
     c = Math.sqrt(gamma * rgas * localtg[n_bar][j]);
     /* Supersonic outflow; works for defaults conditions. */
     if (uprime > c){
     /* Use a backward difference 2nd order derivative approximation */
     /* To set values at exit */
       localug[n_bar+1][j].a = 
              2.0 * localug[n_bar][j].a - localug[n_bar-1][j].a;
       localug[n_bar+1][j].b = 
              2.0 * localug[n_bar][j].b - localug[n_bar-1][j].b;
       localug[n_bar+1][j].c = 
              2.0 * localug[n_bar][j].c - localug[n_bar-1][j].c;
       localug[n_bar+1][j].d = 
              2.0 * localug[n_bar][j].d - localug[n_bar-1][j].d;
        
       localpg[n_bar+1][j] = 2.0 * localpg[n_bar][j] - localpg[n_bar-1][j];
       localtg[n_bar+1][j] = 2.0 * localtg[n_bar][j] - localtg[n_bar-1][j];
     }
     /* Subsonic Outflow; works for defaults conditions. */
     else if (uprime < c && uprime > 0) {
       jplus = u1.ihat + 2.0/(gamma - 1) * c;
       v = localug[n_bar][j].c / localug[n_bar][j].a;
       s = Math.log(localpg[n_bar][j]) -
                    gamma * Math.log(localug[n_bar][j].a);
       
       u = (jplus + jminusff) / 2.0;
       scrap =(jplus - u)* (gamma-1.0) * 0.5;
       localtg[n_bar+1][j] = (1.0 / (gamma * rgas)) * scrap * scrap;
       localpg[n_bar+1][j] = Math.exp(s) / 
                       Math.pow((rgas * localtg[n_bar+1][j]), gamma);
       localpg[n_bar+1][j] = Math.pow(localpg[n_bar+1][j], 1.0 / 
                             (1.0-gamma));
       rho = localpg[n_bar+1][j]/ (rgas * localtg[n_bar+1][j]);
       
       /* And now, construct the new state vector. */
       localug[n_bar+1][j].a = rho;
       localug[n_bar+1][j].b = rho * u;
       localug[n_bar+1][j].c = rho * v;
       localug[n_bar+1][j].d = rho * (Cv * localtg[n_bar+1][j] 
                   + 0.5 * (u*u + v*v));
       
     }

     /* Other cases that shouldn't have to be used. */
     if (uprime < -c) {
       /* Supersonic inflow.  Use far field conditions */
       localug[0][j].a = rhoff;
       localug[0][j].b = rhoff * uff;
       localug[0][j].c = rhoff * vff;
       localug[0][j].d = rhoff * (Cv * tff + 0.5 * (uff*uff + vff*vff));
       localtg[0][j] = tff;
       localpg[0][j] = pff;
     }
     /* Subsonic inflow.  This works on the initial conditions. */
     else if(uprime < 0.0) {

       /* Debug: throw exception here? */
       /* Calculate Riemann invarients here. */

       jminus = u1.ihat - 2.0/(gamma-1.0) * c;
       s = Math.log(pff) - gamma * Math.log(rhoff);
       v = vff;
       
       u = (jplusff + jminus) / 2.0;
       scrap = (jplusff - u)* (gamma-1.0) * 0.5;
       localtg[0][j] = (1.0 / (gamma * rgas)) * scrap * scrap;
       localpg[0][j] = Math.exp(s) / Math.pow((rgas * localtg[0][j]), gamma);
       localpg[0][j] = Math.pow(localpg[0][j], 1.0 / (1.0 - gamma));
       
       /* And now: construct the new state vector. */

       localug[0][j].a = localpg[0][j] / (rgas * localtg[0][j]);
       localug[0][j].b = localug[0][j].a * u;
       localug[0][j].c = localug[0][j].a * v;
       localug[0][j].d = localug[0][j].a * (Cv * tff + 0.5 * (u*u + v*v));
     }
     else if ((uprime < c && uprime > 0) || (uprime > c)){}
     /* Other Options */
     /* Debug: throw exception here? */
     else {
       System.err.println("You have inflow at the outlet, which is not allowed.");
     }
   } 
   /* Do something with corners to avoid division by zero errors */
   /* What you do shouldn't matter */
   localug[0][0] = localug[1][0];
   localug[n_bar+1][0] = localug[n_bar+1][1];
   localug[0][jmax] = localug[1][jmax];
   localug[n_bar+1][jmax] = localug[n_bar+1][jmax-1];
  }

  void drawEdge(Graphics g, int width, int height) {
    /* Draws the outside border of the grid (as opposed to the frame) */
    int gridheight,  /* height of drawing region for grid in pixels */
        gridwidth;   /* Width of same */
    int i, j;
    int x1=0, x2, y1=0, y2;
    int xoffset, yoffset;
    double scale;

    gridheight = height;  /* Leave room at bottom for labels */
    gridwidth = width;
    scale = Math.min(gridwidth/(xmax-xmin), gridheight/(ymax-ymin));
    xoffset = (int) (xmin * scale);
    yoffset = (int) (ymin * scale);

    g.setColor(Color.white);
    for (i = 0; i < imax-1; ++i) {
      x1 = (int) (scale * xnode[i][0]) - xoffset;
      y1 = gridheight - (int) (scale * ynode[i][0]) - yoffset;
      x2 = (int) (scale * xnode[i+1][0]) - xoffset;
      y2 = gridheight - (int) (scale * ynode[i+1][0]) - yoffset;
      g.drawLine(x1, y1, x2, y2);

      x1 = (int) (scale * xnode[i][jmax-1]) - xoffset;
      y1 = gridheight - (int) (scale * ynode[i][jmax-1]) - yoffset;
      x2 = (int) (scale * xnode[i+1][jmax-1]) - xoffset;
      y2 = gridheight - (int) (scale * ynode[i+1][jmax-1]) - yoffset;
      g.drawLine(x1, y1, x2, y2);
    }
    for (j =0; j < jmax-1; ++j) {
      x1 = (int) (scale * xnode[imax-1][j]) - xoffset;
      y1 = gridheight - (int) (scale * ynode[imax-1][j]) - yoffset;
      x2 = (int) (scale * xnode[imax-1][j+1]) - xoffset;
      y2 = gridheight - (int) (scale * ynode[imax-1][j+1]) - yoffset;
      g.drawLine(x1, y1, x2, y2);

      x1 = (int) (scale * xnode[0][j]) - xoffset;
      y1 = gridheight - (int) (scale * ynode[0][j]) - yoffset;
      x2 = (int) (scale * xnode[0][j+1]) - xoffset;
      y2 = gridheight - (int) (scale * ynode[0][j+1]) - yoffset;
      g.drawLine(x1, y1, x2, y2);
    }
    int k = 1;
    String s;
    int tempX1=0;

    for (i = 0; i < imax-1; ++i)
      for (j = 0; j < jmax-1; ++j) {
	x1 = (int) (scale * xnode[i][j]) - xoffset;
	y1 = gridheight - (int) (scale * ynode[i][j]) - yoffset;
	x2 = (int) (scale * xnode[i+1][j]) - xoffset;
	y2 = gridheight - (int) (scale * ynode[i+1][j]) - yoffset;
        if (i == n_bar*k){
          g.setColor(Color.lightGray);
          g.fillRect(x2-3,0,6,height);
//	  g.drawLine(x2, 0, x2, height);
          g.setColor(Color.yellow);
          s = "Proc "+k;
          tempX1 = (int)(tempX1+x1)/2;
          g.drawString(s,tempX1,(int)height/2);
          tempX1 = x1;
          k++;
        }
    }
    s = "Proc "+k;
    tempX1 = (int)(tempX1+x1)/2;
    g.drawString(s,tempX1,(int)height/2);
  }

  void drawTufts(Graphics g, int width, int height) {
    Color tuftscolor;
    double scale, theta;
    int gridwidth, gridheight;
    int i, j;
    int x1, x2, y1, y2;
    int xoffset, yoffset;

    tuftscolor = new Color(150,150,150);

    gridheight = height;  /* Leave room for labels if necessary */
    gridwidth = width;

    g.setColor(tuftscolor);
    scale = Math.min(gridwidth/(xmax-xmin), gridheight/(ymax-ymin));
    xoffset = (int) (xmin * scale);
    yoffset = (int) (ymin * scale);
    for(i = 0; i < imax-1; ++i)
      for (j = 0; j < jmax-1; ++j) {
	x1 = (int) (scale * xnode[i][j]) - xoffset;
	y1 = gridheight - (int) (scale * ynode[i][j]) - yoffset;
	if (Math.abs(ug[i][j].b) > 0.00001) {
	    theta = -Math.atan(ug[i][j].c/ug[i][j].b);
	  }
	else {
	  if (ug[i][j].c > 0.0)
	    theta = -Math.PI * 0.5;
	  else
	    theta = -Math.PI * 1.5;
	}
	x2 = x1 + (int) (6.0*Math.cos(theta));
	y2 = y1 + (int) (6.0*Math.sin(theta));
	g.drawLine(x1, y1, x2, y2);
      }
  }

  void drawGrid(Graphics g, int width, int height) {
    /* Draws the grid; does not draw outside edges */
    Color gridcolor;
    int i, j;               /* Dummy Counters */
    int gridheight,         /* height of drawing region for grid in pixels */
        gridwidth;          /* Width of same */
    int x1, y1, x2, y2;
    int xoffset, yoffset;
    double scale;
    java.awt.Dimension x;

    gridheight = height;    /* Leave room for labels if necessary */
    gridwidth = width;

    gridcolor = new Color(100,100,100);
    g.setColor(gridcolor);
    scale = Math.min(gridwidth/(xmax-xmin), gridheight/(ymax-ymin));
    xoffset = (int) (xmin * scale);
    yoffset = (int) (ymin * scale);
    for (i = 0; i < imax-1; ++i)
      for (j = 0; j < jmax-1; ++j) {
	x1 = (int) (scale * xnode[i][j]) - xoffset;
	y1 = gridheight - (int) (scale * ynode[i][j]) - yoffset;
	x2 = (int) (scale * xnode[i+1][j]) - xoffset;
	y2 = gridheight - (int) (scale * ynode[i+1][j]) - yoffset;
	
	g.drawLine(x1, y1, x2, y2);
	x2 = (int) (scale * xnode[i][j+1]) - xoffset;
	y2 = gridheight - (int) (scale * ynode[i][j+1]) - yoffset;
	g.drawLine(x1, y1, x2, y2);
      }
    for (i = 0; i < imax-1; ++i) {
      x1 = (int) (scale * xnode[i][jmax-1]) - xoffset;
      y1 = gridheight - (int) (scale * ynode[i][jmax-1]) - yoffset;
      x2 = (int) (scale * xnode[i+1][jmax-1]) - xoffset;
      y2 = gridheight - (int) (scale * ynode[i+1][jmax-1]) - yoffset;
      g.drawLine(x1, y1, x2, y2);
    }
    for (j =0; j < jmax-1; ++j) {
      x1 = (int) (scale * xnode[imax-1][j]) - xoffset;
      y1 = gridheight - (int) (scale * ynode[imax-1][j]) - yoffset;
      x2 = (int) (scale * xnode[imax-1][j+1]) - xoffset;
      y2 = gridheight - (int) (scale * ynode[imax-1][j+1]) - yoffset;
      g.drawLine(x1, y1, x2, y2);
    }
  }

  void drawLinePlot(Graphics g, int ndata, int width, int height) {
    int gridheight, gridwidth;
    int jmid;
    int i, j;
    int plotheight, plotwidth;
    double plotdata[][];
    double scalex, scaley;
    double mach, pres;
    double plotmin, plotmax;
    double temp, vel, c;
    double u, v;
    int xoffset, yoffset;
    int n, x, y;
    int x1, y1, x2, y2;
    int xmargin = 55;
    int ymargin = 30;

    gridheight = height;
    gridwidth = width;
    plotheight = height-ymargin-20;  /* Leave room at bottom for labels */
    plotwidth = width-xmargin-20;

    plotmin=Double.MAX_VALUE;
    plotmax = Double.MIN_VALUE;
    plotdata = new double[3][imax];
    jmid = (jmax + 1) / 2;
    for (n = 0; n < 3; ++n) {
      if (n == 0)
	j = 1;
      else if (n==1)
	j = jmid;
      else
	j = jmax;
      
      for (i=1; i < imax+1; ++i) {
	switch(ndata) {
	case 0:                /* Density */
	  plotdata[n][i-1] = ug[i][j].a;
	  plotmin = Math.min(plotdata[n][i-1], plotmin);
	  plotmax = Math.max(plotdata[n][i-1], plotmax);
	  break;
	case 1:                /* Pressure */
	  u = ug[i][j].b/ug[i][j].a;
	  v = ug[i][j].c/ug[i][j].a;
	  plotdata[n][i-1] = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  plotdata[n][i-1] = plotdata[n][i-1]/Cv;
	  plotdata[n][i-1] = plotdata[n][i-1] * rgas * ug[i][j].a;
	  plotmax = Math.max(plotmax, plotdata[n][i-1]);
	  plotmin = Math.min(plotmin, plotdata[n][i-1]);
	  break;
	case 2:                /* Temperature */
	  u = ug[i][j].b/ug[i][j].a;
	  v = ug[i][j].c/ug[i][j].a;
	  plotdata[n][i-1] = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  plotdata[n][i-1] = plotdata[n][i-1]/Cv;
	  plotmax = Math.max(plotmax, plotdata[n][i-1]);
	  plotmin = Math.min(plotmin, plotdata[n][i-1]);
	  break;
	case 3:                /* Mach Number */
	  u = ug[i][j].b/ ug[i][j].a;
	  v = ug[i][j].c / ug[i][j].a;
	  vel = Math.sqrt(u*u + v*v);
	  temp = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  temp = temp/Cv;
	  c = Math.sqrt(gamma * rgas * temp);
	  plotdata[n][i-1] = vel / c;
	  plotmax = Math.max(plotmax, plotdata[n][i-1]);
	  plotmin = Math.min(plotmin, plotdata[n][i-1]);
	break;
	case 4:                /* Axial Velocity */
	  plotdata[n][i-1] = ug[i][j].b / ug[i][j].a;
	  plotmax = Math.max(plotmax, plotdata[n][i-1]);
	  plotmin = Math.min(plotmin, plotdata[n][i-1]);
	  break;
	case 5:                /* Normal Velocity */
	  plotdata[n][i-1] = ug[i][j].c / ug[i][j].a;
	  plotmax = Math.max(plotmax, plotdata[n][i-1]);
	  plotmin = Math.min(plotmin, plotdata[n][i-1]);
	  break;
	case 6:                /* Stagnation (Total) Pressure */
	  u = ug[i][j].b/ ug[i][j].a;
	  v = ug[i][j].c / ug[i][j].a;
	  vel = Math.sqrt(u*u + v*v);
	  temp = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  temp = temp/Cv;
	  c = Math.sqrt(gamma * rgas * temp);
	  mach = vel/c;
	  mach = (1.0 + (gamma-1)*0.5*mach*mach);
	  pres = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  pres = pres/Cv;
	  pres = pres * rgas * ug[i][j].a;
	  plotdata[n][i-1] = pres * Math.pow(mach, gamma/(gamma-1.0));
	  plotmax = Math.max(plotmax, plotdata[n][i-1]);
	  plotmin = Math.min(plotmin, plotdata[n][i-1]);
	  break;
	default:
	  break;
	}  /* ends the switch */
      }
    }

    /* Now draw it up! */
    /* Start with Axes */
    g.setColor(Color.white);
    if (plotmin > 0 && plotmax > 0) {
      plotmin = 0;
      plotmax *=1.20;
    }
    else if (plotmin < 0 && plotmax < 0) {
      plotmax = 0;
      plotmin *= 1.20;
    }

    /* Draw x axis */
    y = gridheight + (int) ( (plotmin / (plotmax - plotmin))
		      * (double) plotheight)-ymargin;
    g.drawLine(xmargin, y, xmargin+plotwidth, y);
    /* Draw y axis */
    g.drawLine(xmargin, gridheight-ymargin,
	       xmargin, gridheight-ymargin-plotheight);
    scalex = (double) plotwidth/(xmax-xmin);
    scaley = (double) plotheight/(plotmax-plotmin);
    xoffset = (int) (xmin * scalex);
    yoffset = (int) (plotmin * scaley);

    if (ndata == 3) {
      g.setColor(new Color(128, 128, 128));
      y = gridheight -ymargin - (int)(1.0*scaley)+yoffset;
      g.drawLine(xmargin, y, xmargin+plotwidth, y);
      g.setColor(Color.white);
      g.drawString("1.0", 5, y);
    }

    StringBuffer writeBuffer = new StringBuffer().append(plotmin);
    writeBuffer.setLength(6);
    g.drawString(writeBuffer.toString(), 5, gridheight-ymargin);
    g.drawString(String.valueOf(xmin), xmargin, gridheight-5);
    StringBuffer writeBuffer2 = new StringBuffer().append(plotmax);
    writeBuffer2.setLength(6);
    g.drawString(writeBuffer2.toString(), 5, gridheight-plotheight-ymargin+5);
    g.drawString(String.valueOf(xmax), plotwidth+xmargin-10, gridheight-5);
    x1 = gridwidth/2 - 10;
    g.drawString("Axial Position(x)", x1, gridheight-5);
//    g.drawString("Red: Top Wall", xmargin+15, gridheight-(ymargin+25));
    g.drawString("Red: Top Wall", xmargin+15, gridheight-(ymargin+800));
    g.drawString("Blue: Centerline", xmargin+15, gridheight-(ymargin+770));
    g.drawString("Green: Lower Wall", xmargin+15, gridheight-(ymargin+740));

    g.setColor(Color.green);
    for (i = 0; i < imax-1; ++i) {
      x1 = (int) (scalex * xnode[i][0]) - xoffset + xmargin;
      y1 = gridheight - ymargin - (int) (plotdata[0][i] * scaley) + yoffset;
      x2 = (int) (scalex * xnode[i+1][0]) - xoffset + xmargin;
      y2 = gridheight - ymargin - (int) (plotdata[0][i+1] * scaley) + yoffset;
      g.drawLine(x1, y1, x2, y2);
      g.drawOval(x1-3, y1-3, 6,6);
    }
    g.setColor(Color.blue);
    for (i = 0; i < imax-1; ++i) {
      x1 = (int) (scalex * xnode[i][jmid]) - xoffset + xmargin;
      y1 = gridheight - ymargin - (int) (plotdata[1][i] * scaley) + yoffset;
      x2 = (int) (scalex * xnode[i+1][jmid]) - xoffset + xmargin;
      y2 = gridheight - ymargin - (int) (plotdata[1][i+1] * scaley) + yoffset;
      g.drawLine(x1, y1, x2, y2);
      g.drawOval(x1-3, y1-3, 6,6);
    }
    g.setColor(Color.red);
    for (i = 0; i < imax-1; ++i) {
      x1 = (int) (scalex * xnode[i][jmax-1]) - xoffset + xmargin;
      y1 = gridheight - ymargin - (int) (plotdata[2][i] * scaley) + yoffset;
      x2 = (int) (scalex * xnode[i+1][jmax-1]) - xoffset + xmargin;
      y2 = gridheight - ymargin - (int) (plotdata[2][i+1] * scaley) + yoffset;
      g.drawLine(x1, y1, x2, y2);
      g.drawOval(x1-3, y1-3, 6,6);
    }    
  }

  void drawContour(Color ctable[], Graphics g, int ndata,
		   int width, int height) {

    /* Draws contours outlining data set specified by ndata */

    boolean drawcont=false;
        double c;  /* Speed of sound */
    double contval[];
    double data[][];
    
    double frac;
    double mach;
    double pres;
    double scale;
    double scrap;
    double u,       /* x-velocity */
          v,        /* y-velocity */
          vel,      /* total velocity */
          temp;     /* Temperature */
    double uprime, vprime;
    double xnode1, xnode2;
    double ynode1, ynode2;
    int i, j;
    int i1=0, i2=0, i3=0, i4=0;
    int j1=0, j2=0, j3=0, j4=0;
    int n;
    int gridheight,   /* height of drawing region for grid in pixels */
        gridwidth;    /* Width of same */
    int speciesindex;
    int xdraw1, xdraw2;
    int ydraw1, ydraw2;
    int xoffset, yoffset;

    /* Calculate the data you want from the state vector */

    datamin = Double.MAX_VALUE;
    datamax = Double.MIN_VALUE;
    data = new double[imax][jmax];
    for (i = 1; i < imax+1; ++i)
      for (j = 1; j < jmax+1; ++j) {
	switch(ndata){
	case 0:           /* Density */
	  data[i-1][j-1] = ug[i][j].a;
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	break;
	case 1:          /* Pressure */
	  u = ug[i][j].b/ug[i][j].a;
	  v = ug[i][j].c/ug[i][j].a;
	  data[i-1][j-1] = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  data[i-1][j-1] = data[i-1][j-1]/Cv;
	  data[i-1][j-1] = data[i-1][j-1] * rgas * ug[i][j].a;
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	  break;
	case 2:         /* Temperature */
	  u = ug[i][j].b/ug[i][j].a;
	  v = ug[i][j].c/ug[i][j].a;
	  data[i-1][j-1] = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  data[i-1][j-1] = data[i-1][j-1]/Cv;
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	  break;
	case 3:        /* Mach Number */
	  u = ug[i][j].b/ ug[i][j].a;
	  v = ug[i][j].c / ug[i][j].a;
	  vel = Math.sqrt(u*u + v*v);
	  temp = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  temp = temp/Cv;
	  c = Math.sqrt(gamma * rgas * temp);
	  data[i-1][j-1] = vel / c;
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	break;
	case 4:        /* Axial Velocity */
	  data[i-1][j-1] = ug[i][j].b / ug[i][j].a;
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	  break;
	case 5:        /* Normal Velocity */
	  data[i-1][j-1] = ug[i][j].c / ug[i][j].a;
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	  break;
	case 6:        /* Stagnation (Total) Pressure */
	  u = ug[i][j].b/ ug[i][j].a;
	  v = ug[i][j].c / ug[i][j].a;
	  vel = Math.sqrt(u*u + v*v);
	  temp = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  temp = temp/Cv;
	  c = Math.sqrt(gamma * rgas * temp);
	  mach = vel/c;
	  mach = (1.0 + (gamma-1)*0.5*mach*mach);
	  pres = ug[i][j].d/ug[i][j].a - (0.5 * (u*u + v*v));
	  pres = pres/Cv;
	  pres = pres * rgas * ug[i][j].a;
	  data[i-1][j-1] = pres * Math.pow(mach, gamma/(gamma-1.0));
	  datamax = Math.max(datamax, data[i-1][j-1]);
	  datamin = Math.min(datamin, data[i-1][j-1]);
	  break;
	default:

	  /* Needs error checking here too?? */
	  break;
	}
      }

    /* Determine contour values */

    contval = new double[ncontour];
    scrap = datamax-datamin;
    for (n = 0; n < ncontour; ++n)
      contval[n] = datamin + ((double)n/(double)(ncontour-1))*scrap;
    /* Set screen values */

    gridheight = height;  /* Leave room at bottom for labels */
    gridwidth = width;
    scale = Math.min(gridwidth/(xmax-xmin), gridheight/(ymax-ymin));
    xoffset = (int) (xmin * scale);
    yoffset = (int) (ymin * scale);
    /* Go through cells looking for crossings */
    for (i = 0; i < imax-1; ++i)
      for (j = 0; j < jmax-1; ++j) {
	for (n = 0; n < ncontour; ++n) {
	  
	  /* First check the SW triangle */

	  drawcont = false;
	  if ((contval[n] > data[i][j] && contval[n] < data[i+1][j]) ||
	      (contval[n] < data[i][j] && contval[n] > data[i+1][j])) {
	    i1 = i; j1 = j; i2 = i+1; j2  = j; drawcont = true; 
	  }
	  if ((contval[n] > data[i][j] &&  contval[n] < data[i][j+1]) ||
	      (contval[n] < data[i][j] &&  contval[n] > data[i][j+1]))
	    if(drawcont) {
	      i3=i; j3=j; i4 = i; j4 = j+1;
	    }
	    else {
	      i1 = i; j1 = j; i2 = i; j2 = j+1; drawcont = true;
	    }
	  if ((contval[n] > data[i][j+1] &&
	       contval[n] < data[i+1][j]) ||
	      (contval[n] < data[i][j+1] && 
	       contval[n] > data[i+1][j])) {
	    i3 = i; j3 = j+1; i4 = i+1; j4 = j;
	  }
	  
	  if(drawcont) {  /* Go ahead, draw the contour */

	    /* Sets color of contour */

	    g.setColor(ctable[n]);
	    frac = Math.abs((contval[n]-data[i1][j1])/
	                    (data[i1][j1]-data[i2][j2]));
	    xnode1 = (xnode[i2][j2]-xnode[i1][j1]) * frac + xnode[i1][j1];
	    xdraw1 = (int) (scale * xnode1) - xoffset;
	    ynode1 = (ynode[i2][j2]-ynode[i1][j1]) * frac + ynode[i1][j1];
	    ydraw1 = gridheight - (int) (scale * ynode1) - yoffset;

	    frac = Math.abs((contval[n]-data[i3][j3])/
			      (data[i3][j3]-data[i4][j4]));
	    xnode2 = (xnode[i4][j4]-xnode[i3][j3]) * frac + xnode[i3][j3];
	    xdraw2 = (int) (scale * xnode2) - xoffset;
	    ynode2 = (ynode[i4][j4]-ynode[i3][j3]) * frac + ynode[i3][j3];
	    ydraw2 = gridheight - (int) (scale * ynode2) - yoffset;
	    g.drawLine(xdraw1, ydraw1, xdraw2, ydraw2);	    
	  }
	 
	  /* Then check the NE triangle */

	  drawcont = false;
	  if ((contval[n] > data[i][j+1] &&
	       contval[n] < data[i+1][j+1]) ||
	      (contval[n] < data[i][j+1] &&
	       contval[n] > data[i+1][j+1])) {
	    i1 = i; j1 = j+1; i2 = i+1; j2  = j+1; drawcont = true; 
	  }
	  if ((contval[n] > data[i+1][j] &&  contval[n] < data[i+1][j+1]) ||
	      (contval[n] < data[i+1][j] && contval[n] > data[i+1][j+1]))
	    if(drawcont) {
	      i3=i+1; j3=j; i4 = i+1; j4 = j+1;
	     }
	    else {
	      i1 = i+1; j1 = j; i2 = i+1; j2 = j+1; drawcont = true;
	    }
	  if ((contval[n] > data[i][j+1] && contval[n] < data[i+1][j]) ||
	      (contval[n] < data[i][j+1] && contval[n] > data[i+1][j])) {
	    i3 = i; j3 = j+1; i4 = i+1; j4 = j;
	  }
	  
	  if(drawcont) {  /* Go ahead, draw the contour */
	    g.setColor(ctable[n]);
	    frac = Math.abs((contval[n]-data[i1][j1])/
	                    (data[i1][j1]-data[i2][j2]));
	    xnode1 = (xnode[i2][j2]-xnode[i1][j1]) * frac + xnode[i1][j1];
	    xdraw1 = (int) (scale * xnode1) - xoffset;
	    ynode1 = (ynode[i2][j2]-ynode[i1][j1]) * frac + ynode[i1][j1];
	    ydraw1 = gridheight - (int) (scale * ynode1) - yoffset;

	    frac = Math.abs((contval[n]-data[i3][j3])/
			      (data[i3][j3]-data[i4][j4]));
	    xnode2 = (xnode[i4][j4]-xnode[i3][j3]) * frac + xnode[i3][j3];
	    xdraw2 = (int) (scale * xnode2) - xoffset;
	    ynode2 = (ynode[i4][j4]-ynode[i3][j3]) * frac + ynode[i3][j3];
	    ydraw2 = gridheight - (int) (scale * ynode2) - yoffset;
	    g.drawLine(xdraw1, ydraw1, xdraw2, ydraw2);	    
	  }
	}  /* Ends "for (n = 0; n < ncoutour...)" */
      }
  }
}

class Statevector
{
  double a;   /* Storage for Statevectors */
  double b;
  double c;
  double d;
  
  Statevector() {
    a = 0.0;
    b = 0.0;
    c = 0.0;
    d = 0.0;
  }

  /* Most of these vector manipulation routines are not          */
  /* used in this program because I inlined them for speed.      */
  /* I leave them here because they may be useful in the future. */

  public Statevector amvect(double m, Statevector that) {

    /* Adds statevectors multiplies the sum by scalar m */

     Statevector answer = new Statevector();

    answer.a = m * (this.a + that.a);
    answer.b = m * (this.b + that.b);
    answer.c = m * (this.c + that.c);
    answer.d = m * (this.d + that.d);
    return answer;
  }

  public Statevector avect(Statevector that) {
     Statevector answer = new Statevector();

    /* Adds two statevectors */

    answer.a = this.a + that.a;
    answer.b = this.b + that.b;
    answer.c = this.c + that.c;
    answer.d = this.d + that.d;
    return answer;
  }

  public Statevector mvect(double m) {
     Statevector answer = new Statevector();

    /* Multiplies statevector scalar m */    

    answer.a = m * this.a;
    answer.b = m * this.b;
    answer.c = m * this.c;
    answer.d = m * this.d;
    return answer;
  }

  public Statevector svect(Statevector that) {
     Statevector answer = new Statevector();

    /* Subtracts vector that from this */

    answer.a = this.a - that.a;
    answer.b = this.b - that.b;
    answer.c = this.c - that.c;
    answer.d = this.d - that.d;
    return answer;
  }

  public Statevector smvect(double m, Statevector that) {
     Statevector answer = new Statevector();

    /* Subtracts statevector that from this and multiplies the */
    /* result by scalar m */
    answer.a = m * (this.a - that.a);
    answer.b = m * (this.b - that.b);
    answer.c = m * (this.c - that.c);
    answer.d = m * (this.d - that.d);
    return answer;
  }
}

class Vector2
{
  double ihat;   /* Storage for 2-D vector */
  double jhat;

  Vector2() {
    ihat = 0.0;
    jhat = 0.0;
  }

  public double magnitude() {
    double mag;

    mag = Math.sqrt(this.ihat*this.ihat + this.jhat * this.jhat);
    return mag;
  }

  public double dot(Vector2 that) {
    /* Calculates dot product of two 2-d vector */
    double answer;

    answer = this.ihat * that.ihat + this.jhat * that.jhat;
    return answer;
  }
}

class DataPanel extends Panel {
  boolean gridSwitch = false;
  boolean tuftsSwitch = false;
  Color ctable[];
  datafield data2D;
  public double machff;
  public double fourthOrderDamping;
  public double secondOrderDamping;
  public int nplot;
  public int ntime;
  public int nview;
  int itercount;
  final int iterationsPerCycle = 1;  /* Number of iterations per redraw */
  final int keyWidth=100;            /* width of color key in pixels */

  /* For the screen buffering */

  private Image offScreenImage;
  private Graphics offScreenGraphics;
  private Dimension offScreenSize;

  DataPanel(InputStream instream) throws MPIException{ /* Read in the input file */
    itercount = 0;

    try {
      data2D = new datafield(instream);
   } catch (IOException e) {
      System.err.println("  Input/output error: probably a bad filename or bad connection." + e.getMessage());
    }
    
    machff = data2D.machfftarget;
    fourthOrderDamping = 1.0;
    secondOrderDamping = 1.0;

    /* Initialize Color Table */

    ctable = new Color[16];
    ctable[0] = new Color(0,0,255);
    ctable[1] = new Color(0,64,255);
    ctable[2] = new Color(0,128,255);
    ctable[3] = new Color(0,192,255);
    ctable[4] = new Color(0,255,255);
    ctable[5] = new Color(0,255,192);
    ctable[6] = new Color(0,255,128);
    ctable[7] = new Color(0,255,64);
    ctable[8] = new Color(0,255,0);
    ctable[9] = new Color(32,255,0);
    ctable[10] = new Color(96,255,0);
    ctable[11] = new Color(160,255,0);
    ctable[12] = new Color(255,255,0);
    ctable[13] = new Color(255,160,0);
    ctable[14] = new Color(255,96,0);
    ctable[15] = new Color(255,0,0);
  }

  public void doIteration() throws MPIException {
    int n;
    itercount++;
/**************************** bcast here ****************************/
    double element[] = new double[3];
    element[0] = secondOrderDamping;
    element[1] = fourthOrderDamping;
    element[2] = ntime;

    MPI.COMM_WORLD.Bcast(element,0,3,MPI.DOUBLE,0);

    secondOrderDamping = element[0];
    fourthOrderDamping = element[1];
    ntime = (int)element[2];

    for (n = 0; n < iterationsPerCycle; ++n) {
      data2D.machfftarget = machff;
      data2D.secondOrderDamping = secondOrderDamping;
      data2D.fourthOrderDamping = fourthOrderDamping;
      data2D.ntime = ntime;
      data2D.doIteration();
    }
    repaint();
  }

  public void setGridSwitch(boolean nchoice) {
    gridSwitch = nchoice;
  }

  public void setTuftsSwitch(boolean nchoice) {
    tuftsSwitch = nchoice;
  }
  
  public void paint(Graphics g) {
    if (itercount == 0)
      return;
  
    g.setColor(Color.black);
    if (nplot == 0) {
      g.fillRect(0, 0, getSize().width-keyWidth, getSize().height - 1);
      if (gridSwitch)
	data2D.drawGrid(g, getSize().width-keyWidth, getSize().height-1);
      if (tuftsSwitch)
	data2D.drawTufts(g, getSize().width-keyWidth, getSize().height-1);
      data2D.drawContour(ctable, g, nview, getSize().width-keyWidth,
			 getSize().height-1);
      data2D.drawEdge(g, getSize().width-keyWidth, getSize().height-1);
      data2D.drawKey(ctable, g, keyWidth, getSize().height-1, 
		     getSize().width-1, getSize().height-1);
    }
    else {
      g.fillRect(0, 0, getSize().width-1, getSize().height - 1);
      data2D.drawLinePlot(g, nview, getSize().width-1, getSize().height-1);
    }
  }

  public final synchronized void update (Graphics theG)

    /* This section is based on  a borrowed  (Matt Gray - MIT)  */
    /* NoFlickerApplet. (impliments Double Buffering) */ 

    {
      Dimension d = getSize();
      if((offScreenImage == null) || (d.width != offScreenSize.width) ||
	 (d.height != offScreenSize.height)) 
	{
	  offScreenImage = createImage(d.width, d.height);
	  offScreenSize = d;
	  offScreenGraphics = offScreenImage.getGraphics();
	  offScreenGraphics.setFont(getFont());
	}
    offScreenGraphics.setColor(Color.black);
    if (nplot == 0) {
      offScreenGraphics.fillRect(0,0,getSize().width-keyWidth, getSize().height - 1);
      if (gridSwitch){
	data2D.drawGrid(offScreenGraphics, getSize().width-keyWidth, 
			getSize().height-1);
      }
      if (tuftsSwitch){
	data2D.drawTufts(offScreenGraphics, getSize().width-keyWidth,
			 getSize().height-1);
      }
      data2D.drawContour(ctable, offScreenGraphics, nview,
			 getSize().width-keyWidth,
			 getSize().height-1);
      data2D.drawEdge(offScreenGraphics, getSize().width-keyWidth,
		      getSize().height-1);
      data2D.drawKey(ctable, offScreenGraphics, keyWidth, getSize().height-1, 
		     getSize().width-1, getSize().height-1);
    }
    else {
      offScreenGraphics.fillRect(0, 0, getSize().width-1, getSize().height - 1);
      data2D.drawLinePlot(offScreenGraphics, nview, getSize().width-1,
			  getSize().height-1);
    }
            theG.drawImage(offScreenImage, 0, 0, null);
    }
}

class ParameterPanel extends Panel implements AdjustmentListener,ActionListener {
  final int sliderMin = 0;
  final int sliderMax = 100;
  double sliderValMin;
  double sliderValue;
  double sliderValMax;
  Scrollbar slider;
  TextField textField;

  double getValue() {
    double f;
    try {
      f = Double.valueOf(textField.getText()).doubleValue();
    } catch (java.lang.NumberFormatException e) {
      f = sliderValMin;
    }
    return f;
  }

  void makeSlidePanel(String parameterTitle) {

    /* This lays out components, sets initial appearances */

    GridBagConstraints c = new GridBagConstraints();
    GridBagLayout  gridbag = new GridBagLayout();
    setLayout(gridbag);

    c.fill = GridBagConstraints.HORIZONTAL;

    Label label = new Label (parameterTitle, Label.LEFT);
    c.weightx = 0.0;
    gridbag.setConstraints(label, c);
    add(label);

    textField = new TextField(String.valueOf(sliderValue), 6);
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(textField, c);
    add(textField);
    textField.addActionListener(this);

    int sliderIntValue = 
      (int) ((sliderValue-sliderValMin)/(sliderValMax-sliderValMin) *
	     (double) (sliderMax-sliderMin)) + sliderMin;
    slider = new Scrollbar(Scrollbar.HORIZONTAL, sliderIntValue, 10,
			   sliderMin, sliderMax);
    c.weightx = 0.0;
    c.gridheight = 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(slider, c);
    add(slider);
    slider.addAdjustmentListener(this);
  }

  void setValue(int n) {
    setSliderValue(n);
    textField.setText(String.valueOf(sliderValue));
  }

  void setSliderValue(double f) {
    sliderValue = f;
    int sliderIntValue;
    sliderIntValue = 
      (int) ((f-sliderValMin) / (sliderValMax-sliderValMin) 
	     * (double) (sliderMax-sliderMin)) + sliderMin;
    if (sliderIntValue > sliderMax) {
      sliderIntValue = sliderMax;
      sliderValue = sliderValMax;
    }
    if (sliderIntValue < sliderMin) {
      sliderIntValue = sliderMin;
      sliderValue = sliderValMin;
    }
    slider.setValue(sliderIntValue);
    textField.setText(String.valueOf(sliderValue));
  }

  public void adjustmentValueChanged(AdjustmentEvent e) {
      sliderValue = ((double) slider.getValue()/
	(double) (sliderMax-sliderMin)) * 
	(sliderValMax-sliderValMin) + sliderValMin;
      textField.setText(String.valueOf((float)sliderValue));
  }

  public void actionPerformed (ActionEvent e) {
     setSliderValue(getValue());
  }
}

class MachffPanel extends ParameterPanel  {

  /* Ok, here's where we handle events for this scrollbar */

  MachffPanel(String parameterTitle, double min, double max, 
	      double initialMachff) {
    super();
    sliderValMin = min;
    sliderValMax = max;
    sliderValue = initialMachff;
    makeSlidePanel(parameterTitle);    
  }

  public void adjustmentValueChanged(AdjustmentEvent e) {
    double temp;

    /* Overrides ParameterPanel's default behavior */

      sliderValue = ((double) slider.getValue()/
	(double) (sliderMax-sliderMin)) * 
	(sliderValMax-sliderValMin) + sliderValMin;
      if (sliderValue > 0.9 && sliderValue < 1.3)
	sliderValue = 1.3;
      textField.setText(String.valueOf((float)sliderValue));
  }

  public void ActionPerformed (ActionEvent e){
    double temp = getValue();
      if (temp > 0.9 && temp < 1.3) {
	temp = 1.3;
        textField.setText(String.valueOf(temp));
      }
      setSliderValue(temp);  
  }
}

class DampingPanel extends ParameterPanel {

  /* Ok, here's where we handle events for this scrollbar */

  DampingPanel(String parameterTitle, double min, double max, 
	      double initialDamping) {
    super();
    sliderValMin = min;
    sliderValMax = max;
    sliderValue = initialDamping;
    makeSlidePanel(parameterTitle);    
  }
}

