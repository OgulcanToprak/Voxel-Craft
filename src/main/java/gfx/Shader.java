package gfx;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER; // for GL_COMPILE_STATUS, GL_VERTEX_SHADER, etc.
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import org.lwjgl.system.MemoryStack;

public class Shader {
    private int programId;

    public Shader(String vertexPath, String fragmentPath) throws Exception {
        String vertexCode   = new String(Files.readAllBytes(Paths.get(vertexPath)));
        String fragmentCode = new String(Files.readAllBytes(Paths.get(fragmentPath)));

        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexCode);
        glCompileShader(vertexId);
        if (glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Vertex shader failed:\n" + glGetShaderInfoLog(vertexId));

        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentCode);
        glCompileShader(fragmentId);
        if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Fragment shader failed:\n" + glGetShaderInfoLog(fragmentId));

        programId = glCreateProgram();
        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader linking failed:\n" + glGetProgramInfoLog(programId));

        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);
    }

    public void use() {
        glUseProgram(programId);
    }

    public void delete() {
        glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(programId, name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform3f(String name, float x, float y, float z) {
        int location = glGetUniformLocation(programId, name);
        glUniform3f(location, x, y, z);
    }

    /** ← ADD THIS METHOD: */
    public void setUniform1i(String name, int value) {
        int location = glGetUniformLocation(programId, name);
        glUniform1i(location, value);
    }
}
