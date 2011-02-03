public class Clock {

  // timer() returns seconds...

  static double timer() {    
    return (double) System.currentTimeMillis() / 1.0e3; 
  }
}
