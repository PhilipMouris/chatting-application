import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	Socket socket;
	int clientNumber;
	String clientSentence;
	String serverResponse;
	BufferedReader inFromClient;
	PrintWriter outToClient;

	public ClientHandler(Socket socket, int clientNumber) throws IOException {
		this.socket = socket;
		this.clientNumber = clientNumber;
	}

	@Override
	public void run() {
		while (true) {
			try {
				inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				clientSentence = inFromClient.readLine();
				if (clientSentence.equals("QUIT")) {
					socket.close();
					int i;
					for (i = 0; i < Server.clientList.size(); i++) {
						if (Server.clientList.get(i) == socket)
							break;
					}
					Server.clientList.remove(i);
					Server.clientNames.remove(i);
					break;
				} else if (clientSentence.equals("members")) {
					MemberListResponse();
				} else if (clientSentence.split(":")[0].equals("Join")) {
					if (JoinResponse(clientSentence.split(":")[1])) {
						outToClient = new PrintWriter(socket.getOutputStream(), true);
						serverResponse = "Server: Successful";
						outToClient.println(serverResponse);
					} else {
						outToClient = new PrintWriter(socket.getOutputStream(), true);
						serverResponse = "Server: This name is already taken";
						outToClient.println(serverResponse);
					}
				} else {
					int i;
					for (i = 0; i < Server.clientList.size(); i++) {
						if (socket.getPort() == Server.clientList.get(i).getPort())
							break;
					}
					String sender = "";
					if (i < Server.clientNames.size())
						sender = Server.clientNames.get(i) + ">>";
					System.out.println(clientSentence);
					int begin = 0;
					int end = 0;
					int pos = 0;
					int l = clientSentence.length() - 1;
					for (pos = 0; pos < l; pos++) {
						char c = clientSentence.charAt(pos);
						if (c == ':')
							begin = pos;
						if (c == ',')
							end = pos;
					}
					begin++;
					if(end==0) {
						String messageOnly = clientSentence.substring(begin);
						System.out.println(messageOnly);
						Route(sender + messageOnly, clientSentence.split(":")[0], "2");
					}
					else {
						String messageOnly = clientSentence.substring(begin, end);
						System.out.println(messageOnly);
						Route(sender + messageOnly, clientSentence.split(":")[0], clientSentence.split(",")[1]);
					}
					
				}
			} catch (IOException e) {
				System.out.println("Error handling client");
				e.printStackTrace();
			}

		}
	}

	public boolean JoinResponse(String name) {
		for (int i = 0; i < Server.clientNames.size(); i++) {
			if (name.equals(Server.clientNames.get(i)))
				return false;
		}
		Server.clientNames.add(name);
		return true;
	}

	public void MemberListResponse() throws IOException {
		outToClient = new PrintWriter(socket.getOutputStream(), true);
		serverResponse = "";
		for (int i = 0; i < Server.clientNames.size(); i++)
			serverResponse += Server.clientNames.get(i) + "/";
		outToClient.println(serverResponse);
	}

	public void Route(String message, String destination, String timeToLive) throws IOException {
		int targetLocalPort = 0;
		int targetPort = 0;
		int i;
		int TTL = Integer.parseInt(timeToLive);
		TTL--;

		if (TTL <= 0) {
			for (i = 0; i < Server.clientNames.size(); i++) {
				if (Server.clientNames.get(i).equals(message.split(">>")[0])) {
					targetLocalPort = Server.clientList.get(i).getLocalPort();
					targetPort = Server.clientList.get(i).getPort();
					break;
				}
			}
			if (targetLocalPort == socket.getLocalPort()) {
				outToClient = new PrintWriter(Server.clientList.get(i).getOutputStream(), true);
				outToClient.println("Message dropped please increase TTL");
			} else {
				Socket temp = new Socket("localhost", targetLocalPort);
				DataOutputStream outToServer = new DataOutputStream(temp.getOutputStream());
				outToServer.writeBytes(Server.clientNames.get(i) + ":" + "Message dropped please increase TTL" + ",20" + '\n');

			}

		} else {
			for (i = 0; i < Server.clientNames.size(); i++) {
				if (Server.clientNames.get(i).equals(destination)) {
					targetLocalPort = Server.clientList.get(i).getLocalPort();
					targetPort = Server.clientList.get(i).getPort();
					break;
				}
			}
			if (targetLocalPort == socket.getLocalPort()) {
				outToClient = new PrintWriter(Server.clientList.get(i).getOutputStream(), true);
				outToClient.println(message);
			} else if (targetLocalPort == 0 && targetPort == 0) {
				outToClient = new PrintWriter(socket.getOutputStream(), true);
				outToClient.println("Client Not Found");
			} else {
				Socket temp = new Socket("localhost", targetLocalPort);
				DataOutputStream outToServer = new DataOutputStream(temp.getOutputStream());
				outToServer.writeBytes(destination + ":" + message + "," + TTL + '\n');
			}
		}

	}

}
