package propaganda.appl;

import fpg.sundry.*;

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
