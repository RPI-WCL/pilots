package pilots.runtime;

// TODO: Remove DebugPrint, use Logger to track the logging events.
public class DebugPrint {
    private static boolean debugPrintEnabled_ = true;

    public void enable() {
        debugPrintEnabled_ = true;
    }

    public void disable() {
        debugPrintEnabled_ = false;
    }

    public void dbgPrint (String str) {
        if (!debugPrintEnabled_)
            return;

        // assuming this class is inherited to user classes
        System.out.println( "[" + this.getClass().getSimpleName() + "] " + str );
    }
}