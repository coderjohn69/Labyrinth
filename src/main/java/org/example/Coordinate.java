package org.example;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public  boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof Coordinate)) return false;

        else{Coordinate c = (Coordinate)o;
         return this.x == c.x && this.y == c.y;}
    }
}
