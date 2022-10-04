package com.monchromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import com.fazecast.jSerialComm.*;


public class HelloController {


    @FXML
    private LineChart result;

    @FXML
    protected void plotDatas() {
        SerialPort comPort = SerialPort.getCommPort("COM3");
        comPort.setComPortParameters(9600, 8, 1, 0);
        //comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        comPort.openPort();

        //Send a request to the Arduino
        comPort.writeBytes("r".getBytes(), 1);
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

        int Y[] = new int[400];
        //initialise Y axis with 0
        for (int i = 0; i < 400; i++) {
            Y[i] = 0;
        }

        byte[] readBuffer = new byte[400];
        int numRead = comPort.readBytes(readBuffer, 400);
        for (int i = 0; i < 400; i++) {
            if (readBuffer[i] >= 0 && readBuffer[i] <= 100) {
                Y[i] = readBuffer[i];
                System.out.println(Y[i]);
            }
        }



        for(int i=400; i<800; i+=1){
            series.getData().add(new XYChart.Data(i, Y[i-400]));
        }

        //add linechar to result
        result.getData().clear();
        result.getData().add(series);
        result.setCreateSymbols(false); //hide dots
        //close serial port
        comPort.closePort();


    }
}
