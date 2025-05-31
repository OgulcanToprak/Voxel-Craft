// game/Chunk.java
package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import gfx.Shader;
import noise.OpenSimplex2F;

public class Chunk {
    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 32;
    public static final int CHUNK_SIZE_Z = 16;

    private final List<Block> blocks = new ArrayList<>();
    private final HashSet<Vec3i> occupied = new HashSet<>();
    private final int chunkX, chunkZ;

    public Chunk(Shader shader, OpenSimplex2F noise, int chunkX, int chunkZ, float scale, float heightMag) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        generate(shader, noise, scale, heightMag);
    }

private void generate(Shader shader, OpenSimplex2F noise, float scale, float heightMag) {
    int originX = chunkX * CHUNK_SIZE_X;
    int originZ = chunkZ * CHUNK_SIZE_Z;
    Random rand = new Random((originX * 73856093) ^ (originZ * 19349663)); // seeded for consistent randomness per chunk

    for (int x = 0; x < CHUNK_SIZE_X; x++) {
        for (int z = 0; z < CHUNK_SIZE_Z; z++) {
            float nx = (originX + x) * scale;
            float nz = (originZ + z) * scale;
            float raw = (float) noise.noise2(nx, nz);
            int height = (int) ((raw + 1f) * 0.5f * heightMag);

            for (int y = 0; y <= height; y++) {
                BlockType type;

                if (y == height) {
                    type = BlockType.GRASS;
                } else {
                    // Deep layers have a chance to become stone or ore
                    if (y < height - 3) {
                        int chance = rand.nextInt(100);
                        if (chance < 5) type = BlockType.IRON_ORE;     // 5% chance
                        else if (chance < 10) type = BlockType.COAL_ORE; // 5% chance
                        else if (chance < 25) type = BlockType.STONE;    // 15% chance
                        else type = BlockType.SOIL;
                    } else {
                        type = BlockType.SOIL;
                    }
                }

                Block block = new Block(shader, new Vector3f(originX + x, y, originZ + z), type);
                blocks.add(block);
                occupied.add(new Vec3i(originX + x, y, originZ + z));
            }
        }
    }
}


    public void render(org.joml.Matrix4f view, org.joml.Matrix4f projection) {
        for (Block b : blocks) {
            b.render(view, projection, occupied);
        }
    }

    public void delete() {
        for (Block b : blocks) {
            b.delete();
        }
    }
}
