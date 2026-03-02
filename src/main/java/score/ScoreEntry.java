package score;

/**
 * ScoreEntry represents ONE player's record on the leaderboard.
 * It stores:
 *  - the player's name (never changes)
 *  - the player's total score (accumulates over time)
 *
 * In this project, score is updated by Leaderboard when a player wins
 * (ex: +100 points per win).
 */
public class ScoreEntry {

    // Player identifier (kept final because it should not change once created)
    private final String name;

    // Player's running total score (ex: wins * 100)
    private int score;

    /**
     * Creates a new player record starting at 0 points.
     * @param name the player's display name (from the Start Menu)
     */
    public ScoreEntry(String name) {
        this.name = name;
        this.score = 0;
    }

    /** @return the player's name */
    public String getName() {
        return name;
    }

    /** @return the player's current total score */
    public int getScore() {
        return score;
    }

    /**
     * Adds points to the player's total score.
     * This is called by Leaderboard.addPoints / Leaderboard.addWin.
     * @param points points to add (example: 100 for a win)
     */
    public void addPoints(int points) {
        this.score += points;
    }
}