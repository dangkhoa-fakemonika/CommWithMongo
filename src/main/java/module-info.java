module org.example.commwithmongo {
    requires javafx.controls;
    requires javafx.fxml;
    requires mongo.java.driver;
    requires java.desktop;


    opens org.example.commwithmongo to javafx.fxml;
    exports org.example.commwithmongo;
}