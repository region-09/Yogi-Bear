package model;

import java.util.Objects;

public class GameID {
    public final String difficulty;
    public String name;
    public final int    level;
    
    // initially we set all names with none because no one solved this level yet
    public GameID(String difficulty, int level) {
        this.difficulty = difficulty;
        this.name = "none";
        this.level = level;
    }
    public GameID(String name, String difficulty, int level) {
        this.name = name;
        this.difficulty = difficulty;
        this.level = level;
    }
    
    public void setName(String input) {
        this.name = input;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.difficulty);
        hash = 29 * hash + this.level;
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameID other = (GameID) obj;
        if (this.level != other.level) {
            return false;
        }
        if (!Objects.equals(this.difficulty, other.difficulty)) {
            return false;
        }
        return true;
    }
    
    
}
