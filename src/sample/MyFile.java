package sample;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

public class MyFile {

    private final SimpleStringProperty fileName;

    public MyFile(File file){
        fileName = new SimpleStringProperty((String)file.getName());
    }

    public String getFileName() {
        return fileName.toString();
    }
}
