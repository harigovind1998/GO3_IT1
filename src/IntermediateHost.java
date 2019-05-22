import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class IntermediateHost {
	DatagramSocket clientRecieveSocket, clientSendSocket, sendRecieveSocket;
	DatagramPacket clientRecievePacket, clientSendPacket, serverSendPacket, serverRecievePacket;
	ComFunctions com;
	int port = 69;
	int mode;
	private int interHostPort = 23;
	/**
	 * Waits to recieve a message from the client and passes that on to the server
	 */
	public void recieveMessage(){
		while(true) {
			
			//Recieving a message to from the client, prints the message, created a new packet to send to the server, prints that message for clarification and sends it the server
			clientRecievePacket = com.recievePacket(clientRecieveSocket, 516);
			
			if(mode == 1) {
				System.out.println(com.verboseMode("Recieve", clientRecievePacket));
			}
			
			serverSendPacket = com.createPacket(clientRecievePacket.getData(), port);
			
			if(mode == 1) {
				System.out.println(com.verboseMode("Send", clientRecievePacket));
			}
			
			com.sendPacket(serverSendPacket, sendRecieveSocket);
			
			//Listens to the server response, and forwards that on to the client in the reverse manner, printing each each of the messages
			serverRecievePacket = com.recievePacket(sendRecieveSocket,516);
			
			port = serverRecievePacket.getPort();
			

			if(mode == 1) {
				System.out.println(com.verboseMode("Recieve", serverRecievePacket));
			}
			
			clientSendPacket = com.createPacket(serverRecievePacket.getData(), clientRecievePacket.getPort());
			
			if(mode == 1) {
				System.out.println(com.verboseMode("Send", clientSendPacket));
			}
			com.sendPacket(clientSendPacket, clientSendSocket);
		}
	}
	
	
	public IntermediateHost() {
		// TODO Auto-generated constructor stub
		Scanner sc = new Scanner(System.in);
		System.out.println("Select Mode : Quiet [0], Verbose [1]");
		mode = sc.nextInt();
		sc.close();
		com = new ComFunctions();
		clientSendSocket = com.startSocket();
		clientRecieveSocket = com.startSocket(interHostPort);
		sendRecieveSocket = com.startSocket();
	}
	
	public static void main(String[] args) {
		IntermediateHost host = new IntermediateHost();
		host.recieveMessage();
		
	}

}
