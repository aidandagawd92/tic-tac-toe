package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for GameOverView.fxml (the "Game Over" popup window).
 * Responsibilities:
 *  - Receive the match results from TicTacToeApp (winner/tie, scores, wins)
 *  - Display those results in the popup labels
 *  - Handle popup button clicks (Play Again closes popup, Exit quits app)
 */
public class GameOverController {

    // These UI components are injected from GameOverView.fxml via fx:id.
    @FXML private Label resultLabel;   // Top label showing "X Wins!", "O Wins!", or "It's a tie!"
    @FXML private Label xStatsLabel;   // Label showing Player 1 (X) score + wins
    @FXML private Label oStatsLabel;   // Label showing Player 2 (O) score + wins

    @FXML private Button playAgainButton; // Closes the popup and returns to the game window
    @FXML private Button exitButton;      // Exits the entire application

    /**
     * Called by TicTacToeApp right before the popup is shown.
     * This method fills in the text for all labels in the popup.
     *
     * @param resultText text like "Aidan (X) Wins!" or "It's a tie!"
     * @param xName      Player 1 name (X)
     * @param xScore     Player 1 total score (leaderboard points)
     * @param xWins      Player 1 wins in the current match session
     * @param oName      Player 2 name (O)
     * @param oScore     Player 2 total score (leaderboard points)
     * @param oWins      Player 2 wins in the current match session
     */
    public void setResults(String resultText,
                           String xName, int xScore, int xWins,
                           String oName, int oScore, int oWins) {

        // Winner/tie headline
        resultLabel.setText(resultText);

        // Per-player stats (total points + wins this match)
        xStatsLabel.setText(xName + " (X): " + xScore + " points | Wins: " + xWins);
        oStatsLabel.setText(oName + " (O): " + oScore + " points | Wins: " + oWins);
    }

    /**
     * Handles "Play Again" button click.
     * Closes only the popup window; the main game window stays open.
     */
    @FXML
    private void onPlayAgain() {
        Stage stage = (Stage) playAgainButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles "Exit" button click.
     * Quits the entire program.
     */
    @FXML
    private void onExit() {
        System.exit(0);
    }
}