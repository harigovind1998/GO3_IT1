import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
public class ServerWorker extends Thread {
	private final int BLOCK_SIZE = 512;
	private DatagramPacket initialPacket, RecievedResponse, SendingResponse;
	private int clientPort;
	private String fileName; 
	private DatagramSocket SendRecieveSocket; 
	private ComFunctions com;
	private int job, requestPort; //

	
	
	private void getFileName() {
		byte[] data = initialPacket.getData();
		fileName = "";
		int[] secondZero = {3,0,0};
		int track = 1;
		for(int i = 3; i<data.length ; i ++) {
			if(data[i] == 0) {
				secondZero[track] = i;
				track++;
				if (track == 3) {
					break;
				}
			}
		}
		
		byte[] file = Arrays.copyOfRange(data, 4 , secondZero[1]-1);
		byte[] mode = Arrays.copyOfRange(data, secondZero[1]+1, secondZero[2]-1);
		System.out.println(new String(file));
	}
	
	
	private void determineJob() {
		job = initialPacket.getData()[1]; //format of the message has been checked so second bit will determine if the request is a read or write
		requestPort = initialPacket.getPort();
		getFileName();
		
		
	}
	
	
	private void serve() {
		while(true){
			
		}
	}
	
	public void run() {
		determineJob();
		serve();
		
	}
	
	public ServerWorker(String name, DatagramPacket packet ) {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		SendRecieveSocket = com.startSocket();
		RecievedResponse = com.createPacket(BLOCK_SIZE);
		initialPacket = packet;
		clientPort  = packet.getPort();
		
		
	}
	
}
