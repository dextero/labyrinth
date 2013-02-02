package pl.labyrinth;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/28/12
 * Time: 12:32 PM
 */
public class AffineTransform {
    private Vector3 translation;
    private Vector3 rotationAxis;
    private float rotationAngle;
    private Vector3 scale;

    public AffineTransform(Vector3 translation, Vector3 rotationAxis, float rotationAngle, Vector3 scale) {
        this.translation = (Vector3)translation.clone();
        this.rotationAxis = (Vector3)rotationAxis.normalized();
        this.rotationAngle = rotationAngle;
        this.scale = (Vector3)scale.clone();
    }

    public static AffineTransform translate(Vector3 translation) {
        return new AffineTransform(translation, new Vector3(0.f, 1.f, 0.f), 0.f, new Vector3(1.f));
    }

    public static AffineTransform rotate(Vector3 axis, float angle) {
        return new AffineTransform(new Vector3(0.f), axis, angle, new Vector3(1.f));
    }

    public static AffineTransform scale(Vector3 scale) {
        return new AffineTransform(new Vector3(0.f), new Vector3(0.f, 1.f, 0.f), 0.f, scale);
    }

    public Vector3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3 translation) {
        this.translation = translation;
    }

    public Vector3 getRotationAxis() {
        return rotationAxis;
    }

    public void setRotationAxis(Vector3 rotationAxis) {
        this.rotationAxis = rotationAxis.normalized();
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public Vector3 getEulerAngles() {
        Vector3 yawPitchRoll = new Vector3();

        double sin = Math.sin(rotationAngle);
        double cos = Math.cos(rotationAngle);

        double t = 1.0 - cos;

        if (rotationAxis.x() * rotationAxis.y() * t + rotationAxis.z() * sin > 0.998) {
            yawPitchRoll.setX((float)(2. * Math.atan2(rotationAxis.x() * Math.sin(rotationAngle * .5),
                                                       Math.cos(rotationAngle * .5))));
            yawPitchRoll.setY((float)(Math.PI * .5));
            yawPitchRoll.setZ(0.f);
        } else if (rotationAxis.x() * rotationAxis.y() * t + rotationAxis.z() * sin < -0.998) {
            yawPitchRoll.setX((float)(-2. * Math.atan2(rotationAxis.x() * Math.sin(rotationAngle * .5),
                    Math.cos(rotationAngle * .5))));
            yawPitchRoll.setY((float) (-Math.PI * .5));
            yawPitchRoll.setZ(0.f);
        } else {
            yawPitchRoll.setX((float)(Math.atan2(rotationAxis.y() * sin - rotationAxis.x() * rotationAxis.z() * t,
                                                 1. - (rotationAxis.y() * rotationAxis.y() + rotationAxis.z() * rotationAxis.z()) * t)));
            yawPitchRoll.setY((float) (Math.asin(rotationAxis.x() * rotationAxis.y() * t + rotationAxis.z() * sin)));
            yawPitchRoll.setZ((float)(Math.atan2(rotationAxis.x() * sin - rotationAxis.y() * rotationAxis.z() * t,
                                                 1. - (rotationAxis.x() * rotationAxis.x() + rotationAxis.z() * rotationAxis.z()) * t)));
        }

        return yawPitchRoll;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    public Matrix4 toMatrix() {
        return Matrix4.translate(translation).mulRet(
                   Matrix4.rotate(rotationAxis, rotationAngle).mulRet(
                       Matrix4.scale(scale)));

//        return Matrix4.scale(scale).mulRet(
//                   Matrix4.rotate(rotationAxis, rotationAngle).mulRet(
//                       Matrix4.translate(translation)));
    }

    @Override
    public String toString() {
        return String.format("translation: (%f, %f, %f)\nrotation: (%f, %f, %f), angle: %f\nscale: (%f, %f %f)\n",
                             translation.x(), translation.y(), translation.z(),
                             rotationAxis.x(), rotationAxis.y(), rotationAxis.z(), rotationAngle,
                             scale.x(), scale.y(), scale.z());
    }
}
