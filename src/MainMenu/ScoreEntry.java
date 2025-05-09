package MainMenu;

public class ScoreEntry {
    private String name;
    private String date;
    private int score;

    public ScoreEntry(String name, String date, int score) {
        this.name = name;
        this.date = date;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return name + "," + date + "," + score;
    }
}
