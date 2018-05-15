package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	public int numGames=0;
	public static Map<String, Client> clientsMap = new HashMap<String, Client>();
	public static Map<SocketAddress, Thread> socketThreads = new HashMap<SocketAddress, Thread>();

	public static Client clients[] = new Client[2];
	public ArrayList<Game> partidas = new ArrayList<Game>();	
	public static int turno=0;
	
	
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
	
	private void loginMessage(Message message,Socket s,DataInputStream in) throws IOException {
		Login login = (Login) message;
		String nick=login.getNick();
		if(isClientFree(nick)) {
			System.out.println("Client accepted: " + nick);
			Client client = new Client(s,nick);
			client.setIn(in);
			clientsMap.put(nick, client);
			OkLogin ok = new OkLogin(turno);			;
			ok.writeTo(client.getOut());
			if(clients[0]==null) { 
				clients[turno]=client;
				turno++;
			}else if(clients[1]==null){
				clients[turno]=client;
				Game game = new Game(numGames,clients[0],clients[1]);
				partidas.add(game);
				numGames++;
				turno=0;
				clients = new Client[2];
				System.out.println("Empieza partida");
				Thread thread = new Thread(new GameAt(game));
				thread.start();
			}			
		}else {
			System.out.println("Client rejected: " + nick);
			ErrorMessage error = new ErrorMessage();
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
			error.writeTo(out);
		}
	}
	
	
	private void connect() {
		try{
			System.out.println("wait clients ...");					
			for(;;){
				Socket s = socket.accept();
			    InputStream reader= s.getInputStream();
		        DataInputStream in=new DataInputStream(reader);
				Message message = Message.ReadFrom(in);
				System.out.println("Mensaje recibido: " + message);
				if(message.type()==Message.MessageTypes.LOGIN) {
					loginMessage(message,s,in);		
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
		Thread thread = new Thread(){
			public void run(){
				connect();
			}
		};
		thread.start();
	}

	public static void main(String[] args) {
		new Thread(){
			public void run(){
				Server s = new Server();
				s.start();
			}		
		}.start();		
	}		
	
	
	private class GameAt implements Runnable {

		private Game g;
		public GameAt(Game g) {
			this.g=g;
		}		
		
		private void sendMove(Message message,Client client) {
	    	new Runnable(){
                @Override
                public void run() {
                	DataOutputStream out;
            		if(g.client1==client) {
            			out= g.client1.out;
            		}else {
            			out= g.client2.out;
            		}
				    message.writeTo(out);					
                }
	    	};
		}
		
		private void playMessage(Message message) {
			Play play = (Play) message;
			String nick=play.getNick();
			if(!isClientFree(nick)) {
				if(g.client1.nick==nick) {
					sendMove(message,g.client2);
				}else {
					sendMove(message,g.client1);
				}
			}
		}
		private synchronized void receiveMessages(Client client) throws IOException {
			//for(;;) {
				Message message = Message.ReadFrom(client.getIn());
				System.out.println("Mensaje recibido: " + message);
				switch(message.type()){					
					case PLAY:
						playMessage(message);
						break;
					case ERROR:
						System.out.println("Error!");
						break;
					default:
						break;
				}
				receiveMessages(g.client2);
			//}
		}
		
	/*	public void closeAll(){
			try {
				if(in != null){
					in.close();
					in = null;
				}
				if(out != null){
					out.close();
					out = null;
				}	
				if(socket != null){
					socket.close();
					socket = null;
				}
			} catch (IOException e) {
				System.err.println(this + ": " + e);  
			}
		}
		*/
		@Override
		public void run() {
			try{
				receiveMessages(g.client1);	
			} catch (Exception e) {
				System.out.println(e.getMessage());;	
			} finally{
				//closeAll();
			}			
		}		

	}

}


