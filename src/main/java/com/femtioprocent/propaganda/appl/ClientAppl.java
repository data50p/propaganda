package com.femtioprocent.propaganda.appl;

import com.femtioprocent.fpd.sundry.Appl;
import com.femtioprocent.fpd.sundry.S;

public class ClientAppl extends Appl
{
    @Override
    public void main() {
	S.pL("main() in ClientAppl");
    }

    public static void main(String[] args) {
	decodeArgs(args);
	main(new ClientAppl());
    }
}