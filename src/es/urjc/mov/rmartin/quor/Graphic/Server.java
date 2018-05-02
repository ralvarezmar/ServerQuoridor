package es.urjc.mov.rmartin.quor.Graphic;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import es.urjc.mov.rmartin.quor.Game.Move;

public class Server{

	private static final int PORT = 2020;
	private ServerSocket socket;
	private Thread thread;
	protected Attend attend;
	private Client clients[] = new Client[2];
	
	public Server(){
		try {
			this.socket = new ServerSocket(PORT);
			//this.workers = new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void connect() {
		try{
			System.out.println("wait clients ...");
			for(;;){
				if(clients[0]==null || clients[1]==null) {
					int c;
					Socket incon = socket.accept();
					//ObjectInputStream o = new ObjectInputStream(incon.getInputStream());
				    InputStream in = incon.getInputStream();			
				    //Move m = null;
					String nick= "";
					
						while ((c = in.read()) != -1) {
						      nick = nick + (char) c;
						 }	
					//} 
					SocketAddress ip = incon.getRemoteSocketAddress();
					Client client = new Client(ip,nick);
					if(clients[0]==null) {
						clients[0]=client;
					}else if(clients[1]==null){
						clients[1]=client;
					}		
					System.out.println("Cliente nuevo: " + client);
					thread = new Thread(new Attend(incon));
					thread.start();	
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

	private class Attend implements Runnable{

		private Socket incon;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private Socket outcon;

		public Attend(Socket incon){
			this.incon = incon;
			try {
				in = new ObjectInputStream(incon.getInputStream());
				//ObjectOutputStream out = new ObjectOutputStream(incon.getOutputStream());
			} catch (IOException e) {
				closeAll();
				thread.interrupt();
				//throw new RuntimeException(this + "open streams: " + e);
			}
		}
		private void receiveMoves() throws ClassNotFoundException, IOException{
			try {
				for(;;){
					Move m;				
					System.out.println("Espero jugada");
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
					outcon.connect(ip, 2000);
					ObjectOutputStream out = new ObjectOutputStream(outcon.getOutputStream());
					out.writeObject(m);				
					outcon.close();
				}
			}catch(RuntimeException e) {
				System.out.println("conection closed");
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

	public static void main(String[] args) {
		new Thread(){
			public void run(){
				Server s = new Server();
				s.start();
			}		
		}.start();		
	}
}


