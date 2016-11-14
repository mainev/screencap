/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screencapture;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

/**
 *
 * @author maine
 */
public class ScreenCapture extends Application {

    private Stage stage;
    private static final String iconImageLoc
            = "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        /**
         * This will add the application to the system tray.
         */
       // Platform.setImplicitExit(false);
      //  javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        Parent root = FXMLLoader.load(getClass().getResource("screencap.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("ScreenCap 1.0.0");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        stage.setScene(scene);
        
        stage.show();

    }

   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
