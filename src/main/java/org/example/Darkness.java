package org.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Darkness {
    private final int SCREEN_WIDTH = 775;
    private final int SCREEN_HEIGHT = 615;

    
    private Coordinate c;
    private Area Dark;
    private Ellipse2D.Double Light;

        public Darkness(){
            c = new Coordinate(0,0);
        }
        public void setCoords(Coordinate cx){


            this.c.x = (cx.x - 3)*40+10;
            this.c.y = (cx.y - 3)*30;
    }


    public void draw(Graphics g){
        Graphics2D gx = (Graphics2D) g;
        Dark = new Area(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT));
        gx.setColor(new Color(0,0,0,120));
        gx.fill(Dark);

        Light = new Ellipse2D.Double(c.x,c.y, SCREEN_WIDTH/3, SCREEN_HEIGHT/3);
        Dark.subtract(new Area(Light));

        gx.setColor(Color.BLACK);
        gx.fill(Dark);
    }

}