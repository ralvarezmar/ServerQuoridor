package es.urjc.mov.rmartin.quor.Graphic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	Socket s;
	String nick;
	DataInputStream in;
	DataOutputStream out;
	Client(Socket s, String nick) throws IOException{
		this.s=s;
		this.nick=nick;
		this.in = new DataInputStream(s.getInputStream());
        this.out = new DataOutputStream(s.getOutputStream());		
	}
	
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}

	@Override
	public String toString() {
		return "Client [s=" + s + ", nick=" + nick + ", in=" + in + ", out=" + out + "]";
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
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
	
}
