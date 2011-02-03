#ifdef sma

#include <sys/time.h>

double timer()
{
     double msec;
     struct timeval tv;

     gettimeofday(&tv,NULL);
     msec = tv.tv_sec  + tv.tv_usec * 1.0e-6;
     return msec;
}

#else

#include <math.h>
#include <windows.h>

//===================GLOBALS===========================================

LARGE_INTEGER TicksPerSecond = {0,0}; // Global ticks per second
double TicksPerMicrosecond = 0;       // Global ticks/microsecond

//===================PIVATE FUNCTION===================================
// InitTimer() initializes the globals.

void InitTimer(void) { 
  QueryPerformanceFrequency(&TicksPerSecond);
  TicksPerMicrosecond = (float) (TicksPerSecond.LowPart/ 1E6);
  return;
}

//===================PUBLIC FUNCTIONS==================================
// GetStartTime

LARGE_INTEGER GetStartTime(void) {
  LARGE_INTEGER StartTime;
  QueryPerformanceCounter(&StartTime);
  return StartTime;
};


double ElapsedMicroseconds() {
  extern LARGE_INTEGER StartTime;
  LARGE_INTEGER StopTime; // ticks of stop time
  LARGE_INTEGER dif;      // difference (stop-start) 
  double delta;           // dif as a normalized float
                          

  QueryPerformanceCounter(&StopTime); // read current clock

// if this is a first-time call, initialize the globals.

  if (TicksPerSecond.LowPart == 0)
    InitTimer();

// FANCY MATH -----------------------------------------------------
// compute the ticks elapsed since start time.

  dif.QuadPart = ( StopTime.QuadPart - StartTime.QuadPart);

// convert ticks to microseconds. (first do low part)

  delta = dif.LowPart / TicksPerMicrosecond; // low part 
  if (dif.HighPart != 0)                     // high part
    delta += ldexp(dif.HighPart,32)/TicksPerMicrosecond;  // done with the fancy 64-bit math.

  return delta;
};


double timer()
{
  return ElapsedMicroseconds() / 1E6;
}

#endif
