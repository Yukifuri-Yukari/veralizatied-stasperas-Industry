package yukifuri.mc.vsindustry.util;

public class Math {
    public static long doubleHash(String s) {
        return ((long)s.hashCode() << 32) | s.length();
    }
}
