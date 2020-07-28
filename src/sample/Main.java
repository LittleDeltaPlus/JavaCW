package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class Main extends Application {
    stats telemetry = new stats();
    Controller controller;

    private class openListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            File input = showSingleFileChooser();
            if(input != null){
                //if a file is chosen load and display telemetry
                telemetry.LoadFile(input);
                createChart();
                controller.text.setText("{Mean: " + telemetry.getMean() + ", Variance: "+ telemetry.getVarience() +
                                        ", Median: "+telemetry.getMedian() +", StandardDeviation: "+telemetry.getSD()+"}");
                controller.text1.setText("{Alpha: "+telemetry.getAlpha()+", Mu: "+telemetry.getMu()+", Sigma: "+telemetry.getSigma()+"}");
            }
        }
    }
    private class exportListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if(telemetry.isLoaded()){
                //Graph found, so try exporting
                File output = showSingleFileExporter();
                WritableImage image = controller.chartStack.snapshot(new SnapshotParameters(),
                        new WritableImage((int)controller.chartStack.getWidth(), (int)controller.chartStack.getHeight()));
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(output.getAbsolutePath()));
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else {
                //No Graph to export, so error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error: No data");
                alert.setHeaderText("Please open a file");
                alert.setContentText("As no graphs are currently displayed they cannot be saved");
                alert.showAndWait();
            }
        }
    }

    //Create an Open Window
    private File showSingleFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return(fileChooser.showOpenDialog(null));
    }
    //Create a save as window
    private File showSingleFileExporter() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileChooser.setInitialFileName("Graph");
        return(fileChooser.showSaveDialog(null));
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Create the scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Scalable");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        controller = loader.getController();
        //Set Listeners
        controller.open.setOnAction(new openListener());
        controller.export.setOnAction(new exportListener());
        //Make the graph *Satisfying*
        controller.xAxis.setAnimated(false);
        controller.yCurve.setAnimated(false);
        //show the scene
        primaryStage.show();
    }

    private void createChart(){
        //Get Data
        double[] xData = telemetry.getXData(), yData = telemetry.getYData(),xNormal = telemetry.getXNormal() , yNormal = telemetry.getYNormal();
        //Change the graph properties to get it to display nicely (Making thr curve chart invisible except the curve)
        controller.View.setBarGap(0);
        controller.View.setCategoryGap(1.0);
        controller.View.setLegendVisible(false);
        controller.curve.setLegendVisible(false);
        controller.curve.setCreateSymbols(false);
        controller.curve.setAlternativeRowFillVisible(false);
        controller.curve.setAlternativeColumnFillVisible(false);
        controller.curve.setHorizontalGridLinesVisible(false);
        controller.curve.setVerticalGridLinesVisible(false);
        controller.curve.getStylesheets().addAll(getClass().getResource("curveStyle.css").toExternalForm());
        //Create Graph Data
        XYChart.Series<String,Double> dataSeries1 = new XYChart.Series<>();
        dataSeries1.setName("Histogram");
        XYChart.Series<String,Double> dataSeries2 = new XYChart.Series<>();
        dataSeries2.setName("Polynomial");
        for (int i = 0; i < xData.length; i++) {
            dataSeries1.getData().add(new XYChart.Data<>(""+ xData[i], yData[i]));
        }
        for (int i = 0; i < xNormal.length; i++) {
            dataSeries2.getData().add(new XYChart.Data<>(""+ xNormal[i], yNormal[i]));
        }
        //Remove Graph data if it already exists
        if(!controller.curve.getData().isEmpty()){
            controller.curve.getData().removeAll(controller.curve.getData());
        }
        if(!controller.View.getData().isEmpty()){
            controller.View.getData().removeAll(controller.View.getData());
        }
        //Update graph in GUI
        Platform.runLater(()->{
            controller.curve.getData().add(dataSeries2);
            controller.View.getData().add(dataSeries1);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}


