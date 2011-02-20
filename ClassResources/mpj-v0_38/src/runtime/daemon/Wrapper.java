/*
 The MIT License

 Copyright (c) 2005 - 2010
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2010)
   3. Bryan Carpenter (2005 - 2010)
   4. Mark Baker (2005 - 2010)

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * File         : Wrapper.java 
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sun Dec 12 12:22:15 BST 2004
 * Revision     : $Revision: 1.19 $
 * Updated      : $Date: Wed Mar 31 15:22:37 PKT 2010$
 */

package runtime.daemon;

import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.* ;

public class Wrapper {

  String configFileName = null;
  int processes = 0;
  String className = null ;
  Class c = null;
  String deviceName = null;
  String rank = null ; 
  String [] nargs = null ; 
  String hostName = null ;

  public Wrapper() {
  }

  /**
   * Executes MPJ program in a new JVM. This method is invoked in main 
   * method of this class, which is started by MPJDaemon. This method 
   * can start multiple threads in a JVM. This will also parse
   * configuration file.
   * @param args Arguments to this method. 
   *           args[0] is configFileName 'String', 
   *           args[1] is number of processes, 
   *           args[2] is deviceName, 
   *           args[3] is rank, 
   *           args[4] is className 
   */
  public void execute(String args[]) throws Exception {

    InetAddress localaddr = InetAddress.getLocalHost();
    hostName = localaddr.getHostName();

    configFileName = args[0];
    processes = (new Integer(args[1])).intValue();
    deviceName = args[2];
    rank = args[3] ; 
    className = args[4];

    nargs = new String[ (args.length-5) ] ;
    System.arraycopy(args, 5, nargs, 0, nargs.length) ;

    c = Class.forName(className) ; 

    try {
      System.out.println("Starting process <"+rank+"> on <"+hostName+">");

      String arvs[] = new String[nargs.length+3]; 

      arvs[0] = rank;
      arvs[1] = configFileName;
      arvs[2] = deviceName;
            
      for(int i=0 ; i<nargs.length ; i++) {
        arvs[i+3] = nargs[i];
      }

      Method m = c.getMethod("main", new Class[] {arvs.getClass()});
      m.setAccessible(true);
      int mods = m.getModifiers();
      if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
                                             !Modifier.isPublic(mods)) {
		  throw new NoSuchMethodException("main");
      }

      m.invoke(null, new Object[] {arvs});

      System.out.println("Stopping process <"+rank+"> on <"+hostName+">");
    }
    catch (Exception ioe) {
        System.out.println("multi-threaded starter: exception"
                           + ioe.getMessage());
        ioe.printStackTrace();
    }

  } 

  public static void main(String args[]) throws Exception {
    Wrapper wrap = new Wrapper();
    wrap.execute(args);
  } 
}
