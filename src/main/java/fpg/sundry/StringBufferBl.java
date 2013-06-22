package fpg.sundry;

public class StringBufferBl  {
    StringBuffer sb = new StringBuffer();
    char delim;
    
    public StringBufferBl() {
	this(' ');
    }

    public StringBufferBl(char delim) {
	this.delim = delim;
    }
    
    public StringBuffer append(String s) {
	if ( sb.length() > 0 )
	    sb.append(delim);
	return sb.append(s);
    }

    public StringBuffer append(char ch) {
	if ( sb.length() > 0 )
	    sb.append(delim);
	return sb.append(ch);
    }

    @Override
    public String toString() {
	return sb.toString();
    }
}

