package ui;
//import javafx classes here
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//smoke test meant to check if everything is running properly with FX
public class TicTacToeApp extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane(new Label("JavaFX is running✅"));
        Scene scene = new Scene(root, 600, 600); //scene=UI tree, root = what to display, 600, 600 = initial window size

        stage.setTitle("Tic Tac Toe (Smoke Test)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[]args){
        launch(args);
    }
}

