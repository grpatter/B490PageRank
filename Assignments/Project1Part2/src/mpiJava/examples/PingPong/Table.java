import java.io.*;

public class Table {

  static final double  M = 1000000.;
  static int pow_MAX = ( 1 << PingPong.LOG2N_MAX );
  static int count_MAX = 1;

  static void table_top(int npes, int n_init, int n_mess,
			     int samples, double t_call, String Title)
  {
    System.out.println("\n\n  " + Title + "\n");
    System.out.println(" Number of PEs:  " + npes); 
    System.out.println(" Starts:         " + n_init);
    System.out.println(" Messages:       " + n_mess);
    System.out.println(" Samples:        " + samples);
    System.out.println(" Timer overhead: " + t_call * M + "  microsecond \n");
    System.out.println("\n         Length          Time                   Rate   ");
    System.out.println("\n Slave   [Bytes]     [Microsec.]              [Mbyte/s] ");
    System.out.println("\n -----  ---------    -----------              ---------- ");

    while( (pow_MAX = pow_MAX / 10) > 0 )
      count_MAX++; 

  }

  static void table_body(int islave, int n_byte, int n_init, 
			 int n_mess, int samples, double t_call, 
			 double tf[], PrintStream pfout)
  {
    double t_last;
    int    ns,i;
    int    count_nbyte = 1;
    int    pow_nbyte   =  n_byte ;    

    while( (pow_nbyte = pow_nbyte / 10) > 0 ) 
      count_nbyte++; 

    t_last  = (tf[samples]- tf[samples-1])- t_call;
    
    System.out.print( "   "+islave+"      ");
    System.out.print(n_byte);
    for (i = 0; i <= (count_MAX-count_nbyte); i++) 
      System.out.print(" "); 

    System.out.println("   "+ (float) (t_last * M )/n_mess + "      " +(float) (n_mess*n_byte/(t_last*M)) );

    pfout.println(islave+"  "+n_byte+"  "+samples+"  "+n_init+"  "+n_mess);

    for (ns = 1; ns <= samples; ++ns) {
      pfout.println( ns+"  "+(tf[ns]-tf[ns-1]-t_call) );      
    }
  }
}

