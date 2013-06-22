package com.femtioprocent.fpd.sundry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIOAppl extends Appl {

    public interface IOService {
	public void doIO(InputStream is, OutputStream os) throws IOException;
    }

    class ServerDaemon extends Thread {
	ServerSocket sso;
	IOService ioservice;
	int port;

	public class ServerConnection extends Thread {
	    Socket so;

	    ServerConnection(Socket so) {
		super("httpd.ServerConnection");
		this.so = so;
	    }

	    @Override
             public void run() {
		try {
		    InputStream is = so.getInputStream();
		    OutputStream os = so.getOutputStream();

		    ioservice.doIO(is, os);
		    is.close();
		    os.flush();
		    os.close();
		    so.close();
		} catch (IOException ex) {
		} finally {
		}
	    }
	}

	ServerDaemon(int port, IOService ioservice) {
	    this.port = port;
	    this.ioservice = ioservice;
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

    protected void createAndStartServerDaemon(int port, IOService ioservice) {
	ServerDaemon sd = new ServerDaemon(port, ioservice);
	sd.start();
    }

    @Override
    public void main() {
    }
}
