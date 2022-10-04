package com.example.monchromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;


public class HelloController {


    @FXML
    private LineChart result;

    @FXML
    protected void plotDatas() {
        //plot data in result
        //Defining X axis
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Years");


        //Defining y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("No.of schools");

        LineChart linechart = new LineChart(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        series.setName("Absorbance en fonction de la longueur d'onde");

        //TODO: get datas from arduino via serial port
        for(int i=400; i<=800; i+=1){
            series.getData().add(new XYChart.Data(i, Math.random()*100));
        }

        //add linechar to result
        result.getData().clear();
        result.getData().add(series);


    }
}
