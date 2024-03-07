package org.example;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author dheeraj
 */

public class GameGUI {
    private JFrame frame;
    private GameEngine gameArea;
    private Database db;
    private JFrame highscoresFrame;
    private JMenuBar menuBar;
    private JMenu menu;

    /**
     * Creates the main frame, creates a menu, and creates a secondary frame for high scores which is initially invisible.
     */
    public GameGUI() throws SQLException {
        frame = new JFrame("Labyrinth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        db = new Database();


        try{
        gameArea = new GameEngine();}
        catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

//        database = gameArea.getDatabase();
        //database.emptyTheTable();
        updateHighscoresFrame();

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menu = new JMenu("Menu");

        JMenuItem highscores = new JMenuItem("Highscores");
        JMenuItem pause = new JMenuItem("Pause");
        JMenuItem restart = new JMenuItem("Restart");
        JMenuItem quit = new JMenuItem("Quit Game");


        highscores.addActionListener(new menuActionListener('s'));
        menu.add(highscores);

        pause.addActionListener(new menuActionListener('p'));
        menu.add(pause);

        restart.addActionListener(new menuActionListener('r'));
        menu.add(restart);

        quit.addActionListener(new menuActionListener('q'));
        menu.add(quit);


        menuBar.add(menu);

        gameArea.setFocusable(true);
        frame.getContentPane().add(gameArea, BorderLayout.CENTER);
        frame.getContentPane().add(gameArea.getTimer(), BorderLayout.SOUTH);

        frame.setPreferredSize(new Dimension(775, 650));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private class menuActionListener implements ActionListener
    {
        private char c;
        menuActionListener(char c){
            this.c = c;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (c){
                case 'p':
                    gameArea.pause();
                    break;
                case 'r':
                    gameArea.restart(0);
                    break;
                case 's':
                    gameArea.pause();
                    try {
                        updateHighscoresFrame();
                        highscoresFrame.setVisible(true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                case 'q':
                    System.exit(0);
            }
        }
    }

    /**
     * Creates a new frame which contains a table of the top 10 high scores.
     */
    private void updateHighscoresFrame() throws SQLException {
        highscoresFrame = new JFrame("Highscores");
        JTable table = new JTable(db.getDataMatrix(),db.getColumnNamesArray());
        table.setEnabled(false);
        table.setRowHeight(50);
        JScrollPane sp = new JScrollPane(table);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(230);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(200);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        columnModel.getColumn(0).setCellRenderer(cellRenderer);
        columnModel.getColumn(1).setCellRenderer(cellRenderer);
        columnModel.getColumn(2).setCellRenderer(cellRenderer);
        columnModel.getColumn(3).setCellRenderer(cellRenderer);
        highscoresFrame.add(sp);
        highscoresFrame.setSize(new Dimension(600, 400));
        highscoresFrame.setLocationRelativeTo(null);
    }

}

