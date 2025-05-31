package game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import gfx.Mesh;
import gfx.Shader;
import gfx.Texture;

public class Block {
    private static Mesh    topMesh;
    private static Mesh bottomMesh;
    private static Mesh  frontMesh;
    private static Mesh   backMesh;
    private static Mesh   leftMesh;
    private static Mesh  rightMesh;

    private static Map<BlockType, Texture> textureMap = new HashMap<>();

    private final Shader    shader;
    private final Vector3f  position;
    private final BlockType type;

    public Block(Shader shader, Vector3f position, BlockType type) {
        this.shader   = shader;
        this.position = position;
        this.type     = type;

        // — load textures just once —
        if (!textureMap.containsKey(BlockType.GRASS)) {
            textureMap.put(BlockType.GRASS, new Texture("resources/textures/grass.png"));      // top‐face for grass
            textureMap.put(BlockType.SOIL,  new Texture("resources/textures/soil.png"));       // for soil top & bottom
            textureMap.put(BlockType.STONE, new Texture("resources/textures/grass_side.png")); // side texture for grass
        }

        // — build each face’s Mesh once —
        if (topMesh    == null) topMesh    = new Mesh(CubeData.TOP,    shader.getProgramId());
        if (bottomMesh == null) bottomMesh = new Mesh(CubeData.BOTTOM, shader.getProgramId());
        if (frontMesh  == null) frontMesh  = new Mesh(CubeData.FRONT,  shader.getProgramId());
        if (backMesh   == null) backMesh   = new Mesh(CubeData.BACK,   shader.getProgramId());
        if (leftMesh   == null) leftMesh   = new Mesh(CubeData.LEFT,   shader.getProgramId());
        if (rightMesh  == null) rightMesh  = new Mesh(CubeData.RIGHT,  shader.getProgramId());
    }

    /** Renders only the faces that have no neighbor in that direction. */
    public void render(Matrix4f view, Matrix4f projection, HashSet<Vec3i> occupied) {
        // 1) build Model matrix for this cube
        Matrix4f model = new Matrix4f().translate(position);

        shader.use();
        shader.setUniformMatrix4f("model",      model);
        shader.setUniformMatrix4f("view",       view);
        shader.setUniformMatrix4f("projection", projection);

        // 2) figure out integer coords of this block
        int bx = (int)Math.floor(position.x);
        int by = (int)Math.floor(position.y);
        int bz = (int)Math.floor(position.z);


        // 3) test for all six neighbors
        boolean hasBelow = occupied.contains(new Vec3i(bx, by - 1, bz));
        boolean hasAbove = occupied.contains(new Vec3i(bx, by + 1, bz));
        boolean hasLeft  = occupied.contains(new Vec3i(bx - 1, by, bz));
        boolean hasRight = occupied.contains(new Vec3i(bx + 1, by, bz));
        boolean hasFront = occupied.contains(new Vec3i(bx, by, bz + 1));
        boolean hasBack  = occupied.contains(new Vec3i(bx, by, bz - 1));

        // 4) draw the four SIDE faces (if there is no neighbor in that direction):
        //    – grass cubes use grass_side.png (stored under BlockType.STONE) on their sides
        //    – soil (and any other type) uses its own texture on the sides
        BlockType sideType = (type == BlockType.GRASS) ? BlockType.STONE : type;
        textureMap.get(sideType).bind();
        if (!hasFront)  frontMesh.render();
        if (!hasBack)   backMesh.render();
        if (!hasLeft)   leftMesh.render();
        if (!hasRight)  rightMesh.render();

        // 5) draw BOTTOM face if there is no block directly below
        if (!hasBelow) {
            textureMap.get(BlockType.SOIL).bind();  // bottom of every cube is “soil”
            bottomMesh.render();
        }

        // 6) draw TOP face if there is no block directly above
        if (!hasAbove) {
            if (type == BlockType.GRASS) {
                // a grass block’s top is grass.png with polygon offset
                textureMap.get(BlockType.GRASS).bind();
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(1.0f, 1.0f);
                topMesh.render();
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            } 
            else if (type == BlockType.SOIL) {
                // *** This ensures that any “soil” cube which happens to be the top of a cliff now
                // *** actually shows a flat soil top (instead of leaving a black gap).
                textureMap.get(BlockType.SOIL).bind();
                topMesh.render();
            }
            // … (if you have more block types that need their own “top face,” check them here) …
        }
    }

    public void delete() {
        for (Texture tex : textureMap.values()) {
            tex.delete();
        }
    }

    public Vector3f getPosition() {
        return position;
    }
}
