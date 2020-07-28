package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


public class Controller {
    public Pane BG;
        public MenuBar Menu;
            public MenuItem open;
            public MenuItem export;
        public StackPane chartStack;
            public BarChart<String,Double> View;
                public CategoryAxis xAxis;
                public NumberAxis yAxis;
                public LineChart<String, Double> curve;
                    public CategoryAxis xCurve;
                    public NumberAxis yCurve;
        public TextArea text;
        public TextArea text1;
}
