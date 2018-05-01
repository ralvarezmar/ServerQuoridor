package es.urjc.mov.rmartin.quor.Graphic;
import java.io.Serializable;

public class Coordinate implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2387403022373648877L;
	/**
	 * 
	 */
	private int x;
    private int y;
    public Coordinate(int x, int y){
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString(){
        return "{x: " + this.x + " y: "+this.y +"}";
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}