package curves;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.waltonrobotics.controller.PathData;
import org.waltonrobotics.controller.Pose;

public class PointAngleGroup extends PointGroup {

  private final SimpleDoubleProperty translatedX = new SimpleDoubleProperty(1.0);
  private final SimpleDoubleProperty translatedY = new SimpleDoubleProperty(1.0);
  private final SimpleDoubleProperty translatedAngle = new SimpleDoubleProperty(1.0);
  private final SimpleObjectProperty<PathData> pathDataProperty = new SimpleObjectProperty<>();

  public PathData getPathDataProperty() {
    return pathDataProperty.get();
  }

  public SimpleObjectProperty<PathData> pathDataPropertyProperty() {
    return pathDataProperty;
  }

  public PointAngleGroup(String name, double centerX, double centerY, double angle) {
    super(centerX, centerY);

    setName(name);
    angleProperty().setValue(angle);

    ObjectBinding<PathData> objectBinding = Bindings
        .createObjectBinding(() -> new PathData(getPose(), false), centerXProperty(), centerYProperty(),
            angleProperty());
    pathDataProperty.bind(objectBinding);
  }

  public PointAngleGroup(String name, Pose pose) {
    this(name, pose.getX(), pose.getY(), pose.getAngle());
  }

  public PointAngleGroup(String name, double centerX, double centerY) {
    this(name, centerX, centerY, 0);
  }

  public PointAngleGroup(String name) {
    this(name, 0, 0, 0);
  }

  public static List<Pose> mapToPoses(Collection<? extends PointAngleGroup> points) {
    return points.stream().map(PointAngleGroup::getPose).collect(Collectors.toList());
  }

  public static List<Pose> mapToRealPoses(Collection<? extends PointAngleGroup> keyPoints) {
    return keyPoints.stream().map(PointAngleGroup::getRealPose).collect(Collectors.toList());
  }

  public SimpleDoubleProperty translatedXProperty() {
    return translatedX;
  }

  public SimpleDoubleProperty translatedYProperty() {
    return translatedY;
  }


  public SimpleDoubleProperty translatedAngleProperty() {
    return translatedAngle;
  }

  private Pose getRealPose() {
    return new Pose(translatedX.get(), translatedY.get(),
        -StrictMath.toRadians(translatedAngle.get()));
  }


  public Pose getPose() {
    return new Pose(getPositionPoint().getCenterX(), getPositionPoint().getCenterY(), angleProperty().get());
  }

  public Pose offset(double distance) {

    Pose pose = getPose();
    pose = new Pose(StrictMath.cos(-pose.getAngle()) * distance + pose.getX(),
        StrictMath.sin(-pose.getAngle()) * distance + pose.getY(), pose.getAngle());
    return pose;
  }
}
