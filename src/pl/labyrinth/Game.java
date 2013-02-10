package pl.labyrinth;

import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.curve.opengl.TextRenderer;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.geom.opengl.SVertex;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.glsl.ShaderState;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;
import jp.nyatla.nyartoolkit.jmf.utils.JmfCaptureDevice;
import jp.nyatla.nyartoolkit.jmf.utils.JmfCaptureDeviceList;
import jp.nyatla.nyartoolkit.jmf.utils.NyARJmfCamera;
import jp.nyatla.nyartoolkit.jogl.utils.NyARGlMarkerSystem;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import javax.vecmath.SingularMatrixException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/25/12
 * Time: 9:46 PM
 */
public class Game extends JFrame {
    private final String CAMERA_PARAM_FILE = "data/camera_param.dat";
    private final String AR_MARKER_FILE = "data/r.patt";

    private final String MAP_FILE = "data/labyrinth.map";

    private Labyrinth labyrinth;

    // rozmiar obrazu z kamery
    private Dimension resolution = new Dimension(640, 480);
    private GLCanvas canvas;
    private BufferedImage frame;

    private NyARMarkerSystemConfig markerSystemConfig;
    private NyARGlMarkerSystem glMarkerSystem;
    private NyARJmfCamera camera;
    private int markerId;

    private Shader backgroundShader;
    private Shader textShader;
    private Shader perspectiveShader;
    private Shader diffuseShader;

    private Drawable[] axes;
    private Drawable gravityLine;
    private Drawable backgroundQuad;
    private Drawable coloredQuad;
    private Drawable cube;
    private Drawable testSphere;
    private Texture backgroundTexture;

    private Matrix4 perspectiveMatrix = Matrix4.perspective((float)(Math.PI * 0.27), 1.33f, 0.5f, 10000.f);
    private Matrix4 orthographicMatrix = Matrix4.orthographic(-1.f, 1.f, -1.f, 1.f, -1.f, 1.f);
    private Matrix4 textMatrix = Matrix4.orthographic(-(float)resolution.getWidth() * 0.5f, (float)resolution.getWidth() * 0.5f,
                                                      (float)resolution.getHeight() * 0.5f, -(float)resolution.getHeight() * 0.5f,
                                                      -1.f, 1.f);

    private boolean showBackground = true;
    private boolean testProjectionMatrix = false;
    private boolean calibration = true;
    private boolean isRunning = false;
    private boolean showXYZAxes = false;
    private boolean showGravityLine = false;

    private int calibrationFrames = 0;
    private int maxCalibrationFrames = 100;
    private float calibrationMaxError = 0.05f;
    //private Vector3 zeroRotation = new Vector3(0.f, 0.f, 0.f);
    private Matrix4 zeroRotation = Matrix4.identity();

    private String currentMessage;
    private int messageFramesShowing;
    private static final int MESSAGE_MAX_FRAMES = 100;

    private int mapSize = 15;
    private long startTimeMillis;
    private long endTimeMillis = 0;

    // event handlery
    class WindowEventHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            release();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            dispose();
            System.exit(0);
        }
    }

    class KeyEventHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            final float MODIFY_STEP = 1.f;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_1:
                    showBackground = !showBackground;
                    break;
                case KeyEvent.VK_2:
                    testProjectionMatrix = !testProjectionMatrix;
                    break;
                case KeyEvent.VK_3:
                    showXYZAxes = !showXYZAxes;
                    break;
                case KeyEvent.VK_4:
                    showGravityLine = !showGravityLine;
                    break;
                case KeyEvent.VK_SPACE:
                    calibration = true;
                    calibrationFrames = 0;
                    break;
                case KeyEvent.VK_ESCAPE:
                    dispose();
                    System.exit(0);
                    break;
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_EQUALS:
                    ++mapSize;
                    showMessage(String.format("map size = %d", mapSize));
                    break;
                case KeyEvent.VK_UNDERSCORE:
                case KeyEvent.VK_MINUS:
                    if (mapSize > 5)
                        --mapSize;
                    showMessage(String.format("map size = %d", mapSize));
                    break;
