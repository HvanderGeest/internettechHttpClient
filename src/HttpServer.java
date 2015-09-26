import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
	private static ServerSocket  serverSocket;
	private static final int GATE_NUMBER = 4444;

	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(GATE_NUMBER);
		while(true){
			Socket socket = serverSocket.accept();
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			boolean go = true;
			while(go){
				String text = reader.readLine();
				if(text == null){
					go = false;
					
					break;
				}
				System.out.println(text);
			}
			
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output);
			writer.write("HTTP/1.x 200 OK\r\n");
			writer.write("Content-Type: text/html\r\n\r\n");
			writer.write("<p>fasfasdfs</p>\r\n");
			System.out.println("einde");
			writer.flush();
			
			
			
		}
		

	}
	
	

}

class WebServer {
	
	  
	    
	  
}
