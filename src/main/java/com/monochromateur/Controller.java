package com.monochromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;


public class Controller {


    public Button button;
    public ComboBox COMPort;
    public Slider slide;
    public Label slideY;
    public Label slideX;
    public HBox displayData;
    @FXML
    private LineChart result;
    @FXML
    private ProgressIndicator progress;
    private String port;

    private final int[] Y = new int[401];

    @FXML
    protected void plotDatas() {
        if(port!=null) {
            button.setVisible(false);
            progress.setVisible(true);
            new Thread(() -> {
                SerialPort comPort = SerialPort.getCommPort(port);
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
                    System.out.println("Start command sent on " + port);
                } else {

                    System.out.println("Error sending start command");
                    progress.setVisible(false);
                    button.setVisible(true);
                    comPort.closePort();
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error sending start command");
                        alert.setContentText("Please select another COM port and try again");
                        alert.showAndWait();
                    });
                    return;
                }


                NumberAxis xAxis = new NumberAxis();
                xAxis.setLabel("Years");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("No.of schools");
                XYChart.Series series = new XYChart.Series();
                series.setName("Absorbance en fonction de la longueur d'onde");

                //read data from arduino
                //int Y[] = new int[401];
                int maxat = 0;
                for (int i = 0; i < 401; i++) {
                    StringBuilder data = new StringBuilder();
                    byte[] readBuffer = new byte[1];
                    comPort.readBytes(readBuffer, readBuffer.length);
                    while (readBuffer[0] != 10 && data.length() < 3 && data.length() >= 0) {
                        if (readBuffer[0] == 0) {
                        } else if (readBuffer[0] >= 48 && readBuffer[0] <= 57) {
                            String d = String.valueOf((char) readBuffer[0]);
                            data.append(d);
                        }
                        comPort.readBytes(readBuffer, readBuffer.length);
                    }
                    if (data.length() > 0) {
                        Y[i] = parseInt(data.toString());
                        if (Y[i] > Y[maxat]) {
                            maxat = i;
                        }
                        System.out.println(i + " Data received: " + Y[i]);
                        int finalI = i;
                        javafx.application.Platform.runLater(() -> {
                            progress.setProgress((double) finalI / 400);
                        });
                    }
                }

                for (int i = 400; i <= 800; i += 1) {
                    series.getData().add(new XYChart.Data(i, Y[i - 400]));
                }

                //send series to main thread
                int finalMaxat = maxat;
                javafx.application.Platform.runLater(() -> {
                    result.setAnimated(true);
                    result.getData().clear();
                    result.getData().add(series);
                    result.setCreateSymbols(false);
                    progress.setVisible(false);
                    result.setTitle("Résultat");
                    button.setText("Refaire une mesure");
                    button.setVisible(true);
                    progress.setProgress(0);
                    slide.setVisible(true);
                    displayData.setVisible(true);
                    sliderMoved();
                });
                //close serial port
                comPort.closePort();
            }).start();
        }
        else {
            System.out.println("No COM port selected");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Select a COM port");
            alert.setHeaderText("No COM port selected");
            alert.setContentText("Please select a COM port and try again");
            alert.showAndWait();
        }
    }

    @FXML
    protected void sliderMoved() {
        new Thread(() -> {
            XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data((int)slide.getValue(), 0));
            series.getData().add(new XYChart.Data((int)slide.getValue(), 100));
            series.setName("Curseur");
            javafx.application.Platform.runLater(() -> {
                result.setAnimated(false);
                if(result.getData().size()>1) {
                   result.getData().remove(1);
                }
                result.getData().add(series);
                slideX.setText(String.valueOf((int)slide.getValue()));
                slideY.setText(String.valueOf(Y[(int)slide.getValue()-400]));

            });
        }).start();
    }

    @FXML
    protected void initialize() {
        List<String> ports = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            ports.add(port.getSystemPortName());
        }
        COMPort.getItems().addAll(ports);
        COMPort.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            port = newValue.toString();
            System.out.println(port);
        });
    }
}


