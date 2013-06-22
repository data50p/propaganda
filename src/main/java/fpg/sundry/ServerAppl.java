package fpg.sundry;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ServerAppl extends Appl {

    public interface Service {
	public Map eval(HashMap hm);
    }

    class ServerDaemon extends Thread {
	ServerSocket sso;
	Service service;
	int port;

	public class ServerConnection extends Thread {
	    Socket so;

	    ServerConnection(Socket so) {
		super("httpd.ServerConnection");
		this.so = so;
	    }

	    private List getHeader(BufferedReader rd) throws IOException {
		List<String> li = new ArrayList<String>();

		for(;;) {
		    String s = rd.readLine();
		    if ( s.length() == 0 )
			return li;
		    li.add(s);
		}
	    }

	    Map getAnswer(HashMap hm) throws IOException {
		return service.eval(hm);
	    }

	    String getFileName(String get_post, String fn) {
		S.pL("getFileName " + get_post + ' ' + fn);
		int ix = fn.indexOf('?');
		if ( ix != -1 ) {
		    if ( ix == 0 && get_post.equals("GET") )
			return null;
		    fn = fn.substring(0, ix);
		    S.pL("getFileName0 " + fn);
		    return fn;
		} else {
		    S.pL("getFileName1 " + fn);
		    return fn;
		}
	    }

	    /**
	       URL decode the string.
	    */
	    public String url_decode(String s) {
		if ( s == null )
		    return null;
		try {
		    String url_decoded = java.net.URLDecoder.decode(s, "ISO-8859-15");
		    return url_decoded;
		} catch (IllegalArgumentException ex) {
		    return s;
		} catch (UnsupportedEncodingException ex) {
		    return s;
		} finally {
		}
	    }

	    /**
	       URL encode the string.
	    */
	    public String url_encode(String s) {
		if ( s == null )
		    return null;
		try {
		    String url_encoded = java.net.URLEncoder.encode(s, "ISO-8859-15");
		    return url_encoded;
		} catch (UnsupportedEncodingException ex) {
		    return s;
		} finally {
		}
	    }


	    private HashMap<String, String> getQueryString(String fn) {
		S.pL("getQueryString " + fn);

		HashMap<String, String> hm = new HashMap<String, String>();

		int ix = fn.indexOf('?');
		if ( ix != -1 ) {
		    // fn = prefix + fn.substring(0, ix);
		    String qq = fn.substring(ix+1);
		    String sa[] = S.split(qq, "&");
		    S.pL("Q " + S.a2s(sa));
		    for(int i = 0; i < sa.length; i++) {
			String ss = sa[i];
			int ix2 = ss.indexOf('=');
			if ( ix2 != -1 ) {
			    String ss1 = ss.substring(0, ix2);
			    String ss2 = url_decode(ss.substring(ix2+1));

			    hm.put(ss1, ss2);
			} else {
			    hm.put(ss, "");
			}
		    }
		    return hm;
		} else {
		    return null;
		}
	    }

	    void serve(List sL, BufferedReader rd, DataOutputStream dos) {
		try {
		    // GET fn?q HTTP/1.1
		    String[] sa = S.split((String)(sL.get(0)), " ");
		    HashMap<String, String> q_hm = getQueryString(sa[1]);
		    String fn = getFileName(sa[0], sa[1]);
		    String cmd = sa[0];
		    doPostGet(cmd, fn, q_hm, sL, rd, dos);
		} catch (IOException ex) {
		    S.pL("serve(): Exception " + ex);
		}
	    }

	    void doPostGet(String cmd,
			   String fn,
			   HashMap q_hm,
			   List sL,
			   BufferedReader rd,
			   DataOutputStream dos) throws IOException {

		Map answer_hm = getAnswer(q_hm);

		dos.writeBytes("HTTP/1.0 200 OK\r\n");
		dos.writeBytes("Server: FakeBusinessEngine-Server 1.0\r\n");
		dos.writeBytes("MIME-Version: 1.0\r\n");
		String mime = "text/plain";
		dos.writeBytes("Content-type: " + mime + "\r\n");
		dos.writeBytes("\r\n");

		Iterator it = answer_hm.entrySet().iterator();
		while(it.hasNext()) {
		    Map.Entry ent = (Map.Entry)it.next();
		    String val = ent.getValue() == null ? "" : ent.getValue().toString();
		    String sss = (String)ent.getKey() + '=' + val + "\r\n";
		    byte[] ba = sss.getBytes("ISO-8859-1");
		    dos.write(ba);//Bytes((String)ent.getKey() + '=' + (String)ent.getValue() + "\r\n");
		}
		dos.flush();
	    }

	    @Override
             public void run() {
		try {
		    BufferedReader rd = new BufferedReader(new InputStreamReader(so.getInputStream()));
		    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(so.getOutputStream()));

		    List li = getHeader(rd);
		    serve(li, rd, dos);
		    so.close();
		} catch (IOException ex) {
		} finally {
		}
	    }
	}

	ServerDaemon(int port, Service service) {
	    this.port = port;
	    this.service = service;
	}

	@Override
         public void run() {
	    try {
		sso = new ServerSocket(port);
		S.pL("httpd: Started, " + sso.toString());
		for(;;) {
		    Socket so = sso.accept();
		    ServerConnection con = new ServerConnection(so);
		    S.pL("httpd: Accepted, " + so);
		    con.start();
		}
	    } catch (IOException ex) {
		S.pL("ServerDaemon:run(): " + ex);
	    }
	}
    }

    protected void createAndStartServerDaemon(int port, Service service) {
	ServerDaemon sd = new ServerDaemon(port, service);
	sd.start();
    }

    @Override
    public void main() {
    }
}
