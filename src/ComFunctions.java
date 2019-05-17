import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.*;
import java.io.*;

public class ComFunctions {
	
	public final int KNOWNLEN = 4;
	public final int UNKNOWNLEN = 100;
	private final static char[] hexArr = "0123456789ABCDEF".toCharArray();
	public int fileLength;
	
	/**
	 * Recieves a packet on the specified socket 
	 * @param socket Socket to listen to
	 * @param len length of the packet, either 4 or 100 bytes long
	 * @return recieved DatagramPakcet
	 */
	public DatagramPacket recievePacket(DatagramSocket socket, int len) {
		
		DatagramPacket tempPacket = createPacket(len);
		try {
			socket.receive(tempPacket);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return tempPacket;
	}
	
	/**
	 * Sends a packet through an initilized socket 
	 * @param packet packet to send
	 * @param socket socket to send the packet through
	 */
	public void sendPacket(DatagramPacket packet, DatagramSocket socket) {
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Creates a DatagramSocket on available port
	 * @return DatagramSocket
	 */
	public DatagramSocket startSocket() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		}catch (SocketException e){
			e.printStackTrace();
			System.exit(1);
		}
		return socket;
	}
	
	/**
	 * Creates a DatagramSocket on specified port
	 * @param port port the Socket needs to created on
	 * @return DatagramSocket linked to the specified port
	 */
	public DatagramSocket startSocket(int port) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
		}catch (SocketException e){
			e.printStackTrace();
			System.exit(1);
		}
		return socket;
	}
	
	/**
	 * Creates an DatagramPocket to be passed through a DatagramSocket.
	 * @param msg Byte array message
	 * @param address address of the destination socket
	 * @param port port number of the destination socket
	 * @return DatagramPacket that can be used for sending
	 */
	public DatagramPacket createPacket(byte[] msg, int port ) {
		DatagramPacket sendPacket = null;
		try {
			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), port);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return sendPacket;		
		
	}
	
	/**
	 * Creates an DatagramPocket used to handle DatagramSocket receive.
	 * @param len desired length of the packet
	 * @return new empty DatatgramPacket of desired length
	 */
	public DatagramPacket createPacket(int len) {
		DatagramPacket sendPacket = null;
		byte[] buff = new byte[len];
		
		try {
			sendPacket = new DatagramPacket(buff, buff.length);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(1);
		}
		return sendPacket;		
	}
	
	
	/**
	 * Generates a byte message to be passed through the sockets. Creates an empty byte array of length equal
	 * to the length of the file name, plus the length of the format, plus 4 to account for the 2 bytes used 
	 * for specify if the message operation and 2 more for the 0 in the middle and end of the message.
	 * @param type Message operation that is being sent, either a read or a write
	 * @param file name of the file
	 * @param format format of the file
	 * @return byte array of specified message format
	 */
	public byte[] generateMessage(byte[] type, byte[] file, String format) {
		byte[] msg = new byte[file.length + format.length() + 4];
		msg[0] = type[0];
		msg[1] = type[1];
		int track = 2; 
		
		for(byte c : file) {
			msg[track] = c; 
			fileLength++;
			track ++;
		}
		
		msg[track] = 0;
		track ++;
		
		for(byte b : format.getBytes()) {
			msg[track] = b;
			track ++;
		}
		
		msg[msg.length - 1] = 0 ;
		
		return msg;
	}
	
	/**
	 * Prints out the contents of a packet
	 * @param init initial message 
	 * @param msg message to print 
	 */
	public void printMessage(String init, byte[] msg) {
		//prints the message in string format
		System.out.println(init + " "+ new String(msg));
		
		System.out.print("In string format: "); //prints out the byte array
		for(byte b: msg) {
			System.out.print(b + " ");
		}
		System.out.print("\n");
	}
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArr[v >>> 4];
	        hexChars[j * 2 + 1] = hexArr[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public void guiPrintArr(String init, byte[] msg, JTextArea a) {
		String msgAsString = init + " " + new String(msg) + bytesToHex(msg) + "\n";
		a.append(msgAsString);
	}	
	public void guiPrint(String msg, JTextArea a) {
		a.append(msg);
	}
}
