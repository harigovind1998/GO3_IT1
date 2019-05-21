import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.Math; 
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.io.FileOutputStream; 
import java.io.OutputStream; 
import java.util.Scanner;

public class Client {
	ComFunctions com;
	DatagramSocket sendRecieveSocket;
	private static JFrame frame = new JFrame();
	private static JTextArea area = new JTextArea();
	private static JScrollPane scroll = new JScrollPane(area);
	private static byte[] messageReceived;
	private static Path f1path = FileSystems.getDefault().getPath("SYSC3303", "test.txt");
	private static Path f2path = FileSystems.getDefault().getPath("SYSC3303", "returnTest.txt");
	private int fileLength;
	private byte[] fileContent = new byte[fileLength];
	private static byte[] rrq = {0,1};
	private static byte[] wrq = {0,2};
	private static int mode;
	private int byteCounter = 0;
	
	public Client() {
		// TODO Auto-generated constructor stub
		com = new ComFunctions();
		sendRecieveSocket = com.startSocket();
		frame.setSize(420, 440);
		area.setBounds(10, 10, 380, 380);
		scroll.setSize(400, 400);
		scroll.add(area);
		frame.getContentPane().add(scroll);
		frame.setLayout(null);
		frame.setVisible(true);
	}
	
    static File byteToFile(byte[] bytes, String path) { 
    	System.out.println("Converting byte array to file...");
    	File file = new File(path);
        try { 
            // Initialize a pointer 
            // in file using OutputStream 
            OutputStream os = new FileOutputStream(file); 
  
            // Starts writing the bytes in it 
            os.write(bytes); 
            System.out.println("Successfully converted byte array to file"); 
  
            // Close the file 
            os.close(); 
        } 
  
        catch (Exception e) { 
            System.out.println("Exception: " + e); 
        }
        return file;
    }
		
