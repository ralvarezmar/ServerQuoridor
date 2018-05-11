package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.urjc.mov.rmartin.quor.Graphic.Message.ErrorMessage;
import es.urjc.mov.rmartin.quor.Graphic.Message.Login;
import es.urjc.mov.rmartin.quor.Graphic.Message.OkMessage;

public class Server{

	private static final int PORT = 2020;
	private ServerSocket socket;
	private Thread thread;
	private int numGames=0;
	//protected Attend attend;
	Map<String, SocketAddress> clientsMap = new HashMap<String, SocketAddress>();
	private Client clients[] = new Client[2];
	private ArrayList<Game> partidas = new ArrayList<Game>();
	
	
	public Server(){
		try {
			this.socket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	private Boolean isClient(String nick) {
		if(clientsMap.get(nick)!=null) {
			return true;
		}
		return false;
	}
	
	private void loginMessage(Message message,Socket s) throws IOException {
		System.out.println("Entra en funcion");
		Login login = (Login) message;
		String nick=login.getNick();
		if(!isClient(nick)) {
			System.out.println("Client accepted: " + nick);
			SocketAddress ip = s.getRemoteSocketAddress();
			Client client = new Client(ip,nick);
			clientsMap.put(nick, ip);
			if(clients[0]==null) {
				clients[0]=client;
			}else if(clients[1]==null){
				clients[1]=client;
				Game game = new Game(numGames,clients[0],clients[1]);
				partidas.add(game);
				numGames++;
				clients = new Client[2];
			}
			OkMessage ok = new OkMessage();
			OutputStream writer= s.getOutputStream();
		    DataOutputStream out=new DataOutputStream(writer);
			ok.writeTo(out);
		}else {
			System.out.println("Client rejected: " + nick);
			ErrorMessage error = new ErrorMessage();
			OutputStream writer= s.getOutputStream();
		    DataOutputStream out=new DataOutputStream(writer);
			error.writeTo(out);
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
							break;
						case ERROR:
							break;
						default:
							break;
					}
					//Client client = new Client(ip,message.getNick());
					/*
					if(clientsMap.get(nick)==null) {
					}
						*/
					//thread = new Thread(new Attend(incon));
					//thread.start();	
				}else {
					
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
	private class Attend implements Runnable{

		private Socket incon;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private Socket outcon;

		public Attend(Socket incon){
			this.incon = incon;			
		}
		private void receiveMoves() throws ClassNotFoundException, IOException{
			for(;;){
				Socket incon = socket.accept();			
				in = new ObjectInputStream(incon.getInputStream());
				Move m;				
				System.out.println("Espero jugada");					
				while(in.available()<=0);
				m = (Move) in.readObject();
				System.out.println("Jugada recibida: " + m);
				in.close();
				incon.close();
				outcon = new Socket();
				SocketAddress ip;
				if(incon.getRemoteSocketAddress()==clients[0].ip) {
					ip = clients[1].ip;
					System.out.println("Mando jugada a: " + clients[1]);
				}else {
					ip = clients[0].ip;
					System.out.println("Mando jugada a: " + clients[0]);
				}
				outcon.connect(ip);
				ObjectOutputStream out = new ObjectOutputStream(outcon.getOutputStream());
				out.writeObject(m);				
				outcon.close();
			}
		}

		public void run(){	
			try {
				receiveMoves();
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				closeAll();
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


