import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class ServerWorker extends Thread {
	private final int BLOCK_SIZE = 512;
	private DatagramPacket initialPacket, RecievedResponse, SendingResponse;
	private int clientPort;
	private String fileName, mode; 
	private DatagramSocket SendRecieveSocket; 
	private ComFunctions com;
	private int job;
	//private byte[] fileByteReadArray, fileByteWriteArray;

	
	/**
	 * Gets the name of the file that is being written into or read from
	 */
	private void getFileName() {
		byte[] data = initialPacket.getData();
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
		byte[] file = Arrays.copyOfRange(data, 2 , secondZero[1]);
		byte[] mode = Arrays.copyOfRange(data, secondZero[1]+1, secondZero[2]);
		this.fileName = new String(file);
		this.mode = new String(mode);
	}
	
	/**
	 * Decodes the incoming packet to get the necessary information, namely the file name and weather the its a read or write request
	 */
	private void decodePacket() {
		job = initialPacket.getData()[1]; //format of the message has been checked so second bit will determine if the request is a read or write
		clientPort = initialPacket.getPort();	
		getFileName();
	}
	
	
	/**
	 * Sends the contents over to the client
	 */
	private void readServe() {
		byte [] fileByteReadArray = com.readFileIntoArray("./server/" + fileName);
		int blockNum = 1;
		while(true){
			byte[] msg = com.generateDataPacket(com.intToByte(blockNum), com.getBlock(blockNum, fileByteReadArray));
			SendingResponse = com.createPacket(msg, clientPort);
			com.sendPacket(SendingResponse, SendRecieveSocket);
			RecievedResponse = com.recievePacket(SendRecieveSocket, 100);
			if(!com.CheckAck(RecievedResponse, blockNum)) {
				System.out.println("Wrong block recieved");
			}
			if(SendingResponse.getData()[SendingResponse.getLength() -1] == 0){
				System.out.println("End of file reached");
				break;
			}
		}
	}
	
	private void writeServe(){
		
	}
	
	/**
	 * decodes and then performs the necessary task
	 */
	public void run() {
		decodePacket();
		if(job == 1) {
			writeServe();
		}else if (job ==2) {
			readServe();
		}
	}
	
	public ServerWorker(String name, DatagramPacket packet ) {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		SendRecieveSocket = com.startSocket();
		initialPacket = packet;
		
	}
	
}
