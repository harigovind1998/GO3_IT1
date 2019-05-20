import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class Server {
	private final int BLOCK_SIZE = 512;
	DatagramSocket recieveSocket, errorSocket;
	DatagramPacket recievePacket, errorPacket;
	ComFunctions com;
	
	/**
	 * loops and keeps serving all the incoming requests
	 */
	public void serve() {
		while(true) {
			recievePacket = com.recievePacket(recieveSocket, BLOCK_SIZE);
			if (com.checkMessage(recievePacket.getData())) {
				ServerWorker worker = new ServerWorker(Integer.toString((recievePacket.getPort())), recievePacket);
				worker.run();
			}else {
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
		recievePacket = com.createPacket(BLOCK_SIZE);
		ServerExitListener exit = new ServerExitListener("Exit listener");
		exit.run();
	}

	
	public static void main(String[] args) {
		Server server = new Server();
		server.serve();
	}
}
