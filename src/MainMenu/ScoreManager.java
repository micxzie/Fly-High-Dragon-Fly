package MainMenu;
import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String FILE_NAME = "scores.txt";

    public static void saveScores(ScoreEntry entry) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)){
            writer.write(entry.toString() + "\n");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                if (parts.length == 3){
                    scores.add(new ScoreEntry(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return scores;
    }

    public static List<ScoreEntry> loadAndSortScores() {
        List<ScoreEntry> scores = loadScores();
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return scores;
    }

}
