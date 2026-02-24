package ui;

import javafx.application.Application;   // Base class for JavaFX apps
import javafx.scene.Scene;               // Scene holds the UI content
import javafx.scene.control.Label;       // A simple text control
import javafx.scene.layout.StackPane;    // Simple layout that centers/“stacks” items
import javafx.stage.Stage;               // The window

/*
 * Smoke Test App:
 * This file is ONLY to confirm JavaFX is set up correctly (Maven deps + IDE config).
 * If this window opens, JavaFX is working on our machine.
 */
public class TicTacToeApp extends Application {

    /*
     * JavaFX calls start(...) for us after launch(args).
     * Stage = the window.
     * In here we build the UI, put it in a Scene, and show the window.
     */
    @Override
    public void start(Stage stage) {
        // Main UI container. StackPane layers items, and with one item it just centers it.
        StackPane root = new StackPane(new Label("JavaFX is running ✅"));

        // Scene = the UI tree + window size (width, height)
        Scene scene = new Scene(root, 600, 600);

        // Window title (what you see at the top of the window)
        stage.setTitle("Tic Tac Toe (Smoke Test)");

        // Put the scene inside the window, then show it
        stage.setScene(scene);
        stage.show();
    }

    /*
     * Normal Java entry point.
     * launch(args) starts the JavaFX runtime, then JavaFX calls start(...).
     */
    public static void main(String[] args) {
        launch(args);
    }
}