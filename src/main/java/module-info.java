module com.example.monchromateur {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.monchromateur to javafx.fxml;
    exports com.example.monchromateur;
}