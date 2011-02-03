import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import mpi.*; 

public class Metrop extends Frame implements ActionListener, Instructions {

  private static int flg = 1;

  private Panel p, pv, pb;

  private Button Brun, Bstop, Bquit, Bblank;

  static String[] args;
  static int[] runflag = new int[1];

  private MonteGraphics mg, mgraphics;
  private MetroInputs minputs;

  private static InstructionStream instrs = new InstructionStream() ;

  ////////////////////////////////////////////////////////////
  public void init() {
    setFont(new Font("Helvetica", Font.PLAIN, 14));

    pb = new Panel();

    //mb = new MonteButton();
    minputs = new MetroInputs(this);

    //pv.setLayout(new FlowLayout())
    //pb.setLayout(new FlowLayout());
    pb.setLayout(new GridLayout(10, 1));
    pb.setBackground(Color.orange);
    //pb.setForeground(Color.red);


    Brun = new Button("Run");
    //Brun.addActionListener(new MonteButton(mg));
    Brun.addActionListener(this);
    pb.add(Brun);

    Bstop = new Button("Stop");
    //Brun.addActionListener(new MonteButton(mg));
    Bstop.addActionListener(this);
    pb.add(Bstop);

    Bblank = new Button("");
    //Brun.addActionListener(new MonteButton(mg));
    //Bquit.addActionListener(this);
    pb.add(Bblank);

    Bquit = new Button("Quit");
    //Brun.addActionListener(new MonteButton(mg));
    Bquit.addActionListener(this);
    pb.add(Bquit);



    setLayout(new BorderLayout());
    mgraphics = new MonteGraphics();
    add("Center",mgraphics);
    add("North",minputs);
    add("West",pb);

  }

  ////////////////////////////////////////////////////////////
  public void actionPerformed(ActionEvent evt) {

    if(evt.getActionCommand().equals("Run"))
      instrs.add(RUN) ;
    else if(evt.getActionCommand().equals("Stop"))
      instrs.add(STOP) ;
    else if(evt.getActionCommand().equals("Quit"))
      instrs.add(QUIT) ;
 }

  ////////////////////////////////////////////////////////////
  void update() {
    if(CommVars.procnum == 0) 
      mgraphics.repaint();
  }

  ////////////////////////////////////////////////////////////
  static public void main(String[] args) throws MPIException
  {
    
    // Initialize some MPI stuff.     
    MPI.Init(args);

    Metrop tester = new Metrop();

    // Initialize some parallel stuff.
    Init.met_mpi(); 

    if(CommVars.procnum == 0) {
      tester.init();
      tester.setSize(1000, 700);
      tester.setLocation(20,30);
      tester.show(); 
    }


    while (true) {
      if(CommVars.procnum == 0)
        runflag [0] = instrs.getNext() ;

      MPI.COMM_WORLD.Bcast(runflag,0,1,MPI.INT,0);	
        
      if(runflag [0] == QUIT)
        break ;
      
      MPI.COMM_WORLD.Barrier();

      // Read in the parameters.    
      Init.input();
	
      // Do domain decomposition of the lattice over the nodes.
      Domain.decompose(); 

      tester.metstart();

    }  // end while
    
    MPI.Finalize();
    System.exit(0) ; 
  }


  ////////////////////////////////////////////////////////////  
  public void metstart() throws MPIException {
    int    i, start;
    long   seed;
    double starttime, endtime, time;
    

    System.out.println("proc is "+MPI.Get_processor_name());

    // Initialize the random number generator.
    Prand.randinit(); 
 
 
    // Initialize the spins.
    Init.init_spins();
 
 
    // Initialize the data.
    Measure.init();

    CommVars.iter = 0;
    // Thermalize the system.
    metupdate(CommVars.therm_sweeps);
 
    // Metropolis update, taking measurements every 'sweeps' iterations.
 
    time = 0;
 
  stop:
    for (i=0; i<CommVars.measurements; i++) {
      starttime = MPI.Wtime();

      metupdate(CommVars.sweeps);

      endtime = MPI.Wtime();
      time += (endtime - starttime);

      Measure.compute();

      if(CommVars.procnum == 0) 
        Measure.mid_output();

      if(CommVars.procnum == 0)
        runflag [0] = instrs.getStopping() ? STOP : RUN ;
 
      MPI.COMM_WORLD.Bcast(runflag,0,1,MPI.INT,0);	

      if(runflag[0] == STOP) break stop;
    }
 
    Measure.final_output();
    System.out.println("\n\n Time is "+time+" seconds\n\n");
  }

