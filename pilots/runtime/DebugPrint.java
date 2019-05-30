package pilots.runtime;

public class DebugPrint {
    private static boolean debugPrintEnabled = true;

    public void enable() {
        debugPrintEnabled = true;
    }

    public void disable() {
        debugPrintEnabled = false;
    }

    public void dbgPrint (String str) {
        if (!debugPrintEnabled)
            return;

        // assuming this class is inherited to user classes
        System.out.println("[" + this.getClass().getSimpleName() + "] " + str);
    }
}
