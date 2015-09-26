import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
	private static ServerSocket serverSocket;
	private static final int GATE_NUMBER = 4444;
	private static final boolean ACTIVE = true;

	public static void main(String[] args) throws IOException  {

		HttpServer httpserver = new HttpServer();
		serverSocket = new ServerSocket(GATE_NUMBER);
	

//		while (ACTIVE) {
		

				Socket socket = serverSocket.accept();
				ResponseThread thread = httpserver.new ResponseThread(socket);
				thread.run();
			
//		}

	}

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
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));

					System.out.println(reader.readLine());

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

					output = socket.getOutputStream();
					PrintWriter writer = new PrintWriter(output, false);
					writer.print("HTTP/1.1 200 OK\r\n");
					writer.print("Content-Type: text/html\r\n\r\n\r\n");
					writer.print("<h1>Hello World</h1>\r\n");

					writer.flush();
					
					output.close();
			

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
