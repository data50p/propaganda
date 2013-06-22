package propaganda.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import propaganda.data.Datagram;
import fpg.sundry.S;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import propaganda.connector.Connector_Http;
import propaganda.data.AddrType;

@SuppressWarnings("unchecked")
public class HttpConnectedServer {

    String prefix = "webroot";
    HttpServer server;
    Connector_Http connector;
    static int cnt = 1;

    HttpConnectedServer(Connector_Http connector, HttpServer server) {
        this.server = server;
        this.connector = connector;
    }

    private List parseHttpHeaders(BufferedReader rd) throws IOException {
        List li = new ArrayList();

        for (;;) {
            String s = rd.readLine();
            if (s.length() == 0) {
                return li;
            }
            li.add(s);
        }
    }

    private boolean access(String fn) {
        File f = new File(fn);
        return f.isFile();
    }

    byte[] getData(String fn) throws IOException {
        File f = new File(fn);
        FileInputStream fin = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        fin.read(data);
        return data;
    }

    int dispatchAsDatagram(HashMap<String, String> hm) throws IOException {
        int n = 0;
        // process d=.., _d_autoregister_=, d0=.., d1=.., ..., d9=..
        for (int i = -2; i < 10; i++) {
            String datagram_string = null;
            if (i == -2) {
                datagram_string = hm.get("d");
                if (datagram_string == null) {
                    datagram_string = hm.get("D");
                }
            } else if (i == -1) {
                datagram_string = hm.get("_d_autoregister_");
            } else {
                datagram_string = hm.get("d" + i);
                if (datagram_string == null) {
                    datagram_string = hm.get("D" + i);
                }
            }
            if (datagram_string == null) {
                continue;
            }
            if (connector.getDefaultClientGhost() != null) {
                final AddrType defaultAddrType = connector.getDefaultClientGhost().getDefaultAddrType();
                datagram_string = datagram_string.replaceFirst("^[ ]*_ ", defaultAddrType.getAddrTypeString() + " ");
            }
            Datagram datagram = new Datagram(datagram_string);

            int dmCnt = connector.dispatchMsg(datagram);
            n++;
        }
        return n;
    }

    String[] whatFile(List li) {
        String s[] = new String[3];

        String[] sa = S.split((String) (li.get(0)), " ");
        String fn = sa[1];
        String q = null;
        int ix = fn.indexOf('?');
        if (ix != -1) {
            fn = prefix + fn.substring(0, ix);
            q = fn.substring(ix + 1);
            s[1] = fn;
            s[2] = q;
            return s;
        } else {
            fn = prefix + fn;
            if (access(fn) == false) {
                fn = fn + "/index.html";
            }
            s[0] = fn;
            return s;
        }
    }

    String getFN(String get_post, String fn) {
        int ix = fn.indexOf('?');
        if (ix != -1) {
            if (ix == 0 && get_post.equals("GET")) {
                return null;
            }
            fn = prefix + fn.substring(0, ix);
            // String q = fn.substring(ix+1);
            if (access(fn) == false) {
                ;//fn += "/index.html";
            }
            return fn;
        } else {
            return prefix + fn;
        }
    }

    private HashMap getQ(String fn) {
        HashMap hm = new HashMap();

        int ix = fn.indexOf('?');
        if (ix != -1) {
            // fn = prefix + fn.substring(0, ix);
            String qq = fn.substring(ix + 1);
            String sa[] = S.split(qq, "&");
            for (int i = 0; i < sa.length; i++) {
                String ss = sa[i];
                int ix2 = ss.indexOf('=');
                if (ix2 != -1) {
                    String ss1 = ss.substring(0, ix2).replaceAll("[+]", " ");
                    String ss2 = ss.substring(ix2 + 1)
                            .replaceAll("[+]", " ")
                            .replaceAll("%22", "\"")
                            .replaceAll("%7B", "{")
                            .replaceAll("%7D", "}")
                            .replaceAll("%7b", "{")
                            .replaceAll("%7d", "}")
                            .replaceAll("%20", " ");
                    String ss3 = ss.substring(ix2 + 1);
                    String ss4;
                    try {
                        ss4 = URLDecoder.decode(ss3, "utf-8");
                    } catch (UnsupportedEncodingException ex) {
                        ss4 = ss2;
                    }
                    hm.put(ss1, ss4);
                } else {
                    hm.put(ss, "");
                }
            }
            return hm;
        } else {
//  	    fn = prefix + fn;
//  	    if ( access(fn) == false )
//  		fn = fn + "/index.html";
            return null;
        }
    }
    static String[][] mimeT = {{"text/html", ".html"},
        {"audio/x-wav", ".wav"},
        {"audio/x-au", ".au"},
        {"image/gif", ".gif"},
        {"image/png", ".png"},
        {"image/jpeg", ".jpg"},
        {"image/jpeg", ".jpeg"}
    };

    String getMime(String fn) {
        for (int i = 0; i < mimeT.length; i++) {
            if (fn.endsWith(mimeT[i][1])) {
                return mimeT[i][0];
            }
        }
        return "text/plain";
    }

