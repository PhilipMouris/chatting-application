import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public static ArrayList<Socket> clientList = new ArrayList<Socket>();
	public static ArrayList<String> clientNames = new ArrayList<String>();
	ServerSocket welcomeSocket;
	Socket listener;
	ExecutorService pool;
	int clientNumber;

	public Server(int serverSocket) {
		try {
			welcomeSocket = new ServerSocket(serverSocket);
			pool = Executors.newFixedThreadPool(20);
			clientNumber = 0;
			System.out.println("Server is running.");
			while(true) {
				listener= welcomeSocket.accept();
				Server.clientList.add(listener);
				pool.execute(new ClientHandler(listener, clientNumber++));	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static Thread server1 = new Thread() {
		public void run() {
			Server s1 = new Server(6000);
		}
	};
	
	public static Thread server2 = new Thread() {
		public void run() {
			Server s2 = new Server(8000);
		}
	};
	
	public static void main(String args[]) {
		server1.start();
		server2.start();
	}

	
}
