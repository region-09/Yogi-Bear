package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;
import model.Game;
import model.LevelItem;
import model.Position;
import res.ResourceLoader;

public class Board extends JPanel {
    private Game game;
    private final Image box, destination, player, wall, empty, mountain, tree;
    private double scale;
    private int scaled_size;
    private final int tile_size = 32;
    public Timer timer;
    public int time = 0;
    public MainWindow mainWindow;
    
    public Board(Game g) throws IOException {
        game = g;
        scale = 1.0;
        scaled_size = (int)(scale * tile_size);
        box = ResourceLoader.loadImage("res/box.png");
        destination = ResourceLoader.loadImage("res/destination.png");
        player = ResourceLoader.loadImage("res/player.png");
        wall = ResourceLoader.loadImage("res/wall.png");
        empty = ResourceLoader.loadImage("res/empty.png");
        tree = ResourceLoader.loadImage("res/tree.png");
        mountain = ResourceLoader.loadImage("res/mountain.png");
    }
    
    public void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                game.move();
                repaint();
            }
        }, 0, 1000);
    }
    
    public boolean setScale(double scale){
        this.scale = scale;
        scaled_size = (int)(scale * tile_size);
        return refresh();
    }
    
    public boolean refresh(){
        if (!game.isLevelLoaded()) return false;
        Dimension dim = new Dimension(game.getLevelCols() * scaled_size, game.getLevelRows() * scaled_size);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
        repaint();
        return true;
    }
    
    // This function is responsible for painting components of our game with 
    // fastest changing rate.
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D)g;
        int w = game.getLevelCols();
        int h = game.getLevelRows();
        Position p = game.getPlayerPos();
        if (!game.isLevelLoaded()) return;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++){
                Image img = null;
                LevelItem li = game.getItem(y, x);
                switch (li){
                    case BASKET: img = box; break;
                    case RANGER: img = destination; break;
                    case RANGERV: img = destination; break;
                    case TREE: img = tree; break;
                    case MOUNTAIN: img = mountain; break;
                    case WALL: img = wall; break;
                    case EMPTY: img = empty; break;
                    default:
                        break;
                }
                if (p.x == x && p.y == y) img = player;
                if (img == null) continue;
                gr.drawImage(img, x * scaled_size, y * scaled_size, scaled_size, scaled_size, null);
            }
        }
    }
}
