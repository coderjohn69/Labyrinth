package org.example;

import java.awt.*;

public class movableObject extends GameObject{

    public movableObject(int x, int y, int width, int height, Image image, boolean isWall) {
        super(x, y, width, height, image, isWall);
    }

    public void move(Direction d){
        this.c = new Coordinate((this.c.x + (40*d.x)),(this.c.y + (30*d.y)));
    }

    public Coordinate peek(Direction d){
        return new Coordinate((this.c.x + (40*d.x))/40,(this.c.y + (30*d.y))/30);
    }
}
