package game;

import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import game.animation.PunchAnimation;
import game.physics.GravitySystem;
import gfx.Mesh;
import gfx.Shader;    // make sure this is on the classpath
import gfx.Texture;
import gfx.Window;

public class Player {
    private final Camera camera;
    private final World  world;

    private final float  moveSpeed        = 0.1f;
    private final float  mouseSensitivity = 0.3f;
    private float verticalVelocity = 0f;
    private boolean isOnGround = false;
    private int chunkLoadCooldown = 0;

    private final Vector3f halfExtents = new Vector3f(0.25f, 0.9f, 0.25f);
    private final float eyeOffset = 0.2f;
    private final float eyeHeight = halfExtents.y * 2f;

    private Vector3f playerPosition;
    private Map<String, float[]> bodyParts;

    // Animation
    private PunchAnimation punchAnim;
    private static final float PUNCH_DURATION = 0.2f;
    private boolean wasPunchPressed = false;

    // ─── Textures ───────────────────────────────────────────────────────────────
    private Texture headFrontTex, headBackTex;
    private Texture headLeftTex, headRightTex;
    private Texture headTopTex, headBottomTex;
    private Texture bodyTex;
    private Texture leftArmTex, rightArmTex;
    private Texture leftLegTex, rightLegTex;

    // ─── Meshes (one‐per‐face and one‐per‐cube) ─────────────────────────────────
    private static Mesh headFrontMesh;
    private static Mesh headBackMesh;
    private static Mesh headLeftMesh;
    private static Mesh headRightMesh;
    private static Mesh headTopMesh;
    private static Mesh headBottomMesh;

    private Texture bodyFrontBackTex;
    private Texture bodySideTex;
    
    // Six Mesh objects for the torso
    private static Mesh bodyFrontMesh;
    private static Mesh bodyBackMesh;
    private static Mesh bodyLeftMesh;
    private static Mesh bodyRightMesh;
    private static Mesh bodyTopMesh;
    private static Mesh bodyBottomMesh;

    // Six meshes for the left arm:
    private static Mesh leftArmFrontMesh;
    private static Mesh leftArmBackMesh;
    private static Mesh leftArmLeftMesh;
    private static Mesh leftArmRightMesh;
    private static Mesh leftArmTopMesh;
    private static Mesh leftArmBottomMesh;

    // Six meshes for the right arm:
    private static Mesh rightArmFrontMesh;
    private static Mesh rightArmBackMesh;
    private static Mesh rightArmLeftMesh;
    private static Mesh rightArmRightMesh;
    private static Mesh rightArmTopMesh;
    private static Mesh rightArmBottomMesh;


    // In Player class fields (add these):
    private static Mesh leftLegFrontMesh;
    private static Mesh leftLegBackMesh;
    private static Mesh leftLegLeftMesh;
    private static Mesh leftLegRightMesh;
    private static Mesh leftLegTopMesh;
    private static Mesh leftLegBottomMesh;
    
    private static Mesh rightLegFrontMesh;
    private static Mesh rightLegBackMesh;
    private static Mesh rightLegLeftMesh;
    private static Mesh rightLegRightMesh;
    private static Mesh rightLegTopMesh;
    private static Mesh rightLegBottomMesh;
    // ────────────────────────────────────────────────────────────────────────────

    public Player(Camera camera, World world, Vector3f spawnPosition, boolean startsFirstPerson ) {
        this.camera = camera;
        this.world  = world;
        this.playerPosition = new Vector3f(spawnPosition);

        // ← Grab all parts (six tiny head faces, plus full unit‐cubes for body/limbs)
        this.bodyParts = PlayerData.createPlayerParts();

        // animation hleper
        this.punchAnim = new PunchAnimation(PUNCH_DURATION);

        // Load textures (six for head, one for body, etc.)
        this.headFrontTex  = new Texture("resources/textures/player-face.png");
        this.headBackTex   = new Texture("resources/textures/player-head-back-16x16.png");
        this.headLeftTex   = new Texture("resources/textures/player-left-ear.png");
        this.headRightTex  = new Texture("resources/textures/player-right-ear.png");
        this.headTopTex    = new Texture("resources/textures/player-head-top-16x16.png");
        this.headBottomTex = new Texture("resources/textures/player-bottom-head.png");

        this.bodyFrontBackTex = new Texture("resources/textures/player-body.png");
        this.bodySideTex      = new Texture("resources/textures/player-body-side.png");
        this.leftArmTex  = new Texture("resources/textures/player-arm.png");
        this.rightArmTex = new Texture("resources/textures/player-arm.png");
        this.leftLegTex  = new Texture("resources/textures/player-leg.png");
        this.rightLegTex = new Texture("resources/textures/player-leg.png");


        updateCameraPosition(startsFirstPerson);
    }

