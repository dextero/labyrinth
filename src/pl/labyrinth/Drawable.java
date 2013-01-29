package pl.labyrinth;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/26/12
 * Time: 7:00 PM
 */
public class Drawable {
    private GL3 gl;

    private int verticesBuffer;
    private int indicesBuffer;
    private int numIndices;
    private int[] offsets;
    private int[] componentsNum;
    private String[] attribNames;

    private Vector3 position = new Vector3(0.f, 0.f, 0.f);
    private Vector3 scale = new Vector3(1.f, 1.f, 1.f);
    private Vector3 rotationAxis = new Vector3(0.f, 1.f, 0.f);
    private float rotationAngle = 0.f;

    private int type;

    public static Drawable createLine(GL3 gl, float[] vertices, float[] color) {
        float[] colors = new float[] {
                color[0], color[1], color[2],
                color[0], color[1], color[2]
        };
        return new Drawable(gl, GL3.GL_LINES,
                            new float[][] { vertices, colors },
                            2,
                            new int[] { 3, 3 },
                            new int[] { 0, 1 },
                            new String[] { "aPosition", "aColor" });
    }

    public static Drawable createTexturedQuad(GL3 gl, float[] vertices, float[] texcoords, String[] attribs) {
        return new Drawable(gl, GL3.GL_TRIANGLE_STRIP,
                            new float[][] { vertices, texcoords },
                            4,
                            new int[] { 3, 2 },
                            new int[] { 0, 1, 2, 3 },
                            attribs);
    }

    public static Drawable createColoredQuad(GL3 gl, float[] vertices, float[] colors, String[] attribs) {
        return new Drawable(gl, GL3.GL_TRIANGLE_STRIP,
                            new float[][] { vertices, colors },
                            4,
                            new int[] { 3, 3 },
                            new int[] { 0, 1, 2, 3 },
                            attribs);
    }

    public static Drawable createMesh(GL3 gl, float[] vertices, int[] indices, float[] colors, String[] attribs) {
        return new Drawable(gl, GL3.GL_TRIANGLE_STRIP,
                            new float[][] { vertices, colors },
                            vertices.length / 3,
                            new int[] { 3, 3 },
                            indices,
                            attribs);
    }

    public static Drawable createCube(GL3 gl, float[] color) {
        float[] colors = new float[3 * 3 * 8];
        for (int i = 0; i < 3 * 8; ++i) {
            colors[i * 3] = color[0];
            colors[i * 3 + 1] = color[1];
            colors[i * 3 + 2] = color[2];
        }

        return new Drawable(gl, GL3.GL_TRIANGLES,
                            new float[][] { {
                                    // 3 normalne na kazdy wierzcholek
                                    -1.f, -1.f, -1.f, -1.f, -1.f, -1.f, -1.f, -1.f, -1.f,
                                    -1.f, -1.f,  1.f, -1.f, -1.f,  1.f, -1.f, -1.f,  1.f,
                                    -1.f,  1.f, -1.f, -1.f,  1.f, -1.f, -1.f,  1.f, -1.f,
                                    -1.f,  1.f,  1.f, -1.f,  1.f,  1.f, -1.f,  1.f,  1.f,
                                     1.f, -1.f, -1.f,  1.f, -1.f, -1.f,  1.f, -1.f, -1.f,
                                     1.f, -1.f,  1.f,  1.f, -1.f,  1.f,  1.f, -1.f,  1.f,
                                     1.f,  1.f, -1.f,  1.f,  1.f, -1.f,  1.f,  1.f, -1.f,
                                     1.f,  1.f,  1.f,  1.f,  1.f,  1.f,  1.f,  1.f,  1.f
                                }, colors, {
                                    -1.f,  0.f,  0.f,  0.f, -1.f,  0.f,  0.f,  0.f, -1.f,
                                    -1.f,  0.f,  0.f,  0.f, -1.f,  0.f,  0.f,  0.f,  1.f,
                                    -1.f,  0.f,  0.f,  0.f,  1.f,  0.f,  0.f,  0.f, -1.f,
                                    -1.f,  0.f,  0.f,  0.f,  1.f,  0.f,  0.f,  0.f,  1.f,
                                     1.f,  0.f,  0.f,  0.f, -1.f,  0.f,  0.f,  0.f, -1.f,
                                     1.f,  0.f,  0.f,  0.f, -1.f,  0.f,  0.f,  0.f,  1.f,
                                     1.f,  0.f,  0.f,  0.f,  1.f,  0.f,  0.f,  0.f, -1.f,
                                     1.f,  0.f,  0.f,  0.f,  1.f,  0.f,  0.f,  0.f,  1.f
                            } },
                            24,
                            new int[] { 3, 3, 3 },
                            new int[] {
                                     0,  3,  6,   3,  9,  6,
                                    12, 18, 15,  15, 18, 21,
                                     4,  1, 16,  16,  1, 13,
                                    19,  7, 22,   7, 10, 22,
                                     5, 17, 11,  11, 17, 23,
                                     8, 20, 14,   2,  8, 14
                            }, new String[] {
                                "aPosition", "aColor", "aNormal"
                            });
    }

