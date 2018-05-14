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
	public int numGames=0;
	public static Map<String, SocketAddress> clientsMap = new HashMap<String, SocketAddress>();
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

	private void connect() {
		try{
			System.out.println("wait clients ...");			
			for(;;){
				Socket s = socket.accept();
				thread = new Thread(new ClientAt(s));
				thread.start();				
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

	public static void main(String[] args) {
		new Thread(){
			public void run(){
				Server s = new Server();
				s.start();
			}		
		}.start();		
	}		
	
	
	private class ClientAt implements Runnable {

		private Socket socket;
		private DataInputStream in;
		private DataOutputStream out;
		private String nick;
		private SocketAddress id;
		
		public ClientAt(Socket socket) {
			this.socket=socket;
			try {
				this.in = new DataInputStream(socket.getInputStream());
				this.out = new DataOutputStream(socket.getOutputStream());
				//this.id = incon.getRemoteSocketAddress().toString().split(":")[1];	
				this.id = socket.getRemoteSocketAddress();
			} catch (IOException e) {
				throw new RuntimeException(this + "open streams: " + e);
			}		
		}

		private Boolean isClientFree(String nick) {
			if(clientsMap.get(nick)==null) {
				return true;
			}
			return false;
		}
		
		private void loginMessage(Message message) throws IOException {
			Login login = (Login) message;
			String nick=login.getNick();
			if(isClientFree(nick)) {
				System.out.println("Client accepted: " + nick);
				Client client = new Client(id,nick);
				clientsMap.put(nick, id);
				OkLogin ok = new OkLogin(turno);
				ok.writeTo(out);
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
				}			
			}else {
				System.out.println("Client rejected: " + nick);
				ErrorMessage error = new ErrorMessage();
				error.writeTo(out);
			}
		}
		
		private void sendMove(Message message,Client client) {
		    SocketAddress sockaddr = client.ip;
		    try {
		    	//recuperar thread del otro cliente y mandarlo a través de ese 
				Socket s = new Socket();
				s.connect(sockaddr);
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
		private void receiveMessages() throws IOException {
			for(;;) {
				Message message = Message.ReadFrom(in);	
				System.out.println("Mensaje recibido: " + message);
				switch(message.type()){
					case LOGIN:
						loginMessage(message);
						break;
					case PLAY:
						playMessage(message);
						break;
					case ERROR:
						System.out.println("Error!");
						break;
					default:
						break;
				}
			}
		}
		
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
				if(socket != null){
					socket.close();
					socket = null;
				}
			} catch (IOException e) {
				System.err.println(this + ": " + e);  
			}
		}
		
		@Override
		public void run() {
			try{
				receiveMessages();	
			} catch (Exception e) {
				System.out.println(e.getMessage());;	
			} finally{
				closeAll();
			}			
		}		

		public SocketAddress getId() {
			return id;
		}		
		public Socket getS() {
			return socket;
		}
		public void setS(Socket s) {
			this.socket = s;
		}
		public DataInputStream getIn() {
			return in;
		}
		public void setIn(DataInputStream in) {
			this.in = in;
		}
		public DataOutputStream getOut() {
			return out;
		}
		public void setOut(DataOutputStream out) {
			this.out = out;
		}
		public String getNick() {
			return nick;
		}
		public void setNick(String nick) {
			this.nick = nick;
		}
	}

}


