package com.monchromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import com.fazecast.jSerialComm.*;

import static java.lang.Integer.parseInt;


public class HelloController {


    @FXML
    private LineChart result;
    //hide result


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

        //reads 400 datas from com3 and store them in Y

        for (int i = 0; i < 400; i++) {
            byte[] readBuffer = new byte[4];
            //convert buffer to int
            int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                System.out.println(readBuffer[0]);
                System.out.println(readBuffer[1]);
                System.out.println(readBuffer[2]);
                System.out.println(readBuffer[3]);

                System.out.println(" ");

            //convert readbuffer to ascii string

            if(readBuffer[0] >=48 && readBuffer[0] <= 57 && readBuffer[1] >=48 && readBuffer[1] <= 57 && readBuffer[2] >=48 && readBuffer[2] <= 57) {
                String str = new String(readBuffer);
                System.out.println(str);
                Y[i] = parseInt(str);
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
