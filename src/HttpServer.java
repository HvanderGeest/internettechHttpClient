import java.io.BufferedReader;
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
		
		/**
		 * Verstuurd een afbeelding
		 * @param writer	De active printWriter
		 * @param output	De actieve outputStream
		 * @param fileName	De bestandsnaam van de afbeelding
		 */
		public void sendImage(PrintWriter writer, OutputStream output, String fileName){
			try {
				FileInputStream file = new FileInputStream(new File(fileName));
				writer.print("HTTP/1.1 200 OK\r\n");
				writer.print("Content-Type: image/jpeg\r\n");
				writer.print("\r\n");
				writer.flush();
				
				int bytesRead = 0;
				while(bytesRead != -1){
					byte[] byteArray = new byte[1024];
					bytesRead = file.read(byteArray, 0, 1024);
					output.write(byteArray);
					output.flush();
				}
				
				file.close();
			
			} catch (IOException e) {
				e.printStackTrace();
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
					System.out.println(getReqeustLine);	
					String htmlString= null;
							
									
					
					while(reader.ready()){
						String text = reader.readLine();
						System.out.println(text);						
					}
					
					//Get a filename out of the request
					String filname= getFileName(getReqeustLine);
					
					if(getReqeustLine.contains("images/")){
						
						sendImage(writer, output, filname);
					
					} 
					else if(getReqeustLine.startsWith("GET") && !getReqeustLine.contains("/favicon.ico")){
						htmlString= switchToFile(filname);
						
						if(htmlString!=null){
							writer.print("HTTP/1.1 200 OK\r\n");
							writer.print("Content-Type: text/html\r\n\r\n\r\n");
							writer.print(htmlString+"\r\n");
							writer.flush();
						}else{
							System.out.println("404");
							writer.print("HTTP/1.1 404 OK\r\n");	
							writer.flush();
						}
					
					}
			
					else {
						System.out.println("406");
						writer.println("HTTP/1.1 406 Not Acceptable\r\n");
						writer.flush();
				
					}

								
					
					output.close();

					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			

			
		}
		
		/**
		 * Geeft een String van het bestand
		 * @param filename		De bestandsnaam
		 * @return				De String met de bestandsinhoud
		 */
		private String switchToFile(String filename) {
			
			System.out.println(filename);
			if(filename.equals("")){
				return convertFileToString("index.html");
			}else{
				return convertFileToString(filename);
			}
				
			
		}
		
		/**
		 * Haalt een bestandsnaam op uit een request
		 * @param request	Het Http request
		 * @return 			De bestandsnaam
		 */
		private String getFileName(String request){
			String[] splitedString=request.split("\\s+");
			String getRequest= splitedString[1];
			
			//removes the / before the file name
			String fileName=getRequest.substring(1, getRequest.length());

			return fileName;
			
		}
		
		/**
		 * Zet een bestand om naar een String
		 * @param fileName	De bestandsnaam
		 * @return			De String met de bestandsinhoud
		 */
		private String convertFileToString(String fileName){
		
			try {
				
				BufferedReader bufferReader= new BufferedReader (new FileReader(fileName));
				StringBuilder stringBuilder= new StringBuilder();
				
				String line= null;
				
				while((line=bufferReader.readLine())!=null){
					stringBuilder.append(line);
				}
					
				bufferReader.close();
				return stringBuilder.toString();
			} catch (IOException e) {
				
				return null;
			}
			
		}
		
	}

}
