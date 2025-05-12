package MainMenu;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;

public class FlyHighDragonFly extends JPanel implements ActionListener{
    int boardWidth = 360;
    int boardHeight = 640;

    // Variables for initializing the images
    Image background1Img;
    Image background2Img;
    Image background3Img;
    Image dragonImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image gameOverImg;

    // Variables for transitioning the images using alpha composite
    private Image currentDrawingBackground;
    private Image targetTransitionBackground;
    private boolean isTransitioning = false;
    private float transitionAlpha = 0.0f;
    private Timer transitionTimer;
    private static final int TRANSITION_DURATION_MS = 1000; // 1 second fade
    private static final int TIMER_DELAY_MS = 20;          // Approx 50 FPS
    private float alphaIncrement;

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
    int dragonWidth = 70; //size of dragon
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

    // For Pipes
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

    // Game logic variables
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

        // Loading in the images
        background1Img = new ImageIcon("src/img/gamebg.png").getImage();
        dragonImg = new ImageIcon("src/img/SPRITEFINAL.gif").getImage();
        topPipeImg = new ImageIcon("src/img/toppipefinal.png").getImage();
        bottomPipeImg = new ImageIcon("src/img/bottompipefinal.png").getImage();
        background2Img = new ImageIcon("src/img/background2.png").getImage();
        background3Img = new ImageIcon("src/img/background3.png").getImage();
        gameOverImg = new ImageIcon("src/img/gameover.png").getImage(); // Load game over image

        currentDrawingBackground = background1Img;
        // Handle dragon and pipes
        dragon = new Dragon(dragonImg);
        pipes = new ArrayList<>();

        // Buttons on top during game play
        buttons();

        // Buttons when game is over
        overButton();

