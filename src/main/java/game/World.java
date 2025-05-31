// game/World.java
package game;

import java.util.ArrayList;
import java.util.List;

import gfx.Shader;
import noise.OpenSimplex2F;

public class World {
    private final List<Chunk> chunks = new ArrayList<>();
    private final OpenSimplex2F noise = new OpenSimplex2F(123456); // seed

    private float scale = 0.06f;
    private float heightMag = 12f;
    private final Shader shader;

    public World(Shader shader) {
        this.shader = shader;
        regenerateChunks();
    }

    public void regenerateChunks() {
        chunks.clear();
        int chunkCount = 2;
        for (int cx = -chunkCount / 2; cx < chunkCount / 2; cx++) {
            for (int cz = -chunkCount / 2; cz < chunkCount / 2; cz++) {
                chunks.add(new Chunk(shader, noise, cx, cz, scale, heightMag));
            }
        }
    }

    public void updateSettings(float newScale, float newHeightMag) {
        this.scale = newScale;
        this.heightMag = newHeightMag;
        regenerateChunks();
    }

    public void render(org.joml.Matrix4f view, org.joml.Matrix4f projection) {
        for (Chunk chunk : chunks) {
            chunk.render(view, projection);
        }
    }

    public void delete() {
        for (Chunk chunk : chunks) {
            chunk.delete();
        }
    }
}
