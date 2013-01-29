package pl.labyrinth;

import jp.nyatla.nyartoolkit.core.raster.INyARRaster;

import javax.media.opengl.GL3;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/27/12
 * Time: 10:21 PM
 */
public class Texture {
    private GL3 gl;
    private int id;

    public Texture(GL3 gl, INyARRaster img) {
        this.gl = gl;

        id = loadTexture(img);
    }

    public void bind(int unit) {
        gl.glActiveTexture(GL3.GL_TEXTURE0 + unit);
        gl.glEnable(GL3.GL_TEXTURE_2D);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, id);

        int loc = gl.glGetUniformLocation(Shader.getCurrent().getId(), "uTexture" + unit);
        if (loc == -1)
            System.out.println("couldnt get uniform location for texture");
        gl.glUniform1i(loc, unit);
    }

    public void release() {
        IntBuffer buf = IntBuffer.allocate(1);
        buf.put(0, id);
        gl.glDeleteTextures(1, buf);
    }

    private int loadTexture(INyARRaster texture) {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGenTextures(1, intBuffer);

        int id = intBuffer.get(0);
        if (id < 0)
            throw new RuntimeException("could not generate texture");

        gl.glEnable(GL3.GL_TEXTURE_2D);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, id);
        gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);

        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);

        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB8, texture.getWidth(), texture.getHeight(),
                        0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE,
                        ByteBuffer.wrap((byte[]) texture.getBuffer()));

        return id;
    }
}
