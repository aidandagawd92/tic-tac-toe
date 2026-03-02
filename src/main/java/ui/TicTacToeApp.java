package ui;

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
// Main JavaFX app: shows splash screen, runs TicTacToe match, and updates Top-5 leaderboard.
public class TicTacToeApp extends Application {

    // -----------------------------
    // A) Game board state
    // -----------------------------

    //button[][] = UI buttons the user clicks (VIEW)
    //board[][] = game state values used for win/tie checks (model)
    private Button[][] buttons = new Button[3][3];
    private int[][] board = new int[3][3];   // 0 empty, 1 X, -1 O
    private boolean xTurn = true;

    // -----------------------------
    // B) Professor requirements
    // -----------------------------//gameStarted blocks moves until names are entered (assignment required)
    private boolean gameStarted = false;
    private String playerXName = "";
    private String playerOName = "";

    private int xWins = 0;   // wins for THIS match (current X player)
    private int oWins = 0;   // wins for THIS match (current O player)
    //Leaderboard tracks total scores across all users; each win adds 100+ points
    // Scoreboard across ALL users (Top 5)
    private final Leaderboard leaderboard = new Leaderboard(100);
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    // -----------------------------
    // C) UI: name inputs + scoreboard list
    // -----------------------------
    private final TextField xNameField = new TextField();
    private final TextField oNameField = new TextField();

    private final ObservableList<String> scoreboardLines = FXCollections.observableArrayList();
    private final ListView<String> scoreboardView = new ListView<>(scoreboardLines);

