package com.femtioprocent.fpd.sundry;


public class DemoAppl extends Appl {
    @Override
    public void main() {
	S.pL("main() in DemoAppl");
    }

    public static void main(String[] args) {
	decodeArgs(args, true, new String[][] {
	    new String[] {"help", "", "Show this help text"},
	    new String[] {"foo", "arg", "bar"},
	    new String[] {"fooo ooo ooooo oooo", "", "baaa aaaaa aaa aaaar"}
	});
	main(new DemoAppl());
    }
}
