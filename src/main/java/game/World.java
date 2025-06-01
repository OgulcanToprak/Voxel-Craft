package game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gfx.Shader;
import noise.OpenSimplex2F;

/**
 * The World class manages a dynamic set of Chunk instances using (chunkX, chunkZ) coordinates.
 */
public class World {
    private final Map<Vec2i, Chunk> chunks = new HashMap<>();
    private final OpenSimplex2F noise = new OpenSimplex2F(123456); // seed for terrain
    private float scale = 0.06f;   // controls noise frequency
    private float heightMag = 12f; // controls terrain height
    private final Shader shader;

    // Once a chunk’s center is 3 or more chunks away (in x or z), we delete it:
    private static final int CHUNK_UNLOAD_RADIUS = 2;

    public World(Shader shader) {
        this.shader = shader;
        regenerateChunks();
    }

    /**
     * Rebuild all chunks. Currently creates a 2×2 grid of chunks centered at (0,0).
     */
    public void regenerateChunks() {
        chunks.clear();
        int chunkCount = 2;
        for (int cx = -chunkCount / 2; cx < chunkCount / 2; cx++) {
            for (int cz = -chunkCount / 2; cz < chunkCount / 2; cz++) {
                Vec2i key = new Vec2i(cx, cz);
                Chunk chunk = new Chunk(shader, noise, cx, cz, scale, heightMag);
                chunks.put(key, chunk);
            }
        }
    }

    public void updateSettings(float newScale, float newHeightMag) {
        this.scale = newScale;
        this.heightMag = newHeightMag;
        regenerateChunks();
    }

    public void render(Matrix4f view, Matrix4f projection) {
        for (Chunk chunk : chunks.values()) {
            chunk.render(view, projection);
        }
    }

    public void delete() {
        // Delete all remaining chunks’ data (blocks, etc.). Does not delete shared textures/meshes.
        for (Chunk chunk : chunks.values()) {
            chunk.delete();
        }
        chunks.clear();
    }

    public float getHeightAt(float x, float z) {
        return (float) noise.noise2(x * scale, z * scale) * heightMag;
    }

    public Set<Vec3i> getAllOccupiedBlocks() {
        Set<Vec3i> all = new HashSet<>();
        for (Chunk chunk : chunks.values()) {
            all.addAll(chunk.getOccupiedBlockPositions());
        }
        return all;
    }

    public boolean isBlockOccupied(int x, int y, int z) {
        Vec3i key = new Vec3i(x, y, z);
        for (Chunk chunk : chunks.values()) {
            if (chunk.getOccupiedBlockPositions().contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ensure a chunk exists at the given world (x, z) position.
     * If not, generate it and put it in `chunks`.
     */
// In World.java:

/** 1) This method creates a chunk at (chunkX, chunkZ) if it isn’t already loaded. */
public void ensureChunkAt(float x, float z) {
    int chunkX = (int) Math.floor(x / Chunk.CHUNK_SIZE_X);
    int chunkZ = (int) Math.floor(z / Chunk.CHUNK_SIZE_Z);
    Vec2i key = new Vec2i(chunkX, chunkZ);
    if (!chunks.containsKey(key)) {
        Chunk newChunk = new Chunk(shader, noise, chunkX, chunkZ, scale, heightMag);
        chunks.put(key, newChunk);
    }
}

/** 2) This method walks the entire `chunks` map and deletes any chunk ≥ 3 chunks away. */
public void unloadFarChunks(Vector3f playerPosition) {
    int px = (int) Math.floor(playerPosition.x / Chunk.CHUNK_SIZE_X);
    int pz = (int) Math.floor(playerPosition.z / Chunk.CHUNK_SIZE_Z);

  // System.out.println("Player is in chunk: " + px + ", " + pz);
  // System.out.println("Chunks loaded before: " + chunks.size());

    Set<Vec2i> keysCopy = new HashSet<>(chunks.keySet());
    Set<Vec2i> chunksToRemove = new HashSet<>();

    for (Vec2i key : keysCopy) {
        int dx = key.x - px;
        int dz = key.z - pz;
    //    System.out.println("Checking chunk: " + key + " (dx=" + dx + ", dz=" + dz + ")");
        if (Math.abs(dx) >= CHUNK_UNLOAD_RADIUS || Math.abs(dz) >= CHUNK_UNLOAD_RADIUS) {
          //  System.out.println("Marking for removal: " + key);
            chunksToRemove.add(key);
        }
    }

    for (Vec2i key : chunksToRemove) {
        Chunk removed = chunks.remove(key);
        if (removed != null) {
        //    System.out.println("Removing chunk: " + key);
            removed.delete();
        }
    }

  //  System.out.println("Chunks loaded after: " + chunks.size());
}



    // In game/World.java, add:
public boolean isChunkLoaded(int chunkX, int chunkZ) {
    return chunks.containsKey(new Vec2i(chunkX, chunkZ));
}

}
