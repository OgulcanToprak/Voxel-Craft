import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
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

        // Enable depth buffer and back‐face culling once:
        glEnable(GL_DEPTH_TEST);
   //     glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        Renderer renderer = new Renderer();
        renderer.setClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        Shader shader = new Shader("shaders/vertex.glsl", "shaders/fragment.glsl");
        shader.use();
        int texLoc = glGetUniformLocation(shader.getProgramId(), "texture0");
        glUniform1i(texLoc, 0); // Tell shader that "texture0" uses GL_TEXTURE0

        Camera camera = new Camera();
        World world = new World(shader);

        while (!window.shouldClose()) {
            renderer.clear();
            window.pollMouse();

            // Zoom using W / S
            if (window.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_W)) {
                camera.zoom(-0.2f);
            }
            if (window.isKeyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_S)) {
                camera.zoom(0.2f);
            }

            // Rotate camera on left-click & drag
            if (window.isMouseButtonDown()) {
                float mouseSensitivity = 0.4f;
                float dx = (float) window.getDeltaY();
                float dy = (float) window.getDeltaX();
                camera.rotate(dx * mouseSensitivity, dy * mouseSensitivity);
            }

            Matrix4f view = camera.getViewMatrix();
            Matrix4f projection = 
                new Matrix4f().perspective((float)Math.toRadians(45.0), 800f/600f, 0.1f, 100f);

            world.render(view, projection);
            window.update();
        }

        world.delete();
        shader.delete();
        window.terminate();
    }
}
