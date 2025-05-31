package gfx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import org.lwjgl.system.MemoryStack;

public class TextRenderer {
    private static final int BITMAP_W = 512;
    private static final int BITMAP_H = 512;

    private final STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
    private final ByteBuffer fontBuffer;
    private final ByteBuffer bitmap;
    private final int fontTex;

    private final STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(96); // ASCII 32..126

    public TextRenderer(String fontPath, int fontSize) throws IOException {
        fontBuffer = Utils.ioResourceToByteBuffer(fontPath, 160 * 1024);
        bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);

        stbtt_BakeFontBitmap(fontBuffer, fontSize, bitmap, BITMAP_W, BITMAP_H, 32, charData);

        fontTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, fontTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    }

    public void drawText(String text, float x, float y) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, fontTex);
        glBegin(GL_QUADS);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xb = stack.floats(x);
            FloatBuffer yb = stack.floats(y);

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c < 32 || c >= 128) continue;

                stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - 32, xb, yb, quad, true);

                glTexCoord2f(quad.s0(), quad.t0());
                glVertex2f(quad.x0(), quad.y0());

                glTexCoord2f(quad.s1(), quad.t0());
                glVertex2f(quad.x1(), quad.y0());

                glTexCoord2f(quad.s1(), quad.t1());
                glVertex2f(quad.x1(), quad.y1());

                glTexCoord2f(quad.s0(), quad.t1());
                glVertex2f(quad.x0(), quad.y1());
            }
        }

        glEnd();
    }

    public void delete() {
        glDeleteTextures(fontTex);
        charData.free();
        quad.free();
    }
}
