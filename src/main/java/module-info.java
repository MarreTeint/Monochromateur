module com.example.monchromateur {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fazecast.jSerialComm;

    opens com.monchromateur to javafx.fxml;
    exports com.monchromateur;
}