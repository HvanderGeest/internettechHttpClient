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
import java.util.ArrayList;
import java.util.Base64;
import java.util.function.UnaryOperator;

/**
 * De http server
 */
public class HttpServer {
	private static ServerSocket serverSocket;
	private static final int GATE_NUMBER = 4444;
	private static final boolean ACTIVE = true;
	private static ArrayList<User> users = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		HttpServer httpserver = new HttpServer();
		serverSocket = new ServerSocket(GATE_NUMBER);
		users.add(new User("test", "testpw"));
		users.add(new User("test2", "testpw2"));

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
		private void sendFile(PrintWriter writer, OutputStream output,
				String fileName, String contentType) {
			try {
				FileInputStream file = new FileInputStream(new File(fileName));

				writer.print("HTTP/1.1 200 OK\r\n");
				if (contentType.equals("*/*")) {
					if (fileName.contains(".js")) {
						System.out.println("het is javascript");
						writer.print("Content-Type: application/javascript");

					}
				} else if (fileName.contains(".jpg")
						|| fileName.contains(".png")) {
					writer.print("Content-Type: image/jpeg");
				} else {
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

		private void sendUnautirized(PrintWriter writer) {
			writer.print("HTTP/1.1 401 Unauthorized\r\n");
			writer.print("WWW-Authenticate: Basic realm='nmrs_m7VKmomQ2YM3:'\r\n");
			writer.flush();
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
				System.out.println("requestline= " + getReqeustLine);

				String contentType = null;
				String authorizationString = null;
				while (reader.ready()) {
					// request is read here.
					String text = reader.readLine();
					if (text.startsWith("Accept:")) {
						contentType = text.substring(8);
						System.out.println("hier: " + contentType);
					}
					if (text.startsWith("Authorization:")) {
						authorizationString = new String(Base64.getDecoder()
								.decode(text.substring(21)));

					}

					System.out.println(text);
				}

				// check for autirisation
				boolean isAutorisedBoolean = false;
				if (authorizationString != null) {
					String[] credentials = authorizationString.split(":");
					if(credentials.length == 2){
						String userName = credentials[0];
						String password = credentials[1];
						isAutorisedBoolean = isAutorised(userName, password);
					}
				}

				if (isAutorisedBoolean) {
					if (getReqeustLine != null) {
						// Get a filename out of the request
						String filename = getFileName(getReqeustLine);

						if (getReqeustLine.startsWith("GET")
								&& !getReqeustLine.contains("/favicon.ico")) {
							sendFile(writer, output, filename, contentType);

						}

						else {
							System.out.println("406");
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
			if (fileName.isEmpty()) {
				fileName = "index.html";
			}

			return fileName;

		}
		/**
		 * checks if the password and username are correct
		 * @param userName the input username
		 * @param password the input password
		 * @return true if the password and username are correct else false
		 */
		private boolean isAutorised(String userName, String password) {
			for (User u : users) {
				if (u.getUserName().equals(userName)
						&& u.getPassWord().equals(password)) {
					return true;
				}
			}
			return false;
		}

	}

}
