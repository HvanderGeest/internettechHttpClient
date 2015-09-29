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
					
					String getReqeustLine= reader.readLine();
					System.out.println(getReqeustLine);	
					String htmlString= switchToHtmlFile(getReqeustLine);
					
					while(reader.ready()){
						String text = reader.readLine();
						System.out.println(text);						
					}
					
					
					
					writer.print("HTTP/1.1 200 OK\r\n");
					writer.print("Content-Type: text/html\r\n\r\n\r\n");
					writer.print(htmlString+"\r\n");

					writer.flush();
					
					output.close();

					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			

			
		}
		
		/**
		 * Get a the html page of a request
		 * @return	a String with the html page
		 */
		private String switchToHtmlFile(String reqeust) {
			
			String[] splitedString=reqeust.split("\\s+");
			String getRequest= splitedString[1];
			System.out.println("htmlFile Request= "+ getRequest);
			
			
			String htmlString=null;
			//Opens the index page
			if(getRequest.equals("/")||getRequest.equals("/index")){
				htmlString=getHtmlString("index.html");				
				//Opens the SecondPage
			}else if(getRequest.equals("/index/SecondPage")){
				htmlString=getHtmlString("SecondPage.html");
			}else if(getRequest.equals("/index/ThirdPage")){
				htmlString=getHtmlString("ThirdPage.html");
			}
			
			return htmlString;
			
		}
		
		/**
		 * Convert the htmlfile into a String
		 * @param htmlPage	the html file name
		 * @return			De html String
		 */
		private String getHtmlString(String htmlPage){
			List<String> htmlLines;
			try {
				htmlLines = Files.readAllLines(Paths.get(htmlPage));
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
