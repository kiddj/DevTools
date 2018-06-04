import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class Cprint {
    //Info
    public static void i(String message) {
        System.out.println(ansi( ).fg( GREEN ).a(message).reset());
    }
    public static void i(String message, String end) {
        System.out.print(ansi( ).fg( GREEN ).a(message + end).reset());
    }

    //Error
    public static void e(String message) {
        System.out.println(ansi( ).fg( RED ).a(message).reset());
    }
    public static void e(String message, String end) {
        System.out.print(ansi( ).fg( RED ).a(message + end).reset());
    }

    //Warning
    public static void w(String message) {
        System.out.println(ansi( ).fg( YELLOW ).a(message).reset());
    }
    public static void w(String message, String end) {
        System.out.println(ansi( ).fg( YELLOW ).a(message + end).reset());
    }

    //Blue
    public static void b(String message) {
        System.out.println(ansi( ).fg( CYAN ).a(message).reset());
    }
    public static void b(String message, String end) {
        System.out.println(ansi( ).fg( CYAN ).a(message + end).reset());
    }
}