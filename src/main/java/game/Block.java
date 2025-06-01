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

    // Shared, one‐per‐type textures:
    private static Map<BlockType, Texture> textureMap = new HashMap<>();

    private final Shader    shader;
    private final Vector3f  position;
    private final BlockType type;

    public Block(Shader shader, Vector3f position, BlockType type) {
        this.shader   = shader;
        this.position = position;
        this.type     = type;

        // — Load textures exactly once, on first Block construction —
        if (textureMap.isEmpty()) {
            textureMap.put(BlockType.GRASS,      new Texture("resources/textures/grass.png"));
            textureMap.put(BlockType.SOIL,       new Texture("resources/textures/soil.png"));
            textureMap.put(BlockType.GRASS_SIDE, new Texture("resources/textures/grass_side.png"));
            textureMap.put(BlockType.STONE,      new Texture("resources/textures/stone.png"));
            textureMap.put(BlockType.COAL_ORE,   new Texture("resources/textures/coal_ore.png"));
            textureMap.put(BlockType.IRON_ORE,   new Texture("resources/textures/iron_ore.png"));
        }

        // — Build each face’s Mesh once, on first Block construction —
        if (topMesh    == null) topMesh    = new Mesh(CubeData.TOP,    shader.getProgramId());
        if (bottomMesh == null) bottomMesh = new Mesh(CubeData.BOTTOM, shader.getProgramId());
        if (frontMesh  == null) frontMesh  = new Mesh(CubeData.FRONT,  shader.getProgramId());
        if (backMesh   == null) backMesh   = new Mesh(CubeData.BACK,   shader.getProgramId());
        if (leftMesh   == null) leftMesh   = new Mesh(CubeData.LEFT,   shader.getProgramId());
        if (rightMesh  == null) rightMesh  = new Mesh(CubeData.RIGHT,  shader.getProgramId());
    }

    /** Renders only the faces that have no neighbor in that direction. */
    public void render(Matrix4f view, Matrix4f projection, HashSet<Vec3i> occupied) {
        // 1) Build Model matrix for this block
        Matrix4f model = new Matrix4f().translate(position);

        shader.use();
        shader.setUniformMatrix4f("model",      model);
        shader.setUniformMatrix4f("view",       view);
        shader.setUniformMatrix4f("projection", projection);

        // 2) Determine this block’s integer (x,y,z)
        int bx = (int)Math.floor(position.x);
        int by = (int)Math.floor(position.y);
        int bz = (int)Math.floor(position.z);

        // 3) See if each neighbor is occupied
        boolean hasBelow = occupied.contains(new Vec3i(bx, by - 1, bz));
        boolean hasAbove = occupied.contains(new Vec3i(bx, by + 1, bz));
        boolean hasLeft  = occupied.contains(new Vec3i(bx - 1, by, bz));
        boolean hasRight = occupied.contains(new Vec3i(bx + 1, by, bz));
        boolean hasFront = occupied.contains(new Vec3i(bx, by, bz + 1));
        boolean hasBack  = occupied.contains(new Vec3i(bx, by, bz - 1));

        // 4) Draw only the faces that have no neighbor
        if (type == BlockType.GRASS) {
            // Sides use the grass‐side texture
            textureMap.get(BlockType.GRASS_SIDE).bind();
            if (!hasFront)  frontMesh.render();
            if (!hasBack)   backMesh.render();
            if (!hasLeft)   leftMesh.render();
            if (!hasRight)  rightMesh.render();

            // Bottom is soil
            if (!hasBelow) {
                textureMap.get(BlockType.SOIL).bind();
                bottomMesh.render();
            }

            // Top is grass, with a slight polygon offset to prevent z‐fighting
            if (!hasAbove) {
                textureMap.get(BlockType.GRASS).bind();
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(1.0f, 1.0f);
                topMesh.render();
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            }

        } else if (type == BlockType.SOIL) {
            textureMap.get(BlockType.SOIL).bind();
            if (!hasFront)  frontMesh.render();
            if (!hasBack)   backMesh.render();
            if (!hasLeft)   leftMesh.render();
            if (!hasRight)  rightMesh.render();
            if (!hasBelow)  bottomMesh.render();
            if (!hasAbove)  topMesh.render();

        } else {
            // STONE, COAL_ORE, IRON_ORE all use their own texture on every face
            textureMap.get(type).bind();
            if (!hasFront)  frontMesh.render();
            if (!hasBack)   backMesh.render();
            if (!hasLeft)   leftMesh.render();
            if (!hasRight)  rightMesh.render();
            if (!hasBelow)  bottomMesh.render();
            if (!hasAbove)  topMesh.render();
        }
    }

    /**  
     * We no longer delete textures here—shared textures live until program exit. 
     * If you have no per‐block GPU resources to free, you can leave this empty.
     */
    public void delete() {
        // Intentionally empty. Shared meshes & textures are cleaned up elsewhere.
    }

    /** Call this exactly once when the entire game is shutting down. */
    public static void cleanupAllBlockTextures() {
        for (Texture tex : textureMap.values()) {
            tex.delete();
        }
        textureMap.clear();
    }

    public Vector3f getPosition() {
        return position;
    }
}
