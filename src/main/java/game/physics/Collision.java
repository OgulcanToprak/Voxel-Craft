// game/physics/Collision.java
package game.physics;

import java.util.Set;

import org.joml.Vector3f;

import game.Vec3i;

public class Collision {

    /**
     * Resolves the player's desired movement vector against all occupied blocks
     * so that the player's AABB does not penetrate any block.
     *
     * @param playerPos      The player's current center‐position in world space.
     * @param desiredMove    The raw movement vector (dx, dy, dz) from input.
     * @param halfExtents    The player's AABB half‐extents (width/2, height/2, depth/2).
     * @param occupiedBlocks A set of Vec3i representing integer (x,y,z) coords of all solid blocks.
     * @return A new Vector3f containing the “corrected” movement vector that avoids collisions.
     */
    public static Vector3f resolveMovement(Vector3f playerPos,
                                       Vector3f desiredMove,
                                       Vector3f halfExtents,
                                       Set<Vec3i> occupiedBlocks) {
    Vector3f corrected = new Vector3f(desiredMove);
    Vector3f tempPos = new Vector3f(playerPos);

    // --- X movement ---
    if (corrected.x != 0f) {
        float dx = corrected.x;
        tempPos.add(dx, 0f, 0f);
        AABB boxX = new AABB(tempPos, halfExtents);
        for (Vec3i block : occupiedBlocks) {
            AABB blockBox = new AABB(
                new Vector3f(block.x, block.y, block.z),
                new Vector3f(block.x + 1f, block.y + 1f, block.z + 1f),
                true
            );
            if (boxX.intersects(blockBox)) {
                corrected.x = 0f;
                tempPos.sub(dx, 0f, 0f); // revert
                break;
            }
        }
    }

    // --- Z movement ---
    if (corrected.z != 0f) {
        float dz = corrected.z;
        tempPos.add(0f, 0f, dz);
        AABB boxZ = new AABB(tempPos, halfExtents);
        for (Vec3i block : occupiedBlocks) {
            AABB blockBox = new AABB(
                new Vector3f(block.x, block.y, block.z),
                new Vector3f(block.x + 1f, block.y + 1f, block.z + 1f),
                true
            );
            if (boxZ.intersects(blockBox)) {
                corrected.z = 0f;
                tempPos.sub(0f, 0f, dz); // revert
                break;
            }
        }
    }

    // --- Y movement (gravity / jump) ---
    if (corrected.y != 0f) {
        float dy = corrected.y;
        tempPos.add(0f, dy, 0f);
        AABB boxY = new AABB(tempPos, halfExtents);
        for (Vec3i block : occupiedBlocks) {
            AABB blockBox = new AABB(
                new Vector3f(block.x, block.y, block.z),
                new Vector3f(block.x + 1f, block.y + 1f, block.z + 1f),
                true
            );
            if (boxY.intersects(blockBox)) {
                corrected.y = 0f;
                tempPos.sub(0f, dy, 0f); // revert
                break;
            }
        }
    }

    return corrected;
}

}
