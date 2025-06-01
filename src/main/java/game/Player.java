package game;

import java.util.Set;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import game.physics.GravitySystem;
import gfx.Window;

public class Player {
    private final Camera camera;
    private final World  world;
    private final float  moveSpeed        = 0.1f;
    private final float  mouseSensitivity = 0.3f;
    private float verticalVelocity = 0f;
    private boolean isOnGround = false;
    private int chunkLoadCooldown = 0;

    private final Vector3f halfExtents = new Vector3f(0.25f, 0.9f, 0.25f); // AABB size
    private final float eyeOffset = 0.2f;                                   // Offset from top
    private final float eyeHeight = halfExtents.y * 2f;

    private Vector3f playerPosition; // actual center of collision box

    public Player(Camera camera, World world, Vector3f spawnPosition) {
        this.camera = camera;
        this.world  = world;
        this.playerPosition = new Vector3f(spawnPosition);
        updateCameraPosition(); // set initial camera position
    }

public void update(Window window) {
    // Only generate chunks every N frames
    if (chunkLoadCooldown <= 0) {
        int px = (int)Math.floor(playerPosition.x / Chunk.CHUNK_SIZE_X);
        int pz = (int)Math.floor(playerPosition.z / Chunk.CHUNK_SIZE_Z);

        // ─── Generate TWO chunks: current + “forward” ────────────────────────
        float chunkWorldX = px * Chunk.CHUNK_SIZE_X + Chunk.CHUNK_SIZE_X / 2f;
        float chunkWorldZ = pz * Chunk.CHUNK_SIZE_Z + Chunk.CHUNK_SIZE_Z / 2f;
        world.ensureChunkAt(chunkWorldX, chunkWorldZ);

        Vector3f forward = camera.getForwardDirection();
        int fpx = (int)Math.floor((playerPosition.x + forward.x * Chunk.CHUNK_SIZE_X) / Chunk.CHUNK_SIZE_X);
        int fpz = (int)Math.floor((playerPosition.z + forward.z * Chunk.CHUNK_SIZE_Z) / Chunk.CHUNK_SIZE_Z);
        float forwardChunkX = fpx * Chunk.CHUNK_SIZE_X + Chunk.CHUNK_SIZE_X / 2f;
        float forwardChunkZ = fpz * Chunk.CHUNK_SIZE_Z + Chunk.CHUNK_SIZE_Z / 2f;
        world.ensureChunkAt(forwardChunkX, forwardChunkZ);
        // ─────────────────────────────────────────────────────────────────────

        // ✅ Right here: unload anything outside your unload-radius
        world.unloadFarChunks(playerPosition);

        chunkLoadCooldown = 15;
    } else {
        chunkLoadCooldown--;
    }

    handleMouseLook(window);
    handleMovement(window);
    updateCameraPosition();
}



    private void handleMouseLook(Window window) {
        float dy = (float) window.getDeltaY();
        float dx = (float) window.getDeltaX();

        Vector3f rot = camera.getRotation();
        rot.x += dy * mouseSensitivity; // pitch
        rot.y += dx * mouseSensitivity; // yaw

        rot.x = Math.max(-89f, Math.min(89f, rot.x));
        if (rot.y < 0)    rot.y += 360f;
        if (rot.y >= 360f) rot.y -= 360f;

        camera.setRotation(rot);
    }

    private void handleMovement(Window window) {
        Vector3f rawForward = camera.getForwardDirection();
        Vector3f rawRight   = camera.getRightDirection();

        Vector3f forward = new Vector3f(rawForward.x, 0f, rawForward.z).normalize();
        Vector3f right   = new Vector3f(rawRight.x,   0f, rawRight.z).normalize();

        Vector3f movement = new Vector3f();
        if (window.isKeyPressed(GLFW.GLFW_KEY_W))     movement.add(forward);
        if (window.isKeyPressed(GLFW.GLFW_KEY_S))     movement.sub(forward);
        if (window.isKeyPressed(GLFW.GLFW_KEY_A))     movement.add(right);
        if (window.isKeyPressed(GLFW.GLFW_KEY_D))     movement.sub(right);

        if (movement.lengthSquared() > 0) {
            movement.normalize().mul(moveSpeed);
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE) && isOnGround) {
            verticalVelocity = 0.4f;
            isOnGround = false;
        }

        Set<Vec3i> occupied = world.getAllOccupiedBlocks();

        GravitySystem.GravityResult result = new GravitySystem.GravityResult();
        Vector3f correctedMove = GravitySystem.applyGravity(
            playerPosition,
            movement,
            verticalVelocity,
            halfExtents,
            occupied,
            result
        );

        if (correctedMove.lengthSquared() > 0) {
            playerPosition.add(correctedMove);
        }

        verticalVelocity = result.newVelocity;
        isOnGround = result.isOnGround;
    }

    private void updateCameraPosition() {
        camera.setPosition(
            playerPosition.x,
            playerPosition.y + (eyeHeight / 2f - eyeOffset),
            playerPosition.z
        );
    }

    public Camera getCamera() {
        return camera;
    }
}
