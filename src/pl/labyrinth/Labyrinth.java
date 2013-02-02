package pl.labyrinth;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.OverlappingPairCache;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.media.opengl.GL3;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/28/12
 * Time: 7:11 PM
 */
public class Labyrinth {
    private GL3 gl;

    private int width;
    private int height;
    private char[][] map;
    private int boxes;
    private Point start;
    private Point end;

    private boolean isCompleted;
    private boolean hasExploded;

    class Physics {
        public CollisionShape ballShape;
        public CollisionShape groundShape;
        public CollisionShape borderShapeW;
        public CollisionShape borderShapeH;
        public CollisionShape wallShape;

        public MotionState ballMotionState;

        public RigidBody ballBody;
        public RigidBody groundBody;
        public RigidBody leftBorderBody;
        public RigidBody rightBorderBody;
        public RigidBody topBorderBody;
        public RigidBody bottomBorderBody;
        public RigidBody endBody;
        public RigidBody[] wallBodies;

        public BroadphaseInterface broadphase;
        public DefaultCollisionConfiguration collisionConfiguration;
        public CollisionDispatcher dispatcher;
        public SequentialImpulseConstraintSolver solver;
        public DiscreteDynamicsWorld dynamicsWorld;

        public Physics(float wallThickness, float width, float height, char[][] map, int wallsCount, Point start, Point end) {
            broadphase = new DbvtBroadphase();
            collisionConfiguration = new DefaultCollisionConfiguration();
            dispatcher = new CollisionDispatcher(collisionConfiguration);
            solver = new SequentialImpulseConstraintSolver();
            dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

            // tworzenie cial fizycznych
            float halfWallThickness = wallThickness * 0.5f;
            float halfWidth = width * 0.5f;
            float halfHeight = height * 0.5f;

            ballShape = new SphereShape(0.8f * halfWallThickness);
            groundShape = new BoxShape(new Vector3(halfWidth  + wallThickness, halfHeight + wallThickness, halfWallThickness));
            borderShapeW = new BoxShape(new Vector3(halfWallThickness, halfHeight + wallThickness, halfWallThickness));
            borderShapeH = new BoxShape(new Vector3(halfWidth + wallThickness, halfWallThickness, halfWallThickness));
            wallShape = new BoxShape(new Vector3(halfWallThickness, halfWallThickness, halfWallThickness));

            Vector3 startPos = new Vector3(-width * 0.5f + ((float)start.getX() + 0.5f) * wallThickness,
                                             -height * 0.5f + ((float)start.getY() + 0.5f) * wallThickness,
                                            wallThickness * 2.f);
            ballBody = new RigidBody(1.f, new DefaultMotionState(), ballShape);
            ballBody.translate(startPos);

            groundBody = new RigidBody(0.f, null, groundShape);
            groundBody.translate(new Vector3(0.f, 0.f, -wallThickness));

            leftBorderBody = new RigidBody(0.f, null, borderShapeW);
            leftBorderBody.translate(new Vector3(-width * 0.5f - wallThickness * 0.5f, 0.f, 0.f));
            rightBorderBody = new RigidBody(0.f, null, borderShapeW);
            rightBorderBody.translate(new Vector3(width * 0.5f + wallThickness * 0.5f, 0.f, 0.f));
            topBorderBody = new RigidBody(0.f, null, borderShapeH);
            topBorderBody.translate(new Vector3(0.f, -height * 0.5f - wallThickness * 0.5f, 0.f));
            bottomBorderBody = new RigidBody(0.f, null, borderShapeH);
            bottomBorderBody.translate(new Vector3(0.f, height * 0.5f + wallThickness * 0.5f, 0.f));

            endBody = new RigidBody(0.f, null, wallShape);
            endBody.translate(new Vector3(-width * 0.5f + ((float)end.getX() + 0.5f) * wallThickness,
                    -height * 0.5f + ((float)end.getY() + 0.5f) * wallThickness,
                    -wallThickness * 0.99f));

            wallBodies = new RigidBody[wallsCount];
            int current = 0;
            for (int y = 0; y < (int)(height / wallThickness); ++y)
                for (int x = 0; x < (int)(width / wallThickness); ++x) {
                    if (map[x][y] == '#') {
                        RigidBody body = new RigidBody(0.f, null, wallShape);
                        Vector3 pos = new Vector3(-width * 0.5f + ((float)x + 0.5f) * wallThickness,
                                                    -height * 0.5f + ((float)y + 0.5f) * wallThickness,
                                                    0.f);
                        body.translate(pos);
                        wallBodies[current++] = body;
                    }
                }

            // wlasciwe dodawanie cial do swiata
            dynamicsWorld.addRigidBody(ballBody);
            dynamicsWorld.addRigidBody(groundBody);
            dynamicsWorld.addRigidBody(leftBorderBody);
            dynamicsWorld.addRigidBody(rightBorderBody);
            dynamicsWorld.addRigidBody(topBorderBody);
            dynamicsWorld.addRigidBody(bottomBorderBody);

            for (int i = 0; i < wallBodies.length; ++i)
                dynamicsWorld.addRigidBody(wallBodies[i]);

            dynamicsWorld.addRigidBody(endBody);

            // kolizja pilki z koncem labiryntu
            dynamicsWorld.setInternalTickCallback(new InternalTickCallback() {
                @Override
                public void internalTick(DynamicsWorld dynamicsWorld, float v) {
                    if (!isCompleted && !hasExploded) {
                        OverlappingPairCache cache = dynamicsWorld.getBroadphase().getOverlappingPairCache();

                        for (BroadphasePair pair: cache.getOverlappingPairArray()) {
                            if ((pair.pProxy0.clientObject == ballBody && pair.pProxy1.clientObject == endBody) ||
                                (pair.pProxy1.clientObject == ballBody && pair.pProxy0.clientObject == endBody)) {
                                System.out.println("hit!");
                                isCompleted = true;

                            }
                        }
                    }
                }
            }, null);

        }

