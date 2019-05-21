import java.io.File;
import java.io.FileInputStream;
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


public class Client {
	ComFunctions com;
	DatagramSocket sendRecieveSocket;

	private static JFrame frame = new JFrame();
	private static JTextArea area = new JTextArea();
	private static JScrollPane scroll = new JScrollPane(area);
	private static byte[] messageReceived;
	private static Path f1path = FileSystems.getDefault().getPath("SYSC3303", "test.txt");
	private static Path f2path = FileSystems.getDefault().getPath("SYSC3303", "returnTest.txt");
	private static File fileToSend = new File("C:\\Users\\noric\\Documents\\SYSC3303\\test.txt");
	private int fileLength;
	private byte[] fileContent = new byte[fileLength]
	
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
	
	public static byte[] fileToByte(String path) {
		File file = new File(path);
		FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
	try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
		return (bytesArray);
	}

	

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
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DatagramPacket recievePacket = com.recievePacket(sendRecieveSocket, com.KNOWNLEN);
		
		messageReceived = recievePacket.getData();
		com.guiPrintArr("Recieved message from Host:", messageReceived, area);
		
		byte[] dataArr = com.ParseBlockData(messageReceived);
		System.arraycopy(dataArr, 0, fileContent, 0, dataArr.length);

	}
	
	public static void main(String[] args) {
		Client client = new Client();

		client.sendMesage(new byte[] {0,1}, fileToSend, "Ascii");
		
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
