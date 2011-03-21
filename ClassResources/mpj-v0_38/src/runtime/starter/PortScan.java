/*
 The MIT License

 Copyright (c) 2010
   1. Aamir Shafi (2010)

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
 * File         : PortScan.java 
 * Author       : Kamran Hameed
 * Created      : Tue Jan 26 17:12:50 PKT 2010
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: Tue Jan 26 17:12:50 PKT 2010
 */

package runtime.starter;

import java.net.*;
import java.io.*;
public class PortScan {
  public static void main(String [] args) {
    try {
      InetAddress address = InetAddress.getByName(args[0]);
      System.exit(isBusy(address,Integer.parseInt(args[1])) ? 0 : 1) ;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  static boolean isBusy(final InetAddress remote,int port) {
    try {
      Socket s = new Socket(remote,port);
      s.close();
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
}