    // Needed for popup ownership
    private Stage primaryStage;
    //App launches into splash menu first; board scene is created only after start game.
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Tic Tac Toe");
        showStartMenu();   // loads StarMenuView.fxml and connects its controller to this app instance.
        stage.show();
    }

    private void showStartMenu() {
        try {
            // make sure Top 5 list is up to date before showing menu
            refreshScoreboard();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StartMenuView.fxml"));
            Parent root = loader.load();

            StartMenuController controller = loader.getController();
            controller.setApp(this); // your StartMenuController must have setApp(TicTacToeApp)

            primaryStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called by StartMenuController when user clicks "Start Game"
    public void startMatchFromMenu(String xName, String oName) {
        // preload the existing game screen name fields
        xNameField.setText(xName);
        oNameField.setText(oName);

        showGameScene();   // switch to the board scene
        startGame();       // reuse your existing validation + setup logic
    }
    //switches Stage to the board + scoreboard scene.
    private void showGameScene() {
        Parent gameRoot = buildGameRoot();
        primaryStage.setScene(new Scene(gameRoot));
    }

    //Builds and returns the main game screen (top bar + 3x3 board + Top-5 panel)
    private Parent buildGameRoot() {
        GridPane grid = new GridPane(); //GridPane is the 3x3 layout container for the board
        grid.setHgap(5);
        grid.setVgap(5);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button(" ");
                btn.setMinSize(120, 120);
                btn.setFont(Font.font(36));
                //capture row/col for the lambda so each button knows its position
                final int row = i;
                final int col = j;
                //clicking a cell calls handleMove(row, col).
                btn.setOnAction(e -> handleMove(row, col));

                buttons[i][j] = btn;
                grid.add(btn, j, i);
            }
        }

        // board starts disabled until Start Game is pressed, prevents play until Start Game validates names.
        setBoardEnabled(false);

        Label xLabel = new Label("Player 1 (X):");
        Label oLabel = new Label("Player 2 (O):");

        xNameField.setPromptText("Enter name for X");
        oNameField.setPromptText("Enter name for O");

        Button startBtn = new Button("Start Game");
        startBtn.setOnAction(e -> startGame());
        //Main menu resets match state and returns to splash screen
        Button menuBtn = new Button("Main Menu");
        menuBtn.setOnAction(e -> {
            gameStarted = false;
            resetBoard();
            showStartMenu();
        });

        HBox topBar = new HBox(10, xLabel, xNameField, oLabel, oNameField, startBtn, menuBtn);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        //Right panel shows Top 5 users by total score.
        Label sbTitle = new Label("Scoreboard (Top 5)");
        sbTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        scoreboardView.setPrefWidth(240);
        refreshScoreboard();

        VBox rightPane = new VBox(10, sbTitle, scoreboardView);
        rightPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(grid);
        root.setRight(rightPane);

        return root;
    }
    // -----------------------------
    // D) Start Game button logic (Requirement #1)
    // -----------------------------
    //Validates names, sets match players, resets wins, enables board, and clears baord.
    private void startGame() {
        String x = xNameField.getText().trim();
        String o = oNameField.getText().trim();

        if (x.isEmpty() || o.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Names");
            alert.setHeaderText("Enter both player names before starting.");
            alert.showAndWait();
            return;
        }

        if (x.equalsIgnoreCase(o)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Names");
            alert.setHeaderText("Names must be different.");
            alert.showAndWait();
            return;
        }

        // Set current match players
        playerXName = x;
        playerOName = o;

        // Reset match wins (scores stay in leaderboard across users)
        xWins = 0;
        oWins = 0;

        gameStarted = true;
        setBoardEnabled(true);
        resetBoard();
    }
    // Enables/disables all 9 board buttons at once.
    private void setBoardEnabled(boolean enabled) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(!enabled);
            }
        }
    }

    // -----------------------------
    // E) Handle a move (block if game not started)
    // -----------------------------
    // Move flow: 1) ignore invalid moves, 2) place X/O, 3) check win/tie, 4) show game over 5) reset board
    private void handleMove(int row, int col) {
        if (!gameStarted) return;
        if (board[row][col] != 0) return;

        if (xTurn) {
            buttons[row][col].setText("X");
            buttons[row][col].setTextFill(Color.BLUE);
            board[row][col] = 1;
        } else {
            buttons[row][col].setText("O");
            buttons[row][col].setTextFill(Color.RED);
            board[row][col] = -1;
        }

        xTurn = !xTurn;

        int winner = checkWinner();
        if (winner != 0) {
            showWinner(winner);
            resetBoard();
        } else if (isTie()) {
            showTie();
            resetBoard();
        }
    }

    // -----------------------------
    // F) Winner / tie (Requirement #2: win = +100)
    // -----------------------------
    //Winner gets +100 points and Top-5 scoreboard refreshes.
    private void showWinner(int winner) {
        if (winner == 1) { // X
            xWins++;
            leaderboard.addWin(playerXName); // +100 points
            refreshScoreboard();

            showGameOverPopup(playerXName + " (X) Wins!");
        } else { // O
            oWins++;
            leaderboard.addWin(playerOName); // +100 points
            refreshScoreboard();

            showGameOverPopup(playerOName + " (O) Wins!");
        }
    }

    private void showTie() {
        showGameOverPopup("It's a tie!");
    }

    // Pull total score for a name from the leaderboard
    private int getScoreFor(String name) {
        for (int i = 0; i < leaderboard.size(); i++) {
            ScoreEntry e = leaderboard.get(i);
            if (e.getName().equalsIgnoreCase(name)) return e.getScore();
        }
        return 0;
    }

    // -----------------------------
    // G) Popup window (shows player score + wins)
    // -----------------------------

    //Loads GameOverView.fxml, passes results into controller, then shows modal window.
    private void showGameOverPopup(String resultText) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverView.fxml"));
            Parent root = loader.load();

            GameOverController controller = loader.getController();

            int xScore = getScoreFor(playerXName);
            int oScore = getScoreFor(playerOName);

            controller.setResults(resultText,
                    playerXName, xScore, xWins,
                    playerOName, oScore, oWins);

            Stage popup = new Stage();
            popup.setTitle("Game Over");
            popup.setScene(new Scene(root));
            popup.initOwner(primaryStage);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);

            popup.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------------
    // H) Scoreboard Top 5 (Requirement #3)
    // -----------------------------
    //Rebuilds the Top-5 list from the leaderboard's sorted entries.
    private void refreshScoreboard() {
        scoreboardLines.clear();

        int shown = Math.min(5, leaderboard.size());
        for (int i = 0; i < shown; i++) {
            ScoreEntry e = leaderboard.get(i);
            scoreboardLines.add((i + 1) + ") " + e.getName() + " - " + e.getScore());
        }

        if (leaderboard.size() == 0) {
            scoreboardLines.add("No scores yet.");
        }
    }

    // -----------------------------
    // I) Winner logic + reset (your original)
    // -----------------------------
    //Uses sums of rows/cols/diagonals: x=1, O=-1; sum 3=> X wins, -3 => O wins.
    private int checkWinner() {
        int[] lines = new int[8];

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
    //Tie occurs when no empty cells remain
    private boolean isTie() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == 0) return false;
        return true;
    }
    //Clears board[][] and resets button labels/colors for next round.
    private void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
                buttons[i][j].setText(" ");
                buttons[i][j].setTextFill(Color.BLACK);
            }
        xTurn = true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}