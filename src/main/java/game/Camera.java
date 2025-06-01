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
  view.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0)) // yaw
    .rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0)) // pitch
    .translate(-position.x, -position.y, -position.z);


    return view; 
    }


    public Matrix4f getViewMatrix(boolean playerMode) {
    Matrix4f view = new Matrix4f();
    view.identity();

    if (playerMode) {
        // First-person mode
        view.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
            .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
            .translate(-position.x, -position.y, -position.z);
    } else {
        // Bird view mode
        view.translate(0, 0, -distance);
        view.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
            .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
    }

    return view;
}


public Vector3f getForwardDirection() {
    float yawRad = (float) Math.toRadians(rotation.y);
    return new Vector3f(
        (float)Math.sin(yawRad),
        0f,
        (float)-Math.cos(yawRad)
    ).normalize();
}

public Vector3f getRightDirection() {
    Vector3f forward = getForwardDirection();
    return new Vector3f(forward.z, 0, -forward.x).normalize();
}


public void setRotation(Vector3f newRotation) {
    this.rotation.set(newRotation);
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

public void setPosition(float x, float y, float z) {
    this.position.set(x, y, z);
}


    public Vector3f getPosition() {return position;}
    public Vector3f getRotation() {return rotation;}


    /**
     * Compute a yaw/pitch so that the camera “looks at” the given world‐space target.
     * This sets `rotation.x` = pitch, `rotation.y` = yaw (in degrees).
     */
    public void lookAt(Vector3f target) {
        Vector3f direction = new Vector3f();
        target.sub(position, direction);

        // Yaw (around Y axis): atan2(dir.x, -dir.z)
        float yaw   = (float) Math.toDegrees(Math.atan2(direction.x, -direction.z));

        // Pitch (around X axis): atan2(dir.y, sqrt(dx² + dz²))
        float horizontalDist = (float) Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float pitch = (float) Math.toDegrees(Math.atan2(direction.y, horizontalDist));

        rotation.set(pitch, yaw, 0f);
    }
}