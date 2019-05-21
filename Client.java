package iteration0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.io.FileOutputStream;
import java.io.FileReader;
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
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	private String filename = "";

	// run in normal (send directly to server) or test
	// (send to simulator)
	public static enum Mode {
		TEST, NORMAL
	};

	// DATA PACKET
	byte in[] = new byte[512];
	byte out[] = new byte[512];

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

	/**
	 * Sends the specified message to the intermediate host and waits for a response
	 * 
	 * @param type   read or write
	 * @param file   file name
	 * @param format format of file
	 */
	public void sendMesage(byte[] type, byte[] file, String format) {
		// generating the message in byte format
		byte[] msg = com.generateMessage(type, file, format);

		com.printMessage("Sending Message:", msg);

		DatagramPacket sendPacket = com.createPacket(msg, 23); // creating the datagram, specifying the destination port
		// and message

		com.sendPacket(sendPacket, sendRecieveSocket);

		DatagramPacket recievePacket = com.recievePacket(sendRecieveSocket, com.KNOWNLEN);

		messageReceived = recievePacket.getData();
		com.guiPrintArr("Recieved message from Host:", messageReceived, area);

		byte[] fileContent = new byte[com.fileLength];
		System.arraycopy(messageReceived, 2, fileContent, 0, com.fileLength);
	}

	// construct data packet and ack packets and send data
	public void sendData(String filename) {

		byte[] data = new byte[512]; // 512 blocks of data
		int bytes = 0; // counts bytes read
		boolean end = true;
		try {
			// read from file
			FileReader input = new FileReader(filename);
			BufferedReader reader = new BufferedReader(input);
			for (int datablck = 1; reader.read() != -1; datablck++) {

				// opcode for data 03
				data[0] = (byte) 0;
				data[1] = (byte) 3;
				data[2] = (byte) 0;
				data[3] = (byte) datablck;
				sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 69);
				// setTimeout

				while (end) {
					DatagramPacket info = new DatagramPacket(in, in.length);
					try {
						// check if acknowledgmnet packetblock matches data block
						// op code for ack is 04
						sendReceiveSocket.receive(info);
						if (in[1] == 4 && in[3] == datablck) {
							break;
						}

					} catch (SocketTimeoutException se) {
						// if packet not recieved send packet again
						se.printStackTrace();

					}
				}
			} // end for loop
			input.close(); // close filereader
			sendReceiveSocket.close(); // close socket
		} catch (IOException ee) {
			ee.printStackTrace();
		}
	}

	public byte[] ack(int blocknum) {
		byte[] ack = new byte[5];
		ack[0] = (byte) 0;
		ack[1] = (byte) 4;
		ack[2] = (byte) 0;
		ack[3] = (byte) blocknum;

		return ack;
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.sendMesage(new byte[] { 0, 1 }, fileToSend, "Ascii");

		try {
			byte[] fileReceived = Files.readAllBytes(f2path);
			byte[] fileSent = Files.readAllBytes(f1path);

			int isSame = 0;
			for (byte b : fileSent) {
				if (fileReceived[b] != fileSent[b]) {
					isSame++;
				}
			}

			if (isSame != 0) {
				area.append("Files do not match");
			} else {
				area.append("Files Match!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (;;) {

			DatagramPacket send = new DatagramPacket(client.in, client.in.length);
			try {

				client.sendReceiveSocket.receive(send);
				// check if incoming data is a data block
				if (client.in[1] == 3) {
					byte[] store = new byte[512];
					System.arraycopy(client.in, 4, store, 0, store.length);
					int blocknum = client.in[3]; // assign blocknumber
					if (send.getLength() >= 512) { // check if blocks at least 512
						DatagramPacket sendP = new DatagramPacket(client.ack(blocknum), client.ack(blocknum).length,
								InetAddress.getLocalHost(), 69);
						// send ack
						client.sendReceiveSocket.send(sendP);
					}
				}
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
	}
}