package org.example;

import javax.swing.*;
import java.awt.*;

public class Dragon extends movableObject{

    public Direction movingDirection;
    public Dragon(int x, int y) {
        super(x*40, y*30, 40, 30, new ImageIcon("data/images/Dragon.png").getImage(), false);
    }


}
