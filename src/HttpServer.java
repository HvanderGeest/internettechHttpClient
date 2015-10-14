import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * De http server
 */
public class HttpServer {
	private static ServerSocket serverSocket;
	private static final int GATE_NUMBER = 4444;
	private static final boolean ACTIVE = true;

	public static void main(String[] args) throws IOException {

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

		/**
		 * Verstuurd een afbeelding
		 * 
		 * @param writer
		 *            De active printWriter
		 * @param output
		 *            De actieve outputStream
		 * @param fileName
		 *            De bestandsnaam van de afbeelding
		 */
		public void sendFile(PrintWriter writer, OutputStream output, String fileName, String contentType) {
			try {
				FileInputStream file = new FileInputStream(new File(fileName));
				
				writer.print("HTTP/1.1 200 OK\r\n");
				if(contentType.equals("*/*")){
					if(fileName.contains(".js")){
						System.out.println("het is javascript");
						writer.print("Content-Type: application/javascript");
						
					}
				} else if(fileName.contains(".jpg") || fileName.contains(".png")){
					writer.print("Content-Type: image/jpeg");
				}
				else {
					writer.print("Content-Type: "+contentType);
				}
				writer.print("\r\n");
				writer.print("Content-Length: "+file.available());
				writer.print("\r\n");
				writer.print("\r\n");
				writer.flush();

				int bytesRead = 0;
				while (bytesRead != -1) {
					byte[] byteArray = new byte[1024];
					bytesRead = file.read(byteArray, 0, 1024);
					if(bytesRead == -1){
						return;
					}
					output.write(byteArray, 0, bytesRead);
					output.flush();
				}

				file.close();

			} catch (IOException e) {
				e.printStackTrace();
				writer.print("HTTP/1.1 404 FILE NOT FOUND\r\n");	
				writer.flush();
			}

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
				
					String getReqeustLine= reader.readLine();
					System.out.println("requestline= "+getReqeustLine);	
					String codeString= null;
							
									
					String contentType =null;
					while(reader.ready()){
						String text = reader.readLine();
						if(text.startsWith("Accept:")){
							contentType = text.substring(8);
							System.out.println("hier: "+contentType);
						}
						System.out.println(text);						
					}
					
					if(getReqeustLine!=null){
					//Get a filename out of the request
					String filename= getFileName(getReqeustLine);
					
				
					if(getReqeustLine.startsWith("GET") && !getReqeustLine.contains("/favicon.ico")){
						sendFile(writer, output, filename, contentType);
						
					}
			
					else {
						System.out.println("406");
						writer.println("HTTP/1.1 406 Not Acceptable\r\n");
						writer.flush();
				
					}

								
					
					output.close();

					socket.close();
					}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			

			
		}

	

		/**
		 * Haalt een bestandsnaam op uit een request
		 * 
		 * @param request
		 *            Het Http request
		 * @return De bestandsnaam
		 */
		private String getFileName(String request) {
			
			String[] splitedString = request.split("\\s+");
			String getRequest = splitedString[1];

			// removes the / before the file name
			String fileName = getRequest.substring(1, getRequest.length());
			if(fileName.isEmpty()){
				fileName = "index.html";
			}

			return fileName;

		}

		

	}

}
