package MainMenu;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;

public class FlyHighDragonFly extends JPanel implements ActionListener{
    int boardWidth = 360;
    int boardHeight = 640;

    //Variables for initializing the images
    Image backgroundImg;
    Image background1Img;
    Image background2Img;
    Image background3Img;
    Image dragonImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image gameOverImg;

    // Main Buttons when game is over
    JButton playAgainButton = new JButton(new ImageIcon(new ImageIcon("src/img/PlayAgain.png").getImage().getScaledInstance(150, 35, Image.SCALE_SMOOTH)));
    JButton menuButton = new JButton(new ImageIcon(new ImageIcon("src/img/ReturnMenu.png").getImage().getScaledInstance(150, 35, Image.SCALE_SMOOTH)));
    JButton addScore = new JButton(new ImageIcon(new ImageIcon("src/img/RecordScores.png").getImage().getScaledInstance(150, 35, Image.SCALE_SMOOTH)));

    // Buttons on the top left during game play
    JButton pauseButton = new JButton(new ImageIcon(new ImageIcon("src/img/Pause.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
    JButton retryButton = new JButton(new ImageIcon(new ImageIcon("src/img/Retry.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
    JButton menuMiniButton = new JButton(new ImageIcon(new ImageIcon("src/img/Menu.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    //For Dragon
    int DragonX = boardWidth/8; //place dragon 1/8 from the left
    int DragonY = boardHeight/2; //place dragon center, 1/2 from the top
    int dragonWidth = 50; //size of dragon
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

    //For Pipes
    ArrayList<Pipe> pipes;
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
    int velocityX = -4; //speed of moving pipes to the left (simulates dragon moving right)
    int velocityY = 0;
    int gravity = 1;
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;
    int finalScore = 0;
    boolean paused = false;
    JLabel resumeLabel = new JLabel("<html><div style='text-align: center;'>Press the Space bar<br>to resume</html>");


    FlyHighDragonFly(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(keyListener);
        addMouseListener(mouseListener);
        setLayout(null);

        //Loading in the images
        background1Img = new ImageIcon("src/img/gamebg.png").getImage();
        dragonImg = new ImageIcon("src/img/dragonfly.png").getImage();
        topPipeImg = new ImageIcon("src/img/toppipefinal.png").getImage();
        bottomPipeImg = new ImageIcon("src/img/bottompipefinal.png").getImage();
        background2Img = new ImageIcon("src/img/background2.png").getImage();
        background3Img = new ImageIcon("src/img/background3.png").getImage();
        gameOverImg = new ImageIcon("src/img/gameover.png").getImage(); // Load game over image

        backgroundImg = background1Img;
        //Handle dragon and pipes
        dragon = new Dragon(dragonImg);
        pipes = new ArrayList<Pipe>();

        //Buttons on top during game play
        buttons();

        //Buttons when game is over
        overButton();

        //Place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() { //this will add pipes every 1.5 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //Game timer
        gameLoop = new Timer(1000/60, this); //we do 60 frames per second so 1000/60
        gameLoop.start();

        //In-game text
        resumeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        resumeLabel.setForeground(Color.WHITE);
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
            backgroundImg = background2Img;
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
            backgroundImg = background1Img;
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
        //This is where we update the background
        if(score >= 12){
            backgroundImg = background3Img;
        }

        if(score >= 5){
            backgroundImg = background2Img;
        }

        draw(g); //draw pipes, background, and score
        if (gameOver) {
            int panelSize = 200;
            int x = (getWidth() - panelSize) / 2;
            int y = (getHeight() - panelSize) / 2;

            // Background
            Image gameOver = gameOverImg;
            g2d.drawImage(gameOver, x-10, y, null);

            // Text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString(String.valueOf((int) score), x + 95,  y + 80);
            finalScore = (int)score;
        }
    }

    public void draw(Graphics g){
        //Background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //Dragon
        g.drawImage(dragonImg, dragon.x, dragon.y, dragon.width, dragon.height, null);

        //Pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i); //get index of pipe
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //Score display during in game
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        if(!gameOver){
            g.drawString(String.valueOf((int) score), 170, 100);
        }
    }

    public void move(){
        //Dragon
        velocityY += gravity;
        dragon.y += velocityY;
        dragon.y = Math.max(dragon.y, 0);

        //pipes
        // Iterate backwards to safely remove elements by index
        for(int i = pipes.size() - 1; i >= 0; i--){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            // Check if the pipe is completely off-screen to the left
            if (pipe.x + pipe.width < 0) {
                pipes.remove(i); // Safe to remove when iterating backwards
                continue; // Skip further processing for this removed pipe
            }

            if(!pipe.passed && dragon.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
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
        return  a.x < b.x + b.width && //Dragon's top left corner doesn't reach pipe's top right corner
                a.x + a.width > b.x && //Dragon's top right corner passes pipe's top left corner
                a.y < b.y + b.height && //Dragon's top left corner doesn't reach pipe's bottom left corner
                a.y + a.height > b.y; //Dragon's bottom left corner passes pipe's top left corner
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


            int panelSize = 200;
            int x = (getWidth() - panelSize) / 2;
            int y = (getHeight() - panelSize) / 2;
            playAgainButton.setBounds(x + 40, y+98, 130, 35);
            addScore.setBounds(x + 40, y+145, 130, 35);
            menuButton.setBounds(x + 40, y+185, 130, 35);
            playAgainButton.setVisible(true);
            menuButton.setVisible(true);
            addScore.setVisible(true);
        }
    }

    KeyAdapter keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                velocityY = -9;
            }
        }
    };

    MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            velocityY = -9;
        }
    };
}
