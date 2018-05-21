package es.urjc.mov.rmartin.quor.Graphic;

public class Position {
	String nick;
	double latitude;
	double longitude;
	Position(String nick, double latitude,double longitude){
		this.nick=nick;
		this.latitude=latitude;
		this.longitude=longitude;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	@Override
	public String toString() {
		return "Position [nick=" + nick + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
}