    public void update(Window window, float dt, boolean firstPerson)
 {
        // (unchanged chunk‐loading, movement, gravity logic)
        if (chunkLoadCooldown <= 0) {
            int px = (int)Math.floor(playerPosition.x / Chunk.CHUNK_SIZE_X);
            int pz = (int)Math.floor(playerPosition.z / Chunk.CHUNK_SIZE_Z);
            float chunkWorldX = px * Chunk.CHUNK_SIZE_X + Chunk.CHUNK_SIZE_X / 2f;
            float chunkWorldZ = pz * Chunk.CHUNK_SIZE_Z + Chunk.CHUNK_SIZE_Z / 2f;
            world.ensureChunkAt(chunkWorldX, chunkWorldZ);

            Vector3f forward = camera.getForwardDirection();
            int fpx = (int)Math.floor((playerPosition.x + forward.x * Chunk.CHUNK_SIZE_X) / Chunk.CHUNK_SIZE_X);
            int fpz = (int)Math.floor((playerPosition.z + forward.z * Chunk.CHUNK_SIZE_Z) / Chunk.CHUNK_SIZE_Z);
            float forwardChunkX = fpx * Chunk.CHUNK_SIZE_X + Chunk.CHUNK_SIZE_X / 2f;
            float forwardChunkZ = fpz * Chunk.CHUNK_SIZE_Z + Chunk.CHUNK_SIZE_Z / 2f;
            world.ensureChunkAt(forwardChunkX, forwardChunkZ);

            world.unloadFarChunks(playerPosition);
            chunkLoadCooldown = 15;
        } else {
            chunkLoadCooldown--;
        }

        handleMouseLook(window);
        handleMovement(window);

        // ── NEW: detect “mouse‐down edge” rather than “button is down”
        boolean isNowPressed = window.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1);

    // ── NEW: Start a punch if user pressed LEFT CLICK (or whatever key/button) ───
    if (isNowPressed && !punchAnim.isActive()) {
        punchAnim.start();
    }
    wasPunchPressed = isNowPressed;
        // ── NEW: advance the punch animation each frame ───────────────────────────  
    
    punchAnim.update(dt);

    updateCameraPosition(firstPerson);
    }

    private void handleMouseLook(Window window) {
        float dy = (float) window.getDeltaY();
        float dx = (float) window.getDeltaX();
        Vector3f rot = camera.getRotation();

        rot.x += dy * mouseSensitivity;
        rot.y += dx * mouseSensitivity;
        rot.x = Math.max(-89f, Math.min(89f, rot.x));
        if (rot.y < 0)    rot.y += 360f;
        if (rot.y >= 360f) rot.y -= 360f;

        camera.setRotation(rot);
    }

private void handleMovement(Window window) {
    Vector3f rawForward = camera.getForwardDirection();
    Vector3f rawRight   = camera.getRightDirection();
    Vector3f forward = new Vector3f(rawForward.x, 0f, rawForward.z).normalize();
    Vector3f right   = new Vector3f(rawRight.x,   0f, rawRight.z).normalize();

    Vector3f movement = new Vector3f();
    if (window.isKeyPressed(GLFW.GLFW_KEY_W)) movement.add(forward);
    if (window.isKeyPressed(GLFW.GLFW_KEY_S)) movement.sub(forward);
    if (window.isKeyPressed(GLFW.GLFW_KEY_A)) movement.add(right);
    if (window.isKeyPressed(GLFW.GLFW_KEY_D)) movement.sub(right);

    if (movement.lengthSquared() > 0) {
        movement.normalize().mul(moveSpeed);
    }
    if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE) && isOnGround) {
        verticalVelocity = 0.4f;
        isOnGround = false;
    }

    Set<Vec3i> occupied = world.getAllOccupiedBlocks();
    GravitySystem.GravityResult result = new GravitySystem.GravityResult();
    Vector3f corrected = GravitySystem.applyGravity(
        playerPosition, movement, verticalVelocity, halfExtents, occupied, result
    );

    // Apply whatever motion survived collision‐resolution:
    playerPosition.add(corrected);
    verticalVelocity = result.newVelocity;
    isOnGround = result.isOnGround;

    // ─────────────────────────────────────────────────────
    // SNAP TO BLOCK TOP (to eliminate the tiny gap)
    //─────────────────────────────────────────────────────
    if (isOnGround) {
        // Compute feetY = bottom of AABB after collision:
        float feetY = playerPosition.y - halfExtents.y;
        // Find the integer block‐Y that sits directly under feetY:
        int blockY = (int) Math.floor(feetY + 1e-6f);
        // Now force playerY so that feetY == blockY exactly:
        playerPosition.y = blockY + halfExtents.y;
    }
}


