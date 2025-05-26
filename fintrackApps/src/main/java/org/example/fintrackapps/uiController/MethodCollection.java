package org.example.fintrackapps.uiController;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class MethodCollection {

    public boolean confirmationAlert(String messege){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null); // or set a custom header
        alert.setContentText(messege);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    public boolean isValid(String str){
        int alphaCounter = 0;
        int numberCounter = 0;
        int symbolCounter = 0;


        for(Character i:str.toCharArray()){
            if (isAlpha(i)){
                alphaCounter++;
            }
            if (isNum(i.toString())){
                numberCounter++;
            }
            if (!isAlpha(i) && !isNum(i.toString())){
                symbolCounter++;
            }
        }
        if (alphaCounter < 6 && numberCounter < 1 && symbolCounter < 1){
            return false;
        }
        return true;
    }

    public boolean isAlpha(Character chr) {
        if (chr == null) {
            return false;
        }
        if (Character.isLetter(chr)){
            return true;
        }
        return false;
    }

    public boolean isNum(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str); // or Integer.parseInt(str) if you want only integers
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getNowDateTime(){
        LocalDate currentDate = LocalDate.now();
        // Current time
        LocalTime currentTime = LocalTime.now();
        // Current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Formatted date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = currentDateTime.format(formatter);
        return formatted;
    }

    public String getNowDate(){
        LocalDate currentDate = LocalDate.now();
        // Current time
        LocalTime currentTime = LocalTime.now();
        // Current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Formatted date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formatted = currentDateTime.format(formatter);
        return formatted;
    }

    Boolean isThereAnyLetter(String str){
        for(Character i : str.toCharArray()){
            if (isAlpha(i)){
                return true;
            }
        }
        return false;
    }

    String idrFormat(Double amount){
        double number = amount;

        // Use Indonesian locale
        Locale indonesia = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getNumberInstance(indonesia);
        nf.setMinimumFractionDigits(2); // Show 2 decimal places
        nf.setMaximumFractionDigits(2);

        String formatted = nf.format(number);
        return formatted;
    }

    public String toCssColor(String hex) {
        Color fxColor = Color.web(hex);  // or from ColorPicker.getValue()
        int r = (int) Math.round(fxColor.getRed() * 255);
        int g = (int) Math.round(fxColor.getGreen() * 255);
        int b = (int) Math.round(fxColor.getBlue() * 255);
        double a = fxColor.getOpacity();

        String cssColor = String.format("rgba(%d, %d, %d, %.2f)", r, g, b, a);
        return cssColor;
    }

    public Double[] cssRgbaToDouble(String rgba){
        MethodCollection method = new MethodCollection();
        String[] splittedRgba = rgba.split(",");
        System.out.println(Arrays.toString(splittedRgba));

        for (int i = 0; i < splittedRgba.length;i++){
            char[] data = splittedRgba[i].toCharArray();
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < data.length; j++){
                if(method.isNum(String.valueOf(data[j]))){
                    temp.append(data[j]);
                }
            }
            splittedRgba[i] = temp.toString();
        }
        splittedRgba[splittedRgba.length-2] = splittedRgba[splittedRgba.length-2].toString() + "." + splittedRgba[splittedRgba.length-1].toString();
//        splittedRgba.
        Double[] result = new Double[4];
        for (int i = 0; i < 4; i++){
            result[i] = Double.parseDouble(splittedRgba[i]);
        }
        System.out.println(Arrays.toString(result));
        return result;
    }

    public Rectangle createRectangle(Double width, Double Height, String rgba){
        Rectangle rectangle = new Rectangle(width, Height); // width, height
        rectangle.setArcWidth(5.0);
        rectangle.setArcHeight(5.0);

        Double[] fill = cssRgbaToDouble(rgba);
        System.out.println(Arrays.toString(fill));
        rectangle.setFill(Color.rgb(fill[0].intValue(),fill[1].intValue(),fill[2].intValue(),fill[3]));

        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        return rectangle;
    }

    public String getMonth(String date){
        LocalDate localdate = strToLocalDate(date);
        String month = localdate.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH);
        return month;
    }

    public LocalDate strToLocalDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localdate = LocalDate.parse(date, formatter);
        return localdate;
    }

    public String exportNodeToPdf(VBox node, String fileName) {
        try {
            WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            Image image = Image.getInstance(byteArrayOutputStream.toByteArray());
            image.scaleToFit(500, 700); // optional scaling
            document.add(image);
            document.close();

            System.out.println("PDF exported to: " + fileName);
            return "PDF exported to: " + fileName;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: "+ex.getMessage();
        }
    }

    public String choosePdfSaveLocation(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF As");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf")
        );
        fileChooser.setInitialFileName("export.pdf");

        File file = fileChooser.showSaveDialog(stage);
        return file != null ? file.getAbsolutePath() : null;
    }

    public String getMonthAndYear(String date){
        String[] splittedDate = date.split("-");
        String month = getMonth(date);
        String year = splittedDate[0];
        return month+"-"+year;
    }


}
