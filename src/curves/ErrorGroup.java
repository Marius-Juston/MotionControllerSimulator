package curves;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import org.waltonrobotics.controller.ErrorVector;
import org.waltonrobotics.controller.MotionController;

public class ErrorGroup extends Group {

  private static final Color ANGLE_ERROR_COLOR = Color.ORANGE;
  private static final Line CROSS_TRACK_ERROR = new Line();
  private static final Line LAG_ERROR = new Line();
  private static final Arc ANGLE_ERROR = new Arc();
  private final PointAngleGroup targetRobot;
  private final PointAngleGroup actualRobot;
  private SimpleObjectProperty<ErrorVector> errorVectorProperty = new SimpleObjectProperty<>();
  private SimpleDoubleProperty angleErrorLength = new SimpleDoubleProperty(100);

  ErrorGroup(PointAngleGroup targetRobot, PointAngleGroup actualRobot) {
    this.targetRobot = targetRobot;
    this.actualRobot = actualRobot;

    ObjectBinding<ErrorVector> objectBinding = Bindings.createObjectBinding(
        () -> MotionController.findCurrentError(targetRobot.getPathDataProperty(), actualRobot.getPose()),
        targetRobot.pathDataPropertyProperty(), actualRobot.centerYProperty(), actualRobot.centerXProperty(),
        actualRobot.angleProperty());

    errorVectorProperty.bind(objectBinding);
//    errorVectorProperty.addListener((observable, oldValue, newValue) -> System.out.println(newValue)
//    );

    createAngleError();
    createThing();
  }

  private void createThing() {
    DoubleBinding x = Bindings.createDoubleBinding(() ->
        {
          double aX = actualRobot.centerYProperty().get();
          double tanT = StrictMath.tan(-targetRobot.angleProperty().get());
          double tanA = StrictMath.tan(-actualRobot.angleProperty().get());
          double a = (aX - tanA * actualRobot.centerXProperty().get());
          double b = targetRobot.centerYProperty().get() - tanT * targetRobot.centerXProperty().get();
          return (a - b) / (tanT - tanA);
        }
        , actualRobot.centerYProperty(), targetRobot.centerYProperty(), targetRobot.angleProperty(),
        actualRobot.angleProperty()
    );

    DoubleBinding y = Bindings
        .createDoubleBinding(() -> {
              double tanT = StrictMath.tan(-targetRobot.angleProperty().get());
              double b = targetRobot.centerYProperty().get() - tanT * targetRobot.centerXProperty().get();

              return tanT * x.get() + b;
            }, x,
            targetRobot.angleProperty(), targetRobot.centerYProperty());

    MoveTo target = new MoveTo();
    target.xProperty().bind(targetRobot.centerXProperty());
    target.yProperty().bind(targetRobot.centerYProperty());

    LineTo point = new LineTo();
    point.xProperty().bind(x);
    point.yProperty().bind(y);

    LineTo actual = new LineTo();
    actual.xProperty().bind(actualRobot.centerXProperty());
    actual.yProperty().bind(actualRobot.centerYProperty());

    Path anglePath = new Path(target, point, actual);
//
//    DoubleBinding radius = Bindings
//        .createDoubleBinding(() -> targetRobot.getPose().distance(new Pose(x.get(), y.get())) * .3, x, y,
//            targetRobot.centerYProperty(), targetRobot.centerXProperty(), targetRobot.angleProperty());
//
//    Arc arc = new Arc();
//    arc.radiusXProperty().bind(radius);
//    arc.radiusYProperty().bind(radius);
//
//    arc.centerXProperty().bind(x);
//    arc.centerYProperty().bind(y);
//
//    DoubleBinding angleBinding = Bindings.createDoubleBinding(() -> {
//      if (targetRobot.centerXProperty().get() > actualRobot.centerXProperty().get()) {
//        return StrictMath.toDegrees(targetRobot.angleProperty().get());
//      }
//      return StrictMath.toDegrees(actualRobot.angleProperty().get());
//    }, targetRobot.centerXProperty(), actualRobot.centerXProperty());
//
//    arc.startAngleProperty().bind(angleBinding);
//
//    DoubleBinding doubleBinding = Bindings.createDoubleBinding(
//        () -> StrictMath.toDegrees(targetRobot.angleProperty().subtract(actualRobot.angleProperty()).multiply(-1).get()),
//        targetRobot.angleProperty(), actualRobot.angleProperty());
//    arc.lengthProperty().bind(doubleBinding);
//
//    arc.setFill(Color.TRANSPARENT);
//    arc.setStroke(ANGLE_ERROR_COLOR);
//    arc.setType(ArcType.ROUND);

    getChildren().addAll(anglePath
//        , arc
    );
  }


  private void createAngleError() {
    ANGLE_ERROR.centerXProperty().bind(actualRobot.centerXProperty());
    ANGLE_ERROR.centerYProperty().bind(actualRobot.centerYProperty());

    ANGLE_ERROR.radiusXProperty().bind(angleErrorLength);
    ANGLE_ERROR.radiusYProperty().bind(angleErrorLength);

    DoubleBinding angle = Bindings
        .createDoubleBinding(() -> StrictMath.toDegrees(actualRobot.angleProperty().get()),
            actualRobot.angleProperty());
    ANGLE_ERROR.startAngleProperty().bind(angle);

    DoubleBinding doubleBinding = Bindings
        .createDoubleBinding(() -> StrictMath.toDegrees(errorVectorProperty.get().getAngle()), errorVectorProperty);
    ANGLE_ERROR.lengthProperty().bind(doubleBinding);

    ANGLE_ERROR.setType(ArcType.ROUND);
    ANGLE_ERROR.setFill(Color.TRANSPARENT);
    ANGLE_ERROR.setStroke(ANGLE_ERROR_COLOR);

    Text text = new Text();
    text.setFill(ANGLE_ERROR_COLOR);
    StringBinding stringBinding = Bindings
        .createStringBinding(() -> String.format("%.2f", StrictMath.toDegrees(errorVectorProperty.get().getAngle())),
            errorVectorProperty);
    text.textProperty().bind(stringBinding);

    DoubleBinding textX = Bindings.createDoubleBinding(() -> (
            StrictMath.cos(-actualRobot.angleProperty().get() - errorVectorProperty.get().getAngle() / 2.0)
                * angleErrorLength
                .get() + actualRobot.centerXProperty().get()),
        actualRobot.centerXProperty(), actualRobot.angleProperty(), errorVectorProperty);

    DoubleBinding textY = Bindings.createDoubleBinding(() -> (
            StrictMath.sin(-actualRobot.angleProperty().get() - errorVectorProperty.get().getAngle() / 2.0)
                * angleErrorLength
                .get() + actualRobot.centerYProperty().get()),
        actualRobot.centerYProperty(), actualRobot.angleProperty(), errorVectorProperty);

    text.xProperty().bind(textX);
    text.yProperty().bind(textY);
//    text.xProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

    getChildren().addAll(ANGLE_ERROR, text);
  }

  public ErrorVector getErrorVectorProperty() {
    return errorVectorProperty.get();
  }

  public SimpleObjectProperty<ErrorVector> errorVectorPropertyProperty() {
    return errorVectorProperty;
  }
}
