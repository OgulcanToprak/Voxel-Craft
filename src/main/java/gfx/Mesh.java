package gfx;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import org.lwjgl.system.MemoryUtil;

public class Mesh {
    private int vboId;
    private int vertexCount;
    private int posAttrib;
    private int texCoordAttrib;

    public Mesh(float[] vertices, int shaderProgramId) {
        vertexCount = vertices.length / 5;

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        // Get shader attribute locations
        posAttrib = glGetAttribLocation(shaderProgramId, "position");
        texCoordAttrib = glGetAttribLocation(shaderProgramId, "texCoord");

        if (posAttrib == -1 || texCoordAttrib == -1) {
            throw new RuntimeException("Shader missing 'position' or 'texCoord' attribute.");
        }

        // Enable position (3 floats)
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);

        // Enable texCoord (2 floats)
        glEnableVertexAttribArray(texCoordAttrib);
        glVertexAttribPointer(texCoordAttrib, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glEnableVertexAttribArray(posAttrib);
        glEnableVertexAttribArray(texCoordAttrib);

        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glVertexAttribPointer(texCoordAttrib, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        glDisableVertexAttribArray(posAttrib);
        glDisableVertexAttribArray(texCoordAttrib);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void renderFace(Shader shader, float[] vertices) {
    int vboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboId);

    FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
    buffer.put(vertices).flip();
    glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

    int posAttrib = glGetAttribLocation(shader.getProgramId(), "position");
    int texCoordAttrib = glGetAttribLocation(shader.getProgramId(), "texCoord");

    glEnableVertexAttribArray(posAttrib);
    glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);

    glEnableVertexAttribArray(texCoordAttrib);
    glVertexAttribPointer(texCoordAttrib, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

    glDrawArrays(GL_TRIANGLES, 0, vertices.length / 5);

    glDisableVertexAttribArray(posAttrib);
    glDisableVertexAttribArray(texCoordAttrib);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDeleteBuffers(vboId);
    MemoryUtil.memFree(buffer);
    }


    public static void renderRaw(float [] vertices, Shader shader) {
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        int posAttrib = glGetAttribLocation(shader.getProgramId(), "position");
        int texCoordAttrib = glGetAttribLocation(shader.getProgramId(), "texCoord");

        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);

        glEnableVertexAttribArray(texCoordAttrib);
        glVertexAttribPointer(texCoordAttrib, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

        glDrawArrays(GL_TRIANGLES, 0, vertices.length / 5);

        glDisableVertexAttribArray(posAttrib);
        glDisableVertexAttribArray(texCoordAttrib);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);
        MemoryUtil.memFree(buffer);
    }


    public void delete() {
        glDeleteBuffers(vboId);
    }
}
