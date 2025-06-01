package game.parts;

/**
 * FaceData holds one 6‐vertex (x,y,z, u,v) array per face of a unit cube,
 * centered at the origin (–0.5…+0.5). Each float[] is 6 vertices × (x,y,z,u,v) = 30 floats.
 *
 * We assume:
 *   • STBImage is set to flip vertically on load (so (u=0,v=0) is top‐left of the PNG).
 *   • GL_CULL_FACE is enabled with GL_FRONT_FACE = GL_CCW.
 *
 * Each face below is wound CCW “as seen from outside” and its UVs span [0 → 1].
 */
public class FaceData {

    // ───────────────────────────────────────────────────────────────────────────
    // TOP face (Y = +0.5), viewed from above (+Y).  CCW winding:
    //    Triangle A: (+0.5, +0.5, +0.5) → (−0.5, +0.5, +0.5) → (−0.5, +0.5, −0.5)
    //       UV:         (1, 1)            (0, 1)            (0, 0)
    //    Triangle B: (+0.5, +0.5, +0.5) → (−0.5, +0.5, −0.5) → (+0.5, +0.5, −0.5)
    //       UV:         (1, 1)            (0, 0)            (1, 0)
    public static final float[] TOP = {
        //  X      Y      Z      |   U    V
         0.5f,  0.5f,  0.5f,   1f,  1f,
        -0.5f,  0.5f,  0.5f,   0f,  1f,
        -0.5f,  0.5f, -0.5f,   0f,  0f,

         0.5f,  0.5f,  0.5f,   1f,  1f,
        -0.5f,  0.5f, -0.5f,   0f,  0f,
         0.5f,  0.5f, -0.5f,   1f,  0f
    };

    // ───────────────────────────────────────────────────────────────────────────
    // BOTTOM face (Y = −0.5), viewed from below (−Y).  CCW winding “underneath”:
    //    Triangle A: (−0.5, −0.5, +0.5) → (+0.5, −0.5, +0.5) → (+0.5, −0.5, −0.5)
    //       UV:         (0, 1)            (1, 1)            (1, 0)
    //    Triangle B: (−0.5, −0.5, +0.5) → (+0.5, −0.5, −0.5) → (−0.5, −0.5, −0.5)
    //       UV:         (0, 1)            (1, 0)            (0, 0)
    public static final float[] BOTTOM = {
        //   X       Y       Z     |   U    V
        -0.5f, -0.5f,  0.5f,   0f,  1f,
         0.5f, -0.5f,  0.5f,   1f,  1f,
         0.5f, -0.5f, -0.5f,   1f,  0f,

        -0.5f, -0.5f,  0.5f,   0f,  1f,
         0.5f, -0.5f, -0.5f,   1f,  0f,
        -0.5f, -0.5f, -0.5f,   0f,  0f
    };

    // ───────────────────────────────────────────────────────────────────────────
    // FRONT face (Z = +0.5), viewed from +Z side.  CCW winding:
    //    Triangle A: (−0.5, −0.5, +0.5) → (+0.5, −0.5, +0.5) → (+0.5, +0.5, +0.5)
    //       UV:         (0, 0)            (1, 0)            (1, 1)
    //    Triangle B: (−0.5, −0.5, +0.5) → (+0.5, +0.5, +0.5) → (−0.5, +0.5, +0.5)
    //       UV:         (0, 0)            (1, 1)            (0, 1)
    public static final float[] FRONT = {
        //   X       Y       Z     |   U    V
        -0.5f, -0.5f,  0.5f,   0f,  0f,
         0.5f, -0.5f,  0.5f,   1f,  0f,
         0.5f,  0.5f,  0.5f,   1f,  1f,

        -0.5f, -0.5f,  0.5f,   0f,  0f,
         0.5f,  0.5f,  0.5f,   1f,  1f,
        -0.5f,  0.5f,  0.5f,   0f,  1f
    };

    // ───────────────────────────────────────────────────────────────────────────
    // BACK face (Z = −0.5), viewed from −Z side.  CCW winding:
    //    Triangle A: (+0.5, −0.5, −0.5) → (−0.5, −0.5, −0.5) → (−0.5, +0.5, −0.5)
    //       UV:         (0, 0)            (1, 0)            (1, 1)
    //    Triangle B: (+0.5, −0.5, −0.5) → (−0.5, +0.5, −0.5) → (+0.5, +0.5, −0.5)
    //       UV:         (0, 0)            (1, 1)            (0, 1)
    public static final float[] BACK = {
        //   X       Y       Z     |   U   

        0.5f, -0.5f, -0.5f,   0f,  0f,
        -0.5f, -0.5f, -0.5f,  1f,  0f,
        -0.5f,  0.5f, -0.5f,  1f,  1f,

         0.5f, -0.5f, -0.5f,  0f,  0f,
        -0.5f,  0.5f, -0.5f,  1f,  1f,
         0.5f,  0.5f, -0.5f,  0f,  1f
    };

    // ───────────────────────────────────────────────────────────────────────────
    // LEFT face (X = −0.5), viewed from −X side. CCW winding:
    //    Triangle A: (−0.5, −0.5, +0.5) → (−0.5, −0.5, −0.5) → (−0.5, +0.5, −0.5)
    //       UV:         (0, 0)            (1, 0)            (1, 1)
    //    Triangle B: (−0.5, −0.5, +0.5) → (−0.5, +0.5, −0.5) → (−0.5, +0.5, +0.5)
    //       UV:         (0, 0)            (1, 1)            (0, 1)
    public static final float[] LEFT = {
        //   X       Y       Z     |   U    V
        -0.5f, -0.5f,  0.5f,   0f,  0f,
        -0.5f, -0.5f, -0.5f,   1f,  0f,
        -0.5f,  0.5f, -0.5f,   1f,  1f,

        -0.5f, -0.5f,  0.5f,   0f,  0f,
        -0.5f,  0.5f, -0.5f,   1f,  1f,
        -0.5f,  0.5f,  0.5f,   0f,  1f
    };

    // ───────────────────────────────────────────────────────────────────────────
    // RIGHT face (X = +0.5), viewed from +X side. CCW winding:
    //    Triangle A: (+0.5, −0.5, −0.5) → (+0.5, −0.5, +0.5) → (+0.5, +0.5, +0.5)
    //       UV:         (0, 0)            (1, 0)            (1, 1)
    //    Triangle B: (+0.5, −0.5, −0.5) → (+0.5, +0.5, +0.5) → (+0.5, +0.5, −0.5)
    //       UV:         (0, 0)            (1, 1)            (0, 1)
    public static final float[] RIGHT = {
        //   X       Y       Z     |   U    V
         0.5f, -0.5f, -0.5f,   0f,  0f,
         0.5f, -0.5f,  0.5f,   1f,  0f,
         0.5f,  0.5f,  0.5f,   1f,  1f,

         0.5f, -0.5f, -0.5f,   0f,  0f,
         0.5f,  0.5f,  0.5f,   1f,  1f,
         0.5f,  0.5f, -0.5f,   0f,  1f
    };
}
