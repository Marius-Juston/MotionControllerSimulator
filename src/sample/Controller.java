package sample;

import curves.RobotGroup;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public class Controller implements Initializable {

  public Pane robotPlane;


  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Platform.runLater(() -> {
      robotPlane.setTranslateX(robotPlane.getWidth() / 2.0);
      robotPlane.setTranslateY(robotPlane.getHeight() / 2.0);

      RobotGroup robotGroup = new RobotGroup();

      robotPlane.getChildren().addAll(robotGroup);

    });
  }
}
