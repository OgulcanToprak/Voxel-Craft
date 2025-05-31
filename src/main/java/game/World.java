// World.java (in package game)
package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gfx.Shader;

public class World {
    private final List<Block>    blocks   = new ArrayList<>();
    private final HashSet<Vec3i> occupied = new HashSet<>();

    public World(Shader shader) {
        generateTerrain(shader, 20, 20, 0.3f, 2f);
    }

    public void render(Matrix4f view, Matrix4f projection) {
        for (Block b : blocks) {
            b.render(view, projection, occupied);
        }
    }

    public void delete() {
        for (Block b : blocks) {
            b.delete();
        }
    }

    private void generateTerrain(Shader shader, int width, int depth, float scale, float heightMagnitude) {
        for (int x = -width/2; x < width/2; x++) {
            for (int z = -depth/2; z < depth/2; z++) {
                float raw = (float)((Math.sin(x * scale) + Math.cos(z * scale)) * heightMagnitude);
                int h = (int) raw;
                if (h < 0) h = 0;    // clamp negatives → always at least y=0

                for (int y = 0; y <= h; y++) {
                    BlockType type = (y == h) ? BlockType.GRASS : BlockType.SOIL;
                    Block cube = new Block(shader, new Vector3f(x, y, z), type);
                    blocks.add(cube);
                    occupied.add(new Vec3i(x, y, z));
                }
            }
        }
    }

 //  private void generateTerrain(Shader shader, int width, int depth, float scale, float heightMagnitude) {
 //      // clear first 
 //      blocks.clear();
 //      occupied.clear();

 //      // just lace3 cubes manullay for testing
 //      addCube(shader, 0, 0, 0, BlockType.GRASS);


 //      
 //  }

  //  private void addCube(Shader shader, int x, int y, int z, BlockType type) {
  //     Block block = new Block(shader, new Vector3f(x, y, z), type);
  //      blocks.add(block);
  //      occupied.add(new Vec3i(x,y,z));
  //  }

}
