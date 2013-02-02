package pl.labyrinth;

import javax.media.opengl.GL3;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/27/12
 * Time: 10:11 PM
 */
public class Shader {
    private static Shader currentShader;

    private GL3 gl;

    private int id;

    private int vertexShaderId;
    private int fragmentShaderId;

    public Shader(GL3 gl, String vsFile, String fsFile) {
        this.gl = gl;

        vertexShaderId = loadShader(GL3.GL_VERTEX_SHADER, vsFile);
        fragmentShaderId = loadShader(GL3.GL_FRAGMENT_SHADER, fsFile);
        id = loadProgram(vertexShaderId, fragmentShaderId);
    }

    public void bind() {
        if (id < 1)
            throw new RuntimeException("invalid program");

        currentShader = this;
        gl.glUseProgram(id);
    }

    public int getId() {
        return id;
    }

    public static Shader getCurrent() {
        return currentShader;
    }

    private int loadShader(int type, String filename)  {
        int id = gl.glCreateShader(type);

        File file = new File(filename);
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        char[] buf = new char[(int)file.length()];
        try {
            reader.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String source = new String(buf);
        gl.glShaderSource(id, 1, new String[]{source}, new int[]{source.length()}, 0);
        gl.glCompileShader(id);

        printInfoLog(gl, id, false);

        return id;
    }

    private int loadProgram(int vertexShader, int fragmentShader) {
        int id = gl.glCreateProgram();

        gl.glAttachShader(id, vertexShader);
        gl.glAttachShader(id, fragmentShader);
        gl.glLinkProgram(id);
//        gl.glValidateProgram(id);

        printInfoLog(gl, id, true);

        return id;
    }

    private static void printInfoLog(GL3 gl, int id, boolean linking) {
        IntBuffer logLength = IntBuffer.allocate(1);
        if (linking)
            gl.glGetProgramiv(id, GL3.GL_INFO_LOG_LENGTH, logLength);
        else
            gl.glGetShaderiv(id, GL3.GL_INFO_LOG_LENGTH, logLength);

        ByteBuffer buf = ByteBuffer.allocate(logLength.get(0));
        if (linking)
            gl.glGetProgramInfoLog(id, logLength.get(0), logLength, buf);
        else
            gl.glGetShaderInfoLog(id, logLength.get(0), logLength, buf);

        System.out.println(new String(buf.array()));
    }

}
