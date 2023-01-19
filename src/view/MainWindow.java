package view;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.WindowConstants;
import model.Direction;
import model.Game;
import model.GameID;

public class MainWindow extends JFrame{
    private final Game game;
    private Board board;
    public final JLabel gameStatLabel;
    public Timer timer;
    
    public MainWindow() throws IOException{
        // Responsible for all GUI things!
        game = new Game();
        setTitle("Yogi Bear");
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        URL url = MainWindow.class.getClassLoader().getResource("res/box.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(url));
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Options");
        JButton restart = new JButton("Restart");
        JMenu menuGameLevel = new JMenu("Levels");
        JMenu menuGameScale = new JMenu("Zoom");
        createGameLevelMenuItems(menuGameLevel);
        createScaleMenuItems(menuGameScale, 1.0, 2.0, 0.5);

        restart.addActionListener((ActionEvent e) -> {
            game.restart();
            game.numLive = 3;
        });
        
        JMenuItem menuHighScores = new JMenuItem(new AbstractAction("Table of players") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HighScoreWindow(game.getHighScores(), MainWindow.this);
            }
        });
        
        JMenuItem menuGameExit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuGame.add(menuGameLevel);
        menuGame.add(menuGameScale);
        menuGame.add(menuHighScores);
        menuGame.addSeparator();
        menuGame.add(menuGameExit);
        menuGame.add(restart);
        menuBar.add(menuGame);
        setJMenuBar(menuBar);
        
        setLayout(new BorderLayout(0, 10));
        gameStatLabel = new JLabel("label");

        add(gameStatLabel, BorderLayout.NORTH);
        try { add(board = new Board(game), BorderLayout.CENTER); } catch (IOException ex) {}
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke); 
                if (!game.isLevelLoaded()) return;
                int kk = ke.getKeyCode();
                Direction d = null;
                switch (kk){
                    case KeyEvent.VK_LEFT:  d = Direction.LEFT; break;
                    case KeyEvent.VK_RIGHT: d = Direction.RIGHT; break;
                    case KeyEvent.VK_UP:    d = Direction.UP; break;
                    case KeyEvent.VK_DOWN:  d = Direction.DOWN; break;
                    case KeyEvent.VK_ESCAPE: game.loadGame(game.getGameID());
                }
                board.repaint();
                
                if (d != null && game.step(d)){
                    if (game.isGameEnded()){
                        String msg = "Congrats!";
                        if (game.isBetterHighScore()){
                            msg += " You got better score!";
                        }
                        board.timer.cancel();
                        timer.cancel();
                        String input = JOptionPane.showInputDialog(MainWindow.this, "Store to Database", "INPUT", JOptionPane.INFORMATION_MESSAGE);
                        game.setName(input);
                        if (game.getGameID().level == 10) {
                            game.loadGame(new GameID("LEVEL", 1));
                        }else {
                            game.loadGame(new GameID(game.getGameID().difficulty, game.getGameID().level + 1));
                        }
                        game.time = 0;
                        board.refresh();
                        pack();
                        game.numLive = 3;
                        board.startTimer();
                        refreshGameStatLabel();
                    }
                }
            }
        });

        setResizable(false);
        setLocationRelativeTo(null);
        game.loadGame(new GameID("LEVEL", 1));
        board.setScale(1.5); // board.refresh();
        pack();
        refreshGameStatLabel();
        setVisible(true);
        board.startTimer();
    }
    
    private void refreshGameStatLabel(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            String s = "Number of steps: " + game.getNumSteps();
            String minute = "";
            String second = "";
            if ((game.time / 60) % 10 > 0) {
                minute = Integer.toString(game.time / 60);
            }else {
                minute = "0" + Integer.toString(game.time / 60);
            }
            if ((game.time % 60) / 10 > 0) {
                second = Integer.toString(game.time % 60);
            }else {
                second = "0" + Integer.toString(game.time % 60);
            }
            // Showing time and infos about game
            s += ", Collected baskets: " + game.getLevelNumBoxesInPlace() + "/" + game.getLevelNumBoxes() + ", Life: " + game.getLive() + ", Time: " + minute + ":" + second;
            gameStatLabel.setText(s);
            if (!game.check()) {
                board.timer.cancel();
                // if player caught he/she will receive option pane notifying how much life he/she has
                JOptionPane.showMessageDialog(MainWindow.this, "Caught!", "Lost!", JOptionPane.INFORMATION_MESSAGE);
                if (game.numLive <= 0) {
                    if (game.getGameID().level > 1) {
                        game.loadGame(new GameID("LEVEL", game.getGameID().level - 1));
                        game.numLive = 3;
                    }else {
                        game.loadGame(new GameID("LEVEL", 1));
                        game.numLive = 3;
                    }
                }
                game.restart();
                board.startTimer();
                game.time = 0;
                board.refresh();
                pack();
            }
        }
      }, 0, 100);
    }
    
    private void createGameLevelMenuItems(JMenu menu){
        for (String s : game.getDifficulties()){
            for (Integer i : game.getLevelsOfDifficulty(s)){
                JMenuItem item = new JMenuItem(new AbstractAction("Level-" + i) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        game.loadGame(new GameID(s, i));
                        board.refresh();
                        pack();
                    }
                });
                menu.add(item);
            }
        }
    }
    
    private void createScaleMenuItems(JMenu menu, double from, double to, double by) {
        while (from <= to){
            final double scale = from;
            JMenuItem item = new JMenuItem(new AbstractAction(from + "x") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (board.setScale(scale)) pack();
                }
            });
            menu.add(item);
            
            if (from == to) break;
            from += by;
            if (from > to) from = to;
        }
    }
    
    public static void main(String[] args) {
        try {
            new MainWindow();
        } catch (IOException ex) {}
    }    
}
