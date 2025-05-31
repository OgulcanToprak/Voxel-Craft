// Vec3i.java (in package game)
package game;

public class Vec3i {
    public final int x, y, z;
    public Vec3i(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec3i)) return false;
        Vec3i v = (Vec3i) o;
        return this.x == v.x && this.y == v.y && this.z == v.z;
    }

    @Override
    public int hashCode() {
        return ((x * 73856093) ^ (y * 19349663) ^ (z * 83492791));
    }
}
