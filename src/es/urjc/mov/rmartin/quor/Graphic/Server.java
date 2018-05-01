package es.urjc.mov.rmartin.quor.Graphic;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

	private static final int PORT = 2020;
	private ServerSocket socket;
	private Thread thread;
	protected Attend attend;

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
				Socket incon = socket.accept();
				ObjectInputStream o = new ObjectInputStream(incon.getInputStream());
				Box c = null;
				try {
					c = (Box) o.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				System.out.println("client acepted " + incon.getRemoteSocketAddress() + " " + c);
				
				thread = new Thread(new Attend(incon));
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

	private class Attend implements Runnable{

		private Socket incon;
		private DataInputStream idata;
		private DataOutputStream odata;

		public Attend(Socket incon){
			this.incon = incon;
			try {
				idata = new DataInputStream(incon.getInputStream());
				odata = new DataOutputStream(incon.getOutputStream());
			} catch (IOException e) {
				closeAll();
				thread.interrupt();
				throw new RuntimeException(this + "open streams: " + e);
			}
		}

		private void sendMessages(){
			/*Msg response = null;
			Msg request = null;
			try {
				for(;;){
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
				
			}catch(RuntimeException e) {
				System.out.println("conection closed");
			}
			*/
		}

		public void run(){	
			try{
				sendMessages();	
			} catch (Exception e) {
				e.printStackTrace();	
			} finally{
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


