package iteration0;

import java.io.*;
import java.net.*;

public class TFTPClientt {

   private DatagramPacket sendPacket, receivePacket;
   private DatagramSocket sendReceiveSocket;
   
   // run in normal (send directly to server) or test
   // (send to simulator) 
   public static enum Mode { TEST,NORMAL};

   public TFTPClientt()
   {
      try {
         // Construct a datagram socket and bind it to any available
         // port on the local host machine. This socket will be used to
         // send and receive UDP Datagram packets.
         sendReceiveSocket = new DatagramSocket();
      } catch (SocketException se) {   // Can't create the socket.
         se.printStackTrace();
         System.exit(1);
      }
   }

   public void sendReceive()
   {
      byte[] message = new byte[100], // message to send
             filearr, // filename as an array of bytes
             modarr, // mode as an array of bytes
             data; // response as array of bytes
      String file, mode; // filename and mode as Strings
      int  sendPort;
      Mode run = Mode.TEST; // change to NORMAL to send directly to server
      
      if (run==Mode.NORMAL) 
         sendPort = 69; // server port
      else
         sendPort = 23; // simulator port
      
      // sends 10 packets -- 5 reads, 5 writes, 1 invalid
      for(int i=0; i<=10; i++) {

         System.out.println("Client: creating packet " + i + ".");
         
         // Prepare a DatagramPacket and send it via sendReceiveSocket
         // to sendPort on the destination host (also on this machine).

         // if i even, it's a read; otherwise a write
         // opcode for read is 01, and for write 02

        message[0] = 0;
        if(i%2==0) 
           message[1]=1;
        else 
           message[1]=2;
           
        if(i==8) 
           message[1]=7; // if it's the 8th time, send an invalid request

        // next we have a file name -- let's just pick one
        file = "test.txt";
        // convert to bytes
        filearr = file.getBytes();
        
        // and copy into the msg
        System.arraycopy(filearr,0,message,2,filearr.length);
        
        // now add a 0 byte
        message[filearr.length+2] = 0;

        // now add "netascii" (or "octet")
        mode = "netascii";
        // convert to bytes
        modarr = mode.getBytes();
        
        // and copy into the msg
        System.arraycopy(modarr,0,message,filearr.length+3,modarr.length);
        
        int length = filearr.length+modarr.length+4; // length of the message to find the last index

        // and end with another 0 byte 
        message[length-1] = 0;

        // Construct a datagram packet that is to be sent to a specified port
        // on a specified host.
        // The arguments are:
        //  msg - the message contained in the packet (the byte array)
        //  the length we care about - k+1
        //  InetAddress.getLocalHost() - the Internet address of the
        //     destination host.
        //     In this example, we want the destination to be the same as
        //     the source (i.e., we want to run the client and server on the
        //     same computer). InetAddress.getLocalHost() returns the Internet
        //     address of the local host.
        //  69 - the destination port number on the destination host.
        try {
           sendPacket = new DatagramPacket(message, length,
                                         InetAddress.getLocalHost(), sendPort);
        } catch (UnknownHostException e) {
           e.printStackTrace();
           System.exit(1);
        }

        System.out.println("Client: sending packet " + i + ".");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort());
        System.out.println("Length: " + sendPacket.getLength());
        System.out.println("Containing: ");
        data = sendPacket.getData();
        for (int j=0;j<length;j++) {
            System.out.println("byte " + j + " " + data[j]);
        }

        // Send the datagram packet to the server via the send/receive socket.

        try {
           sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
           e.printStackTrace();
           System.exit(1);
        }

        System.out.println("Client: Packet sent.");

        // Construct a DatagramPacket for receiving packets up
        // to 100 bytes long (the length of the byte array).

        data = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        System.out.println("Client: Waiting for packet.");
        try {
           // Block until a datagram is received via sendReceiveSocket.
           sendReceiveSocket.receive(receivePacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }

        // Process the received datagram.
        System.out.println("Client: Packet received:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("Host port: " + receivePacket.getPort());
        System.out.println("Length: " + receivePacket.getLength());
        System.out.println("Containing: ");

        // Get a reference to the data inside the received datagram.
        data = receivePacket.getData();
        for (int k=0;k<receivePacket.getLength();k++) {
            System.out.println("byte " + k + " " + data[k]);
        }
        
       

      } // end of loop

      // We're finished, so close the socket.
      sendReceiveSocket.close();
   }

   public static void main(String args[])
   {
      TFTPClientt run = new TFTPClientt();
      run.sendReceive();
   }
}


