package pl.labyrinth;

import javax.vecmath.*;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/27/12
 * Time: 5:39 PM
 */
public class Matrix4 extends Matrix4f {
    public static final float ERROR_EPSILON = 0.001f;

    public Matrix4() {
        super();
    }

    public Matrix4(float[] values) {
        super(values);
    }

    public Matrix4(double[] values) {
        float[] floats = new float[16];
        for (int i = 0; i < 16; ++i)
            floats[i] = (float)values[i];

        set(floats);
    }

    public Matrix4(Matrix4 src) {
        super(src);
    }

    public Matrix4 getRotation() {
        Matrix4 ret = new Matrix4(this);
        ret.m03 = 0.f;
        ret.m13 = 0.f;
        ret.m23 = 0.f;
        ret.m33 = 1.f;

        float[] scale = new float[] {
                (float)Math.sqrt(m00 * m00 + m01 * m01 + m02 * m02),
                (float)Math.sqrt(m10 * m10 + m11 * m11 + m12 * m12),
                (float)Math.sqrt(m20 * m20 + m21 * m21 + m22 * m22)
        };

        ret.m00 /= scale[0];
        ret.m01 /= scale[0];
        ret.m02 /= scale[0];

        ret.m10 /= scale[1];
        ret.m11 /= scale[1];
        ret.m12 /= scale[1];

        ret.m20 /= scale[2];
        ret.m21 /= scale[2];
        ret.m22 /= scale[2];

        return ret;
    }

    public AffineTransform decompose() {
        float[] copyData = transposed().getData();

        float[] scale = new float[] {
                (float)Math.sqrt(copyData[0] * copyData[0] + copyData[1] * copyData[1] + copyData[2] * copyData[2]),
                (float)Math.sqrt(copyData[4] * copyData[4] + copyData[5] * copyData[5] + copyData[6] * copyData[6]),
                (float)Math.sqrt(copyData[8] * copyData[8] + copyData[9] * copyData[9] + copyData[10] * copyData[10])
        };

        copyData[0] /= scale[0];
        copyData[1] /= scale[0];
        copyData[2] /= scale[0];

        copyData[4] /= scale[1];
        copyData[5] /= scale[1];
        copyData[6] /= scale[1];

        copyData[8] /= scale[2];
        copyData[9] /= scale[2];
        copyData[10] /= scale[2];

        float[] translation = new float[] { copyData[12], copyData[13], copyData[14] };
        copyData[12] = 0.f;
        copyData[13] = 0.f;
        copyData[14] = 0.f;

        float[] axis = new float[] {
                copyData[9] - copyData[6],
                copyData[2] - copyData[8],
                copyData[4] - copyData[1]
        };
        float angle = 0.f;

        float axisLength = (float)Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
        float trace = copyData[0] + copyData[5] + copyData[10] - 1.f;

        if (axisLength > ERROR_EPSILON) {
            axis[0] /= -axisLength;
            axis[1] /= -axisLength;
            axis[2] /= -axisLength;

            angle = (float)Math.atan2(axisLength, trace);
        } else if (trace > 0.f) {
            axis = new float[] { 0.f, 1.f, 0.f };
        } else {
            int max = 0;
            if (copyData[max * 5] < copyData[5]) max = 1;
            if (copyData[max * 5] < copyData[10]) max = 2;

            int i = max;
            int j = (i + 1) % 3;
            int k = (i + 2) % 3;

            axis[i] = (float)Math.sqrt(copyData[i * 5] - copyData[j * 5] - copyData[k * 5] + 1.f) * 0.5f;

            float s = 0.5f / axis[i];
            axis[j] = copyData[j * 4 + i] * s;
            axis[k] = copyData[k * 4 + i] * s;

            angle = (float)Math.PI;
        }

        return new AffineTransform(new Vector3(translation),
                                   new Vector3(axis), angle,
                                   new Vector3(scale));
    }

    public float[] getData() {
        return new float[] {
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        };
    }

    public Vector3 getEulerAngles() {
        Matrix4 rotMat = getRotation();

        if (rotMat.m10 > 0.998f) {
            return new Vector3((float)Math.atan2(rotMat.m02, rotMat.m22),
                               (float)(Math.PI * .5),
                               0.f);
        } else if (rotMat.m10 < -0.998f) {
            return new Vector3((float)Math.atan2(rotMat.m02, rotMat.m22),
                               (float)(-Math.PI * .5),
                               0.f);
        } else {
            return new Vector3((float)Math.atan2(-rotMat.m20, rotMat.m00),
                               (float)Math.asin(rotMat.m10),
                               (float)Math.atan2(-rotMat.m12, rotMat.m11));
        }
    }