        public void debugDraw() {
            Drawable box = Drawable.createCube(gl, new float[] { 1.f, 1.f, 1.f });
            Drawable blackBox = Drawable.createCube(gl, new float[] { 0.f, 0.f, 0.f });
            Drawable greenBox = Drawable.createCube(gl, new float[] { 0.f, 1.f, 0.f });
            Drawable sphere = Drawable.createSphere(gl, 16, 16, new float[] { 1.f, 0.f, 0.f });

            Transform transform = new Transform();
            Matrix4 matrix = new Matrix4();
            Vector3 vec = new Vector3();
            Vector3 vec2 = new Vector3();

            groundBody.getWorldTransform(transform);
            transform.getMatrix(matrix);

            box.setTransform(matrix.decompose());
            groundBody.getCollisionShape().getAabb(transform, vec, vec2);
            blackBox.setPosition(vec2.addRet(vec).mulRet(0.5f));
            blackBox.setScale(vec2.subRet(vec).mulRet(0.5f));

            blackBox.setupAttributes();
            blackBox.draw();

            RigidBody[] bodies = new RigidBody[] {
                    leftBorderBody, rightBorderBody, topBorderBody, bottomBorderBody
            };
            for (RigidBody body: bodies) {
                body.getWorldTransform(transform);
                transform.getMatrix(matrix);

                box.setTransform(matrix.decompose());
                body.getCollisionShape().getAabb(transform, vec, vec2);
                box.setPosition(vec2.addRet(vec).mulRet(0.5f));
                box.setScale(vec2.subRet(vec).mulRet(0.5f));

                box.setupAttributes();
                box.draw();
            }

            for (RigidBody body: wallBodies) {
                body.getWorldTransform(transform);
                transform.getMatrix(matrix);

                box.setTransform(matrix.decompose());
                body.getCollisionShape().getAabb(transform, vec, vec2);
                box.setPosition(vec2.addRet(vec).mulRet(0.5f));
                box.setScale(vec2.subRet(vec).mulRet(0.5f));

                box.setupAttributes();
                box.draw();
            }

            endBody.getWorldTransform(transform);
            transform.getMatrix(matrix);
            greenBox.setTransform(matrix.decompose());

            endBody.getCollisionShape().getAabb(transform, vec, vec2);
            greenBox.setPosition(vec2.addRet(vec).mulRet(0.5f));
            greenBox.setScale(vec2.subRet(vec).mulRet(0.5f));

            ballBody.getWorldTransform(transform);
            transform.getMatrix(matrix);
            sphere.setTransform(matrix.decompose());

            ballBody.getCollisionShape().getAabb(transform, vec, vec2);
            sphere.setPosition(vec2.addRet(vec).mulRet(0.5f));
            sphere.setScale(vec2.subRet(vec).mulRet(0.5f));

            sphere.setupAttributes();
            sphere.draw();

        }

