package game.physics;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class AABB {
    public final Vector3f min;
    public final Vector3f max;
    

    /** 
     * Construct given center and half-extents. 
     *   center = world center, halfExtents = (width/2, height/2, depth/2)
     */
    public AABB(Vector3f center, Vector3f halfExtents) {
        this.min = new Vector3f(center).sub(halfExtents);
        this.max = new Vector3f(center).add(halfExtents);
    }

    /**
     * Construct directly from min/max corners.
     * Note: different signature—six floats instead of two Vector3f.
     */
    public AABB(float minX, float minY, float minZ,
                float maxX, float maxY, float maxZ) {
        this.min = new Vector3f(minX, minY, minZ);
        this.max = new Vector3f(maxX, maxY, maxZ);
    }

    /** If you still want a “Vector3f min, Vector3f max” constructor, give it a different signature: **/
    public AABB(Vector3f min, Vector3f max, boolean directCorners) {
        // We use the boolean only to differentiate the signature.
        // directCorners == true means “min is really min, max is really max.”
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public boolean intersects(AABB other) {
        return (this.max.x > other.min.x && this.min.x < other.max.x) &&
               (this.max.y > other.min.y && this.min.y < other.max.y) &&
               (this.max.z > other.min.z && this.min.z < other.max.z);
    }



    public void draw() {
    org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
    org.lwjgl.opengl.GL11.glColor3f(1f, 0f, 0f); // Red

    org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_LINES);

    // Bottom rectangle
    GL11.glVertex3f(min.x, min.y, min.z); GL11.glVertex3f(max.x, min.y, min.z);
    GL11.glVertex3f(max.x, min.y, min.z); GL11.glVertex3f(max.x, min.y, max.z);
    GL11.glVertex3f(max.x, min.y, max.z); GL11.glVertex3f(min.x, min.y, max.z);
    GL11.glVertex3f(min.x, min.y, max.z); GL11.glVertex3f(min.x, min.y, min.z);

    // Top rectangle
    GL11.glVertex3f(min.x, max.y, min.z); GL11.glVertex3f(max.x, max.y, min.z);
    GL11.glVertex3f(max.x, max.y, min.z); GL11.glVertex3f(max.x, max.y, max.z);
    GL11.glVertex3f(max.x, max.y, max.z); GL11.glVertex3f(min.x, max.y, max.z);
    GL11.glVertex3f(min.x, max.y, max.z); GL11.glVertex3f(min.x, max.y, min.z);

    // Vertical edges
    GL11.glVertex3f(min.x, min.y, min.z); GL11.glVertex3f(min.x, max.y, min.z);
    GL11.glVertex3f(max.x, min.y, min.z); GL11.glVertex3f(max.x, max.y, min.z);
    GL11.glVertex3f(max.x, min.y, max.z); GL11.glVertex3f(max.x, max.y, max.z);
    GL11.glVertex3f(min.x, min.y, max.z); GL11.glVertex3f(min.x, max.y, max.z);

    org.lwjgl.opengl.GL11.glEnd();

    org.lwjgl.opengl.GL11.glColor3f(1f, 1f, 1f); // Reset color
    org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
}

}
