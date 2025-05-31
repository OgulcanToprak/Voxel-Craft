package game;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f rotation;
    private float distance = 10.0f;


    public Camera() {
        this.position = new Vector3f(0,0,3);
        this.rotation = new Vector3f(0,0,0);
    }

    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f();
        view.identity();

    view.translate(0, 0, -distance);
    view.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
        .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

    return view; 
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public void rotate(float dx, float dy) {
        rotation.add(dx, dy, 0);
    }

    public void zoom(float amount) {
        distance += amount;
        distance = Math.max(2.0f, Math.min(100.0f, distance));
    }



    public Vector3f getPosition() {return position;}
    public Vector3f getRotation() {return rotation;}
}