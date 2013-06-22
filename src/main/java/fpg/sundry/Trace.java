package fpg.sundry;

public class Trace {
    int filter;

    Trace() {
	filter = 0;
    }

    public void on(int bit) {
	filter |= 1 << bit;
    }
    public void off(int bit) {
	filter &= ~(1 << bit);
    }

    public void pe(int bit, String s) {
	int fl = 1 << bit;
	if ( (fl & filter) != 0 )
	    S.pL(s);
    }
}
