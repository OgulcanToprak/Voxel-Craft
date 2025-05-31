// game/CubeData.java
package game;

/**
 * CubeData holds 5‐component (x, y, z, u, v) vertex data for each face of a unit cube,
 * centered at the origin.  We’ll shift each vertex by (x,y,z) in createCubeVertices().
 */
public class CubeData {
    public static final float[] TOP = {
         0.5f,  0.5f,  0.5f,   1f, 0f,
         0.5f,  0.5f, -0.5f,   1f, 1f,
        -0.5f,  0.5f, -0.5f,   0f, 1f,

         0.5f,  0.5f,  0.5f,   1f, 0f,
        -0.5f,  0.5f, -0.5f,   0f, 1f,
        -0.5f,  0.5f,  0.5f,   0f, 0f
    };

    public static final float[] BOTTOM = {
        -0.5f, -0.5f, -0.5f,   1f, 1f,
         0.5f, -0.5f, -0.5f,   0f, 1f,
         0.5f, -0.5f,  0.5f,   0f, 0f,

        -0.5f, -0.5f, -0.5f,   1f, 1f,
         0.5f, -0.5f,  0.5f,   0f, 0f,
        -0.5f, -0.5f,  0.5f,   1f, 0f
    };

    public static final float[] FRONT = {
        -0.5f, -0.5f,  0.5f,   0f, 0f,
         0.5f, -0.5f,  0.5f,   1f, 0f,
         0.5f,  0.5f,  0.5f,   1f, 1f,

        -0.5f, -0.5f,  0.5f,   0f, 0f,
         0.5f,  0.5f,  0.5f,   1f, 1f,
        -0.5f,  0.5f,  0.5f,   0f, 1f
    };

    public static final float[] BACK = {
         0.5f, -0.5f, -0.5f,   0f, 0f,
        -0.5f, -0.5f, -0.5f,   1f, 0f,
        -0.5f,  0.5f, -0.5f,   1f, 1f,

         0.5f, -0.5f, -0.5f,   0f, 0f,
        -0.5f,  0.5f, -0.5f,   1f, 1f,
         0.5f,  0.5f, -0.5f,   0f, 1f
    };

    public static final float[] LEFT = {
        -0.5f, -0.5f,  0.5f,   1f, 0f,
        -0.5f, -0.5f, -0.5f,   0f, 0f,
        -0.5f,  0.5f, -0.5f,   0f, 1f,

        -0.5f, -0.5f,  0.5f,   1f, 0f,
        -0.5f,  0.5f, -0.5f,   0f, 1f,
        -0.5f,  0.5f,  0.5f,   1f, 1f
    };

    public static final float[] RIGHT = {
         0.5f, -0.5f, -0.5f,   1f, 0f,
         0.5f, -0.5f,  0.5f,   0f, 0f,
         0.5f,  0.5f,  0.5f,   0f, 1f,

         0.5f, -0.5f, -0.5f,   1f, 0f,
         0.5f,  0.5f,  0.5f,   0f, 1f,
         0.5f,  0.5f, -0.5f,   1f, 1f
    };


    /**
     * Offsets each face’s [x,y,z,u,v] by (x0,y0,z0). Returns a new float[]
     * containing 6 faces × 6 vertices × 5 floats = 180 floats.
     */
    public static float[] createCubeVertices(float x0, float y0, float z0) {
        // There are 6 faces, each array has length = 6 verts × 5 floats = 30 floats
        float[][] faces = { TOP, BOTTOM, FRONT, BACK, LEFT, RIGHT };
        float[] out = new float[ faces.length * 30 ];

        int ptr = 0;
        for (float[] face : faces) {
            // face.length == 30 (6 verts × (x,y,z,u,v) = 30 floats)
            for (int i = 0; i < face.length; i += 5) {
                // Offset world‐coords by (x0, y0, z0), copy UV⟶same
                out[ptr++] = face[i]     + x0; // X
                out[ptr++] = face[i + 1] + y0; // Y
                out[ptr++] = face[i + 2] + z0; // Z
                out[ptr++] = face[i + 3];      // U
                out[ptr++] = face[i + 4];      // V
            }
        }

        return out;
    }
}
