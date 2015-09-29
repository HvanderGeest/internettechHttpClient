import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * De http server
 */
public class HttpServer {
	private static ServerSocket serverSocket;
	private static final int GATE_NUMBER = 4444;
	private static final boolean ACTIVE = true;

	public static void main(String[] args) throws IOException  {

		HttpServer httpserver = new HttpServer();
		serverSocket = new ServerSocket(GATE_NUMBER);
	

		while (ACTIVE) {
		

				Socket socket = serverSocket.accept();
				ResponseThread thread = httpserver.new ResponseThread(socket);
				thread.run();
			
		}

	}

	/**
	 * De thread die een response geeft aan de http client
	 */
	private class ResponseThread extends Thread {

		Socket socket;

		public ResponseThread(Socket socket) {
			this.socket = socket;

		}

		@Override
		public void run() {
				
			InputStream input;
			OutputStream output;
			try {

				
					input = socket.getInputStream();
					output = socket.getOutputStream();
					PrintWriter writer = new PrintWriter(output, false);
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));

					
					while(reader.ready()){
						String text = reader.readLine();
						System.out.println(text);
						
					}
					
					
					
					writer.print("HTTP/1.1 200 OK\r\n");
					writer.print("Content-Type: text/html\r\n\r\n\r\n");
					writer.print(getHtmlFile()+"\r\n");

					writer.flush();
					
					output.close();

					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			

			
		}
		
		/**
		 * Get a htmlpage
		 * @return	a String with the html page
		 */
		public String getHtmlFile() {
			
			List<String> htmlLines;
			try {
				htmlLines = Files.readAllLines(Paths.get("index.html"));
				StringBuilder stringBuilder= new StringBuilder();
				for(String line: htmlLines){
					stringBuilder.append(line);
				}
				
				return stringBuilder.toString();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			return null;
			
		}
	}

}
