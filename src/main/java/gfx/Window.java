package gfx;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
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
    private double lastMouseX, lastMouseY;
    private double deltaX, deltaY;
    private double scrollOffsetY = 0;

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
        return true;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public void terminate() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void pollMouse() {
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        org.lwjgl.glfw.GLFW.glfwGetCursorPos(window, xpos, ypos);
    
        deltaX = xpos[0] - lastMouseX;
        deltaY = ypos[0] - lastMouseY;
    
        lastMouseX = xpos[0];
        lastMouseY = ypos[0];
    }

    public boolean isMouseButtonDown() {
    return glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
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

}
