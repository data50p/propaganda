package fpg.sundry;

class LoaderAppl extends Appl {
    @Override
    public void main() {
	for(;;) {
	    S.m_sleep(1000);
	    S.pe_(".");
	}
    }

    /**
       Start point, create Class and call main()
     */    
    public static void main(String[] args) {
	decodeArgs(args, true, new String[][] {
	    new String[] {"help", "", "Show this help text"}
	});
	main(new LoaderAppl());
    }    
}
