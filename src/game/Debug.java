package game;

public class Debug {
    public static void out(String msg) {
        if (!Config.DEBUG) return;
        System.out.println(msg);
    }
}