        public void update(float dt) {
            synchronized (dynamicsWorld) {
                dynamicsWorld.stepSimulation(dt, 2);

                if (isCompleted && !hasExploded) {
                    hasExploded = true;

                    Transform transform = new Transform();

                    for (RigidBody body: new RigidBody[] {
                            groundBody, leftBorderBody, rightBorderBody, topBorderBody, bottomBorderBody, endBody
                    }) {
                        dynamicsWorld.removeRigidBody(body);
                        body.setMassProps(1.f, new Vector3(1.f));
                        body.getWorldTransform(transform);
                        Vector3 impulse = new Vector3(transform.origin);
                        impulse.scale(5000.f / impulse.lengthSquared());
                        body.applyCentralImpulse(impulse);
                        dynamicsWorld.addRigidBody(body);
                    }

                    for (RigidBody body : wallBodies) {
                        dynamicsWorld.removeRigidBody(body);
                        body.setMassProps(1.f, new Vector3(1.f));
                        body.getWorldTransform(transform);
                        Vector3 impulse = new Vector3(transform.origin);
                        impulse.scale(5000.f / impulse.lengthSquared());
                        body.applyCentralImpulse(impulse);
                        dynamicsWorld.addRigidBody(body);
                    }
                }
            }
        }
    }

    class GameObject {
        public Drawable drawable;
        public RigidBody body;

        GameObject(Drawable drawable, RigidBody body) {
            this.drawable = drawable;
            this.body = body;
        }

        void sync() {
            Transform transform = new Transform();
            body.getWorldTransform(transform);

            Matrix4 mat = new Matrix4();
            transform.getMatrix(mat);
            drawable.setTransform(mat.decompose());

            AffineTransform trans = mat.decompose();
            Matrix4 mat2 = Matrix4.translate(trans.getTranslation()).mulRet(Matrix4.scale(trans.getScale()));
            Vector3 vec = new Vector3();
//*
            Vector3 vec2 = new Vector3();
            body.getCollisionShape().getAabb(new Transform(mat2), vec, vec2);

            drawable.setPosition(vec2.addRet(vec).mulRet(0.5f));
            drawable.setScale(vec2.subRet(vec).mulRet(0.5f));
/*/
            body.getCollisionShape().getLocalScaling(vec);
            drawable.setScale(vec);
//*/
        }
    }

    private GameObject ball;
    private GameObject endMarker;
    private GameObject[] walls;

    private Physics physics;

    public Labyrinth(GL3 gl, String filename) {
        this.gl = gl;
        loadFromFile(filename);
    }