    public static Drawable createSphere(GL3 gl, int rings, int vertsPerRing, float[] color) {
        if (rings < 1)
            throw new RuntimeException("invalid rings count");
        if (vertsPerRing < 3)
            throw new RuntimeException("invalid vertices per ring count");

        float[] vertices = new float[(rings * vertsPerRing + 2) * 3];
        vertices[0] = 0.f;
        vertices[1] = -1.f;
        vertices[2] = 0.f;

        float thetaStep = (float)(Math.PI / (double)(rings + 1));
        float phiStep = (float)(Math.PI * 2.0 / (double)vertsPerRing);
        for (int i = 0; i < rings; ++i) {
            float theta = thetaStep * (float)(i + 1) + (float)Math.PI;
            float yPos = (float)Math.cos(theta);
            float sinTheta = (float)Math.sin(theta);

            for (int j = 0; j < vertsPerRing; ++j) {
                float phi = phiStep * (float)j;

                vertices[(i * vertsPerRing + j) * 3 + 3] = sinTheta * (float)Math.cos(phi);
                vertices[(i * vertsPerRing + j) * 3 + 4] = yPos;
                vertices[(i * vertsPerRing + j) * 3 + 5] = sinTheta * (float)Math.sin(phi);
            }
        }

        vertices[vertices.length - 3] = 0.f;
        vertices[vertices.length - 2] = 1.f;
        vertices[vertices.length - 1] = 0.f;

        // normals = vertices
        float[] colors = new float[vertices.length];
        for (int i = 0; i < colors.length; i += 3) {
            colors[i] = color[0];
            colors[i + 1] = color[1];
            colors[i + 2] = color[2];
        }

        int[] indices = new int[rings * vertsPerRing * 2 * 3];
        // bottom
        for (int i = 0; i < vertsPerRing; ++i) {
            indices[i * 3] = 0;
            indices[i * 3 + 1] = i + 1;
            indices[i * 3 + 2] = ((i + 1) % vertsPerRing) + 1;
        }

        // middle
        for (int ring = 0; ring < rings - 1; ++ring) {
            int base = 1 + ring * vertsPerRing;
            int faceIdx = vertsPerRing + ring * vertsPerRing * 2;

            for (int vert = 0; vert < vertsPerRing; ++vert) {
                indices[faceIdx * 3] = base + vert;
                indices[faceIdx * 3 + 1] = base + vertsPerRing + vert;
                indices[faceIdx * 3 + 2] = base + vertsPerRing + (vert + 1) % vertsPerRing;

                indices[faceIdx * 3 + 3] = base + (vert + 1) % vertsPerRing;
                indices[faceIdx * 3 + 4] = base + vert;
                indices[faceIdx * 3 + 5] = base + vertsPerRing + (vert + 1) % vertsPerRing;

                faceIdx += 2;
            }
        }

        // top
        for (int i = 0; i < vertsPerRing; ++i) {
            int base = indices.length - 3 * vertsPerRing + 3 * i;
            indices[base] = vertices.length / 3 - vertsPerRing - 1 + i;
            indices[base + 1] = vertices.length / 3 - 1;
            indices[base + 2] = vertices.length / 3 - vertsPerRing - 1 + (i + 1) % vertsPerRing;
        }

        return new Drawable(gl, GL3.GL_TRIANGLES,
                            new float[][] { vertices, colors, vertices },
                            vertices.length,
                            new int[] { 3, 3, 3 },
                            indices,
                            new String[] {
                                "aPosition", "aColor", "aNormal"
                            });
    }