/*
                case KeyEvent.VK_A:
                    labyrinth.getPhysics().modifyBallFriction(MODIFY_STEP);
                    showMessage(String.format("ball_friction = %f", labyrinth.getPhysics().ballBody.getFriction()));
                    break;
                case KeyEvent.VK_Z:
                    labyrinth.getPhysics().modifyBallFriction(-MODIFY_STEP);
                    showMessage(String.format("ball_friction = %f", labyrinth.getPhysics().ballBody.getFriction()));
                    break;
                case KeyEvent.VK_S:
                    labyrinth.getPhysics().modifyGroundFriction(MODIFY_STEP);
                    showMessage(String.format("ground_friction = %f", labyrinth.getPhysics().groundBody.getFriction()));
                    break;
                case KeyEvent.VK_X:
                    labyrinth.getPhysics().modifyGroundFriction(-MODIFY_STEP);
                    showMessage(String.format("ground_friction = %f", labyrinth.getPhysics().groundBody.getFriction()));
                    break;
                case KeyEvent.VK_D:
                    labyrinth.getPhysics().modifyWallFriction(MODIFY_STEP);
                    showMessage(String.format("wall_friction = %f", labyrinth.getPhysics().topBorderBody.getFriction()));
                    break;
                case KeyEvent.VK_C:
                    labyrinth.getPhysics().modifyWallFriction(-MODIFY_STEP);
                    showMessage(String.format("wall_friction = %f", labyrinth.getPhysics().topBorderBody.getFriction()));
                    break;
//*/
            default:
                break;
            }
        }
    }

    public void setLabyrinth(Labyrinth labyrinth) {
        //labyrinth = new Labyrinth(gl, MAP_FILE);
        this.labyrinth = labyrinth;

        java.util.Timer timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isRunning)
                    if (Game.this.labyrinth.update(0.0333f) && endTimeMillis == 0)
                        endTimeMillis = System.currentTimeMillis();
            }
        }, 0, 33);
    }

    class GLEventHandler implements GLEventListener {
        TextRenderer textRenderer;

        @Override
        public void init(GLAutoDrawable drawable) {
            GL3 gl = (GL3)drawable.getGL();
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL3.GL_TEXTURE_2D);
            gl.glClearColor(0.f, 0.f, 0.7f, 0.f);
            gl.glLineWidth(3.f);

            RenderState renderState = RenderState.createRenderState(new ShaderState(), SVertex.factory());
            textRenderer = TextRenderer.create(renderState, 0);
            textRenderer.init((GL2ES2)gl);

            backgroundShader = new Shader(gl, "data/orthographic.vs", "data/texture.fs");
            textShader = new Shader(gl, "data/orthographic.vs", "data/color_text.fs");
            perspectiveShader = new Shader(gl, "data/perspective.vs", "data/color.fs");
            diffuseShader = new Shader(gl, "data/perspective_normal.vs", "data/diffuse.fs");

            backgroundQuad = Drawable.createTexturedQuad(gl,
                    new float[] {
                            -1.f, -1.f, 0.f,
                            -1.f, 1.f, 0.f,
                            1.f, -1.f, 0.f,
                            1.f, 1.f, 0.f
                    }, new float[] {
                            1.f, 0.f,
                            1.f, 1.f,
                            0.f, 0.f,
                            0.f, 1.f
                    }, new String[] {
                            "aPosition", "aTexcoord"
                    });

            coloredQuad = Drawable.createColoredQuad(gl,
                    new float[] {
                            -1.f, -1.f, 0.f,
                            -1.f, 1.f, 0.f,
                            1.f, -1.f, 0.f,
                            1.f, 1.f, 0.f
                    }, new float[] {
                            1.f, 0.f, 1.f,
                            1.f, 0.f, 1.f,
                            1.f, 0.f, 1.f,
                            1.f, 0.f, 1.f
                    }, new String[] {
                            "aPosition", "aColor"
                    });

            cube = Drawable.createCube(gl, new float[]{0.5f, 0.7f, 1.f});
            cube.setPosition(0.f, 0.f, -10.f);

            axes = new Drawable[] {
                Drawable.createLine(gl, new float[] { 0.f, 0.f, 0.f, 100.f, 0.f, 0.f }, new float[] { 1.f, 0.f, 0.f }),
                Drawable.createLine(gl, new float[] { 0.f, 0.f, 0.f, 0.f, 100.f, 0.f }, new float[] { 0.f, 1.f, 0.f }),
                Drawable.createLine(gl, new float[] { 0.f, 0.f, 0.f, 0.f, 0.f, 100.f }, new float[] { 0.f, 0.f, 1.f })
            };
            gravityLine = Drawable.createLine(gl, new float[] { -0.1f, -0.1f, -0.1f, 1.f, 1.f, 1.f }, new float[] { 1.f, 0.f, 1.f });

            testSphere = Drawable.createSphere(gl, 8, 8, new float[] { 1.f, 1.f, 1.f });
            testSphere.setPosition(new Vector3(0.f, 0.f, -10.f));

            System.out.println(Matrix4.translate(new Vector3(1.f, 2.f, 3.f)).decompose());
            System.out.println(Matrix4.rotate(new Vector3(0.f, 0.f, -1.f), 1.f).decompose());
            System.out.println(Matrix4.scale(new Vector3(1.f, 2.f, 3.f)).decompose());
            System.out.println(Matrix4.translate(new Vector3(10.f, 20.f, 30.f)).mulRet(
                    Matrix4.rotate(new Vector3(0.8f, 0.f, -0.6f), 1.5f).mulRet(
                            Matrix4.scale(new Vector3(4.f, 5.f, 6.f)))).decompose());

            System.out.println(AffineTransform.rotate(new Vector3(0.8f, 0.f, 0.6f), 0.5f).getEulerAngles());
            System.out.println(AffineTransform.rotate(new Vector3(0.6f, 0.8f, 0.f), 0.5f).getEulerAngles());
            System.out.println(AffineTransform.rotate(new Vector3(0.f, 0.6f, 0.8f), 0.5f).getEulerAngles());
        }

        private void setProjectionMatrix(GL3 gl, Matrix4 projMat) {
            if (projMat == null)
                projMat = Matrix4.identity();

            int loc = gl.glGetUniformLocation(Shader.getCurrent().getId(), "uProjection");
            if (loc == -1)
                System.out.println("couldnt get uniform location for projection matrix");
            gl.glUniformMatrix4fv(loc, 1, true, projMat.getData(), 0);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
        }

        @Override
        public void display(GLAutoDrawable drawable) {
            GL3 gl = (GL3)drawable.getGL();

            synchronized (camera) {
                try {
                    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

                    // pobieranie obrazu z kamery do tekstury
                    if (backgroundTexture != null)
                        backgroundTexture.release();
                    backgroundTexture = new Texture(gl, camera.getSourceImage());
                    glMarkerSystem.update(camera);

                    // rysowanie tla (obraz z kamery)
                    if (showBackground) {
                        backgroundShader.bind();
                        setProjectionMatrix(gl, orthographicMatrix);
                        backgroundTexture.bind(0);
                        backgroundQuad.setupAttributes();
                        backgroundQuad.draw();

                        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT);
                    }

                    // test macierzy perspektywy
                    if (testProjectionMatrix) {
                        diffuseShader.bind();
                        setProjectionMatrix(gl, perspectiveMatrix);

                        testSphere.setRotationAxis(new Vector3(1.f, 1.f, 0.f).normalized());
                        testSphere.rotate(0.02f);
                        testSphere.setPosition((float)Math.cos(testSphere.getRotationAngle()), (float)Math.sin(testSphere.getRotationAngle()), -10.f);
                        testSphere.setupAttributes();
                        testSphere.draw();
                    }

                    perspectiveShader.bind();
                    setProjectionMatrix(gl, perspectiveMatrix);

                    if (glMarkerSystem.isExistMarker(markerId)) {
                        // znaleziomo marker
                        Matrix4 markerMatrix = new Matrix4(glMarkerSystem.getGlMarkerMatrix(markerId)).transposed();
                        AffineTransform transform = markerMatrix.decompose();
                        Vector3 yawPitchRoll = transform.getEulerAngles();

                        Matrix4 transformedPerspective = perspectiveMatrix.mulRet(transform.toMatrix());

                        if (!calibration) {
                            // kalibracja zakonczona - rysowanie labiryntu
                            isRunning = true;

                            try {
                                Vector3 gravity = new Vector3(0.f, 0.f, -10.f);
                                Matrix4 mat = markerMatrix.getRotation().inverse().mulRet(zeroRotation);
                                mat.transform(gravity);
                                labyrinth.setGravity(gravity);

                                if (showGravityLine) {
                                    Vector3 pos = labyrinth.getBallPos();
                                    markerMatrix.transform(pos);
                                    pos.add(transform.getTranslation());
                                    gravityLine.setPosition(pos);
                                    gravityLine.setRotation(transform.getRotationAxis(), transform.getRotationAngle());
                                    gravityLine.setScale(gravity.mulRet(labyrinth.getWallThickness()));
                                    gravityLine.setupAttributes();
                                    gravityLine.draw();
                                }

                                diffuseShader.bind();
                                setProjectionMatrix(gl, transformedPerspective);
                                labyrinth.draw();
                            } catch (SingularMatrixException e) {
                                System.out.println("warning: singular transformation matrix");
                                isRunning = false;
                            }
                        } else {
                            // kalibracja
                            isRunning = false;

                            gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);

                            Vector3 delta = yawPitchRoll.subRet(zeroRotation.getEulerAngles());

                            if (delta.lengthSquared() > calibrationMaxError) {
                                zeroRotation = markerMatrix.getRotation();
                                calibrationFrames = 0;
                                System.out.println("error exceeded");
                            } else if (++calibrationFrames >= maxCalibrationFrames) {
                                calibration = false;
                                System.out.println("calibration complete!");

                                setLabyrinth(new Labyrinth(gl, mapSize));
                                System.out.printf("map reloaded. size: %d\n", mapSize);

                                startTimeMillis = System.currentTimeMillis();
                                endTimeMillis = 0;
                            }

                            coloredQuad.setTransform(transform);
                            coloredQuad.setScale(50.f, 50.f, 50.f);
                            coloredQuad.setupAttributes();
                            coloredQuad.draw();

                            gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
                        }

                        perspectiveShader.bind();
                        setProjectionMatrix(gl, transformedPerspective);

                        // rysowanie osi
                        if (showXYZAxes) {
                            for (int i = 0; i < axes.length; ++i) {
                                axes[i].setupAttributes();
                                axes[i].draw();
                            }
                        }
                    } else {
                        isRunning = false;
                    }
                } catch (NyARException e) {
                    e.printStackTrace();
                }

                textShader.bind();
                gl.glEnable(GL3.GL_BLEND);
                gl.glBlendFunc(GL3.GL_ONE_MINUS_DST_COLOR, GL3.GL_ZERO);

                backgroundQuad.setupAttributes(); // uh?
                Vector3 textPos = new Vector3(-(float)resolution.getWidth() * 0.5f + 10.f, -(float)resolution.getHeight() * 0.5f + 10.f, 0.f);
                Matrix4 textTranslationMatrix = Matrix4.translate(textPos);
                setProjectionMatrix(gl, textMatrix.mulRet(textTranslationMatrix));
                try {
                    textRenderer.drawString3D((GL2ES2)drawable.getGL(),
                                              FontFactory.getDefault().getDefault(),
                                              "Autor: Marcin Radomski (AGH, WIEiT, Informatyka, II rok)",
                                              new float[]{ 0.f, 0.f, 0.f },
                                              20, new int[]{0}
                                              );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (currentMessage != null && messageFramesShowing < MESSAGE_MAX_FRAMES) {
                    textPos.y *= 0.9f;
                    textTranslationMatrix = Matrix4.translate(textPos);
                    setProjectionMatrix(gl, textMatrix.mulRet(textTranslationMatrix));

                    try {
                        textRenderer.drawString3D((GL2ES2)drawable.getGL(),
                                                  FontFactory.getDefault().getDefault(),
                                                  currentMessage,
                                                  new float[] { 0.f, 0.f, 0.f },
                                                  15, new int[]{0}
                                                  );
                        ++messageFramesShowing;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // czas
                if (labyrinth != null) {
                    textPos = new Vector3(-(float)resolution.getWidth() * 0.5f + 10.f, (float)resolution.getHeight() * 0.45f, 0.f);
                    textTranslationMatrix = Matrix4.translate(textPos);
                    setProjectionMatrix(gl, textMatrix.mulRet(textTranslationMatrix));

                    long time;
                    if (endTimeMillis == 0) {
                        time = System.currentTimeMillis() - startTimeMillis;
                    } else {
                        time = endTimeMillis - startTimeMillis;
                    }

                    String timeString = String.format("Czas: %d.%ds", time / 1000, time % 1000);

                    try {
                        textRenderer.drawString3D((GL2ES2)drawable.getGL(),
                                                  FontFactory.getDefault().getDefault(),
                                                  timeString,
                                                  new float[] { 0.f, 0.f, 0.f },
                                                  15, new int[]{0}
                                                  );
                        ++messageFramesShowing;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                gl.glBlendFunc(GL3.GL_DST_ALPHA, GL3.GL_ONE_MINUS_DST_ALPHA);
            }
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL3 gl = (GL3)drawable.getGL();
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glViewport(0, 0, width, height);
        }
    }

    private boolean init() {
        // NyARToolkit
        try {
            markerSystemConfig = new NyARMarkerSystemConfig(new FileInputStream(CAMERA_PARAM_FILE), resolution.width,  resolution.height);

            JmfCaptureDeviceList devices = new JmfCaptureDeviceList();
            JmfCaptureDevice device = devices.getDevice(0);
            //device.setCaptureFormat(markerSystemConfig.getScreenSize(), 30.0003f);
            device.setCaptureFormat(0);

            camera = new NyARJmfCamera(device);
            glMarkerSystem = new NyARGlMarkerSystem(markerSystemConfig);
            markerId = glMarkerSystem.addARMarker(AR_MARKER_FILE, 16, 25, 80);

            NyARIntSize videoSize = markerSystemConfig.getScreenSize();
            resolution = new Dimension(videoSize.w, videoSize.h);

            synchronized (camera) {
                // bo z jakiegos powodu nie zawsze lapie kamere
                int attempts;
                for (attempts = 10; attempts > 0; --attempts) {
                    try {
                        camera.start();
                        break;
                    } catch (NyARException e) {
                        e.printStackTrace();
                    }
                }

                if (attempts == 0) {
                    System.err.println("fatal error: couldn't connect to camera");
                    System.exit(0);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NyARException e) {
            e.printStackTrace();
            return false;
        }

        frame = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_3BYTE_BGR);

        System.out.println("init done");
        return true;
    }

    private void release() {
        System.out.println("release done");
    }

    public void run() {
        if (!init()) {
            System.out.println("init failed");
            return;
        }

        // swing
        canvas = new GLCanvas();
        add(canvas);
        canvas.addGLEventListener(new GLEventHandler());

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        canvas.setAnimator(animator);
        animator.start();

        setMinimumSize(resolution);
        setSize(1280, 960);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowEventHandler());
        addKeyListener(new KeyEventHandler());
        canvas.addKeyListener(new KeyEventHandler());
    }

    public static float[] toFloatArray(double[] array) {
        float[] ret = new float[array.length];
        for (int i = 0; i < array.length; ++i)
            ret[i] = (float)array[i];
        return ret;
    }

    private void showMessage(String message) {
        currentMessage = message;
        messageFramesShowing = 0;
    }
}
