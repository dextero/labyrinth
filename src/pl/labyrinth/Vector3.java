package pl.labyrinth;

import javax.vecmath.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/30/12
 * Time: 8:20 PM
 */
public class Vector3 extends Vector3f {
    public Vector3() {
        super();
    }

    public Vector3(float uniform) {
        super(uniform, uniform, uniform);
    }

    public Vector3(float x, float y, float z) {
        super(x, y, z);
    }

    public Vector3(float[] xyz) {
        super(xyz);
    }

    public Vector3(Vector3f copy) {
        super(copy);
    }

    public Vector3 normalized() {
        Vector3 ret = new Vector3(this);
        ret.normalize();
        return ret;
    }

    public Vector3 negated() {
        Vector3 ret = new Vector3(this);
        ret.negate();
        return ret;
    }

    public Vector3 addRet(Vector3f b) {
        Vector3 ret = new Vector3(this);
        ret.add(b);
        return ret;
    }

    public Vector3 subRet(Vector3f b) {
        Vector3 ret = new Vector3(this);
        ret.sub(b);
        return ret;
    }

    public Vector3 mulRet(float factor) {
        Vector3 ret = new Vector3(this);
        ret.scale(factor);
        return ret;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }
}
