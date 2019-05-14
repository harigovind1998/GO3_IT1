import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Client {
	ComFunctions com;
	DatagramSocket sendRecieveSocket;
	
	/**
	 * Sends the specified message to the intermediate host and waits for a response
	 * @param type read or write 
	 * @param file file name 
	 * @param format format of file
	 */
	public void sendMesage(byte[] type, String file, String format) {
		//generating the message in byte format
		byte[] msg = com.generateMessage(type, file, format);
		
		com.printMessage("Sending Message:", msg);
		
		DatagramPacket sendPacket = com.createPacket(msg, 23); //creating the datagram, specifying the destination port and message
		
		com.sendPacket(sendPacket, sendRecieveSocket); 
		
		DatagramPacket recievePacket = com.recievePacket(sendRecieveSocket, com.KNOWNLEN);
		
		com.printMessage("Recieved message from Host:", recievePacket.getData());
		
	}
	
	public Client() {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		sendRecieveSocket = com.startSocket();
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.sendMesage(new byte[] {0,1}, "hello.txt", "Ascii");
		client.sendMesage(new byte[] {0,2}, "Have.txt", "Ascii");
		client.sendMesage(new byte[] {0,1}, "AVery.txt", "Ascii");
		client.sendMesage(new byte[] {0,2}, "Good.txt", "Ascii");
		client.sendMesage(new byte[] {0,1}, "Summer.txt", "Ascii");
		client.sendMesage(new byte[] {0,2}, "Break.txt", "Ascii");
		client.sendMesage(new byte[] {0,1}, "Like.txt", "Ascii");
		client.sendMesage(new byte[] {0,2}, "aVery.txt", "Ascii");
		client.sendMesage(new byte[] {0,1}, "good.txt", "Ascii");
		client.sendMesage(new byte[] {0,2}, "person.txt", "Ascii");
		client.sendMesage(new byte[] {2,1}, "deserves.txt", "Ascii");
		
	}
}
