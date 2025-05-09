package MainMenu;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlyHighDragonFly extends JPanel implements ActionListener, KeyListener, MouseListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Variables for the images
    Image backgroundImg;
    Image dragonImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //For Dragon
    int DragonX = boardWidth/8; //place bird 1/8 from the left
    int DragonY = boardHeight/2; //place bird center, 1/2 from the top
    int dragonWidth = 50;
    int dragonHeight = 50;

    class Dragon {
        int x = DragonX;
        int y = DragonY;
        int width = dragonWidth;
        int height = dragonHeight;
        Image img;

        Dragon(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //Game logic variables
    Dragon dragon;
    int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;
    int finalScore = 0;
    boolean paused = false;
    JLabel resumeLabel = new JLabel("<html><div style='text-align: center;'>Press the Space bar<br>to resume</html>");

    //Buttons when game is over
    Image originalPlayAgain = new ImageIcon("PlayAgain.png").getImage();
    Image scaledPlayAgain = originalPlayAgain.getScaledInstance(150, 35, Image.SCALE_SMOOTH);
    ImageIcon scaledPlayAgainIcon = new ImageIcon(scaledPlayAgain);
    JButton playAgainButton = new JButton(scaledPlayAgainIcon);

    Image originalMenuButton = new ImageIcon("ReturnMenu.png").getImage();
    Image scaledMenuButton = originalMenuButton.getScaledInstance(150, 35, Image.SCALE_SMOOTH);
    ImageIcon scaledMenuIcon = new ImageIcon(scaledMenuButton);
    JButton menuButton = new JButton(scaledMenuIcon);

    Image originalAddScore = new ImageIcon("RecordScores.png").getImage();
    Image scaledAddScore = originalAddScore.getScaledInstance(150, 35, Image.SCALE_SMOOTH);
    ImageIcon scaledAddScoreIcon = new ImageIcon(scaledAddScore);
    JButton addScore = new JButton(scaledAddScoreIcon);

    //Buttons on top
    Image originalPause = new ImageIcon("Pause.png").getImage();
    Image scaledPause = originalPause.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon PauseIcon = new ImageIcon(scaledPause);
    JButton pauseButton = new JButton(PauseIcon);

    Image originalRetry = new ImageIcon("Retry.png").getImage(); //placeholder image
    Image scaledRetry = originalRetry.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon RetryIcon = new ImageIcon(scaledRetry);
    JButton retryButton = new JButton(RetryIcon);

    Image originalMenu = new ImageIcon("Menu.png").getImage(); //placeholder image
    Image scaledMenu = originalMenu.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon MenuIcon = new ImageIcon(scaledMenu);
    JButton menuMiniButton = new JButton(MenuIcon);

    FlyHighDragonFly(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setLayout(null);

        backgroundImg = new ImageIcon("gamebg.png").getImage();
        dragonImg = new ImageIcon("dragonfly.png").getImage();
        topPipeImg = new ImageIcon("toppipe.png").getImage();
        bottomPipeImg = new ImageIcon("bottompipe.png").getImage();

        //birb
        dragon = new Dragon(dragonImg);
        pipes = new ArrayList<Pipe>();

        //buttons on top
        buttons();

        //buttons when game is over
        overButton();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() { //this will call/add pipes every 1.5 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this); //we do 60 frames per second so 1000/60
        gameLoop.start();

        //in-game texts
        resumeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        resumeLabel.setForeground(Color.BLACK);
        resumeLabel.setBounds(65, 240, 240, 50);
        add(resumeLabel);
        resumeLabel.setVisible(false);
    }

    public void buttons(){
        //buttons on top
        pauseButton.setBounds(5, 6, 30, 30);
        pauseButton.setFocusable(false);
        add(pauseButton);
        pauseButton.addActionListener(e -> {
            paused = true;
            gameLoop.stop();
            placePipesTimer.stop();
            resumeLabel.setVisible(true);
        });

        //resume the game using space bar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && paused) {
                    paused = false;
                    gameLoop.start();
                    placePipesTimer.start();
                    resumeLabel.setVisible(false);
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        retryButton.setBounds(40, 6, 30, 30);
        retryButton.setFocusable(false);
        add(retryButton);
        retryButton.addActionListener(e -> {
            gameOver = false;
            dragon.y = DragonY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameLoop.start();
            placePipesTimer.start();
            backgroundImg = new ImageIcon("gamebg.png").getImage();
        });

        menuMiniButton.setBounds(75, 6, 30, 30);
        menuMiniButton.setFocusable(false);
        add(menuMiniButton);
        menuMiniButton.addActionListener(e -> {
            JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(menuButton);
            gameFrame.setVisible(false);
            Menu menu = new Menu();
        });
    }

    public void overButton(){
        playAgainButton.setFocusable(false);
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(e -> {
            gameOver = false;
            dragon.y = DragonY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameLoop.start();
            placePipesTimer.start();
            playAgainButton.setVisible(false);
            menuButton.setVisible(false);
            addScore.setVisible(false);
            pauseButton.setVisible(true);
            retryButton.setVisible(true);
            menuMiniButton.setVisible(true);
            backgroundImg = new ImageIcon("gamebg.png").getImage();
        });
        add(playAgainButton);


        menuButton.setFocusable(false);
        menuButton.setVisible(false);
        menuButton.addActionListener(e -> {
            JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(menuButton);
            gameFrame.setVisible(false);
            Menu menu = new Menu();
        });
        add(menuButton);

        //Study this, how to store data
        addScore.setFocusable(false);
        addScore.setVisible(false);
        addScore.addActionListener(e -> {
            String playerName = JOptionPane.showInputDialog(null, "Enter your name:");
            if(playerName != null && !playerName.isEmpty()){
                String currentDate = LocalDate.now().toString();
                ScoreEntry entry = new ScoreEntry(playerName, currentDate, finalScore);
                ScoreManager.saveScores(entry);
                JOptionPane.showMessageDialog(null, "Score saved!");
            }
        });
        add(addScore);
    }

    public void placePipes(){
        //note for random numbers: (0-1) * pipeHeight/2 -> generates number from (0 - 256);
        //more on vid abt the calculations around 40:00 min mark

        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2)); // every pipe has shifter upwards by a quarter of its height minus some random number
        int openingSpace = boardHeight/4;


        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //This is where we update the background and pipes
        // learn how to fade
        if(score >= 3){
            backgroundImg = new ImageIcon("night.jpg").getImage(); //placeholder
        }

        draw(g); //draw pipes, bird, and score
        if (gameOver) {
            int panelSize = 200;
            int x = (getWidth() - panelSize) / 2;
            int y = (getHeight() - panelSize) / 2;


            // Background
            Image gameOver = new ImageIcon("gameover.png").getImage();
            g2d.drawImage(gameOver, x-10, y, null);

            // Text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString(String.valueOf((int) score), x + 95,  y + 80);
            finalScore = (int)score;

            playAgainButton.setBounds(x + 40, y+98, 130, 35);
            addScore.setBounds(x + 40, y+145, 130, 35);
            menuButton.setBounds(x + 40, y+185, 130, 35);

            playAgainButton.setVisible(true);
            menuButton.setVisible(true);
            addScore.setVisible(true);
        }
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(dragonImg, dragon.x, dragon.y, dragon.width, dragon.height, null);

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i); //get index of pipe
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score display during in game
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        if(!gameOver){
            g.drawString(String.valueOf((int) score), 170, 100); //display 10 pixels to the right and 35 pixels down
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        dragon.y += velocityY;
        dragon.y = Math.max(dragon.y, 0); //limit the birds movement upwards, stops at the top

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX; //every frame, we move each pipe over by -4 to the left

            /*This is where we update speed of pipes moving
            if(score >= 3){
                pipe.x += velocityX + 1;
            }*/

            if(!pipe.passed && dragon.x > pipe.x + pipe.width){ //explanation in vid 47:00
                pipe.passed = true;
                score += 0.5; //0.5 bcs there are 2 pipes bird passes through, 0.5*2 = 1 for each set of pipes
            }

            if(collision(dragon, pipe)){
                gameOver = true;
            }
        }

        if(dragon.y > boardHeight){
            gameOver = true;
        }
    }

    public boolean collision(Dragon a, Pipe b){
        return  a.x < b.x + b.width && //Bird's top left corner doesn't reach pipe's top right corner
                a.x + a.width > b.x && //Bird's top right corner passes pipe's top left corner
                a.y < b.y + b.height && //Bird's top left corner doesn't reach pipe's bottom left corner
                a.y + a.height > b.y; //Bird's bottom left corner passes pipe's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); //calls paint method
        if(gameOver){
            placePipesTimer.stop(); //stop adding pipes to arraylist
            gameLoop.stop(); //stops repainting
            pauseButton.setVisible(false);
            retryButton.setVisible(false);
            menuMiniButton.setVisible(false);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        velocityY = -9;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
