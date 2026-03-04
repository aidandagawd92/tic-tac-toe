package ui;

import java.util.Objects;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import score.Leaderboard;
import score.ScoreEntry;

/**
 * Main JavaFX app.
 * Focus (requirements + DSA):
 *  - Requirement #1: players must enter names before playing
 *  - Requirement #2: win = +100 points
 *  - Requirement #3: show Top 5 users
 *
 * DSA highlight:
 *  - Leaderboard uses an array of ScoreEntry and maintains ranking using insertion-style shifting
 *    (the "insert" step of insertion sort) whenever scores change.
 */
public class TicTacToeApp extends Application {

    // -----------------------------
    // A) Game board state (UI + model)
    // -----------------------------

    private final Button[][] buttons = new Button[3][3]; // VIEW: clickable UI cells
    private final int[][] board = new int[3][3];         // MODEL: X=1, O=-1, empty=0
    private boolean xTurn = true;                        // whose turn it is (X starts)

    // -----------------------------
    // B) Requirements state
    // -----------------------------

    private boolean gameStarted = false; // gate: blocks moves until names are validated (Req #1)
    private String playerXName = "";     // used as the "key" in Leaderboard for Player X
    private String playerOName = "";     // used as the "key" in Leaderboard for Player O

    private int xWins = 0; // wins for this match session only (not total points)
    private int oWins = 0;

    // -----------------------------
    // C) DSA: Leaderboard
    // -----------------------------

    private final Leaderboard leaderboard = new Leaderboard(100); // array-backed leaderboard (DSA)
    public Leaderboard getLeaderboard() { return leaderboard; }   // used by StartMenuController

    // -----------------------------
    // D) UI fields shared across scenes
    // -----------------------------

    private final TextField xNameField = new TextField(); // top bar input (main game scene)
    private final TextField oNameField = new TextField();

    // ListView is driven by an ObservableList so UI updates when the list changes
    private final ObservableList<String> scoreboardLines = FXCollections.observableArrayList();
    private final ListView<String> scoreboardView = new ListView<>(scoreboardLines);

    private Stage primaryStage; // needed for scene switching + making popups modal to the main window