    private Drawable(GL3 gl, int type, float[][] data, int elements, int[] sizes, int[] indices, String[] attribs) {
        this.gl = gl;
        this.type = type;

        int totalSize = 0;

        for (int size: sizes)
            totalSize += size * elements * 4;

        IntBuffer vboId = IntBuffer.allocate(2);
        gl.glGenBuffers(2, vboId);

        verticesBuffer = vboId.get(0);
        indicesBuffer = vboId.get(1);

        if (verticesBuffer < 1)
            throw new RuntimeException("could not create vertex buffer");
        if (indicesBuffer < 1)
            throw new RuntimeException("could not create index buffer");

        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, verticesBuffer);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, totalSize, null, GL3.GL_STATIC_DRAW);

        offsets = new int[sizes.length];
        componentsNum = sizes.clone();
        attribNames = attribs.clone();

        int offset = 0;
        for (int i = 0; i < sizes.length; ++i) {
            gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, offset, sizes[i] * elements * 4, FloatBuffer.wrap(data[i]));
            offsets[i] = offset;
            offset += sizes[i] * elements * 4;
        }

        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, indicesBuffer);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, indices.length * 4, IntBuffer.wrap(indices), GL3.GL_STATIC_DRAW);
        numIndices = indices.length;
    }

    public void setTransform(AffineTransform transform) {
        position = (Vector3)transform.getTranslation().clone();
        rotationAxis = (Vector3)transform.getRotationAxis().clone();
        rotationAngle = transform.getRotationAngle();
        scale = (Vector3)transform.getScale().clone();
    }

    public void setPosition(float x, float y, float z) {
        position.setX(x);
        position.setY(y);
        position.setZ(z);
    }

    public void setPosition(Vector3 pos) {
        position = (Vector3)pos.clone();
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setRotation(float axisX, float axisY, float axisZ, float angle) {
        rotationAxis.setX(axisX);
        rotationAxis.setY(axisY);
        rotationAxis.setZ(axisZ);
        rotationAngle = angle;
    }

    public void setRotation(Vector3 axis, float angle) {
        rotationAxis = (Vector3)axis.clone();
        rotationAngle = angle;
    }

    public void setRotationAxis(float x, float y, float z) {
        rotationAxis.setX(x);
        rotationAxis.setY(y);
        rotationAxis.setZ(z);
    }

    public void setRotationAxis(Vector3 axis) {
        rotationAxis = (Vector3)axis.clone();
    }

    public void setRotationAngle(float angle) {
        rotationAngle = angle;
    }

    public Vector3 getRotationAxis() {
        return rotationAxis;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void rotate(float deltaAngle) {
        rotationAngle += deltaAngle;
    }

    public void setScale(float x, float y, float z) {
        scale.setX(x);
        scale.setY(y);
        scale.setZ(z);
    }

    public void setScale(Vector3 s) {
        scale = (Vector3)s.clone();
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setupAttributes() {
        int program = Shader.getCurrent().getId();

        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, verticesBuffer);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer);

        int i = 0;
        for (i = 0; i < offsets.length; ++i) {
            gl.glEnableVertexAttribArray(i);
            gl.glVertexAttribPointer(i, componentsNum[i], GL3.GL_FLOAT, false, 0, offsets[i]);
            gl.glBindAttribLocation(program, i, attribNames[i]);
        }

        for (; i < 8; ++i) {
            gl.glDisableVertexAttribArray(i);
        }

        int loc = gl.glGetUniformLocation(program, "uModelView");
        if (loc == -1)
            System.out.println("couldnt get uniform location for modelview matrix");
        gl.glUniformMatrix4fv(loc, 1, true, getModelViewMatrix().getData(), 0);
    }

    public Matrix4 getModelViewMatrix() {
        return Matrix4.translate(position).mulRet(
                   Matrix4.rotate(rotationAxis, rotationAngle).mulRet(
                       Matrix4.scale(scale)));

//        return Matrix4.scale(scale).mulRet(
//                       Matrix4.rotate(rotationAxis, rotationAngle).mulRet(
//                               Matrix4.translate(position)));
    }

    public void draw() {
        gl.glDrawElements(type, numIndices, GL.GL_UNSIGNED_INT, 0);
    }
}
