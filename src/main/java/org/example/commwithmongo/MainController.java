package org.example.commwithmongo;

import com.mongodb.Block;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.bson.Document;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainController implements Initializable {

    static public Mongo myMongo;

    @FXML
    private TextArea myTextArea;

    @FXML
    private ListView<Label> messages;

    public void updateMessageBuffer(ActionEvent actionEvent){
//        if (actionEvent.getEventType())
    }

    public void addMessage(ActionEvent actionEvent){
        String text = myTextArea.getText().trim();
        if (!text.isEmpty()){
//            Label newLabel = new Label();
//            newLabel.setText("admin: " + myTextArea.getText().trim());
//            messages.getItems().add(newLabel);
            myMongo.pushNewMessage(text);
            myTextArea.setText("");
        }
    }

    public void addMessage(){
        String text = myTextArea.getText().trim();
        if (!text.isEmpty()){
//            Label newLabel = new Label();
//            newLabel.setText("admin: " + myTextArea.getText().trim());
//            messages.getItems().add(newLabel);
            myMongo.pushNewMessage(text);
            myTextArea.setText("");
        }
    }

    public void pushMessage(String t){
        Label newLabel = new Label();
        newLabel.setText(t);
        messages.getItems().add(newLabel);
    }

//    public static void processPushMongo(ChangeStreamDocument<Document> changeDoc){
//        Document thisDoc = changeDoc.getFullDocument();
//        if (thisDoc != null){
//            pushMessage(thisDoc.get("sender").toString() + " : " + thisDoc.get("content"), messages);
//        }
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myMongo = new Mongo("faker", messages);
        myTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    if (keyEvent.isShiftDown()){
                        myTextArea.appendText("\n");
                    }
                    else {
                        addMessage();
                    }
                }
            }
        });

        for (String msg : myMongo.loadLocalMessages()){
            pushMessage(msg);
        }


        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ChangeStreamIterable<Document> changeStream = myMongo.messages.watch();
//                changeStream.forEach((Consumer<? super ChangeStreamDocument<Document>>)  Mongo::processPushMongo);
                changeStream.forEach((Consumer<? super ChangeStreamDocument<Document>>) (n) -> Platform.runLater(() -> Mongo.processPushMongo(n, messages)));
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}