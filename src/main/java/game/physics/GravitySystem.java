// game/physics/GravitySystem.java
package game.physics;

import java.util.Set;

import org.joml.Vector3f;

import game.Vec3i;

public class GravitySystem {

    private static final float GRAVITY = -0.02f;

    public static Vector3f applyGravity(Vector3f position,
                                        Vector3f motion,
                                        float verticalVelocity,
                                        Vector3f halfExtents,
                                        Set<Vec3i> occupiedBlocks,
                                        GravityResult result) {

        // Apply gravity to velocity
        verticalVelocity += GRAVITY;

        // Apply to motion.y
        motion.y = verticalVelocity;

        // Resolve collision
        Vector3f corrected = Collision.resolveMovement(position, motion, halfExtents, occupiedBlocks);

        // Determine ground or ceiling hit
        if (motion.y < 0f && corrected.y == 0f) {
            result.isOnGround = true;
            verticalVelocity = 0f;
        } else if (motion.y > 0f && corrected.y == 0f) {
            verticalVelocity = 0f; // Hit ceiling
        } else {
            result.isOnGround = false;
        }

        result.newVelocity = verticalVelocity;
        result.correctedMotion = corrected;

        return corrected;
    }

    public static class GravityResult {
        public boolean isOnGround = false;
        public float newVelocity = 0f;
        public Vector3f correctedMotion = new Vector3f();
    }
}
