package MainMenu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Leaderboard {
    public void ShowLeaderboard() {
        List<ScoreEntry> scores = ScoreManager.loadAndSortScores();

        //table columns
        String[] columns = {"Name", "Date", "Score"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (ScoreEntry entry : scores) {
            Object[] row = {entry.getName(), entry.getDate(), entry.getScore()};
            model.addRow(row); // ← make sure this is inside the loop!
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JFrame frame = new JFrame("Saved Scores");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.add(scrollPane);
        frame.setVisible(true);
    }
}
