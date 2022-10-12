package com.monchromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;

import static java.lang.Integer.parseInt;


public class Controller {


    public Button button;
    @FXML
    private LineChart result;
    @FXML
    private ProgressIndicator progress;

    @FXML
    protected void plotDatas() {
        button.setVisible(false);
        progress.setVisible(true);


        new Thread(()->{
            SerialPort comPort = SerialPort.getCommPort("COM3");
            comPort.setComPortParameters(2000000, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            comPort.openPort();
            //Arduino restart when openPort
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //Starting message to arduino
            byte[] WriteByte = new byte[1];
            WriteByte[0] = 65; //send A
            if (comPort.writeBytes(WriteByte, 1) == 1) {
                System.out.println("Start command sent");
            } else {
                System.out.println("Error sending start command");
            }


            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Years");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("No.of schools");
            XYChart.Series series = new XYChart.Series();
            series.setName("Absorbance en fonction de la longueur d'onde");

            //read data from arduino
            int Y[] = new int[400];
            for (int i = 0; i < 400; i++) {
                StringBuilder data = new StringBuilder();
                byte[] readBuffer = new byte[1];
                comPort.readBytes(readBuffer, readBuffer.length);
                while (readBuffer[0] != 10 && data.length() < 3 && data.length() >= 0) {
                    if (readBuffer[0] == 0) {
                    }
                    else if(readBuffer[0] >= 48 && readBuffer[0] <= 57){
                        String d = String.valueOf((char) readBuffer[0]);
                        data.append(d);
                    }
                    comPort.readBytes(readBuffer, readBuffer.length);
                }
                if (data.length() > 0) {
                    Y[i] = parseInt(data.toString());
                    System.out.println(i+" Data received: " + Y[i]);
                    int finalI = i;
                    javafx.application.Platform.runLater(()->{
                        progress.setProgress((double) finalI /400);
                    });
                }
            }

            for(int i=400; i<800; i+=1){
                series.getData().add(new XYChart.Data(i, Y[i-400]));
            }

            //send series to main thread
            javafx.application.Platform.runLater(()->{
                result.getData().clear();
                result.getData().add(series);
                result.setCreateSymbols(false);
                progress.setVisible(false);
                result.setVisible(true);
                button.setText("Refaire une mesure");
                button.setVisible(true);
                progress.setProgress(0);
            });

            //close serial port
            comPort.closePort();
        }).start();

         //hide dots


    }
}
