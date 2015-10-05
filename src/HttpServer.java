import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

import javax.imageio.ImageIO;

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
		
		public void sendImage(PrintWriter writer){
			try {
				BufferedImage image = ImageIO.read(new File("banana.jpg"));
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", outputStream);
				
				String byteImageString = Base64.getMimeEncoder().encodeToString(outputStream.toByteArray());
				writer.print("HTTP/1.1 200 OK\r\n");
				writer.print("Content-Type: image/jpeg\r\n");
				writer.println("Content-Length: "+outputStream.toByteArray().length+"\r\n");
				writer.print("\r\n");
				writer.println(byteImageString);
				//System.out.println(byteImageString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
					boolean testImage = true;
					boolean getRequest = false;
					String requestType = reader.readLine();
					if(requestType.startsWith("GET")){
						getRequest = true;
					}

					// Hij loopt vast op de loop, maar ik weet niet waarom. Nu
					// print hij
					// alleen de eerste lijn

					// String line;
					// StringBuilder stringBuilder= new StringBuilder();
					//
					// while((line=reader.readLine())!=null|| !line.isEmpty()||
					// !line.equals(" ")){
					// System.out.println(line);
					// stringBuilder.append(line);
					//
					// System.out.println("inloop inhoudline= "+ line);
					//
					
					//werkende loop :)
					
					
					while(reader.ready()){
						String text = reader.readLine();
						
						System.out.println(text);
						
					}
					
				
					System.out.println("buiten de loop");
					if(testImage){
						sendImage(writer);
					
					} 
					else if(getRequest){
						writer.print("HTTP/1.1 200 OK\r\n");
						writer.print("Content-Type: text/html\r\n\r\n\r\n");
						writer.print("<h1>Hello World</h1>\r\n");
					} else {
						System.out.println("406");
						writer.println("HTTP/1.1 406 Not Acceptable\r\n");
				
					}
					writer.flush();
					
					output.close();
					reader.close();
					
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			

			
		}
	}

}
