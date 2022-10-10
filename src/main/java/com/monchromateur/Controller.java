package com.monchromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import com.fazecast.jSerialComm.*;

import static java.lang.Integer.parseInt;


public class Controller {


    @FXML
    private LineChart result;
    //hide result


    @FXML
    protected void plotDatas() {
        SerialPort comPort = SerialPort.getCommPort("COM3");
        comPort.setComPortParameters(2000000, 8, 1, 0);
        //comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        comPort.openPort();

        //TODO Send a request to the Arduino to start the acquisition
        //comPort.writeBytes("r".getBytes(), 1);
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Years");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("No.of schools");
        XYChart.Series series = new XYChart.Series();
        series.setName("Absorbance en fonction de la longueur d'onde");

        int Y[] = new int[400];
        for (int i = 0; i < 400; i++) {

            StringBuilder data = new StringBuilder();
            //TODO : read bytes one by one from com3 if 10 => new variable, if 13 => ignore, if 0 => wait until new byte
            byte[] readBuffer = new byte[1];
            //convert buffer to int
            comPort.readBytes(readBuffer, readBuffer.length);
            //System.out.println(readBuffer[0]);

            //System.out.println(" ");
            while (readBuffer[0] != 10 && data.length() < 3 && data.length() >= 0) {
                if (readBuffer[0] == 0) {
                }
                else if(readBuffer[0] >= 48 && readBuffer[0] <= 57){
                    String d = String.valueOf((char) readBuffer[0]);
                    data.append(d);
                }
                comPort.readBytes(readBuffer, readBuffer.length);
            }
                System.out.println(data);
                if (data.length() > 0) {
                    Y[i] = parseInt(data.toString());
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
