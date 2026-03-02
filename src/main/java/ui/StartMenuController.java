package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import score.Leaderboard;
import score.ScoreEntry;

/**
 * Controller for StartMenuView.fxml (the splash/landing page).
 * Responsibilities:
 *  - Read player names from the UI
 *  - Validate name input (required + must be different)
 *  - Start the match by calling back into TicTacToeApp
 *  - Display the Top 5 leaderboard on the right panel
 */
public class StartMenuController {

    // These fields are "injected" from StartMenuView.fxml using fx:id.
    // (If an fx:id in the FXML doesn't match, it will be null at runtime.)
    @FXML private TextField xNameField;   // Player 1 name input (X)
    @FXML private TextField oNameField;   // Player 2 name input (O)
    @FXML private ListView<String> top5List; // Shows Top 5 leaderboard lines
    @FXML private Label statusLabel;      // Displays validation errors (e.g., missing names)

    // Reference to the main app so we can call methods like startMatchFromMenu(...)
    private TicTacToeApp app;

    // The data backing the ListView; we rebuild this list whenever the leaderboard changes.
    private final ObservableList<String> lines = FXCollections.observableArrayList();

    /**
     * Called by TicTacToeApp after loading the FXML.
     * This "connects" the controller to the app and initializes the Top 5 list view.
     */
    public void setApp(TicTacToeApp app) {
        this.app = app;

        // Connect the ListView to our observable list.
        top5List.setItems(lines);

        // Populate the Top 5 leaderboard on startup.
        refreshTop5();
    }

    /**
     * Handles the Start Game button click (wired via onAction="#onStartGame" in FXML).
     * Validates input, then tells TicTacToeApp to start a match with those names.
     */
    @FXML
    private void onStartGame() {
        statusLabel.setText(""); // clear any previous error

        // Read and trim the names (trim removes leading/trailing spaces)
        String x = xNameField.getText().trim();
        String o = oNameField.getText().trim();

        // Validate: both names must be provided
        if (x.isEmpty() || o.isEmpty()) {
            statusLabel.setText("Enter both names.");
            return;
        }

        // Validate: names must be different (case-insensitive)
        if (x.equalsIgnoreCase(o)) {
            statusLabel.setText("Names must be different.");
            return;
        }

        // If valid, start the match (TicTacToeApp switches to the game scene)
        app.startMatchFromMenu(x, o);
    }

    /**
     * Handles the Exit button click (wired via onAction="#onExit" in FXML).
     * Exits the entire program.
     */
    @FXML
    private void onExit() {
        System.exit(0);
    }

    /**
     * Rebuilds the Top 5 leaderboard display on the splash screen.
     * It reads the sorted leaderboard from the app and formats it into strings.
     */
    public void refreshTop5() {
        lines.clear(); // clear old rows

        // Leaderboard is stored in the app; it already keeps entries ranked by score.
        Leaderboard lb = app.getLeaderboard();

        // Show only the top 5 (or fewer if there aren't enough players yet)
        int shown = Math.min(5, lb.size());

        for (int i = 0; i < shown; i++) {
            ScoreEntry e = lb.get(i);
            lines.add((i + 1) + ") " + e.getName() + " - " + e.getScore());
        }

        // If no scores exist yet, show a placeholder message
        if (lb.size() == 0) {
            lines.add("No scores yet.");
        }
    }
}