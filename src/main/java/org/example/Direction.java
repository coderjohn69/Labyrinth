package org.example;

public enum Direction {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        public  int x;
        public  int y;

        Direction(int deltaX, int deltaY) {
            this.x = deltaX;
            this.y = deltaY;
        }


    }
