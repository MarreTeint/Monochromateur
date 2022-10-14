module com.example.monochromateur {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fazecast.jSerialComm;

    opens com.monochromateur to javafx.fxml;
    exports com.monochromateur;
}