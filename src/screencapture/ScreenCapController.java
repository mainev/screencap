/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screencapture;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

/**
 *
 * @author maine
 */
public class ScreenCapController implements Initializable {

    @FXML
    private AnchorPane mainStage;
    @FXML
    private Label lblMessage;

    @FXML
    private Button btnStartCapture;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnChangeDirectory;
    

    @FXML
    private TextField directory;
    @FXML
    private TextField countdown;
    
    @FXML
    private Hyperlink aboutLink;

    private File selectedDirectory = null;
    private ObjectProperty<Boolean> startCapture = new SimpleObjectProperty<Boolean>(false);
    private Timer timer;

    @FXML
    private void btnStartCaptureOnClick(ActionEvent event) {
        startCapture.setValue(Boolean.TRUE);
    }

    @FXML
    public void StopScreenCap(ActionEvent event) {
        this.startCapture.setValue(Boolean.FALSE);
    }

    @FXML
    public void aboutClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("About.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
          //  stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("About");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    @FXML
    private void btnChangeDirectoryOnClick(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) mainStage.getScene().getWindow();
        selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory == null) {
            directory.setText("No Directory selected");
        } else {
            directory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnStartCapture.setDisable(true);
        btnStop.setDisable(true);
        directory.setDisable(true);
        countdown.setText("5");
        startCapture.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue value, Object oldValue, Object newValue) {
                boolean startCapture = (boolean) newValue;

                if (startCapture) {
                    lblMessage.setText("ScreenCap started!");
                    btnStartCapture.setDisable(true);
                    btnChangeDirectory.setDisable(true);
                    countdown.setDisable(true);
                    btnStop.setDisable(false);
                    int count = Integer.parseInt(countdown.getText());
                    timer = new java.util.Timer();
                    timer.scheduleAtFixedRate(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            String path = selectedDirectory.getAbsolutePath() + "\\screencap_" + getCurrentTimeStamp() + ".png";

                            try {
                                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                                BufferedImage capture = new Robot().createScreenCapture(screenRect);

                                File outputFile = new File(path);
                                outputFile.getParentFile().mkdirs();
                                ImageIO.write(capture, "png", outputFile);

                            } catch (Exception ex) {
                            }

                        }
                    }, count * 1000, count * 1000);

                } else {
                    System.out.println("ScreenCap stopped.");
                    lblMessage.setText("ScreenCap stopped!");
                    btnStartCapture.setDisable(false);
                    btnChangeDirectory.setDisable(false);
                    countdown.setDisable(false);
                    btnStop.setDisable(true);
                    timer.cancel();
                    timer.purge();
                }
            }
        });

        directory.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (t1.equals("")) {
                    btnStartCapture.setDisable(true);

                } else {
                    btnStartCapture.setDisable(false);

                }
            }
        });

        countdown.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (t1.equals("") || !t1.matches("\\d*")) {
                    btnStartCapture.setDisable(true);
                } else {
                    btnStartCapture.setDisable(false);
                }
            }
        });
    }

}
