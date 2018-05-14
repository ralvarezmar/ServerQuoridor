package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientAttend implements Runnable {

	private Socket s;
	private DataInputStream in;
	private DataOutputStream out;
	private String nick;
	private SocketAddress id;
	
	public ClientAttend(Socket s,String nick) {
		this.incon = incon;
		this.nick = nick;
		try {
			this.idata = new DataInputStream(incon.getInputStream());
			this.odata = new DataOutputStream(incon.getOutputStream());
			//this.id = incon.getRemoteSocketAddress().toString().split(":")[1];	
			this.id = incon.getRemoteSocketAddress();
		} catch (IOException e) {
			closeAll();
			thread.interrupt();
			throw new RuntimeException(this + "open streams: " + e);
		}		
	}
	public String getId() {
		return id;
	}
	
	private void ReceiveAndSend() {
		for(;;) {
			
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