  ////////////////////////////////////////////////////////////
  public void draw() throws MPIException 
  {  
    //int root = 0, i, j, pi, pj, offset;
    int root = CommVars.root2d, i, j, pi, pj, offset;
    int local_size = CommVars.isiz * CommVars.jsiz;
    int global_size = CommVars.globisiz * CommVars.globjsiz;
    int tmp[] = new int[local_size];
    int globaltmp[] = new int[global_size];
    int coords[] = new int[2];
    int rank;
    /*
    for(i=1;i<=CommVars.isiz;i++) 
      for(j=1;j<=CommVars.jsiz;j++) 
	CommVars.spin[i][j] = CommVars.procnum+i;  
    */
    // copy 2D -> 1D; mpiJava can only send 1D array.    
    for(i=1;i<=CommVars.isiz;i++) 
      for(j=1;j<=CommVars.jsiz;j++) 
	tmp[(i-1)*CommVars.jsiz+(j-1)] = CommVars.spin[i][j];
    

    CommVars.comm2d.Gather(tmp,       0, local_size, MPI.INT,
			   globaltmp, 0, local_size, MPI.INT,
			   root);
       
	/*      
    MPI.COMM_WORLD.Gather(tmp,       0, local_size, MPI.INT,
			   globaltmp, 0, local_size, MPI.INT,
			   root);
    */

    offset = 0;
    if(CommVars.procnum==0) {
      for(pi=0; pi<CommVars.procdim[0]; pi++) {
        for(pj=0; pj<CommVars.procdim[1]; pj++) {
	  coords[0] = pi;  coords[1] = pj;
	  rank = CommVars.comm2d.Rank(coords);
	  offset = rank * local_size;
	  //System.out.println(" Cart Rank = "+rank);
	  for(i=0; i<CommVars.isiz; i++) {
	    for(j=0; j<CommVars.jsiz; j++) {
	      CommVars.global_spin[i+pi*CommVars.isiz][j+pj*CommVars.jsiz] = 
		  globaltmp[i*CommVars.jsiz + j + offset] ;
	    }
	  }
	}
      }
      mgraphics.repaint();   // redraw the picture      
    }


  }


  ////////////////////////////////////////////////////////////
/*--------------------------------------------------------------------
                                                                      
                                                                      
    Metropolis Monte Carlo update of the Ising model.                 
                                                                      
    For the Ising model new_spin = - old_spin, so                     
    the old energy   oldE = - old_spin * (sum of neighboring spins)   
    the new energy   newE = - new_spin * (sum of neighboring spins)   
                          = - oldE                                    
                                                                      
    Do black/white checkerboard update, so can pass edge values       
    (blocked data) which will be more efficient than passing          
    single spins at a time (unblocked data).                          
                                                                      
----------------------------------------------------------------------*/