    // -----------------------------
    // App entry: show splash first
    // -----------------------------
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;            // store reference so other methods can switch scenes
        stage.setTitle("Tic Tac Toe");
        showStartMenu();                      // start screen first (Req #1: name entry)
        stage.show();                         // shows whatever scene is currently set on the stage
    }

    // Loads splash screen from FXML and connects controller -> this app instance
    private void showStartMenu() {
        try {
            refreshScoreboard(); // ensures Top 5 is current when splash opens (Req #3)

            // FXML loading can fail if the file path is wrong or the FXML has errors
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StartMenuView.fxml"));
            Parent root = loader.load(); // parse FXML + build the UI node tree

            StartMenuController controller = loader.getController(); // controller created by FXMLLoader
            controller.setApp(this); // lets splash controller call back into this app

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/styles/theme.css")
            ).toExternalForm());
            primaryStage.setScene(scene);
        } catch (Exception e) {
            // Why try/catch? FXML loading throws checked exceptions (IO / parse errors).
            // Printing stack trace is enough for debugging in a class project.
            e.printStackTrace();
        }
    }

    /**
     * Called by StartMenuController AFTER it validates names.
     * This bridges Splash -> Game Scene using the same validation logic in startGame().
     */
    public void startMatchFromMenu(String xName, String oName) {
        xNameField.setText(xName); // copy splash names into main window fields
        oNameField.setText(oName);

        showGameScene();           // build and show the board + scoreboard scene
        startGame();               // reuse validation + set gameStarted true + enable board
    }

    // Switches the stage to the main game UI
    private void showGameScene() {
        Parent gameRoot = buildGameRoot();
        Scene scene = new Scene(gameRoot);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/styles/theme.css")
        ).toExternalForm());
        primaryStage.setScene(scene);
    }
    /**
     * Builds the main game screen layout:
     *  - Top bar: name entry + Start Game + Main Menu
     *  - Center: 3x3 board
     *  - Right: Top 5 leaderboard panel
     */
    private Parent buildGameRoot() {

        GridPane grid = new GridPane(); // 3x3 layout container
        grid.getStyleClass().add("board");
        grid.setHgap(5);                // spacing between cells
        grid.setVgap(5);

        // Create 9 buttons and connect each to handleMove(row, col)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button(" ");      // blank label initially
                btn.getStyleClass().add("cell");
                btn.setMinSize(120, 120);          // cell size
                btn.setFont(Font.font(36));        // large X/O text

                final int row = i;                 // captured for lambda
                final int col = j;

                btn.setOnAction(e -> handleMove(row, col)); // click -> attempt move

                buttons[i][j] = btn;               // store in VIEW array
                grid.add(btn, j, i);               // GridPane uses (col, row)
            }
        }

        setBoardEnabled(false); // disables clicks until names validated (Req #1)

        Label xLabel = new Label("Player 1 (X):");
        Label oLabel = new Label("Player 2 (O):");

        xNameField.setPromptText("Enter name for X");
        oNameField.setPromptText("Enter name for O");

        Button startBtn = new Button("Start Game");
        startBtn.getStyleClass().addAll("btn", "btn-primary");
        startBtn.setOnAction(e -> startGame()); // validates names + enables board

        Button menuBtn = new Button("Main Menu");
        menuBtn.getStyleClass().add("btn");
        menuBtn.setOnAction(e -> {
            gameStarted = false;  // re-lock the board until new names are entered
            resetBoard();         // clear the board before going back
            showStartMenu();      // return to splash screen
        });

        HBox topBar = new HBox(10, xLabel, xNameField, oLabel, oNameField, startBtn, menuBtn);
        topBar.getStyleClass().add("topbar");
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label sbTitle = new Label("Scoreboard (Top 5)");
        sbTitle.getStyleClass().add("scoreboard-title");
        //sbTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        scoreboardView.setPrefWidth(240);
        scoreboardView.getStyleClass().add("scoreboard");

        scoreboardView.setFixedCellSize(50);
        scoreboardView.setPrefHeight(scoreboardView.getFixedCellSize() * 5 + 2);
        scoreboardView.setMaxHeight(scoreboardView.getPrefHeight());

        VBox.setVgrow(scoreboardView, javafx.scene.layout.Priority.NEVER);
        refreshScoreboard(); // UI reads from DSA Leaderboard and formats top 5

        VBox rightPane = new VBox(10, sbTitle, scoreboardView);
        rightPane.setPrefWidth(260);
        rightPane.setMaxWidth(260);
        rightPane.setAlignment(Pos.TOP_LEFT);
        rightPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("game");
        root.setTop(topBar);
        root.setCenter(grid);
        root.setRight(rightPane);

        return root;
    }

    // -----------------------------
    // Requirement #1: Start Game validation
    // -----------------------------
    private void startGame() {
        String x = xNameField.getText().trim(); // trim avoids "  Aidan  " being treated differently
        String o = oNameField.getText().trim();

        if (x.isEmpty() || o.isEmpty()) { // names required
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Names");
            alert.setHeaderText("Enter both player names before starting.");
            alert.showAndWait();
            return;
        }

        if (x.equalsIgnoreCase(o)) { // must be different names (case-insensitive)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Names");
            alert.setHeaderText("Names must be different.");
            alert.showAndWait();
            return;
        }

        playerXName = x; // store names for scoring + popup display
        playerOName = o;

        xWins = 0; // reset match wins only; total points persist in Leaderboard
        oWins = 0;

        gameStarted = true;       // unlocks handleMove() processing
        setBoardEnabled(true);    // enable clicking the board buttons
        resetBoard();             // start from a clean board state
    }

    private void setBoardEnabled(boolean enabled) {
        // Small UI helper: enable/disable all 9 cells at once
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(!enabled); // JavaFX uses "disable" (true means not clickable)
            }
        }
    }

    // -----------------------------
    // Gameplay + scoring hooks
    // -----------------------------
    private void handleMove(int row, int col) {
        if (!gameStarted) return;        // Req #1: ignore clicks until Start Game
        if (board[row][col] != 0) return; // no overwriting existing moves

        if (xTurn) {
            buttons[row][col].setText("X");          // update UI
            buttons[row][col].setTextFill(Color.BLUE);
            board[row][col] = 1;                     // update MODEL
        } else {
            buttons[row][col].setText("O");          // update UI
            buttons[row][col].setTextFill(Color.RED);
            board[row][col] = -1;                    // update MODEL
        }

        xTurn = !xTurn; // swap turns after a valid move

        int winner = checkWinner(); // determine if round ended
        if (winner != 0) {
            showWinner(winner);     // Req #2: award points + popup
            resetBoard();           // prepare for next round
        } else if (isTie()) {
            showTie();              // popup for tie
            resetBoard();
        }
    }

    // -----------------------------
    // Requirement #2: win = +100 points
    // -----------------------------
    private void showWinner(int winner) {
        // Winner is 1 for X or -1 for O (based on the board MODEL values)
        if (winner == 1) {
            xWins++;                             // match win counter
            leaderboard.addWin(playerXName);     // +100 points (DSA structure update)
            refreshScoreboard();                 // Req #3: update Top 5 display
            showGameOverPopup(playerXName + " (X) Wins!");
        } else {
            oWins++;
            leaderboard.addWin(playerOName);     // +100 points
            refreshScoreboard();
            showGameOverPopup(playerOName + " (O) Wins!");
        }
    }

    private void showTie() {
        showGameOverPopup("It's a tie!"); // no points awarded on tie in our rules
    }

    private int getScoreFor(String name) {
        // DSA note: linear search through the array-backed Leaderboard (O(n))
        // This is acceptable because n is small (top 5 / classroom project size)
        for (int i = 0; i < leaderboard.size(); i++) {
            ScoreEntry e = leaderboard.get(i);
            if (e.getName().equalsIgnoreCase(name)) return e.getScore();
        }
        return 0; // player not found => 0 points
    }

    // -----------------------------
    // Popup window (shows player score + wins)
    // -----------------------------
    private void showGameOverPopup(String resultText) {
        try {
            // Try/catch because FXML loading can throw exceptions:
            // - missing resource path, parse error, controller mismatch, etc.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverView.fxml"));
            Parent root = loader.load();

            GameOverController controller = loader.getController();

            // Pull totals from Leaderboard (DSA data structure)
            int xScore = getScoreFor(playerXName);
            int oScore = getScoreFor(playerOName);

            // Pass computed values into the popup controller so it can update labels
            controller.setResults(resultText,
                    playerXName, xScore, xWins,
                    playerOName, oScore, oWins);

            // Modal Stage: blocks interaction with main window until popup closes
            Stage popup = new Stage();
            popup.setTitle("Game Over");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/styles/theme.css")
            ).toExternalForm());
            popup.setScene(scene);
            
            popup.initOwner(primaryStage);                 // ties popup to main window
            popup.initModality(Modality.APPLICATION_MODAL); // makes popup "modal"
            popup.setResizable(false);

            popup.showAndWait(); // waits here until user clicks Play Again or Exit
        } catch (Exception e) {
            e.printStackTrace(); // debug output if FXML path/controller is wrong
        }
    }

    // -----------------------------
    // Requirement #3: Top 5 display
    // -----------------------------
    private void refreshScoreboard() {
        scoreboardLines.clear(); // clear current UI rows

        // Because Leaderboard stays sorted (DSA insertion-style ranking),
        // Top 5 is simply the first 5 entries in the array.
        int shown = Math.min(5, leaderboard.size());
        for (int i = 0; i < shown; i++) {
            ScoreEntry e = leaderboard.get(i); // already in rank order
            scoreboardLines.add((i + 1) + ") " + e.getName() + " - " + e.getScore());
        }

        if (leaderboard.size() == 0) {
            scoreboardLines.add("No scores yet.");
        }
    }

    // -----------------------------
    // Winner logic + reset (board mechanics)
    // -----------------------------
    private int checkWinner() {
        // Uses sums of rows/cols/diagonals: X=1, O=-1; sum 3 => X wins, -3 => O wins.
        int[] lines = new int[8]; // 3 rows + 3 cols + 2 diagonals

        for (int i = 0; i < 3; i++) {
            lines[i] = board[i][0] + board[i][1] + board[i][2];
        }
        for (int j = 0; j < 3; j++) {
            lines[3 + j] = board[0][j] + board[1][j] + board[2][j];
        }

        lines[6] = board[0][0] + board[1][1] + board[2][2];
        lines[7] = board[0][2] + board[1][1] + board[2][0];

        for (int line : lines) {
            if (line == 3) return 1;
            if (line == -3) return -1;
        }
        return 0;
    }

    private boolean isTie() {
        // Tie occurs when there are no empty cells left
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == 0) return false;
        return true;
    }

    private void resetBoard() {
        // Clear MODEL + UI so the next round starts fresh
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
                buttons[i][j].setText(" ");
                buttons[i][j].setTextFill(Color.BLACK); // default color reset
            }
        xTurn = true; // reset to X starting each new round
    }

    

    public static void main(String[] args) {
        launch(args); // JavaFX entry point
    }
}