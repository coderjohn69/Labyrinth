package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private final int limit = 10;
    private Statement statement;
    private PreparedStatement preparedStatementInsert;
    private PreparedStatement preparedStatementRemove;
    private Connection connection;


    public Database() throws SQLException{
        String dbURL = "jdbc:h2:./data/Database/ss";
        try {
            connection = DriverManager.getConnection("jdbc:h2:./data/Database/ss");
            statement = connection.createStatement();
        } catch (SQLException ex) {
            dbURL += ";create=true";
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
        }
        String insertQuery = "INSERT INTO SCORES (NAME, LEVEL, TIME) VALUES (?, ?, ?)";
        preparedStatementInsert = connection.prepareStatement(insertQuery);
        String deleteQuery = "DELETE FROM SCORES WHERE LEVEL=? AND TIME=?";
        preparedStatementRemove = connection.prepareStatement(deleteQuery);
    }

    
    public ArrayList<ScoreObject> getSCORES() throws SQLException {
        String query = "SELECT * FROM SCORES";
        ArrayList<ScoreObject> SCORES = new ArrayList<>();
        ResultSet results = statement.executeQuery(query);
        while (results.next()) {
            String name = results.getString("NAME");
            int level = results.getInt("LEVEL");
            double time = results.getDouble("TIME");
            SCORES.add(new ScoreObject(name, level, time));
        }
        Collections.sort(SCORES);
        return SCORES;
    }
    
    public String[] getColumnNamesArray (){
        String[] columnNames = {"#", "Name", "Levels completed", "Time"};
        return columnNames;
    }

    
    public String[][] getDataMatrix () throws SQLException{
        String[][] columnNames = new String[10][4];
        ArrayList<ScoreObject> SCORES = getSCORES();
        int cnt = 0;
        for(ScoreObject hs : SCORES){
            columnNames[cnt][0] = String.valueOf(cnt+1);
            columnNames[cnt][1] = hs.name;
            columnNames[cnt][2] = String.valueOf(hs.level);
            columnNames[cnt][3] = String.valueOf(hs.time) + " s";
            cnt++;
        }
        for(;cnt < 10; cnt++){
            columnNames[cnt][0] = String.valueOf(cnt+1);
            columnNames[cnt][1] = "";
            columnNames[cnt][2] = "";
            columnNames[cnt][3] = "";
        }
        return columnNames;
    }

    
    public void putHighScore(String name, int level, double time) {
        try {
            ArrayList<ScoreObject> SCORES = getSCORES();
            if (SCORES.size() < limit) {
                insertScore(name, level, time);
            }
            else {
                int leastLevel = SCORES.get(SCORES.size() - 1).level;
                double leastTime = SCORES.get(SCORES.size() - 1).time;
                if (leastLevel < level || (leastLevel == level && leastTime > time) ) {
                    deleteScores(leastLevel, leastTime);
                    insertScore(name, level, time);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private void insertScore(String name, int level, double time) {
        try {
            preparedStatementInsert.setString(1, name);
            preparedStatementInsert.setInt(2, level);
            preparedStatementInsert.setDouble(3, time);
            preparedStatementInsert.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private void deleteScores(int level, double time) throws SQLException {
        preparedStatementRemove.setInt(1, level);
        preparedStatementRemove.setDouble(2, time);
        preparedStatementRemove.executeUpdate();
    }

   

}