  public void metupdate(int iterations) throws MPIException {
    int black_white;
    int n, i, j;
    int new_spin, old_spin;
    int new_energy,old_energy, diff;
    int spin1, spin2, spin3, spin4;
    double randnum;
    
	
    for (n=0; n<iterations; n++) {

      CommVars.iter++;
      CommVars.ACCEPT = 0;
      //Edges.all_edges(CommVars);	
      for (i=1; i<=CommVars.isiz; i++) {
	for (j=1; j<=CommVars.jsiz; j++) {
	  
	  old_spin = CommVars.spin[i][j];
	  
	  // Pick a new random trial spin
	  //if(CommVars.Q == 2) 
	  //new_spin = 1 - old_spin;         /* only for Q=2 */
	  //else {
	    new_spin = (int) ( Prand.rand() * (CommVars.Q-1)) + 1;
	    new_spin = (old_spin + new_spin) % CommVars.Q;
	    //}

	  spin1 = Edges.get_spin(i-1, j);
	  spin2 = Edges.get_spin(i+1, j);
	  spin3 = Edges.get_spin(i,   j-1);
	  spin4 = Edges.get_spin(i,   j+1);
	  
	  old_energy = 0;
	  new_energy = 0;

	  if(old_spin == spin1) old_energy++;
	  if(old_spin == spin2) old_energy++;
	  if(old_spin == spin3) old_energy++;
	  if(old_spin == spin4) old_energy++;
	  
	  if(new_spin == spin1) new_energy++;
	  if(new_spin == spin2) new_energy++;
	  if(new_spin == spin3) new_energy++;
	  if(new_spin == spin4) new_energy++;

	  diff = old_energy - new_energy;
	  // reversed due to - sign in energy
	  if ((diff<=0) ||  Math.exp(-CommVars.beta*diff) > Prand.rand() ) {
	    CommVars.ACCEPT++;
	    CommVars.spin[i][j] = new_spin;
	  }

	  //if(CommVars.procnum == 1 && CommVars.iter==1)
	  //System.out.println("procnum ="+CommVars.procnum+"diff="+diff+", random = "+randnum+", new_spin="+new_spin);

	  if(CommVars.updateflag == 2) 
	    { CommVars.comm2d.Barrier();  draw();}

	}
	if(CommVars.updateflag == 1) 
	  { CommVars.comm2d.Barrier();  draw();}
      } //  End of loop over sites
      if(CommVars.updateflag == 0) 
	{ CommVars.comm2d.Barrier();  draw();}
    } // End of loop over sweeps

    // Pass edges to neighboring processors.  
    Edges.spin_edges();    

  }  
    
}

interface Instructions {

  int NONE = 0 ;
  int RUN  = 1 ;
  int STOP = 2 ;
  int QUIT = 3 ;
}

/**
 *  Instruction buffer, for communication between event handler and
 *  main simulation thread on process 0.
 */
class InstructionStream implements Instructions {

  /**
   *  Value of `next' is NONE, RUN, or QUIT
   */
  private int next = NONE ;

  private boolean stopping = true ;

  private boolean consumerWaiting = false ;

  /**
   *  Non-blocking call.  Buffers up to one RUN or QUIT instruction,
   *  and sets "stopping" flag appropriately.
   */
  synchronized void add(int instr) {
    switch (instr) {
      case RUN :
        if(next != QUIT) {  // ignore RUN instruction if QUIT pending.
          next = RUN ;
          stopping = false ;
          if(consumerWaiting) {
            consumerWaiting = false ;
            notify() ;
          }
        }
        break ;

      case STOP :
        stopping = true ;
        if(next == RUN)
          next = NONE ;  // Note STOP instruction overrides a pending RUN.
        break ;

      case QUIT :
        stopping = true ;
        next = QUIT ;    // Note QUIT instruction overrides a pending RUN.
        if(consumerWaiting) {
          consumerWaiting = false ;
          notify() ;
        }
        break ;
    }
  }
  
  /**
   *  Blocking call: returns RUN or QUIT when instruction available.
   */
  synchronized int getNext() {

    while (next == NONE) {
      consumerWaiting = true ;

      try {
        wait() ;
      } catch (InterruptedException e) {
        System.out.println("Unexpected exception: " + e) ;
        System.exit(1) ;
      }
    }

    int result = next ;

    next = NONE ;
    return result ;        
  }

  /**
   *  Non-blocking call.
   */
  synchronized boolean getStopping() {

    return stopping ;
  }
}




