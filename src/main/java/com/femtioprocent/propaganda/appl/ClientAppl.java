package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.appl.Appl;
import com.femtioprocent.fpd.sundry.S;

public class ClientAppl extends Appl {

    @Override
    public void main() {
	System.err.println("main() in ClientAppl");
    }

    public static void main(String[] args) {
	decodeArgs(args);
	main(new ClientAppl());
    }
}