    void putHMQ(HashMap q, HashMap<String, String> hm) {
        if (q != null) {
            hm.putAll(q);
        }
    }

    void putHMbr(BufferedReader rd, HashMap hm) {
        for (;;) {
            try {
                String s = rd.readLine();
                if (s == null) {
                    break;
                }
                String[] sa = S.split(s, "=");
                if (sa.length == 2) {
                    hm.put(sa[0], sa[1]);
                }
            } catch (IOException ex) {
            }
        }
    }

    void serve(List sL, BufferedReader rd, DataOutputStream dos) {
        try {
            // GET fn?q HTTP/1.1
            String[] sa = S.split((String) (sL.get(0)), " ");
            HashMap q = getQ(sa[1]);
            String fn = getFN(sa[0], sa[1]);
            String cmd = sa[0];
            doPostGet(cmd, fn, q, sL, rd, dos);
        } catch (IOException ex) {
            S.pL("serve(): Exception " + ex);
        }
    }

    /**
     * Return eiter a query string encoded reponse or a simple string. When user
     * request the server with datagram only then use the latter.
     */
    private String qsencode(Datagram datagram) {
        String addendum = datagram.getMessage().getAddendum();
        return "" + datagram.getDatagramString();
    }

    void doPostGet(String cmd, String fn, HashMap q, List sL, BufferedReader rd, DataOutputStream dos) throws IOException {
        HashMap<String, String> hm = new HashMap();;
        if (cmd.equals("GET")) {
            putHMQ(q, hm);
        } else {
            putHMbr(rd, hm);
        }

        String autoAddr = null;
        int n = 0;
        boolean propagandaRequest = hm.size() > 0 && fn.length() < 9 && fn.equals(prefix + '/');

        if (propagandaRequest) {
            autoAddr = autoRegister(hm);
            n = dispatchAsDatagram(hm);
        }

        int timeOut = -1;
        try {
            String to = hm.get("to");
            timeOut = Integer.parseInt(to);
        } catch (Exception _) {
        }

        if (propagandaRequest) {
            if (n > 0) {
                // loop and return all messages to the client as they arrive
                dos.writeBytes("HTTP/1.0 200 OK\r\n");
                dos.writeBytes("Server: Http Propaganda Server" + "\r\n");
                dos.writeBytes("MIME-Version: 1.0\r\n");
                String mime = "text/plain";
                dos.writeBytes("Content-type: " + mime + "\r\n");
                dos.writeBytes("\r\n");
                for (;;) {
                    Datagram recv_datagram = connector.recvMsg(timeOut);
                    if (recv_datagram == null) {
                        S.pL("got null");
                        break;
                    } else {
                        if (false) {
                            dos.writeBytes(recv_datagram.getDatagramString());
                        } else {
                            dos.writeBytes(qsencode(recv_datagram));
                        }

                        dos.writeBytes("\r\n");
                        dos.flush();
                    }
                }
            } else {
                dos.writeBytes("HTTP/1.0 404 OK\r\n");
                dos.writeBytes("Server: Http Propaganda Server" + "\r\n");
                dos.writeBytes("MIME-Version: 1.0\r\n");
                dos.writeBytes("\r\n");
                dos.flush();
            }
        } else {
            if ( fn.equals(prefix + '/') )
                fn += "index.html"; 
            dos.writeBytes("HTTP/1.0 200 OK\r\n");
            dos.writeBytes("Server: Http Propaganda Server" + "\r\n");
            dos.writeBytes("MIME-Version: 1.0\r\n");
            String mime = getMime(fn);
            dos.writeBytes("Content-type: " + mime + "\r\n");
            dos.writeBytes("\r\n");
            final byte[] data = getData(fn);
            dos.write(data);
            dos.flush();
        }

        if (autoAddr != null) {
            hm = new HashMap<String, String>();
            autoUnRegister(hm, autoAddr);
            n = dispatchAsDatagram(hm);
        }
    }
    public static int[] CNT = {0, 0};

    public void prepareAndServe() {
        synchronized (CNT) {
            CNT[0]++;
            CNT[1]++;
        }
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connector.getSocket().getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(connector.getSocket().getOutputStream()));

            List li = parseHttpHeaders(rd);
            serve(li, rd, dos);
            connector.getSocket().close();
        } catch (IOException ex) {
        } finally {
            synchronized (CNT) {
                CNT[0]--;
            }
        }
    }

    private String autoRegister(HashMap<String, String> hm) {
//        String myId = "" + connector.so.getLocalAddress().getHostAddress() + '-' + connector.so.getPort()+ '-' + this.hashCode() + '@' + "AUTOREGISTRED";
        String myId = "" + connector.getHostAddress() + '-' + connector.name + '@' + "AUTOREGISTRED" + '-' + this.hashCode();
        hm.put("_d_autoregister_", ". @ register;request-id " + myId);
        return myId;
    }

    private void autoUnRegister(HashMap<String, String> hm, String autoAddr) {
        hm.put("_d_autoregister_", autoAddr + " @ unregister;");
    }
}
