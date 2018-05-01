package es.urjc.mov.rmartin.quor.Game;

import java.io.Serializable;

import es.urjc.mov.rmartin.quor.Graphic.Coordinate;

public class Move implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2373084954622553649L;
	private Coordinate c;
    private Boolean type;
    public Move(Coordinate c, Boolean type){
        this.c=c;
        this.type=type;
    }

    @Override
    public String toString() {
        return "Move{" +
                "c=" + c +
                ", type=" + type +
                '}';
    }

    public Coordinate getC() {
        return c;
    }

    public void setC(Coordinate c) {
        this.c = c;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
}
