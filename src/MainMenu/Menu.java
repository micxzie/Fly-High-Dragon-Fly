package MainMenu;
import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    int boardWidth = 360;
    int boardHeight = 640;

    //Panels for Main and How to Play Background
    ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel("src/img/First.png");
    ImageBackgroundPanel instructionsPanel = new ImageBackgroundPanel("src/img/HowToPlay.png");

    // Play Button
    ImageIcon playIcon = new ImageIcon(new ImageIcon("src/img/PlayButton.png").getImage().getScaledInstance(135, 45, Image.SCALE_SMOOTH));
    JButton PlayButton = new JButton(playIcon);

    // Score Button
    ImageIcon scoreIcon = new ImageIcon(new ImageIcon("src/img/Scores.png").getImage().getScaledInstance(135, 45, Image.SCALE_SMOOTH));
    JButton ScoreButton = new JButton(scoreIcon);

    // How To Play Button
    ImageIcon howIcon = new ImageIcon(new ImageIcon("src/img/How.png").getImage().getScaledInstance(135, 45, Image.SCALE_SMOOTH));
    JButton InstructionsButton = new JButton(howIcon);

    // Exit Button
    ImageIcon quitIcon = new ImageIcon(new ImageIcon("src/img/Quit.png").getImage().getScaledInstance(135, 45, Image.SCALE_SMOOTH));
    JButton ExitButton = new JButton(quitIcon);

    // Back Button
    ImageIcon backIcon = new ImageIcon(new ImageIcon("src/img/Back.png").getImage().getScaledInstance(80, 26, Image.SCALE_SMOOTH));
    JButton returnButton = new JButton(backIcon);


    public Menu() {
        setSize(boardWidth, boardHeight);
        setFocusable(true);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Main Menu Panel
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, boardWidth, boardHeight);
        backgroundPanel.add(PlayButton);
        backgroundPanel.add(ScoreButton);
        backgroundPanel.add(InstructionsButton);
        backgroundPanel.add(ExitButton);

        //How To Play Panel
        instructionsPanel.setLayout(null);
        instructionsPanel.setBounds(0, 0, boardWidth, boardHeight);
        instructionsPanel.add(returnButton);
        instructionsPanel.setVisible(false);

        menuButtons();
        add(backgroundPanel);
        add(instructionsPanel);

        setVisible(true);
    }

    public void menuButtons(){
        PlayButton.setFocusable(false);
        PlayButton.setBounds(113, 268, 135, 45);
        PlayButton.addActionListener(e -> {
            backgroundPanel.setVisible(false);
            FlyHighDragonFly game = new FlyHighDragonFly();
            add(game);
            pack();
            game.requestFocus();
            game.setVisible(true);
        });

        ScoreButton.setFocusable(false);
        ScoreButton.setBounds(113, 342, 135, 45);
        ScoreButton.addActionListener(e -> {
            Leaderboard leaderboard = new Leaderboard();
            leaderboard.ShowLeaderboard();
        });

        InstructionsButton.setFocusable(false);
        InstructionsButton.setBounds(113, 417, 135, 45);
        InstructionsButton.addActionListener(e -> {
            backgroundPanel.setVisible(false);
            instructionsPanel.setVisible(true);
        });


        ExitButton.setFocusable(false);
        ExitButton.setBounds(113, 492, 135, 45);
        ExitButton.addActionListener(e -> {
            System.exit(0);
        });

        returnButton.setBounds(250, 528, 75, 25);
        returnButton.addActionListener(e -> {
            instructionsPanel.setVisible(false);
            backgroundPanel.setVisible(true);
        });
    }
}

