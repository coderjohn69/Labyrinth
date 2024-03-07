package org.example;

public class ScoreObject implements Comparable<ScoreObject>{
    public final String name;
    public final int level;
    public final double time;

    public ScoreObject(String name, int level, double time){
        this.name = name;
        this.level = level;
        this.time = time;
    }



    @Override
    public String toString() {
        return name + ": " + level + " level " + time + " seconds";
    }


    @Override
    public int compareTo(ScoreObject l) {
        int ld = -Integer.compare(this.level, l.level);
        int td = (int) Double.compare(this.time, l.time);
        int nd = this.name.compareTo(l.name);
        if(ld != 0){
            return ld;
        }
        else if(td != 0){
            return td;
        }
        else return nd;
    }
}