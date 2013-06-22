package fpg.sundry;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class MIf {
    MIf() {
    }

    void emit(int seek, int len) {
	if ( len > 0 )
	    S.p_("" + S.padLeft("" + seek, 9, ' ') +
		 " " +
		 S.padLeft("" + len, 6, ' ') +
		 "\r\n");
    }

    void print(String fn) {
	try {
	    RandomAccessFile raf = new RandomAccessFile(fn, "r");
	    byte[] buf = new byte[8192];
	    int N = 0;
	    int cnt = 0;
	    int seek = 0;

	    File fi = new File(fn);
	    int fN = (int)fi.length();
	    int sN = 0;
	    for(;;) {
		int n = raf.read(buf);

		if ( n == -1 ) {
		    emit(seek, cnt);
		    break;
		}

		sN += n;
		double f = (double)sN / fN;
		S.pe_("\r" + f);
		for(int i = 0; i < n; i++) {
		    if ( buf[i] == '\r' || buf[i] == '\n' ) {
			emit(seek, cnt);
			cnt = 0;
		    } else {
			if ( cnt == 0 )
			    seek = N + i;
			cnt++;
		    }
		}
		N += n;
	    }
	} catch (IOException ex) {
	    S.pL("Ex " + ex);
	}
    }

    public static void main(String[] args) {
	try {
	    MIf nfp = new MIf();
	    nfp.print(args[0]);
	} finally {
	    S.flush();
	}
    }

}