	/**
	 * Sends the specified message to the intermediate host and waits for a response
	 * @param type read or write 
	 * @param file file name 
	 * @param format format of file
	 */
	/**
    public void sendMesage(byte[] type, File file, String format) {
		//generating the message in byte format
		byte[] fileAsByteArr;
		try {
			fileAsByteArr = Files.readAllBytes(file.toPath());
			fileLength = fileAsByteArr.length;
			int numOfBlocks = (int) Math.ceil(fileLength / 512);
			for(int i = 0; i < numOfBlocks; i++) {
				byte[] fileBlock = com.getBlock(i, fileAsByteArr);
				byte[] msg = com.generateMessage(type, fileBlock, format);
				com.printMessage("Sending Message:", msg);
				DatagramPacket sendPacket = com.createPacket(msg, 23); //creating the datagram, specifying the destination port and message
				com.sendPacket(sendPacket, sendRecieveSocket);
				
				DatagramPacket recievePacket = com.recievePacket(sendRecieveSocket, com.KNOWNLEN);
				if(com.CheckAck(recievePacket, i)) {
					messageReceived = recievePacket.getData();
					com.guiPrintArr("Recieved message from Host:", messageReceived, area);
					
					byte[] dataArr = com.parseBlockData(messageReceived);
					System.arraycopy(dataArr, 0, fileContent, 0, dataArr.length);
				}else {
					System.out.println("Wrong Packet Received");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
	public void writeFile(String name, String format) {
		byte[] fileAsByteArr = com.readFileIntoArray(name);
		
		try {
			Files.write(f1path, fileAsByteArr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fileLength = fileAsByteArr.length;
		byte[] request = com.generateMessage(wrq, name, format);
		DatagramPacket requestPacket = com.createPacket(request, 23); //creating the datagram, specifying the destination port and message
		com.sendPacket(requestPacket, sendRecieveSocket);
		
		if (mode == 1) {
			com.verboseMode("Sent", wrq, name, format, area);
		}
		
		int numOfBlocks = (int) Math.ceil(fileLength / 512);
		for(int i = 0; i < numOfBlocks; i++) {
			byte[] fileBlock = com.getBlock(i+1, fileAsByteArr);
			byte[] msg = com.generateDataPacket(com.intToByte(i+1), fileBlock);
			com.printMessage("Sending Message:", msg);
			DatagramPacket sendPacket = com.createPacket(msg, 23); //creating the datagram, specifying the destination port and message
			com.sendPacket(sendPacket, sendRecieveSocket);
			
			byteCounter = 0;
			for(byte b: fileBlock) {
				if(fileBlock[b] != (byte)0) {
					byteCounter++;
				}
			}
			
			if(mode == 1) {
				com.verboseMode("Sent", com.parsePacketType(msg), i+1, byteCounter, area);
			}
			
			DatagramPacket recievePacket = com.recievePacket(sendRecieveSocket, com.KNOWNLEN);
			if(com.CheckAck(recievePacket, i+1)) {
				messageReceived = recievePacket.getData();
				com.guiPrintArr("Recieved message from Host:", messageReceived, area);
				
				if (mode == 1) {
					com.verboseMode("Received", com.parsePacketType(messageReceived), i+1, messageReceived.length, area);
				}
			}else {
				System.out.println("Wrong Packet Received");
			}
		}
	}
	
	public void readFile(String name, String format) {
		byte[] msg = com.generateMessage(rrq, name, format);
		int blockNum = 1;
		com.printMessage("Sending Message:", msg);
		DatagramPacket sendPacket = com.createPacket(msg, 23); //creating the datagram, specifying the destination port and message
		com.sendPacket(sendPacket, sendRecieveSocket);
		
		if (mode == 1) {
			com.verboseMode("Sent", rrq, name, format, area);
		}
		
		outerloop:
		while(true) {
			DatagramPacket recievePacket = com.recievePacket(sendRecieveSocket, 512);
			if(com.CheckAck(recievePacket, blockNum)) {
				blockNum++;
				messageReceived = recievePacket.getData();
				byte[] dataReceived = com.parseBlockData(messageReceived);
				
				byteCounter = 0;
				for(byte b: dataReceived) {
					if(dataReceived[b] != (byte)0) {
						byteCounter++;
					}
				}
				
				if (mode == 1) {
					com.verboseMode("Received", com.parsePacketType(dataReceived), blockNum, byteCounter, area);
				}
				
				System.arraycopy(dataReceived, 0, fileContent, 0, dataReceived.length);
				byte[] ackMsg = com.generateAckMessage(com.intToByte(blockNum));
				DatagramPacket ackPacket = com.createPacket(ackMsg, 23);
				com.sendPacket(ackPacket, sendRecieveSocket);
				
				if (mode == 1) {
					com.verboseMode("Sent", com.parsePacketType(ackMsg), blockNum, byteCounter, area);
				}
				
				//check to see if the bloc size is < 512, and if it is, break	
				if(dataReceived[511] == (byte)0) {
					break outerloop;
				}
			}else {
				System.out.println("Wrong Packet Received");
			}
		}
		try {
			Files.write(f2path, fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		Scanner sc = new Scanner(System.in);
		System.out.println("Select Mode : Quiet [0], Verbose [1]");
		mode = sc.nextInt();
		sc.close();
		
		//client.sendMesage(new byte[] {0,1}, fileToSend, "Ascii");
		client.readFile("test.txt", "Ascii");
		client.writeFile("test.txt", "Ascii");
		
		try {
			byte[] fileReceived = Files.readAllBytes(f2path);
			byte[] fileSent = Files.readAllBytes(f1path);
			
			int isSame = 0;
			for(byte b : fileSent) {
				if(fileReceived[b] != fileSent[b]) {
					isSame++; 
				}
			}
			
			if(isSame != 0 ) {
				area.append("Files do not match");
			} else {
				area.append("Files Match!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
