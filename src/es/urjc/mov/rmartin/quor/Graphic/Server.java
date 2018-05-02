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

		public Attend(Socket incon){
			this.incon = incon;
			try {
				ObjectInputStream in = new ObjectInputStream(incon.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(incon.getOutputStream());
			} catch (IOException e) {
				closeAll();
				thread.interrupt();
				throw new RuntimeException(this + "open streams: " + e);
			}
		}
		private void receiveMoves(){
			try {
				for(;;){
					Move m;				
					try {
						m = (Move) in.readObject();
						in.close();
						if(incon.getRemoteSocketAddress()==clients[0].ip) {
							
						}
					}
					/*
					request = Msg.ReadFrom(idata);
					switch(request.type()){
					case TADD:
						Tadd tadd = (Tadd)request;
						Worker w = tadd.getWorker();
						if(added(w)){
							response = new Msg.Rok();
						}else{
							response = new Msg.Rerror();
						}	
						break;
					default:
						break;
					}
					if(response != null){
						response.writeTo(odata);
					}
				}
				*/
				}
			}catch(RuntimeException e) {
				System.out.println("conection closed");
			}
			*/
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
				if(idata != null){
					idata.close();
					idata = null;
				}
				if(odata != null){
					odata.close();
					odata = null;
				}	
				if(incon != null){
					incon.close();
					incon = null;
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


