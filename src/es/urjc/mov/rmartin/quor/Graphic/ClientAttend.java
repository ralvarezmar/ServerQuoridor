package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientAttend implements Runnable {

	private Socket s;
	private DataInputStream in;
	private DataOutputStream out;
	private String nick;
	
	public ClientAttend(Socket s) {
		
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
