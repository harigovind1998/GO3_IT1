import java.io.BufferedReader;
import java.io.InputStreamReader;
public class ServerExitListener extends Thread {
	
	public ServerExitListener(String name) {
		super(name);
	}
	
	public void run(){
		boolean keepRunning = true;
		String cmd = "";
		BufferedReader input = null;
		while(keepRunning) {
			input = new BufferedReader(new InputStreamReader(System.in));
			try {
				cmd = input.readLine();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(cmd.equals("exit")) {
				
				System.out.println("Server shutting down");
				System.exit(0);
			}		
			
		}
		
	}

}
