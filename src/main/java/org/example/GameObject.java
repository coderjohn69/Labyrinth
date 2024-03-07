package org.example;

import java.awt.*;


public class GameObject {
    /**
     * The coordinates of the top left corner of the sprite
     */
    Coordinate c;
    protected int width;
    protected int height;
    protected Image image;
    protected boolean isWall;

    public GameObject(int x, int y, int width, int height, Image image, boolean isWall) {
        c = new Coordinate(x,y);
        this.width = width;
        this.height = height;
        this.image = image;
        this.isWall = isWall;
    }

    public void draw(Graphics g) {
        g.drawImage(image, c.x, c.y, width, height, null);
    }

    public Coordinate getCoordinate(){
        return c;
    }

    public Coordinate getIndex(){
        return new Coordinate(c.x/40,c.y/30);
    }
}