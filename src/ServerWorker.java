import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class ServerWorker extends Thread {
	private final int BLOCK_SIZE = 512;
	private DatagramPacket initialPacket, RecievedResponse, SendingResponse;
	private int clientPort;
	private String fileName; 
	private DatagramSocket SendRecieveSocket; 
	private ComFunctions com;
	
	private void serve() {
		while(true){
			
		}
	}
	
	public void run() {
		
		serve();
		
	}
	
	public ServerWorker(String name, DatagramPacket packet ) {
		// TODO Auto-generated constructor stub
		com = new  ComFunctions();
		SendRecieveSocket = com.startSocket();
		RecievedResponse = com.createPacket(BLOCK_SIZE);
		initialPacket = packet;
		clientPort  = packet.getPort();
		
		
	}
	
}
