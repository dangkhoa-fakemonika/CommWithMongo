package org.example.commwithmongo;

public class StaticMain {
    public static void main(String[] args) {
        Mongo myMongo = new Mongo("hi", null);

//        myMongo.getMessages();
        myMongo.showMessages();
    }
}
