package game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import game.parts.FaceData;

/**
 * Builds a “player” out of:
 *   – six independent copies of FaceData (for head), each scaled by 0.25
 *   – one full “cube” (from CubeData) for body & limbs, un‐scaled at creation
 */
public class PlayerData {

    public static Map<String, float[]> createPlayerParts() {
        Map<String, float[]> parts = new HashMap<>();

        /** Head parts (each face is a unit‐face scaled by 1.0, but in render we will do .scale(0.25) ) */
        parts.put("headFront",  createFaceCopy("FRONT",  1.0f));
        parts.put("headBack",   createFaceCopy("BACK",   1.0f));
        parts.put("headLeft",   createFaceCopy("LEFT",   1.0f));
        parts.put("headRight",  createFaceCopy("RIGHT",  1.0f));
        parts.put("headTop",    createFaceCopy("TOP",    1.0f));
        parts.put("headBottom", createFaceCopy("BOTTOM", 1.0f));

        /** Body (make a 1.0×1.0×0.5 cuboid now, because body is always 1×1 torso) */
        float bodyWidth  = 1.0f;   // ±0.5 → total width = 1.0
        float bodyHeight = 1.0f;   // ±0.5 → total height = 1.0
        float bodyDepth  = 0.5f;   // ±0.25 → total depth = 0.5

        parts.put("bodyFront",  createScaledFace("FRONT",  bodyWidth,  bodyHeight, bodyDepth));
        parts.put("bodyBack",   createScaledFace("BACK",   bodyWidth,  bodyHeight, bodyDepth));
        parts.put("bodyLeft",   createScaledFace("LEFT",   bodyWidth,  bodyHeight, bodyDepth));
        parts.put("bodyRight",  createScaledFace("RIGHT",  bodyWidth,  bodyHeight, bodyDepth));
        parts.put("bodyTop",    createScaledFace("TOP",    bodyWidth,  bodyHeight, bodyDepth));
        parts.put("bodyBottom", createScaledFace("BOTTOM", bodyWidth,  bodyHeight, bodyDepth));

        /** ARMS (skinny cuboids). We want each arm to be 0.25×1.0×0.25 */
        float armWidth  = 0.25f;   // ±0.125 → total width = 0.25
        float armHeight = 1.0f;    // ±0.5   → total height = 1.0
        float armDepth  = 0.25f;   // ±0.125 → total depth = 0.25

        parts.put("leftArmFront",  createScaledFace("FRONT",  armWidth, armHeight, armDepth));
        parts.put("leftArmBack",   createScaledFace("BACK",   armWidth, armHeight, armDepth));
        parts.put("leftArmLeft",   createScaledFace("LEFT",   armWidth, armHeight, armDepth));
        parts.put("leftArmRight",  createScaledFace("RIGHT",  armWidth, armHeight, armDepth));
        parts.put("leftArmTop",    createScaledFace("TOP",    armWidth, armHeight, armDepth));
        parts.put("leftArmBottom", createScaledFace("BOTTOM", armWidth, armHeight, armDepth));

        parts.put("rightArmFront",  createScaledFace("FRONT",  armWidth, armHeight, armDepth));
        parts.put("rightArmBack",   createScaledFace("BACK",   armWidth, armHeight, armDepth));
        parts.put("rightArmLeft",   createScaledFace("LEFT",   armWidth, armHeight, armDepth));
        parts.put("rightArmRight",  createScaledFace("RIGHT",  armWidth, armHeight, armDepth));
        parts.put("rightArmTop",    createScaledFace("TOP",    armWidth, armHeight, armDepth));
        parts.put("rightArmBottom", createScaledFace("BOTTOM", armWidth, armHeight, armDepth));

        /** LEGS: build them as a pure 1×1×1 cube (centered at origin).  
         *  We will scale to 0.25×1.8×0.25 in the render() method. */
        // Instead of createScaledFace("…", 0.25, 1.0, 0.25), use createFaceCopy("…", 1.0f).
        // That ensures each “leg” mesh is a full unit‐cube (−0.5→+0.5 in all axes).

        // Left leg faces (1×1×1)
        parts.put("leftLegFront",  createFaceCopy("FRONT",  1.0f));
        parts.put("leftLegBack",   createFaceCopy("BACK",   1.0f));
        parts.put("leftLegLeft",   createFaceCopy("LEFT",   1.0f));
        parts.put("leftLegRight",  createFaceCopy("RIGHT",  1.0f));
        parts.put("leftLegTop",    createFaceCopy("TOP",    1.0f));
        parts.put("leftLegBottom", createFaceCopy("BOTTOM", 1.0f));

        // Right leg faces (also 1×1×1)
        parts.put("rightLegFront",  createFaceCopy("FRONT",  1.0f));
        parts.put("rightLegBack",   createFaceCopy("BACK",   1.0f));
        parts.put("rightLegLeft",   createFaceCopy("LEFT",   1.0f));
        parts.put("rightLegRight",  createFaceCopy("RIGHT",  1.0f));
        parts.put("rightLegTop",    createFaceCopy("TOP",    1.0f));
        parts.put("rightLegBottom", createFaceCopy("BOTTOM", 1.0f));

        return parts;
    }