    public void loadFromFile(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        width = 0;
        height = 0;
        boxes = 0; // 4 sciany + podloga
        start = null;
        end = null;

        try {
            Scanner scanner = new Scanner(reader.readLine());
            width = scanner.nextInt();
            height = scanner.nextInt();

            map = new char[width][height];
            boxes = 5;

            for (int i = 0; i < height; ++i) {
                String line = reader.readLine();

                for (int j = 0; j < width; ++j) {
                    map[j][i] = line.charAt(j);
                    switch (map[j][i]) {
                        case '#':
                            ++boxes;
                            break;
                        case '^':
                            if (start != null)
                                throw new RuntimeException("multiple start positions found");
                            start = new Point(j, i);
                            break;
                        case '$':
                            if (end != null)
                                throw new RuntimeException("multiple end positions found");
                            end = new Point(j, i);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        reset();
    }

    public void reset() {
        if (start == null || end == null)
            throw new RuntimeException("no start and/or end position found");

        isCompleted = false;
        hasExploded = false;

        Drawable[] drawables = new Drawable[boxes + 2];
        drawables[0] = Drawable.createCube(gl, new float[]{ 0.f, 0.f, 1.f });
        for (int i = 1; i < boxes; ++i)
            drawables[i] = Drawable.createCube(gl, new float[] { 1.f, 1.f, 1.f });

        drawables[boxes] = Drawable.createCube(gl, new float[] { 1.f, 0.f, 0.f });
        drawables[boxes + 1] = Drawable.createCube(gl, new float[] { 0.f, 1.f, 0.f });

        float unitSize = getWallThickness();
        float halfUnitSize = unitSize * 0.5f;
        float fullWidth = (float)width * unitSize;
        float fullHeight = (float)height * unitSize;
        float halfWidth = (fullWidth + unitSize) * 0.5f;
        float halfHeight = (fullHeight + unitSize) * 0.5f;
        float borderWidth = fullWidth + 2.f * unitSize;
        float borderHeight = fullHeight + 2.f * unitSize;

        // podloga
        drawables[0].setPosition(0.f, 0.f, -unitSize);
        drawables[0].setScale(borderWidth * 0.5f, borderHeight * 0.5f, halfUnitSize);

        // sciany boczne
        drawables[1].setPosition(-halfWidth, 0.f, 0.f);
        drawables[1].setScale(halfUnitSize, borderHeight * 0.5f, halfUnitSize);

        drawables[2].setPosition(halfWidth, 0.f, 0.f);
        drawables[2].setScale(halfUnitSize, borderHeight * 0.5f, halfUnitSize);

        drawables[3].setPosition(0.f, -halfHeight, 0.f);
        drawables[3].setScale(borderWidth * 0.5f, halfUnitSize, halfUnitSize);

        drawables[4].setPosition(0.f, halfHeight, 0.f);
        drawables[4].setScale(borderWidth * 0.5f, halfUnitSize, halfUnitSize);

        // sciany wewnatrz
        int current = 5;
        float baseX = -halfWidth + unitSize;
        float baseY = -halfHeight + unitSize;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (map[x][y] == '#') {
                    drawables[current].setPosition(baseX + (float)x * unitSize, baseY + (float)y * unitSize, 0.f);
                    drawables[current].setScale(halfUnitSize, halfUnitSize, halfUnitSize);
                    ++current;
                } else if (map[x][y] == '^' || map[x][y] == '$') {
                    int index = drawables.length - 1;
                    if (map[x][y] == '^')
                        --index;

                    drawables[index].setPosition(baseX + (float)x * unitSize, baseY + (float)y * unitSize, -unitSize + 0.05f);
                    drawables[index].setScale(halfUnitSize, halfUnitSize, halfUnitSize);
                }
            }
        }

        Drawable ballDrawable = Drawable.createSphere(gl, 16, 16, new float[] { 1.f, 0.f, 0.f });

        if (physics != null)
            physics.dynamicsWorld.destroy();
        physics = new Physics(unitSize, fullWidth, fullHeight, map, boxes - 5, start, end);

        ball = new GameObject(ballDrawable, physics.ballBody);

        walls = new GameObject[drawables.length];
        walls[0] = new GameObject(drawables[0], physics.groundBody);
        walls[1] = new GameObject(drawables[1], physics.leftBorderBody);
        walls[2] = new GameObject(drawables[2], physics.rightBorderBody);
        walls[3] = new GameObject(drawables[3], physics.topBorderBody);
        walls[4] = new GameObject(drawables[4], physics.bottomBorderBody);

        for (int i = 5; i < drawables.length - 2; ++i) {
            walls[i] = new GameObject(drawables[i], physics.wallBodies[i - 5]);
        }

        Drawable endMarkerDrawable = Drawable.createCube(gl, new float[] { 0.f, 1.f, 0.f });
        endMarkerDrawable.setScale(unitSize, unitSize, unitSize);
        endMarker = new GameObject(endMarkerDrawable, physics.endBody);
    }

    public float getWallThickness() {
        return 10.f;
    }

    public void setGravity(Vector3 gravity) {
        synchronized (physics.dynamicsWorld) {
            physics.dynamicsWorld.setGravity(gravity.mulRet(getWallThickness()));
            physics.ballBody.activate();
        }
    }

    public Vector3 getBallPos() {
        return (Vector3)ball.drawable.getPosition().clone();
    }

    public void update(float dt) {
        physics.update(dt);

        synchronized (physics) {
            for (int i = 0; i < walls.length - 2; ++i) {
                GameObject object = walls[i];
                object.sync();
            }

            endMarker.sync();
            ball.sync();
            //System.out.println(String.format("Ball at: %s", ball.drawable.getPosition().toString()));
        }
    }

    public void draw() {
        synchronized (physics) {
/*
            physics.debugDraw();
            //physics.debugDraw();
/*/
            for (int i = 0; i < walls.length - 2; ++i) {
                GameObject object = walls[i];
                object.drawable.setupAttributes();
                object.drawable.draw();
            }

            endMarker.drawable.setupAttributes();
            endMarker.drawable.draw();

            ball.drawable.setupAttributes();
            ball.drawable.draw();
//*/
        }
    }
}
