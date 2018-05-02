package es.urjc.mov.rmartin.quor.Graphic;

import java.net.SocketAddress;

public class Client {
	SocketAddress ip;
	String nick;
	Client(SocketAddress ip, String nick){
		this.ip=ip;
		this.nick=nick;
	}
	public SocketAddress getIp() {
		return ip;
	}
	public void setIp(SocketAddress ip) {
		this.ip = ip;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	@Override
	public String toString() {
		return "Client [ip=" + ip + ", nick=" + nick + "]";
	}	
}
