package es.urjc.mov.rmartin.quor.Graphic;

public class Game{
	int id;
	Client client1;
	Client client2;
	Game(int id, Client client1,Client client2){
		this.id=id;
		this.client1=client1;
		this.client2=client2;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Client getClient1() {
		return client1;
	}
	public void setClient1(Client client1) {
		this.client1 = client1;
	}
	public Client getClient2() {
		return client2;
	}
	public void setClient2(Client client2) {
		this.client2 = client2;
	}	
}