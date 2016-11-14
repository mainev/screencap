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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import screencapture.models.CustomStage;
import screencapture.models.SheetCell;

/**
 *
 * @author maine
 */
public class TrayController {

    @FXML
    private Label lblClose;
    @FXML
    private Button btnContinue;
    @FXML
    private AnchorPane rootNode;
    private CustomStage stage;

    private ScreenCapController parentController;

    public TrayController() {
        initTrayController();
    }

    public TrayController(ScreenCapController controller) {
        this.parentController = controller;
        initTrayController();
    }

    private void initTrayController() {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tray.fxml"));
            fxmlLoader.setController(this);
            try {

                Parent root1 = (Parent) fxmlLoader.load();
                initStage(root1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void initStage(Parent parent) {
        stage = new CustomStage(rootNode, StageStyle.UNDECORATED);
        stage.setScene(new Scene(parent));
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());
        stage.show();
        lblClose.setOnMouseClicked(e -> stageOnClose());
        btnContinue.setOnMouseClicked(e -> stageOnContinue());
    }

    /**
     * Will generate the image of the screenshot.
     *
     * @param event
     */
    @FXML
    public void btnCaptureOnClick(ActionEvent event) {
        if (parentController != null) {
            // String image_path = parentController.selectedFileDirectory.getAbsolutePath() + "\\screencap_" + getCurrentTimeStamp() + ".png";

            stage.hide();
            boolean success = false;
            try {

                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage capture = new Robot().createScreenCapture(screenRect);

                /**
                 * Save image to the specified path.
                 */
//                File outputFile = new File(image_path);
//                outputFile.getParentFile().mkdirs();
//                ImageIO.write(capture, "png", outputFile);
                String file_path = parentController.selectedFileDirectory.getAbsolutePath();
                Workbook workbook = Workbook.getWorkbook(new File(file_path));
                WritableWorkbook workbookCopy = Workbook.createWorkbook(new File(file_path), workbook);

                SheetCell sheetCell = this.getNextSheetCell(workbookCopy);
                WritableSheet wsheet = workbookCopy.getSheet(sheetCell.getSheetNo());

                jxl.write.Label label = new jxl.write.Label(sheetCell.getX(), sheetCell.getY() - 2, "PAGE " + sheetCell.getPage());
                wsheet.addCell(label);

                jxl.write.Label label2 = new jxl.write.Label(sheetCell.getX(), sheetCell.getY(), new SimpleDateFormat("HH:mm").format(new Date()));
                wsheet.addCell(label2);

                jxl.write.WritableImage wi = new jxl.write.WritableImage(sheetCell.getX(), sheetCell.getY(), 9, 15, this.convertImageToByteArray(capture));
                wsheet.addImage(wi);

                workbookCopy.write();
                workbookCopy.close();

//                Workbook workbook = Workbook.getWorkbook(new File("output.xls"));
//                Sheet sheet = workbook.getSheet(0);
//                jxl.Cell cell1 = sheet.getCell(0, 2);
//                System.out.println(cell1.getContents());
//                jxl.Cell cell2 = sheet.getCell(3, 4);
//                System.out.println(cell2.getContents());
//                workbook.close();
                success = true;
            } catch (FileNotFoundException ex) {
                // JOptionPane.showMessageDialog(null, "sample", "InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("File In Use");
                alert.setHeaderText("The action can't be completed because the file is open in another program.");
                alert.setContentText("Close the file and try again.");
                alert.show();
                //ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (success) {
                    String musicFile = "/screencapture/audio/camera-shutter-click-01.mp3";     // For example

                    final URL resource = getClass().getResource(musicFile);
                    final Media media = new Media(resource.toString());
                    final MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.play();
                }

                stage.show();
            }
        }

    }

    public SheetCell getNextSheetCell(WritableWorkbook workbook) throws WriteException {
        SheetCell sc = new SheetCell();

        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date 

        int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        int sheetNoToWrite;
        if (currentHourOfDay >= 7 && currentHourOfDay <= 18) {
            sheetNoToWrite = 1; //sheet 7am to 6pm

        } else {
            sheetNoToWrite = 2; //sheet 6pm to 7am
        }

        sc.setSheetNo(sheetNoToWrite);
        WritableSheet wsheet = workbook.getSheet(sheetNoToWrite);
        /**
         * Scan the worksheet and get the next cell to put the screenshot.
         */
        String currentTime = new SimpleDateFormat("hh aa").format(date);

        int x, y, page;
        for (x = 0; x <= 132; x = x + 11) {
            y = 0;
            jxl.Cell currentCell = wsheet.getCell(x, y);
            if (currentTime.equals(currentCell.getContents())) {
                sc.setX(x);
                y += 4;
                page = 1;
                jxl.Cell cell1 = wsheet.getCell(x, y);
                while (!cell1.getContents().isEmpty()) {
                    y += 18;
                    page++;
                    cell1 = wsheet.getCell(x, y);
                }
                sc.setPage(page);
                sc.setY(y);
                return sc;
            }

        }
        return sc;
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    private void stageOnClose() {
        stage.close();
        this.parentController.startCapture.setValue(Boolean.FALSE);
    }

    private void stageOnContinue() {
        stage.close();
        this.parentController.startCapture.setValue(Boolean.FALSE);
        this.parentController.startCapture.setValue(Boolean.TRUE);
    }

    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;

    }
}