        // Place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() { //this will add pipes every 1.5 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!paused){
                    placePipes();
                }
            }
        });
        placePipesTimer.start();

        // Game timer
        gameLoop = new Timer(1000/60, this); //we do 60 frames per second so 1000/60

        // In-game text
        resumeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        resumeLabel.setForeground(Color.WHITE);
        resumeLabel.setBounds(65, 240, 240, 50);
        add(resumeLabel);
        resumeLabel.setVisible(false);

        // Set up the transition timer for background changes
        setupTransitionTimer();

        // Start game
        startGameTimers();
    }

    // Helper to start/resume game timers
    private void startGameTimers() {
        if (!paused && !gameOver) {
            placePipesTimer.start();
            gameLoop.start();
        }
    }

    // Helper to stop game timers
    private void stopGameTimers() {
        placePipesTimer.stop();
        gameLoop.stop();
    }

    public void buttons(){
        // Buttons on top
        pauseButton.setBounds(5, 6, 30, 30);
        pauseButton.setFocusable(false);
        add(pauseButton);
        pauseButton.addActionListener(e -> {
            if (!gameOver) {
                paused = true;
                stopGameTimers();
                if (transitionTimer.isRunning()) {
                    transitionTimer.stop();
                }
                resumeLabel.setVisible(true);
            }
        });

        // Resume the game using space bar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && paused) {
                    if (!gameOver) { // Resume game
                        paused = false;
                        startGameTimers();
                        if (isTransitioning) { // Resume transition timer if it was running
                            transitionTimer.start();
                        }
                        resumeLabel.setVisible(false);
                    }
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        retryButton.setBounds(40, 6, 30, 30);
        retryButton.setFocusable(false);
        add(retryButton);
        retryButton.addActionListener(e -> {
            if (transitionTimer.isRunning()) { // Stop any ongoing transition
                transitionTimer.stop();
                isTransitioning = false;
            }
            gameOver = false;
            dragon.y = DragonY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            finalScore = 0;
            currentDrawingBackground = background1Img; // Reset to initial background
            targetTransitionBackground = null;
            checkAndUpdateBackground();

            startGameTimers(); // Restart game timers
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
            if (transitionTimer.isRunning()) { // Stop any ongoing transition
                transitionTimer.stop();
                isTransitioning = false;
            }
            gameOver = false;
            dragon.y = DragonY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            finalScore = 0; // Reset final score
            currentDrawingBackground = background1Img; // Reset to initial background
            targetTransitionBackground = null; // Clear any pending transition target
            checkAndUpdateBackground(); // Ensure correct background is set (should be background1Img)

            startGameTimers(); // Use helper to restart game timers
            playAgainButton.setVisible(false);
            menuButton.setVisible(false);
            addScore.setVisible(false);
            pauseButton.setVisible(true);
            retryButton.setVisible(true);
            menuMiniButton.setVisible(true);
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

        // Study this, how to store data
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

    // This method is for transitioning the background images smoothly
    private void setupTransitionTimer() {
        int steps = TRANSITION_DURATION_MS / TIMER_DELAY_MS;
        if (steps == 0) steps = 1;
        alphaIncrement = 1.0f / steps;

        transitionTimer = new Timer(TIMER_DELAY_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionAlpha += alphaIncrement;
                if (transitionAlpha >= 1.0f) {
                    transitionAlpha = 1.0f;
                    currentDrawingBackground = targetTransitionBackground;
                    isTransitioning = false;
                    targetTransitionBackground = null;
                    transitionTimer.stop();
                }
                repaint(); //Trigger a repaint to draw the next frame of the transition
            }
        });
    }

    public void checkAndUpdateBackground() {
        Image newTargetBg = null;

        if (score >= 12) {
            newTargetBg = background3Img;
        } else if (score >= 5) {
            newTargetBg = background2Img;
        } else {
            newTargetBg = background1Img; // Default background
        }

        // Start a transition if the target background is different
        // AND we are not already transitioning to it
        // AND it's different from the current base drawing background
        if (newTargetBg != null && newTargetBg != currentDrawingBackground &&
                (!isTransitioning || targetTransitionBackground != newTargetBg)) {

            if (transitionTimer.isRunning()) {
                // If a transition is already running to a *different* target,
                // we let currentDrawingBackground be what it is (the base of the current fade)
                // and start a new fade from that to newTargetBg.
                // If it was transitioning to the *same* newTargetBg, this condition wouldn't be met.
                transitionTimer.stop();
            }

            targetTransitionBackground = newTargetBg;
            isTransitioning = true;
            transitionAlpha = 0.0f; // Reset alpha for the new transition
            transitionTimer.start();
        } else if (newTargetBg == currentDrawingBackground && isTransitioning) {
            // This case handles if the score changes back to require the 'currentDrawingBackground'
            // while it was transitioning *away* from it.
            transitionTimer.stop();
            isTransitioning = false;
            targetTransitionBackground = null;
            repaint();
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //This is where we update the background
        // 1. Draw the current (or old) background
        if (currentDrawingBackground != null) {
            g2d.drawImage(currentDrawingBackground, 0, 0, boardWidth, boardHeight, null);
        }

        // 2. If transitioning, draw the new background on top with increasing alpha
        if (isTransitioning && targetTransitionBackground != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transitionAlpha));
            g2d.drawImage(targetTransitionBackground, 0, 0, boardWidth, boardHeight, null);
            // Reset composite to default for any subsequent drawing operations
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        drawGameElements(g2d); //Draw pipes, background, and score
        if (gameOver) {
            int panelSize = 200;
            int x = (getWidth() - panelSize) / 2;
            int y = (getHeight() - panelSize) / 2;

            // Background
            Image gameOver = gameOverImg;
            g2d.drawImage(gameOver, x-10, y, null);

            // Show score when game is over
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString(String.valueOf((int) score), x + 95,  y + 80);
            finalScore = (int)score;
        }
    }

    public void drawGameElements(Graphics g){
        // Dragon
        g.drawImage(dragonImg, dragon.x, dragon.y, dragon.width, dragon.height, this);

        // Pipes
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
        if (paused || gameOver) return; // Don't move if paused or game over

        // Dragon
        velocityY += gravity;
        dragon.y += velocityY;
        dragon.y = Math.max(dragon.y, 0);

        // Pipes, iterate backwards to safely remove elements by index
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
                checkAndUpdateBackground(); // Change background
            }

            if(collision(dragon, pipe)){
                gameOver = true;
                finalScore = (int)score;
            }
        }

        if(dragon.y > boardHeight){
            gameOver = true;
            finalScore = (int)score;

            checkAndUpdateBackground();
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
        if (!paused && !gameOver) {
            move();
        }
        repaint();

        if(gameOver){
            stopGameTimers();
            if (transitionTimer.isRunning()) { // Stop transition if game ends mid-transition
                transitionTimer.stop();
                isTransitioning = false; // Ensure state is clean
            }
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
