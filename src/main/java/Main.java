import org.joml.Matrix4f;
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
import game.World;
import gfx.Renderer;
import gfx.Shader;
import gfx.Window;

public class Main {
    public static void main(String[] args) throws Exception {
        Window window = new Window();
        if (!window.init(800, 600, "Voxel Craft")) {
            System.err.println("Failed to initialize window");
            return;
        }

        float scale = 0.05f;
        float heightMag = 16f;
        float prevScale = scale;
        float prevHeightMag = heightMag;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int frames = 0;

        // ✅ Enable OpenGL settings
        glEnable(GL_DEPTH_TEST);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D); // Required for STB fonts

        Renderer renderer = new Renderer();
        renderer.setClearColor(0.702f, 0.831f, 1.0f, 1.0f); // #76A8FF

        Shader shader = new Shader("shaders/vertex.glsl", "shaders/fragment.glsl");
        shader.use();
        int texLoc = glGetUniformLocation(shader.getProgramId(), "texture0");
        glUniform1i(texLoc, 0);

        Camera camera = new Camera();
        World world = new World(shader);

        while (!window.shouldClose()) {
            renderer.clear();
            window.pollMouse();

            // Controls
            if (window.isKeyPressed(GLFW.GLFW_KEY_W)) camera.zoom(-0.2f);
            if (window.isKeyPressed(GLFW.GLFW_KEY_S)) camera.zoom(0.2f);

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

            if (scale != prevScale || heightMag != prevHeightMag) {
                System.out.printf("Scale: %.3f | Magnitude: %.1f%n", scale, heightMag);
                prevScale = scale;
                prevHeightMag = heightMag;
            }

            if (window.isMouseButtonDown()) {
                float mouseSensitivity = 0.4f;
                float dx = (float) window.getDeltaY();
                float dy = (float) window.getDeltaX();
                camera.rotate(dx * mouseSensitivity, dy * mouseSensitivity);
            }

            // Render world
            Matrix4f view = camera.getViewMatrix();
            Matrix4f projection = new Matrix4f().perspective(
                    (float) Math.toRadians(45.0), 800f / 600f, 0.1f, 100f);
            world.render(view, projection);

            // Switch to orthographic mode for 2D UI
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
            glOrtho(0, 800, 600, 0, -1, 1);

            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();

            
            // Restore perspective matrix
            glPopMatrix();
            glMatrixMode(GL_PROJECTION);
            glPopMatrix();
            glMatrixMode(GL_MODELVIEW);


            frames++;
            if(System.currentTimeMillis() - timer >= 1000){
                System.err.println("FPS :" + frames);
                frames = 0;
                timer += 1000;
            }
            window.update();
        }

        world.delete();
        shader.delete();

        window.terminate();
    }
}
