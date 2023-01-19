package model;

public enum LevelItem {
    BASKET('$'), RANGER('.'), RANGERV('V'), WALL('#'), EMPTY(' '), MOUNTAIN('M'), TREE('T'), PLAYER('@');
    LevelItem(char rep){ representation = rep; }
    public final char representation;
}
