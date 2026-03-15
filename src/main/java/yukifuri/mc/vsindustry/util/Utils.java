package yukifuri.mc.vsindustry.util;


public class Utils {
    public static <T> T TODO() {
        throw new NotImplementedError("An operation is not implemented.");
    }

    public static <T> T TODO(String message) {
        throw new NotImplementedError(message);
    }

    public static <T> T ise() {
        throw new IllegalStateException("Program exited due to illegal state.");
    }

    public static <T> T ise(String message) {
        throw new IllegalStateException(message);
    }

    public static class NotImplementedError extends RuntimeException {
        public NotImplementedError(String message) {
            super(message);
        }
    }
}
