package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainController {
    @FXML
    public TextField filePathTextField;
    @FXML
    private Button browseButton;
    private File selectedFile;

    @FXML
    private void browseButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) browseButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void submitButtonClicked() {
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Select a file");
            alert.show();
        } else {
            String input = "";
            try {
                Scanner scanner = new Scanner(selectedFile);
                while (scanner.hasNext()) {
                    input += scanner.next() +" ";
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            String[] inputArray = input.split("\\s+|(?<=[<>();,:.=%+-])|(?=[<>();,:.=%+-])");
            ArrayList<String> cleaningInputArray = new ArrayList<>();

            for (String s : inputArray) {
                String cleanedToken = s.trim();
                if (!cleanedToken.isEmpty()) {
                    cleaningInputArray.add(cleanedToken);
                }
            }

            RecursiveDescentParser recursiveDescentParser = new RecursiveDescentParser(cleaningInputArray);
            recursiveDescentParser.parse();
        }
    }
}

