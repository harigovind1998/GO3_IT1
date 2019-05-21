import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Server {
	private final int REQUEST_SIZE = 100;
	DatagramSocket recieveSocket, errorSocket;
	DatagramPacket recievePacket, errorPacket;
	ComFunctions com;
	
	/**
	 * loops and keeps serving all the incoming requests
	 */
	public void serve() {
		while(true) {
			recievePacket = com.recievePacket(recieveSocket, REQUEST_SIZE); 
			if (com.checkMessage(recievePacket.getData())) {
				System.out.println("Starting ServerWorker");
				ServerWorker worker = new ServerWorker(Integer.toString((recievePacket.getPort())), recievePacket);
				worker.start();
			}else {
				System.out.println("Error Packet");
				errorPacket = com.createPacket(com.generateErrMessage(new byte[] {0, 0}, "The Read or write request was of invalid format"),recievePacket.getPort());
				errorSocket = com.startSocket();
				com.sendPacket(errorPacket, errorSocket);
				errorSocket.close();
			}			
		}
	}
	
	public Server() {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		recieveSocket = com.startSocket(69);
		//recievePacket = com.createPacket(BLOCK_SIZE);
		ServerExitListener exit = new ServerExitListener("Exit listener");
		exit.start();
	}

	
	public static void main(String[] args) {
		Server server = new Server();
		server.serve();
	}
}

