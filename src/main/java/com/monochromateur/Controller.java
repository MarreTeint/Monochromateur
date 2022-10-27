package com.monochromateur;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;


public class Controller {


    public Button button;
    public ComboBox COMPort;
    public Slider slide;
    public Label slideY;
    public Label slideX;
    public HBox displayData;
    public Rectangle color;
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
                comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 5000, 0);
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
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Erreur lors de l'envoi de la commande de démarrage");
                        alert.setContentText("Vérifiez que le port " + port + " est bien connecté au monochromateur");
                        alert.showAndWait();
                    });
                    return;
                }



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
                        if (readBuffer[0] >= 48 && readBuffer[0] <= 57) {
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
                            progress.setProgress((double) finalI / 401);
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
            alert.setTitle("Choisir un port COM");
            alert.setHeaderText("Pas de port COM sélectionné");
            alert.setContentText("Veuillez sélectionner un port COM et réessayer");
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
                int[] rgb = new int[3];
                rgb = wavelengthToRGB((int)slide.getValue());
                color.setFill(javafx.scene.paint.Color.rgb(rgb[0],rgb[1],rgb[2]));
            });
        }).start();
    }

    protected int[] wavelengthToRGB(int wave){
        double r,g,b,s=1;

        if(wave<=380){
            r=80;
            g=0;
            b=81;
        } else if (wave<440) {
            r=(440-(double) wave)*255/(440-380);;
            g=0;
            b=255;
            if (wave<420) {
                s=0.3 + 0.7 * (wave - 380) / (440 - 380);
            }
        } else if (wave<490) {
            r=0;
            g=((double) wave-440)*255/(490-440);
            b=255;
        } else if (wave<510) {
            r=0;
            g=255;
            b=(510-(double) wave)*255/(510-490);
        } else if (wave<580) {
            r=((double) wave-510)*255/(580-510);
            g=255;
            b=0;
        } else if (wave<645) {
            r=255;
            g=(645-(double) wave)*255/(645-580);
            b=0;
        } else if (wave<=780) {
            r=255;
            g=0;
            b=0;
            if(wave>700) {
                s=0.3 + 0.7 * (780 - wave) / (780 - 700);
            }
        } else {
            r=77;
            g=0;
            b=0;
        }
        return new int[]{(int) (r*s),(int) (g*s),(int) (b*s)};
    }

    @FXML
    protected TimerTask listComPorts(){
        //clear list
        COMPort.getItems().clear();
        List<String> ports = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            ports.add(port.getPortDescription()+" - "+port.getSystemPortName());
        }
        COMPort.getItems().addAll(ports);
        return null;
    }

    @FXML
    protected void initialize() {
        //TODO Check com port available everytime in order to avoid errors
        listComPorts();
        COMPort.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            port = newValue.toString().split(" - ")[1];
            System.out.println(port);
        });
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                listComPorts();
            }
        };
        new Timer().scheduleAtFixedRate(task, 1, 5000);
    }
}


