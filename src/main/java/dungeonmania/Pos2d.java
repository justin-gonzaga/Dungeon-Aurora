package dungeonmania;

import java.util.Objects;

import dungeonmania.util.Position;

/**
 * A class that represents a 2d positional coordinate (x, y)
 */
public class Pos2d {
    private int x;
    private int y;

    public Pos2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * calculates the square distance between two coordinates
     * @param o
     * @return the square distance
     */
    public int squareDistance(Pos2d o) {
        return (o.getX() - this.getX()) * (o.getX() - this.getX()) + (o.getY() - this.getY()) * (o.getY() - this.getY());
    }

    /**
     * adds two coordinates together 
     * @param o
     * @return the sum
     */
    public Pos2d plus(Pos2d o) {
        return new Pos2d(o.getX() + this.x, o.getY() + this.y);
    }

    /**
     * Subtracts this coordinate from given coordinate
     * @return the result of subtraction
     */
    public Pos2d minus(Pos2d o) {
        return new Pos2d(-o.getX() + this.x, -o.getY() + this.y);
    }

    /**
     * make a new instance of the Pos2d class from a Position class
     * @param pos
     * @return the new instance
     */
    public static Pos2d from(Position pos) {
        return new Pos2d(pos.getX(), pos.getY());
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Pos2d checkPos = (Pos2d) obj;

        return x == checkPos.getX() && y == checkPos.getY();
    }

}