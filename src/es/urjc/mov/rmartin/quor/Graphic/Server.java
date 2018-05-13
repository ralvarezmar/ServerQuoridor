package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.urjc.mov.rmartin.quor.Graphic.Message.ErrorMessage;
import es.urjc.mov.rmartin.quor.Graphic.Message.Login;
import es.urjc.mov.rmartin.quor.Graphic.Message.OkLogin;
import es.urjc.mov.rmartin.quor.Graphic.Message.Play;

public class Server{

	private static final int PORT = 2020;
	private ServerSocket socket;
	private Thread thread;
	private int numGames=0;
	//protected Attend attend;
	Map<String, SocketAddress> clientsMap = new HashMap<String, SocketAddress>();
	private Client clients[] = new Client[2];
	private ArrayList<Game> partidas = new ArrayList<Game>();	
	int turno=0;
	
	public Server(){
		try {
			this.socket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	private Boolean isClientFree(String nick) {
		if(clientsMap.get(nick)==null) {
			return true;
		}
		return false;
	}
	
	private void loginMessage(Message message,Socket s) throws IOException {
		Login login = (Login) message;
		String nick=login.getNick();
		if(isClientFree(nick)) {
			System.out.println("Client accepted: " + nick);
			SocketAddress ip = s.getRemoteSocketAddress();
			Client client = new Client(ip,nick);
			clientsMap.put(nick, ip);
			OkLogin ok = new OkLogin(turno);
			OutputStream writer= s.getOutputStream();
		    DataOutputStream out=new DataOutputStream(writer);
			ok.writeTo(out);
			if(clients[turno]==null) {
				clients[turno]=client;
				turno++;
			}else if(clients[turno]==null){
				clients[turno]=client;
				Game game = new Game(numGames,clients[0],clients[1]);
				partidas.add(game);
				numGames++;
				turno=0;
				clients = new Client[2];
			}			
		}else {
			System.out.println("Client rejected: " + nick);
			ErrorMessage error = new ErrorMessage();
			OutputStream writer= s.getOutputStream();
		    DataOutputStream out=new DataOutputStream(writer);
			error.writeTo(out);
		}
	}
	
	private void sendMove(Message message,Client client) {
	    SocketAddress sockaddr = client.ip;
	    try {
			Socket s = new Socket();
			s.connect(sockaddr);
			OutputStream writer= s.getOutputStream();
		    DataOutputStream out=new DataOutputStream(writer);
		    message.writeTo(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void playMessage(Message message) {
		Play play = (Play) message;
		String nick=play.getNick();
		if(!isClientFree(nick)) {
			if(clients[0].nick==nick) {
				sendMove(message,clients[1]);
			}else {
				sendMove(message,clients[0]);
			}
		}
	}
	
	private void connect() {
		try{
			System.out.println("wait clients ...");
			for(;;){
				if(clients[0]==null || clients[1]==null) {
					Socket s = socket.accept();
				    InputStream reader= s.getInputStream();
			        DataInputStream o=new DataInputStream(reader);
					Message message = Message.ReadFrom(o);	
					System.out.println("Mensaje recibido" + message);
					switch(message.type()){
						case LOGIN:
							loginMessage(message,s);			
						case PLAY:
							playMessage(message);
							break;
						case ERROR:
							break;
						default:
							break;
					}
				}else {
					Game game = new Game(numGames,clients[0],clients[1]);
					partidas.add(game);
					numGames++;
					turno=0;
					clients = new Client[2];					
				}
			}
		} catch (IOException e) {
			System.err.println("connection error: " + e);
		} finally{
			try{
				if(socket != null){
					socket.close();
				}
			} catch (IOException e) {
				System.err.println("error to close: " + e);  	
			}
		}
	}

	private void start(){
		thread = new Thread(){
			public void run(){
				connect();
			}
		};
		thread.start();
	}
	
/*
		public void closeAll(){
			try {
				if(in != null){
					in.close();
					in = null;
				}
				if(out != null){
					out.close();
					out = null;
				}
				if(incon != null){
					incon.close();
					incon = null;
				}
				if(outcon!=null) {
					outcon.close();
					outcon=null;
				}
				} catch (IOException e) {
				System.err.println(this + ": " + e);  
			}
		}
	}
*/
	public static void main(String[] args) {
		new Thread(){
			public void run(){
				Server s = new Server();
				s.start();
			}		
		}.start();		
	}	
	
}