private void updateCameraPosition(boolean firstPerson) {
    if (firstPerson) {
        // Center of head cube sits at Y = playerY + 2.5
        camera.setPosition(
            playerPosition.x,
            playerPosition.y + 2.5f,
            playerPosition.z
        );
    } else {
        // Pivot camera at the player's feet (playerPosition.y) and back off on Z
        float cameraGroundY   = playerPosition.y;      // feet‐level
        float thirdPersonDist = 2.0f;                  // tweak if you want more/less distance

        camera.setPosition(
            playerPosition.x,
            cameraGroundY,
            playerPosition.z + thirdPersonDist
        );

        // Aim at roughly the player’s torso center (Y = playerY + 1.0)
        Vector3f lookAt = new Vector3f(
            playerPosition.x,
            playerPosition.y + 1.0f,
            playerPosition.z
        );
        camera.lookAt(lookAt);
    }
}




    public Camera getCamera() {
        return camera;
    }

    public Vector3f getPosition() {
    return playerPosition;
}


public void render(Matrix4f view, Matrix4f projection, Shader shader, boolean firstPerson) {
    shader.use();

    // ────────────────────────────────────────────────────────
    // 1) Lazily create all head‐face, body, arm, and leg meshes
    //───────────────────────────────────────────────────────────
    if (headFrontMesh == null) {
        // HEAD faces
        headFrontMesh   = new Mesh(bodyParts.get("headFront"),   shader.getProgramId());
        headBackMesh    = new Mesh(bodyParts.get("headBack"),    shader.getProgramId());
        headLeftMesh    = new Mesh(bodyParts.get("headLeft"),    shader.getProgramId());
        headRightMesh   = new Mesh(bodyParts.get("headRight"),   shader.getProgramId());
        headTopMesh     = new Mesh(bodyParts.get("headTop"),     shader.getProgramId());
        headBottomMesh  = new Mesh(bodyParts.get("headBottom"),  shader.getProgramId());

        // BODY faces
        bodyFrontMesh   = new Mesh(bodyParts.get("bodyFront"),   shader.getProgramId());
        bodyBackMesh    = new Mesh(bodyParts.get("bodyBack"),    shader.getProgramId());
        bodyLeftMesh    = new Mesh(bodyParts.get("bodyLeft"),    shader.getProgramId());
        bodyRightMesh   = new Mesh(bodyParts.get("bodyRight"),   shader.getProgramId());
        bodyTopMesh     = new Mesh(bodyParts.get("bodyTop"),     shader.getProgramId());
        bodyBottomMesh  = new Mesh(bodyParts.get("bodyBottom"),  shader.getProgramId());

        // LEFT ARM faces
        leftArmFrontMesh  = new Mesh(bodyParts.get("leftArmFront"),  shader.getProgramId());
        leftArmBackMesh   = new Mesh(bodyParts.get("leftArmBack"),   shader.getProgramId());
        leftArmLeftMesh   = new Mesh(bodyParts.get("leftArmLeft"),   shader.getProgramId());
        leftArmRightMesh  = new Mesh(bodyParts.get("leftArmRight"),  shader.getProgramId());
        leftArmTopMesh    = new Mesh(bodyParts.get("leftArmTop"),    shader.getProgramId());
        leftArmBottomMesh = new Mesh(bodyParts.get("leftArmBottom"), shader.getProgramId());

        // RIGHT ARM faces
        rightArmFrontMesh  = new Mesh(bodyParts.get("rightArmFront"),  shader.getProgramId());
        rightArmBackMesh   = new Mesh(bodyParts.get("rightArmBack"),   shader.getProgramId());
        rightArmLeftMesh   = new Mesh(bodyParts.get("rightArmLeft"),   shader.getProgramId());
        rightArmRightMesh  = new Mesh(bodyParts.get("rightArmRight"),  shader.getProgramId());
        rightArmTopMesh    = new Mesh(bodyParts.get("rightArmTop"),    shader.getProgramId());
        rightArmBottomMesh = new Mesh(bodyParts.get("rightArmBottom"), shader.getProgramId());

        // LEFT LEG faces
        leftLegFrontMesh  = new Mesh(bodyParts.get("leftLegFront"),  shader.getProgramId());
        leftLegBackMesh   = new Mesh(bodyParts.get("leftLegBack"),   shader.getProgramId());
        leftLegLeftMesh   = new Mesh(bodyParts.get("leftLegLeft"),   shader.getProgramId());
        leftLegRightMesh  = new Mesh(bodyParts.get("leftLegRight"),  shader.getProgramId());
        leftLegTopMesh    = new Mesh(bodyParts.get("leftLegTop"),    shader.getProgramId());
        leftLegBottomMesh = new Mesh(bodyParts.get("leftLegBottom"), shader.getProgramId());

        // RIGHT LEG faces
        rightLegFrontMesh  = new Mesh(bodyParts.get("rightLegFront"),  shader.getProgramId());
        rightLegBackMesh   = new Mesh(bodyParts.get("rightLegBack"),   shader.getProgramId());
        rightLegLeftMesh   = new Mesh(bodyParts.get("rightLegLeft"),   shader.getProgramId());
        rightLegRightMesh  = new Mesh(bodyParts.get("rightLegRight"),  shader.getProgramId());
        rightLegTopMesh    = new Mesh(bodyParts.get("rightLegTop"),    shader.getProgramId());
        rightLegBottomMesh = new Mesh(bodyParts.get("rightLegBottom"), shader.getProgramId());
    }

    // ────────────────────────────────────────────────────────
    // 2) DRAW HEAD (only when NOT in first‐person)
    //───────────────────────────────────────────────────────────
    if (!firstPerson) {
        Matrix4f headModel = new Matrix4f()
            .translate(playerPosition.x, playerPosition.y + 1.5f, playerPosition.z);
        shader.setUniformMatrix4f("model",      headModel);
        shader.setUniformMatrix4f("view",       view);
        shader.setUniformMatrix4f("projection", projection);

        headFrontTex.bind();
        headFrontMesh.render();
        headBackTex.bind();
        headBackMesh.render();
        headLeftTex.bind();
        headLeftMesh.render();
        headRightTex.bind();
        headRightMesh.render();
        headTopTex.bind();
        headTopMesh.render();
        headBottomTex.bind();
        headBottomMesh.render();
    }

  // ────────────────────────────────────────────────────────
    // 3) DRAW BODY, ARMS & LEGS (all rotated together)
    //───────────────────────────────────────────────────────────

    // 3.a) Read camera’s yaw (in degrees) and flip in third‐person
    float camYawDeg = camera.getRotation().y;
    if (!firstPerson) {
        camYawDeg += 180f;
        if (camYawDeg >= 360f) camYawDeg -= 360f;
    }
    float camYawRad = (float) Math.toRadians(camYawDeg);

    // 3.b) Build “root” matrix at playerPosition, rotated by –camYawRad
    Matrix4f root = new Matrix4f()
        .translate(playerPosition.x, playerPosition.y, playerPosition.z)
        .rotateY(-camYawRad);

    //
    // 3.c) DRAW TORSO (unchanged)
    //
    Matrix4f bodyModel = new Matrix4f(root)
        .translate(0f, 0.5f, 0f);
    shader.setUniformMatrix4f("model", bodyModel);
    shader.setUniformMatrix4f("view", view);
    shader.setUniformMatrix4f("projection", projection);

    bodyFrontBackTex.bind();
    bodyFrontMesh.render();
    bodyFrontBackTex.bind();
    bodyBackMesh.render();
    bodySideTex.bind();
    bodyLeftMesh.render();
    bodySideTex.bind();
    bodyRightMesh.render();
    bodyFrontBackTex.bind();
    bodyTopMesh.render();
    bodyFrontBackTex.bind();
    bodyBottomMesh.render();

    //
    // ── NEW: get punch progress (0→1→0) ────────────────────────────────
    float p = punchAnim.getProgress();
    float maxSwingAngleDegrees = 60f;             // adjust as desired
    float swingAngle = maxSwingAngleDegrees * p;  // p=0→1→0 maps to 0→60→0

    //
    // 3.d) DRAW ARMS
    //
    float bodyHalfWidth = 0.5f;
    float armHalfWidth  = 0.125f;
    float armYOffset    = 0.5f;    // torso center Y = playerY + 0.5
    float armHeight     = 1.0f;    // each arm cube is 1 unit tall
    float armPivotY     = armYOffset + (armHeight / 2f); 
    // armPivotY = playerY + 1.0f = shoulder joint in world space

    // ─ LEFT ARM: unchanged pivot around its center ─────────────────────
float leftArmX = -(bodyHalfWidth + armHalfWidth); // = –0.625
Matrix4f leftArmModel = new Matrix4f(root)
    .translate(leftArmX, armYOffset, 0f);
shader.setUniformMatrix4f("model", leftArmModel);
shader.setUniformMatrix4f("view", view);
shader.setUniformMatrix4f("projection", projection);

leftArmTex.bind();
leftArmFrontMesh.render();
leftArmBackMesh.render();
leftArmLeftMesh.render();
leftArmRightMesh.render();
leftArmTopMesh.render();
leftArmBottomMesh.render();

// ─ RIGHT ARM: pivot at shoulder, tilt forward 90°, then punch‐swing ───────────
float rightArmX = (bodyHalfWidth + armHalfWidth); // = +0.625

Matrix4f rightArmModel = new Matrix4f(root)
    // Step A: move pivot to the shoulder position (world space)
    .translate(rightArmX, armPivotY, 0f)
    // Step B: drop cube so its top sits at the origin (pivot = shoulder)
    .translate(0f, -(armHeight / 2f), 0f)
    // ─── NEW: pitch arm 90° downward so it lies horizontal in front of the chest ───
    .rotateX((float) Math.toRadians(-60f))
    // ─── THEN: pitch by the punch amount (so -90° becomes “-90° – punchAngle”) ───
    .rotateX((float) Math.toRadians(-swingAngle))
    // Step D: move cube back so its center sits 0.5 units below shoulder
    .translate(0f, (armHeight / 2f), 0f);

shader.setUniformMatrix4f("model", rightArmModel);
shader.setUniformMatrix4f("view",  view);
shader.setUniformMatrix4f("projection", projection);

rightArmTex.bind();
rightArmFrontMesh.render();
rightArmBackMesh.render();
rightArmLeftMesh.render();
rightArmRightMesh.render();
rightArmTopMesh.render();
rightArmBottomMesh.render();


    //
    // 3.e) DRAW LEGS (using the same root, no swing) ─────────────────────
    //
    float legWidth      = 0.25f;
    float legHeight     = 1.8f;
    float legDepth      = 0.25f;
    float legHalfHeight = legHeight / 2f; // = 0.9
    float legHalfWidth  = legWidth  / 2f; // = 0.125

    float feetY = playerPosition.y - halfExtents.y;
    float bootSink = 0.75f;
    float legCenterY = (feetY + legHalfHeight) - bootSink;
    float legYOffset = legCenterY - playerPosition.y;
    float legXOffset = bodyHalfWidth - legHalfWidth; // = 0.375

    // LEFT LEG
    Matrix4f leftLegModel = new Matrix4f(root)
        .translate(-legXOffset, legYOffset, 0f)
        .scale(legWidth, legHeight, legDepth);
    shader.setUniformMatrix4f("model", leftLegModel);
    shader.setUniformMatrix4f("view", view);
    shader.setUniformMatrix4f("projection", projection);

    leftLegTex.bind();
    leftLegFrontMesh.render();
    leftLegBackMesh.render();
    leftLegLeftMesh.render();
    leftLegRightMesh.render();
    leftLegTopMesh.render();
    leftLegBottomMesh.render();

    // RIGHT LEG
    Matrix4f rightLegModel = new Matrix4f(root)
        .translate(legXOffset, legYOffset, 0f)
        .scale(legWidth, legHeight, legDepth);
    shader.setUniformMatrix4f("model", rightLegModel);
    shader.setUniformMatrix4f("view", view);
    shader.setUniformMatrix4f("projection", projection);

    rightLegTex.bind();
    rightLegFrontMesh.render();
    rightLegBackMesh.render();
    rightLegLeftMesh.render();
    rightLegRightMesh.render();
    rightLegTopMesh.render();
    rightLegBottomMesh.render();
}

}
