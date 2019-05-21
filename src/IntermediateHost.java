import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class IntermediateHost {
	DatagramSocket clientRecieveSocket, clientSendSocket, sendRecieveSocket;
	DatagramPacket clientRecievePacket, clientSendPacket, serverSendPacket, serverRecievePacket;
	ComFunctions com;
	int port = 69;
	/**
	 * Waits to recieve a message from the client and passes that on to the server
	 */
	public void recieveMessage(){
		while(true) {
			
			//Recieving a message to from the client, prints the message, created a new packet to send to the server, prints that message for clarification and sends it the server
			clientRecievePacket = com.recievePacket(clientRecieveSocket, 516);
			
			com.printMessage("Recieved message from client:", clientRecievePacket.getData());
			
			serverSendPacket = com.createPacket(clientRecievePacket.getData(), port);
			
			com.printMessage("Sending message to Server:", serverSendPacket.getData());
			
			com.sendPacket(serverSendPacket, sendRecieveSocket);
			
			//Listens to the server response, and forwards that on to the client in the reverse manner, printing each each of the messages
			serverRecievePacket = com.recievePacket(sendRecieveSocket,516);
			
			port = serverRecievePacket.getPort();
			
			com.printMessage("Recieved message from Server:", serverRecievePacket.getData());
			
			clientSendPacket = com.createPacket(serverRecievePacket.getData(), clientRecievePacket.getPort());
			
			com.printMessage("Sending message to client:", clientSendPacket.getData());
			
			com.sendPacket(clientSendPacket, clientSendSocket);
		}
	}
	
	
	public IntermediateHost() {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		clientSendSocket = com.startSocket();
		clientRecieveSocket = com.startSocket(23);
		sendRecieveSocket = com.startSocket();
	}
	
	public static void main(String[] args) {
		IntermediateHost host = new IntermediateHost();
		host.recieveMessage();
		
	}

}
