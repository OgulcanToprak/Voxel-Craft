import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

import game.Camera;
import game.Player;
import game.World;
import gfx.Renderer;
import gfx.Shader;
import gfx.Window;

public class Main {
    public static void main(String[] args) throws Exception {
        Window window = new Window();
        if (!window.init(1280, 720, "Voxel Craft")) {
            System.err.println("Failed to initialize window");
            return;
        }

        // Initial terrain parameters
        float scale     = 0.05f;
        float heightMag = 16f;
        float prevScale     = scale;
        float prevHeightMag = heightMag;

        long  timer  = System.currentTimeMillis();
        int   frames = 0;

        // Choose a spawn position (try to place it above the terrain)
        float spawnX = 4.12f;
        float spawnY = 13.96f;
        float spawnZ = 1.39f;

        boolean playerMode           = false;
        boolean playerModeToggleHeld = false;
        boolean startInFirstPerson   = false;

        // ─── Enable OpenGL settings ─────────────────────────────────────
        glEnable(GL_DEPTH_TEST);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D); // Required if you use STB fonts, etc.

        // ─── Create renderer & clear color ──────────────────────────────
        Renderer renderer = new Renderer();
        renderer.setClearColor(0.702f, 0.831f, 1.0f, 1.0f); // sky‐blue

        // ─── Load shader & bind texture unit ────────────────────────────
        Shader shader = new Shader("shaders/vertex.glsl", "shaders/fragment.glsl");
        shader.use();
        int texLoc = glGetUniformLocation(shader.getProgramId(), "texture0");
        glUniform1i(texLoc, 0);

        // ─── Create world (generates chunks) ─────────────────────────────
        World world = new World(shader);

        // ─── Create camera & player ─────────────────────────────────────
        Camera camera = new Camera();
        Player player = new Player(camera, world, new Vector3f(spawnX, spawnY, spawnZ),
                                   startInFirstPerson);

        // ─── Main loop ───────────────────────────────────────────────────
        while (!window.shouldClose()) {
            renderer.clear();
            window.pollMouse();

            // ─── Toggle “player mode” with key 9 ──────────────────────
            if (window.isKeyPressed(GLFW.GLFW_KEY_9)) {
                if (!playerModeToggleHeld) {
                    playerMode = !playerMode;
                    playerModeToggleHeld = true;
                    System.out.println("Player Mode: " + (playerMode ? "ON" : "OFF"));
                }
            } else {
                playerModeToggleHeld = false;
            }

            // ─── Movement / camera control ──────────────────────────────
            if (playerMode) {
                // In “player mode”, let the Player class handle movement/collision
                // and position the camera inside the player's head/torso.
                player.update(window, true);
            } else {
                // Free‐camera mode: do not call player.update(...)
                // Instead, allow WASD to zoom in/out and mouse‐look.
                if (window.isKeyPressed(GLFW.GLFW_KEY_W)) camera.zoom(-0.2f);
                if (window.isKeyPressed(GLFW.GLFW_KEY_S)) camera.zoom( 0.2f);

                // Mouse‐look in free mode (right‐mouse held)
                if (window.isMouseButtonDown()) {
                    float sensitivity = 0.3f;
                    float dy = (float) window.getDeltaY();
                    float dx = (float) window.getDeltaX();

                    Vector3f rot = camera.getRotation();
                    rot.x += dy * sensitivity;
                    rot.y += dx * sensitivity;
                    rot.x = Math.max(-89f, Math.min(89f, rot.x));
                    if (rot.y < 0)    rot.y += 360f;
                    if (rot.y >= 360f) rot.y -= 360f;
                    camera.setRotation(rot);
                }
            }

            // ─── Terrain parameter tweaks ─────────────────────────────────
            if (window.isKeyPressed(GLFW.GLFW_KEY_1)) {
                scale -= 0.001f;
                world.updateSettings(scale, heightMag);
            }
            if (window.isKeyPressed(GLFW.GLFW_KEY_2)) {
                scale += 0.001f;
                world.updateSettings(scale, heightMag);
            }
            if (window.isKeyPressed(GLFW.GLFW_KEY_3)) {
                heightMag -= 0.5f;
                world.updateSettings(scale, heightMag);
            }
            if (window.isKeyPressed(GLFW.GLFW_KEY_4)) {
                heightMag += 0.5f;
                world.updateSettings(scale, heightMag);
            }

            // Print out changes to scale/heightMag
            if (scale != prevScale || heightMag != prevHeightMag) {
          //      System.out.printf("Scale: %.3f | HeightMag: %.1f%n", scale, heightMag);
                prevScale = scale;
                prevHeightMag = heightMag;
            }

            // ─── Render 3D world ───────────────────────────────────────────
            Matrix4f view       = camera.getViewMatrix(playerMode);
            Matrix4f projection = new Matrix4f()
                    .perspective((float) Math.toRadians(45.0), 1280f / 720f, 0.1f, 100f);

            world.render(view, projection);
            player.render(view, projection, shader, playerMode);

            // ─── Switch to orthographic mode for any 2D UI (optional) ─────
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
            glOrtho(0, 1280, 720, 0, -1, 1);

            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();

            // (Render any 2D UI here)

            // ─── Restore 3D matrices ─────────────────────────────────────
            glPopMatrix();
            glMatrixMode(GL_PROJECTION);
            glPopMatrix();
            glMatrixMode(GL_MODELVIEW);

            // ─── FPS counter (every second) ───────────────────────────────
            frames++;
            if (System.currentTimeMillis() - timer >= 1000) {
           //     System.err.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }

            window.update();
        }

        // ─── Cleanup on exit ────────────────────────────────────────────
        world.delete();
        shader.delete();
        window.terminate();
    }
}