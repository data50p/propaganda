package fpg.util;

/**
   Easy to call utility functions.
 */
public class _
{
    /**
       Print a formatted string on stdout with no newline
     */
    public static void PF_(String fmt, Object... arg) 
    {
	System.out.printf(fmt, arg);
    }

    /**
       Print a formatted string on err  with no newline
     */
    public static void PE_(String fmt, Object... arg)
    {
	System.err.printf(fmt, arg);
    }

    /**
       Print a formatted string on stdout including newline
     */
    public static void PF(String fmt, Object... arg) 
    {
	System.out.printf(fmt + '\n', arg);
    }

    /**
       Print a formatted string on stderr including newline
     */
    public static void PE(String fmt, Object... arg) 
    {
	System.err.printf(fmt + '\n', arg);
    }
}