    /**
     * Copies one FaceData array, scales only the (x,y,z) components by “scale” (leaving UVs intact).
     * @param faceName  "FRONT","BACK","LEFT","RIGHT","TOP","BOTTOM"
     * @param scale     how much to shrink/enlarge the cube (1.0f = unit‐cube)
     */
    private static float[] createFaceCopy(String faceName, float scale) {
        float[] source;
        switch (faceName) {
            case "FRONT":  source = FaceData.FRONT;  break;
            case "BACK":   source = FaceData.BACK;   break;
            case "LEFT":   source = FaceData.LEFT;   break;
            case "RIGHT":  source = FaceData.RIGHT;  break;
            case "TOP":    source = FaceData.TOP;    break;
            case "BOTTOM": source = FaceData.BOTTOM; break;
            default:
                throw new IllegalArgumentException("Invalid face: " + faceName);
        }

        float[] faceCopy = Arrays.copyOf(source, source.length);
        // Scale only X, Y, Z → UV (faceCopy[i+3], faceCopy[i+4]) stay 0→1
        for (int i = 0; i < faceCopy.length; i += 5) {
            faceCopy[i]     *= scale; // X
            faceCopy[i + 1] *= scale; // Y
            faceCopy[i + 2] *= scale; // Z
            // faceCopy[i+3], faceCopy[i+4] = (u,v) unchanged
        }
        return faceCopy;
    }

    /**
     * Copies a FaceData array, then non‐uniformly scales (x,y,z) by (scaleX, scaleY, scaleZ).
     * Used for torso and arm faces.
     */
    private static float[] createScaledFace(String faceName,
                                            float scaleX, float scaleY, float scaleZ) {
        float[] source = pickFace(faceName);
        float[] faceCopy = Arrays.copyOf(source, source.length);
        for (int i = 0; i < faceCopy.length; i += 5) {
            faceCopy[i]     *= scaleX; // X
            faceCopy[i + 1] *= scaleY; // Y
            faceCopy[i + 2] *= scaleZ; // Z
            // UV coords remain 0→1
        }
        return faceCopy;
    }

    /** Picks the raw unit‐cube face data from FaceData. */
    private static float[] pickFace(String name) {
        switch (name) {
            case "FRONT":  return FaceData.FRONT;
            case "BACK":   return FaceData.BACK;
            case "LEFT":   return FaceData.LEFT;
            case "RIGHT":  return FaceData.RIGHT;
            case "TOP":    return FaceData.TOP;
            case "BOTTOM": return FaceData.BOTTOM;
            default: throw new IllegalArgumentException("Unknown face: " + name);
        }
    }
}
