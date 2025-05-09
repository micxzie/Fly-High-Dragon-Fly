package MainMenu;
import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    int boardWidth = 360;
    int boardHeight = 640;

    public Menu() {
        setSize(boardWidth, boardHeight);
        setFocusable(true);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("First.png");
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, boardWidth, boardHeight);

        //BUTTONS
        Image originalPlay = new ImageIcon("PlayButton.png").getImage();
        Image scaledPlay = originalPlay.getScaledInstance(135, 45, Image.SCALE_SMOOTH);
        ImageIcon playIcon = new ImageIcon(scaledPlay);
        JButton PlayButton = new JButton(playIcon);
        PlayButton.setFocusable(false);
        PlayButton.setBounds(113, 268, 135, 45);

        Image originalScore = new ImageIcon("Scores.png").getImage();
        Image scaledScore = originalScore.getScaledInstance(135, 45, Image.SCALE_SMOOTH);
        ImageIcon ScoreIcon = new ImageIcon(scaledScore);
        JButton ScoreButton = new JButton(ScoreIcon);
        ScoreButton.setFocusable(false);
        ScoreButton.setBounds(113, 342, 135, 45);


        Image originalHow = new ImageIcon("How.png").getImage();
        Image scaledHow = originalHow.getScaledInstance(135, 45, Image.SCALE_SMOOTH);
        ImageIcon HowIcon = new ImageIcon(scaledHow);
        JButton InstructionsButton = new JButton(HowIcon);
        InstructionsButton.setFocusable(false);
        InstructionsButton.setBounds(113, 417, 135, 45);


        Image originalQuit = new ImageIcon("Quit.png").getImage();
        Image scaledQuit = originalQuit.getScaledInstance(135, 45, Image.SCALE_SMOOTH);
        ImageIcon QuitIcon = new ImageIcon(scaledQuit);
        JButton ExitButton = new JButton(QuitIcon);
        ExitButton.setFocusable(false);
        ExitButton.setBounds(113, 492, 135, 45);


        backgroundPanel.add(PlayButton);
        backgroundPanel.add(ScoreButton);
        backgroundPanel.add(InstructionsButton);
        backgroundPanel.add(ExitButton);


        //INSTRUCTION BUTTON
        ImageBackgroundPanel instructionsPanel = new ImageBackgroundPanel("HowToPlay.png");
        instructionsPanel.setLayout(null);
        instructionsPanel.setBounds(0, 0, boardWidth, boardHeight);

        // ig adjust ta nala position han return button based aton image han instructions
        Image originalBack = new ImageIcon("Back.png").getImage();
        Image scaledBack = originalBack.getScaledInstance(80, 26, Image.SCALE_SMOOTH);
        ImageIcon BackIcon = new ImageIcon(scaledBack);
        JButton returnButton = new JButton(BackIcon);
        returnButton.setBounds(250, 528, 75, 25);
        instructionsPanel.add(returnButton);
        instructionsPanel.setVisible(false);


        // BUTTON ACTIONS
        InstructionsButton.addActionListener(e -> {
            backgroundPanel.setVisible(false);
            instructionsPanel.setVisible(true);
        });

        returnButton.addActionListener(e -> {
            instructionsPanel.setVisible(false);
            backgroundPanel.setVisible(true);
        });

        PlayButton.addActionListener(e -> {
            backgroundPanel.setVisible(false);
            FlyHighDragonFly game = new FlyHighDragonFly();
            add(game);
            pack();
            game.requestFocus();
            game.setVisible(true);
        });

        ExitButton.addActionListener(e -> {
            System.exit(0);
        });

        ScoreButton.addActionListener(e -> {
            Leaderboard leaderboard = new Leaderboard();
            leaderboard.ShowLeaderboard();
        });

        //Add Panels to frame
        add(backgroundPanel);
        add(instructionsPanel);

        setVisible(true);
    }
}

