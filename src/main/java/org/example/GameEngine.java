/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.awt.event.*;

/**
 *
 * @author dheeraj
 */
public class GameEngine extends JPanel {
    public GameObject[][] grid ;
    private final int SCREEN_WIDTH = 775;
    private final int SCREEN_HEIGHT = 610;
    private final int FPS = 240;
    private final JPanel screen = this;
    private Player player;
    private Dragon dragon;
    private Timer newFrameTimer;
    private  JLabel timeLabel;
    private long startTime;
    private Timer timer;
    private long elapsedTime;
    private double elapsedTimeInSeconds;
    private boolean paused = false;
    private int currentLevel;

    private Timer dragonMover;
    private Database db;
    private Darkness darkness;
    private long stoppedTime;
    private GameObject pauseObject = new GameObject(SCREEN_WIDTH/2-200, SCREEN_HEIGHT/2-125, 400, 200, new ImageIcon("data/images/paused.jpeg").getImage(),false);

//    public JFrame GUI;
//    private final Database database;

    public GameEngine() throws IOException, InterruptedException, SQLException {
        db = new Database();
        currentLevel = 0;
//        this.GUI = g;
        this.addKeyListener(new CharacterMovement(this));
        grid = new GameObject[19][19];
        loadGame(currentLevel);

        newFrameTimer = new Timer(1000/FPS, new NewFrameListener());
        newFrameTimer.start();
        dragonMover = new Timer(800, new moveDragon());
        dragonMover.start();
//        GUI.setVisible(true);
        startTimer();
//        moveDragon();




    }

