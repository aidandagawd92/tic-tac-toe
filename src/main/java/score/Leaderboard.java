package score;

/**
 * Leaderboard stores and ranks players by TOTAL score.
 * - Each player is a ScoreEntry (name + score).
 * - Scores accumulate over time (ex: +100 points per win).
 *
 * Data-structure notes (DSA):
 * - Uses a fixed-size array (ScoreEntry[]) instead of a database or map.
 * - After updating a player's score, we re-rank the array using an
 *   insertion-style "bubble up" (shift elements) rather than re-sorting
 *   the entire list each time.
 */
public class Leaderboard {

    // Fixed-capacity storage for all players who have ever scored points.
    private final ScoreEntry[] entries;

    // Number of active entries currently stored in the array.
    private int size = 0;

    /**
     * Creates a leaderboard with a maximum number of players.
     * @param capacity maximum number of unique player names we can store
     */
    public Leaderboard(int capacity) {
        entries = new ScoreEntry[capacity];
    }

    /**
     * @return how many players are currently stored
     */
    public int size() {
        return size;
    }

    /**
     * Returns the player at a rank position.
     * Rank 0 is the highest score, rank 1 is second highest, etc.
     */
    public ScoreEntry get(int index) {
        return entries[index];
    }

    /**
     * Convenience method for the assignment rule:
     * - A win is worth +100 points.
     */
    public void addWin(String playerName) {
        addPoints(playerName, 100);
    }

    /**
     * Adds points to a player’s total score.
     * If the player doesn't exist yet, create a new ScoreEntry for them.
     * Then "bubble" that entry up to keep the leaderboard sorted.
     */
    public void addPoints(String playerName, int points) {
        // Find existing player in the array
        int idx = indexOf(playerName);

        // If not found, create a new entry (if capacity allows)
        if (idx == -1) {
            if (size == entries.length) return; // no room left
            entries[size] = new ScoreEntry(playerName);
            idx = size;
            size++;
        }

        // Update the player's total score
        entries[idx].addPoints(points);

        // ---- Re-rank step (insertion-style shift) ----
        // After score change, the player may need to move up in rank.
        ScoreEntry updated = entries[idx];
        int i = idx;

        // While updated outranks the entry above it, shift the above entry down.
        while (i > 0 && compare(updated, entries[i - 1]) > 0) {
            entries[i] = entries[i - 1]; // shift down to make room
            i--;
        }

        // Place updated entry into its new correct position.
        entries[i] = updated;
    }

    /**
     * Linear search to find a player by name (case-insensitive).
     * @return index if found, or -1 if not found
     */
    private int indexOf(String name) {
        for (int i = 0; i < size; i++) {
            if (entries[i].getName().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    /**
     * Compares two ScoreEntry objects for ranking.
     * @return >0 if 'a' should rank higher than 'b'
     *
     * Ranking rules:
     * 1) Higher score ranks higher
     * 2) If tied, alphabetical order as a tie-breaker
     */
    private int compare(ScoreEntry a, ScoreEntry b) {
        if (a.getScore() != b.getScore()) {
            return Integer.compare(a.getScore(), b.getScore());
        }

        // Tie-breaker: alphabetical (A before B)
        // The "* -1" flips it so normal alphabetical is treated as "higher rank"
        return b.getName().compareToIgnoreCase(a.getName()) * -1;
    }
}