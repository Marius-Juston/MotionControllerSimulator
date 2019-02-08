package curves.figures;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {

  private static final double RX = 50;
  private static final double RY = 50;
  private static final double S_ANGLE = 45;
  private static final double ARC_LENGTH = 45;
  private double xMouse, yMouse;
  private Arc arc;
  private Circle handle;
  private Line connection;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Pane pane = new Pane();

    Group designer = createDesigner();
    designer.setLayoutX(100);
    designer.setLayoutY(200);
    pane.getChildren().add(designer);

    Scene sc = new Scene(pane, 600, 600);
    primaryStage.setScene(sc);
    primaryStage.show();
  }

  private Group createDesigner() {

    arc = new Arc();
    arc.setRadiusX(RX);
    arc.setRadiusY(RY);
    arc.setStartAngle(S_ANGLE);
    arc.setLength(ARC_LENGTH);
    arc.setFill(Color.LIGHTBLUE);
    arc.setType(ArcType.ROUND);

//    handle = new Circle();
//    handle.setRadius(5);
//    handle.setStroke(Color.BLACK);
//    handle.setFill(Color.TRANSPARENT);
//
//    handle.setCenterX(
//        RX * Math.cos(Math.toRadians(S_ANGLE))
//    );
//    handle.setCenterY(
//        -RY * Math.cos(Math.toRadians(S_ANGLE))
//    );

//    connection = new Line();
//    connection.startXProperty().bind(arc.centerXProperty());
//    connection.startYProperty().bind(arc.centerYProperty());
//    connection.endXProperty().bind(handle.centerXProperty());
//    connection.endYProperty().bind(handle.centerYProperty());

    arc.setOnMouseDragged(event -> {

      xMouse = event.getX();
      yMouse = event.getY();

      double ac = Math.abs(xMouse- arc.getCenterX());
      double cb = Math.abs(yMouse- arc.getCenterY());

      double r = Math.hypot(ac, cb);
      arc.setRadiusX(r);
      arc.setRadiusY(r);

//      handle.setCenterX(xMouse);
//      handle.setCenterY(yMouse);

      double angleInRadians = Math.atan2(-yMouse, xMouse);

      arc.setStartAngle(Math.toDegrees(angleInRadians));

    });

    return new Group(arc);

  }

}