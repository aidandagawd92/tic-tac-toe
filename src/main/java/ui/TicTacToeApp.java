package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TicTacToeApp extends Application 
{

    private Button[][] buttons = new Button[3][3]; // 3x3 board
    private int[][] board = new int[3][3];         // 0=empty, 1=X, -1=O
    private boolean xTurn = true;                  // X starts

    @Override
    public void start(Stage stage) 
    {
        GridPane grid = new GridPane();
        grid.setHgap(5); // horizontal spacing
        grid.setVgap(5); // vertical spacing

        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                Button btn = new Button(" ");
                btn.setMinSize(120, 120);
                btn.setFont(Font.font(36));
                final int row = i;
                final int col = j;

                btn.setOnAction(e -> handleMove(row, col));

                buttons[i][j] = btn;
                grid.add(btn, j, i); // column=j, row=i
            }
        }

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe");
        stage.show();
    }

    // Handle a button click
    private void handleMove(int row, int col) 
    {
        if (board[row][col] != 0) return; // already taken

        if (xTurn) {
            buttons[row][col].setText("X");
            buttons[row][col].setTextFill(Color.BLUE);
            board[row][col] = 1;
        } else {
            buttons[row][col].setText("O");
            buttons[row][col].setTextFill(Color.RED);
            board[row][col] = -1;
        }

        xTurn = !xTurn; // switch turn

        int winner = checkWinner();
        if (winner != 0) {
            showWinner(winner);
            resetBoard();
        } else if (isTie()) {
            showTie();
            resetBoard();
        }
    }

    // Check for a winner
    private int checkWinner() {
        int[] lines = new int[8]; // 3 rows + 3 cols + 2 diagonals

        // Rows
        for (int i = 0; i < 3; i++) {
            lines[i] = board[i][0] + board[i][1] + board[i][2];
        }
        // Columns
        for (int j = 0; j < 3; j++) {
            lines[3 + j] = board[0][j] + board[1][j] + board[2][j];
        }
        // Diagonals
        lines[6] = board[0][0] + board[1][1] + board[2][2];
        lines[7] = board[0][2] + board[1][1] + board[2][0];

        for (int line : lines) {
            if (line == 3) return 1;  // X wins
            if (line == -3) return -1; // O wins
        }

        return 0; // no winner yet
    }

    // Check if board is full
    private boolean isTie() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == 0) return false;
        return true;
    }

    // Show winner message
    private void showWinner(int winner) {
        String msg = winner == 1 ? "X Wins!" : "O Wins!";
        System.out.println(msg);
    }

    // Show tie message
    private void showTie() {
        System.out.println("It's a tie!");
    }

    // Reset board for new game
    private void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
                buttons[i][j].setText(" ");
            }
        xTurn = true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}