package curves;

import javafx.scene.Group;

public class RobotGroup extends Group {


  public RobotGroup() {
    PointAngleGroup targetRobot = new PointAngleGroup("Target", 0, 0, StrictMath.toRadians(30));
    PointAngleGroup actualRobot = new PointAngleGroup("Actual", targetRobot.offset(50));
    actualRobot.angleProperty().setValue(StrictMath.toRadians(90));
    ErrorGroup errorGroup = new ErrorGroup(targetRobot, actualRobot);

    getChildren().addAll(errorGroup, targetRobot, actualRobot);
  }
}
