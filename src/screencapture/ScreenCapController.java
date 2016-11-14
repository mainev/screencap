/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screencapture;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jxl.Workbook;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

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

    public File selectedFileDirectory = null;
    public ObjectProperty<Boolean> startCapture = new SimpleObjectProperty<Boolean>(false);
    private Timer timer;
    private ScreenCapController controller;

   // public static final WritableFont CONSOLAS = create("Consolas", 9, "ocean blue", true, false, 0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.controller = this;
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
//                    timer.scheduleAtFixedRate(new java.util.TimerTask() {
//                        @Override
//                        public void run() {
//                            String path = selectedDirectory.getAbsolutePath() + "\\screencap_" + getCurrentTimeStamp() + ".png";
//                            Graphics2D imageGraphics = null;
//                            try {
//                                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//                                BufferedImage capture = new Robot().createScreenCapture(screenRect);
//                                /*
//                                Robot robot = new Robot();
//                                GraphicsDevice currentDevice = MouseInfo.getPointerInfo()
//                                        .getDevice();
//                                BufferedImage exportImage = robot.createScreenCapture(currentDevice
//                                        .getDefaultConfiguration().getBounds());
//
//                                imageGraphics = (Graphics2D) exportImage.getGraphics();*/
//                                File outputFile = new File(path);
//                                outputFile.getParentFile().mkdirs();
//                                ImageIO.write(capture, "png", outputFile);
//                                /*ImageIO.write(exportImage, "png", outputFile);*/
//                                
//                            } catch (Exception ex) {
//                            }
//                            
//                        }
//                    }, count * 1000, count * 1000);

                    timer.schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Toolkit.getDefaultToolkit().beep();
                            System.out.println("timer run");
                            TrayController tController = new TrayController(controller);

                        }
                    }, count * 1000);

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

    @FXML
    private void btnChangeDirectoryOnClick(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) mainStage.getScene().getWindow();
        selectedFileDirectory = directoryChooser.showDialog(stage);
        if (selectedFileDirectory == null) {
            directory.setText("");
        } else {
            directory.setText(selectedFileDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void btnChooseFileOnClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) mainStage.getScene().getWindow();
        fileChooser.setTitle("Open existing file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xls")
        );
        selectedFileDirectory = fileChooser.showOpenDialog(stage);
        if (selectedFileDirectory == null) {
            directory.setText("");
        } else {
            directory.setText(selectedFileDirectory.getAbsolutePath());
        }

    }

    @FXML
    private void createDefaultFormat(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) mainStage.getScene().getWindow();
        selectedFileDirectory = directoryChooser.showDialog(stage);
        if (selectedFileDirectory == null) {
            lblMessage.setText("");
        } else {
            try {
                lblMessage.setText("New file created!");
                WritableWorkbook wworkbook;
                String sheet_path = selectedFileDirectory.getAbsolutePath() + "\\screencap_" + getCurrentTimeStamp() + ".xls";

                wworkbook = Workbook.createWorkbook(new File(sheet_path));
                WritableSheet firstSheet = wworkbook.createSheet("Update Report", 0);
                WritableSheet wsheetAM = wworkbook.createSheet("7AM - 6PM", 1);
                WritableSheet wsheetPM = wworkbook.createSheet("7PM - 6AM", 2);

                int x = 0;
                int y = 0;
                String columnLabel ;
                for (x = 0; x <= 132; x++) {
                    switch (x) {
                        case 0:
                            columnLabel = "07 AM";
                            break;
                        case 11:
                            columnLabel = "08 AM";
                            break;
                        case 22:
                            columnLabel = "09 AM";
                            break;
                        case 33:
                            columnLabel = "10 AM";
                            break;
                        case 44:
                            columnLabel = "11 AM";
                            break;
                        case 55:
                            columnLabel = "12 PM";
                            break;
                        case 66:
                            columnLabel = "01 PM";
                            break;
                        case 77:
                            columnLabel = "02 PM";
                            break;
                        case 88:
                            columnLabel = "03 PM";
                            break;
                        case 99:
                            columnLabel = "04 PM";
                            break;
                        case 110:
                            columnLabel = "05 PM";
                            break;
                        case 121:
                            columnLabel = "06 PM";
                            break;
                        case 132:
                            columnLabel = "07 PM";
                            break;
                        default:
                            columnLabel = "END COLUMN";
                            break;
                    }
                    jxl.write.Label label = new jxl.write.Label(x, y, columnLabel);
                    wsheetAM.addCell(label);
                    x += 10;

                }

                for (x = 0; x <= 132; x++) {
                    switch (x) {
                        case 0:
                            columnLabel = "07 PM";
                            break;
                        case 11:
                            columnLabel = "08 PM";
                            break;
                        case 22:
                            columnLabel = "09 PM";
                            break;
                        case 33:
                            columnLabel = "10 PM";
                            break;
                        case 44:
                            columnLabel = "11 PM";
                            break;
                        case 55:
                            columnLabel = "12 AM";
                            break;
                        case 66:
                            columnLabel = "01 AM";
                            break;
                        case 77:
                            columnLabel = "02 AM";
                            break;
                        case 88:
                            columnLabel = "03 AM";
                            break;
                        case 99:
                            columnLabel = "04 AM";
                            break;
                        case 110:
                            columnLabel = "05 AM";
                            break;
                        case 121:
                            columnLabel = "06 AM";
                            break;
                        case 132:
                            columnLabel = "07 AM";
                            break;
                        default:
                            columnLabel = "END COLUMN";
                            break;
                    }
                    jxl.write.Label label = new jxl.write.Label(x, y, columnLabel);
                    wsheetPM.addCell(label);
                    x += 10;

                }

                wworkbook.write();
                wworkbook.close();

            } catch (IOException | WriteException ex) {
                Logger.getLogger(ScreenCapController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

}
