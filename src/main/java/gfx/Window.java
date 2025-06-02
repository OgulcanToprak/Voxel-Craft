package gfx;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Window {
    private long window;

    // ─── For mouse movement ──────────────────────────────────────────────
    private double lastMouseX, lastMouseY;
    private double deltaX, deltaY;

    // ─── For scroll (if you need it later) ───────────────────────────────
    private double scrollOffsetY = 0;

    // ─── For deltaTime ──────────────────────────────────────────────────
    private double lastFrameTime;

    public boolean init(int width, int height, String title) {
        if (!glfwInit()) {
            return false;
        }

        window = glfwCreateWindow(width, height, title, 0, 0);
        if (window == 0) {
            return false;
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(1); // Enable v-sync

        // Initialize timing:
        lastFrameTime = glfwGetTime();
        return true;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void update() {
        // Before swapping, compute deltaTime for this frame:
        double currentTime = glfwGetTime();
        // (We update lastFrameTime here so getDeltaTimeInSeconds() will return the correct dt next call.)
        

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    /** 
     * New: check any GLFW mouse button (e.g. GLFW_MOUSE_BUTTON_1 for left, 2 for right). 
     */
    public boolean isButtonPressed(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

        /**
     * Convenience: “true” if left‐mouse is currently down.
     */
    public boolean isMouseButtonDown() {
        return isButtonPressed(GLFW_MOUSE_BUTTON_LEFT);
    }

    public void terminate() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    /** 
     * Call this each frame (before reading getDeltaX/Y), so we can track mouse deltas. 
     */
    public void pollMouse() {
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        glfwGetCursorPos(window, xpos, ypos);

        deltaX = xpos[0] - lastMouseX;
        deltaY = ypos[0] - lastMouseY;

        lastMouseX = xpos[0];
        lastMouseY = ypos[0];
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public long getWindowHandle() {
        return window;
    }

    public double consumeScrollY() {
        double offset = scrollOffsetY;
        scrollOffsetY = 0;
        return offset;
    }

    /**
     * New: returns time (in seconds) since the last frame. 
     * 
     * Usage note: 
     *     - Call update() at the end of your main loop (which sets lastFrameTime = current time).
     *     - Then, at the start of the next frame, getDeltaTimeInSeconds() will return (now − lastFrameTime).
     */
public float getDeltaTimeInSeconds() {
    double currentTime = glfwGetTime();
    double delta = currentTime - lastFrameTime;
    lastFrameTime = currentTime; // ✅ move this here, so delta is correct
    if (delta < 0) delta = 0;
    return (float) delta;
}

}