    public Matrix4 addRet(Matrix4 b) {
        Matrix4 ret = new Matrix4(this);
        ret.add(b);
        return ret;
    }

    public Matrix4 subRet(Matrix4 b) {
        Matrix4 ret = new Matrix4(this);
        ret.sub(b);
        return ret;
    }

    public Matrix4 mulRet(Matrix4 b) {
        Matrix4 ret = new Matrix4(this);
        ret.mul(b);
        return ret;
    }

    public Matrix4 transposed() {
        Matrix4 ret = new Matrix4(this);
        ret.transpose();
        return ret;
    }

    public Matrix4 inverse() {
        Matrix4 ret = new Matrix4(this);
        ret.invert();
        return ret;
    }

    public static Matrix4 translate(Vector3 pos) {
        Matrix4 ret = Matrix4.identity();
        ret.setTranslation(pos);
        return ret;
    }

    public static Matrix4 rotate(Vector3 axis, float angle) {
        Matrix4 ret = Matrix4.identity();
        ret.setRotation(new AxisAngle4f(axis.normalized(), angle));
        return ret;
    }

    public static Matrix4 rotate(float yaw, float pitch, float roll) {
        float sinX = (float)Math.sin(yaw);
        float cosX = (float)Math.cos(yaw);
        float sinY = (float)Math.sin(pitch);
        float cosY = (float)Math.cos(pitch);
        float sinZ = (float)Math.sin(roll);
        float cosZ = (float)Math.cos(roll);

        return new Matrix4(new float[] {
            cosX * cosY,
            -cosX * sinY * cosZ + sinX * sinZ,
            cosX * sinY * sinZ + sinX * cosZ,
            0.f,

            sinY,
            cosY * cosZ,
            -cosY * sinZ,
            0.f,

            -sinX * cosY,
            sinX * sinY * cosZ + cosX * sinZ,
            -sinX * sinY * sinZ + cosX * cosZ,
            0.f,

            0.f, 0.f, 0.f, 1.f
        });
    }

    public static Matrix4 rotate(Vector3 yawPitchRoll) {
        return rotate(yawPitchRoll.x(), yawPitchRoll.y(), yawPitchRoll.z());
    }

    public static Matrix4 scale(Vector3 factors) {
        Matrix4 ret = Matrix4.identity();
        ret.m00 = factors.x();
        ret.m11 = factors.y();
        ret.m22 = factors.z();
        ret.m33 = 1.f;

        return ret;
    }

    public static Matrix4 identity() {
        return new Matrix4(new float[] {
                1.f, 0.f, 0.f, 0.f,
                0.f, 1.f, 0.f, 0.f,
                0.f, 0.f, 1.f, 0.f,
                0.f, 0.f, 0.f, 1.f
        });
    }

    public static Matrix4 perspective(float fov, float aspectRatio, float near, float far) {
        if (fov <= 0.f || fov > Math.PI)
            throw new RuntimeException(String.format("invalid fov angle: %f. should fit in [0, PI]", fov));

        float hW = (float)Math.tan(fov / 2.f) * near;
        float hH = hW / aspectRatio;

        // odwrocenie osi X
        hW = -hW;

        return new Matrix4(new float[] {
                near / hW, 0.f, 0.f, 0.f,
                0.f, near / hH, 0.f, 0.f,
                0.f, 0.f, -(far + near) / (far - near), -2.f * far * near / (far - near),
                0.f, 0.f, -1.f, 0.f
        }).transposed();
    }

    public static Matrix4 orthographic(float left, float right, float top, float bottom, float near, float far) {
        return new Matrix4(new float[] {
                2.f / (right - left), 0.f, 0.f, -(right + left) / (right - left),
                0.f, 2.f / (top - bottom), 0.f, -(top + bottom) / (top - bottom),
                0.f, 0.f, -2.f / (far - near), -(far + near) / (far - near),
                0.f, 0.f, 0.f, 1.f
        }).transposed();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        float[] data = getData();

        for (int i = 0; i < 4; ++i) {
            builder.append(i == 0 ? "/ " : (i == 3 ? "\\ " : "| "));

            for (int j = 0; j < 4; ++j)
                builder.append(String.format("%8.2f ", data[i * 4 + j]));

            builder.append(i == 0 ? " \\\n" : (i == 3 ? " /\n" : " |\n"));
        }

        return builder.toString();
    }
}