    public void startTimer(){
        timeLabel = new JLabel(" ");
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        startTime = System.currentTimeMillis();
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime = System.currentTimeMillis() - startTime;
                elapsedTimeInSeconds = (double)elapsedTime/1000;
                timeLabel.setText( elapsedTimeInSeconds + " s");
            }
        });
        timer.start();
    }

    public boolean nextwall(movableObject g,Direction d){
            Coordinate c = g.peek(d);
            return grid[c.y][c.x].isWall;
    }



    class moveDragon implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!paused) {
                if( dragon.peek(dragon.movingDirection).equals(new Coordinate(18,1))){
                    setMovingDirection();
                }
                else if(nextwall(dragon,dragon.movingDirection)){
                    setMovingDirection();
                }
                dragon.move(dragon.movingDirection);
            }
        }
    }

    public void endGame(){

        if(playerCaught(dragon))
        {
            String name = JOptionPane.showInputDialog(screen, "Enter your name: ", "You couldn't escape...", JOptionPane.INFORMATION_MESSAGE);
            if(name != null) db.putHighScore(name, currentLevel, elapsedTimeInSeconds);
            int option = JOptionPane.showConfirmDialog(screen, "Start again?", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                restart(0);
                startTime = System.currentTimeMillis();
                timer.restart();
            }
            else System.exit(0);
        } else if (levelPassed()) {
            currentLevel+=1;
            try {
                loadGame(currentLevel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            player = new Player();
//            placeDragon();


        }
    }


    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            repaint();

            endGame();
            repaint();
        }
    }

    public boolean levelPassed(){
        Coordinate winningIndex = new Coordinate(18,1);
        return  winningIndex.equals(player.getIndex());
    }




    public void loadGame(int level) throws IOException {


        BufferedReader br = new BufferedReader(new FileReader("data/maps/level" +level+".txt"));

        int y = 0;
        String line;
        while ((line = br.readLine()) != null) {
            int x = 0;
            for (char c : line.toCharArray()) {
                if (c == '0') {
                    Image image = new ImageIcon("data/images/wall.png").getImage();
                    grid[y][x] = new GameObject(x * 40, y * 30, 40, 30, image, true);
                }
                else {
                    Image image = new ImageIcon("data/images/path.png").getImage();
                    grid[y][x] = new GameObject(x * 40, y * 30, 40, 30, image, false);
                }
                x++;
            }
            y++;
        }
        player = new Player();
        placeDragon();

        darkness = new Darkness();
        darkness.setCoords(player.getIndex());


    }


    public void placeDragon() {
        Random random = new Random();
        int x = random.nextInt(19);
        int y = random.nextInt(19);
//        System.out.println(x);
//        System.out.println(y);
        if (!grid[y][x].isWall && !(x==1 && y==17) && !playerCaught(new Dragon(x, y))) {
            dragon = new Dragon(x, y);
            setMovingDirection();
        }
        else{
                placeDragon();
            }
    }






    public void setMovingDirection() {
        Direction[] directions = Direction.values();
        Random random = new Random();
        int randomIndex = random.nextInt(directions.length);
        Direction dir = directions[randomIndex];

        // Keep track of the directions that have been tried
        Set<Direction> triedDirections = new HashSet<>();

        // Keep track of the number of times the dragon has changed directions
        int numDirectionChanges = 0;

        // While the randomly selected direction is not valid or has been tried, keep generating new directions
        while (nextwall(dragon, dir) || triedDirections.contains(dir) || dragon.peek(dir).equals(new Coordinate(18,1))) {
            randomIndex = random.nextInt(directions.length);
            dir = directions[randomIndex];
            triedDirections.add(dir);

            // If the dragon has tried all directions, reset the triedDirections set and increment the number of direction changes
            if (triedDirections.size() == directions.length) {
                triedDirections.clear();
                numDirectionChanges++;
            }
        }

        // If the dragon has changed directions more than once, reset the triedDirections set
        if (numDirectionChanges > 1) {
            triedDirections.clear();
        }

        dragon.movingDirection = dir;
    }

    public void draw(Graphics g){
        for (GameObject[] row : grid) {
            for (GameObject gx : row) {
                gx.draw(g);
            }
        }
    }



    public boolean playerCaught(Dragon d) {
        Coordinate playerCoord = player.getIndex();
        Coordinate dragonCoord = d.getIndex();
        return  (playerCoord.x+1 == dragonCoord.x && dragonCoord.y==playerCoord.y) ||
                (playerCoord.x-1 == dragonCoord.x && dragonCoord.y==playerCoord.y) ||
                (playerCoord.x == dragonCoord.x && dragonCoord.y+1==playerCoord.y) ||
                (playerCoord.x == dragonCoord.x && dragonCoord.y-1==playerCoord.y) ;
    }





    @Override
    protected void paintComponent(Graphics g) {
        draw(g);
        player.draw(g);
        dragon.draw(g);
        darkness.draw(g);
        if (paused) pauseObject.draw(g);
    }







    void pause() {
        paused = !paused;
        if(paused) {
            timer.stop();
            stoppedTime = System.currentTimeMillis();
        }
        else {
            startTime += System.currentTimeMillis() - stoppedTime;
            timer.restart();
        }
    }

    void restartTimer() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void restart(int i){
        try {
            loadGame(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        player = new Player();
//        placeDragon();
        startTime = System.currentTimeMillis();
        timer.restart();

    }

    public JLabel getTimer(){
        return timeLabel;
    }

    class CharacterMovement extends KeyAdapter {
        GameEngine g;

        public CharacterMovement(GameEngine g){
            this.g=g;
        }

        @Override
        public void keyPressed(KeyEvent key) {
            int kc = key.getKeyCode();
//               Point coords = hero.getCoords();
            switch (kc){
                case KeyEvent.VK_A: {
//                    System.out.println("LEFT");
                    if(!nextwall(player,Direction.LEFT)) player.move(Direction.LEFT);
                    darkness.setCoords(player.getIndex());
                    break;
                }
                case KeyEvent.VK_D: {
                    if(!nextwall(player,Direction.RIGHT)) player.move(Direction.RIGHT);
                    darkness.setCoords(player.getIndex());
                    break;
                }
                case KeyEvent.VK_W: {
                    if(!nextwall(player,Direction.UP)) player.move(Direction.UP);
                    darkness.setCoords(player.getIndex());
                    break;
                }
                case KeyEvent.VK_S: {
                    if(!nextwall(player,Direction.DOWN)) player.move(Direction.DOWN);
                    darkness.setCoords(player.getIndex());
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    pause();
                }

            }

        }
    }




}

