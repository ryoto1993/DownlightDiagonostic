package ui;

import LightingSystem.SocketClient;
import exp.Diagnosis;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.InetSocketAddress;

public class Controller {
    @FXML
    MenuItem menu_close;
    @FXML
    TextField input_id;
    @FXML
    TextField input_lum_interval;
    @FXML
    TextField input_cct_interval;
    @FXML
    Button button_start;
    @FXML
    ProgressBar progress;
    @FXML
    Label label_progress;
    @FXML
    Label label_current;

    public void initialize() {
        // setup socket client
        SocketClient.setEndpoint(new InetSocketAddress("172.20.11.53", 44344));

        // setup menu item
        initMenu();

        // setup components
        initComponents();
    }

    // add listener on menu items
    private void initMenu() {
        menu_close.setOnAction(e -> System.exit(0));
    }

    // add listener on components
    private void initComponents() {
        button_start.setOnAction(event -> {
            Diagnosis diagnosis = new Diagnosis(
                    Integer.parseInt(input_id.getText()),
                    Double.parseDouble(input_lum_interval.getText()),
                    Double.parseDouble(input_cct_interval.getText()));
            // diagnosis.setProgress(progress);
            diagnosis.setCurrentLabel(label_current);
            diagnosis.run();
        });
    }
}
