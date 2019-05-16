import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class Server {
	DatagramSocket recieveSocket, sendSocket;
	DatagramPacket recievePacket, sendPacket;
	ComFunctions com;
	List<Thread> threadList;
	
	
	/**
	 * Checks to see if a byte array is on the required format read/write,text,0,text,0...0
	 * @param msg message that is to be checked
	 * @return true if the format is correct and false if it is not
	 */
	private boolean checkMessage(byte[] msg) {

		if (msg[0] == 0 && (msg [1] == 1 || msg[1] == 2)) { //Check to see if the first 2 bytes specify a read or a write;
			int count = 0; 
			int i = 2;
			//finds the first 2 0's
			for (; i < msg.length; ++i) { 
				if (msg[i] == 0 && (msg[i] != msg[i - 1])) {
					++count;
				}
				if(count == 2) {
					i++;
					break;
				}
			}
			
			//checks to ensure the rest of the message are only 0's
			for(;i<msg.length; i++) {
				if(msg[i] != 0) {
					return false;
				}
			}
				return true;
			
		} else {
			return false;
		}
		
	}
	
	/**
	 * loops and keeps serving all the incoming requests
	 */
	public void serve() {
		while(true) {
			//Recieves the incoming message and prints its contents
			recievePacket = com.recievePacket(recieveSocket, com.UNKNOWNLEN);
			com.printMessage("Recieved message from Host:", recievePacket.getData());
			//handles the request approprioately 
			if( checkMessage(recievePacket.getData())){
				if(recievePacket.getData()[1]== 1){
					sendPacket = com.createPacket(new byte[] {0,3,0,1},recievePacket.getPort());
					com.printMessage("Sending message to Host:", sendPacket.getData());
					com.sendPacket(sendPacket, sendSocket);
				}else{
					sendPacket = com.createPacket(new byte[] {0,4,0,0}, recievePacket.getPort());
					com.printMessage("Sending message to Host:", sendPacket.getData());
					com.sendPacket(sendPacket, sendSocket);
				}
			}else {
				throw new IllegalArgumentException();
			}
			
			
		}
	}
	
	public Server() {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		
		recieveSocket = com.startSocket(69);
		sendSocket = com.startSocket();
		threadList = new List<Thread>();
		
		
	}

	
	public static void main(String[] args) {
		Server server = new Server();
		server.serve();
	}
}
