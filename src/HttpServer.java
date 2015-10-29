import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

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

		private Socket socket;

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
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));

				String getReqeustLine = reader.readLine();
				System.out.println("RequestLine:"+getReqeustLine);

				String contentType = null;
				String authorizationString = null;
				while (reader.ready()) {
					// request is read here.
					String text = reader.readLine();
					if (text.startsWith("Accept:")) {
						contentType = text.substring(8);
						System.out.println("ContentType>> " + contentType);
					}
					if (text.startsWith("Authorization:")) {
						authorizationString = new String(Base64.getDecoder()
								.decode(text.substring(21)));

					}

					System.out.println(text);
				}

				// check for autirisation
				boolean isAutorisedBoolean = true;
				isAutorisedBoolean = isAutorised(authorizationString,
						getReqeustLine);

				if (isAutorisedBoolean) {
					// access granted
					if (getReqeustLine != null) {
						// Get a filename out of the request
						String filename = getFileName(getReqeustLine);

						if (getReqeustLine.startsWith("GET")
								&& !getReqeustLine.contains("/favicon.ico")) {
							// supported request
							sendFile(writer, output, filename, contentType);

						}

						else {
							// unsported request
							writer.println("HTTP/1.1 406 Not Acceptable\r\n");
							writer.flush();

						}

					}
				} else {
					sendUnautirized(writer);
				}

				output.close();

				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	

		/**
		 * Sends a file i.g html or css
		 * 
		 * @param writer
		 *            the active printWriter
		 * @param output
		 *            the actieve outputStream
		 * @param fileName
		 *            file path of the file
		 */
		private void sendFile(PrintWriter writer, OutputStream output,
				String fileName, String contentType) {
			try {
				FileInputStream file = new FileInputStream(new File(fileName));

				writer.print("HTTP/1.1 200 OK\r\n");
				if (contentType.equals("*/*")) {
					if (fileName.contains(".js")) {
						// it's a javascript
						writer.print("Content-Type: application/javascript");

					}
				} else if (fileName.contains(".jpg")
						|| fileName.contains(".png")) {
					// it's a image file
					writer.print("Content-Type: image/jpeg");
				} else if(fileName.contains("html")) {
					// content type is html
					writer.print("Content-Type: text/html");
				} else if(fileName.contains("css")){
					//content type is css
					writer.print("Content-Type: text/css");
				}
				else {
					// content type is requested from the request header
					writer.print("Content-Type: " + contentType);
					
				}
				writer.print("\r\n");
				writer.print("Content-Length: " + file.available());
				writer.print("\r\n");
				writer.print("\r\n");
				writer.flush();

				int bytesRead = 0;
				while (bytesRead != -1) {
					byte[] byteArray = new byte[1024];
					bytesRead = file.read(byteArray, 0, 1024);
					if (bytesRead == -1) {
						file.close();
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

		/**
		 * extracts a filename from a request
		 * 
		 * @param request
		 *            the request
		 * @return the filename
		 */
		private String getFileName(String request) {

			String[] splitedString = request.split("\\s+");
			String getRequest = splitedString[1];

			// removes the / before the file name
			String fileName = getRequest.substring(1, getRequest.length());
			if (fileName.isEmpty()) {
				fileName = "index.html";
			}

			return fileName;

		}

		/**
		 * checks if a request is autorised
		 * 
		 * @param credentails
		 *            the username and password
		 * @param getLine
		 *            the get request status line, used to extract the filepath
		 *            where htaccess could be located.
		 * @return true if the request is autorised else returns false
		 */
		private boolean isAutorised(String credentails, String getLine) {
			try {
				String[] splitedString = getLine.split("\\s+");
				String getRequest = splitedString[1];

				// removes the / before the file name
				String path = getRequest.substring(1, getRequest.length());
				if (path.isEmpty()) {
					path = "htaccess.txt";
				} else {
					// trims the url to the folder of the requested resource
					path = trimeOneDomain(path) + "htaccess.txt";
				}
				File accessFile = new File(path);
				try {

					Scanner scanAccesFile = new Scanner(accessFile);
					// file is found auth required
					if (credentails == null) {
						// no credentials passed, auth required so access not
						// granted.
						scanAccesFile.close();
						return false;
					}

					// checking htaccess file for credentails
					while (scanAccesFile.hasNextLine()) {
						String serverCredentials = scanAccesFile.nextLine();
						if (credentails.equals(serverCredentials)) {
							// credentials match server credentials, access
							// granted
							scanAccesFile.close();
							return true;
						}
					}
					scanAccesFile.close();
					// credentials not found.
					return false;

				} catch (FileNotFoundException e) {
					// no access file found in this folder so access granted
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}

		/**
		 * removes the last part, the file, from a path
		 * 
		 * @param path
		 *            path you want the last part removed
		 * @return path without file at the end
		 */
		private String trimeOneDomain(String path) {
			if (path.isEmpty()) {
				return path;
			}
			if (path.charAt(path.length() - 1) == '/') {
				return path;
			}
			path = path.substring(0, path.length() - 1);
			return trimeOneDomain(path);
		}
		
		/**
		 * return status code 401 unauthorized to the browser
		 */
		private void sendUnautirized(PrintWriter writer) {
			writer.print("HTTP/1.1 401 Unauthorized\r\n");
			writer.print("WWW-Authenticate: Basic realm='Auth'\r\n");
			writer.flush();
		}

	}

}