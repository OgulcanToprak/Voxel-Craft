package gfx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import org.lwjgl.BufferUtils;

public class Utils {
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        try (InputStream source = Utils.class.getClassLoader().getResourceAsStream(resource)) {
            if (source == null)
                throw new IOException("Resource not found: " + resource);

            buffer = BufferUtils.createByteBuffer(bufferSize);
            var rbc = Channels.newChannel(source);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1)
                    break;
                if (buffer.remaining() == 0) {
                    ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 2);
                    buffer.flip();
                    newBuffer.put(buffer);
                    buffer = newBuffer;
                }
            }

            buffer.flip();
        }
        return buffer;
    }
}
