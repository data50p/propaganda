package com.femtioprocent.propaganda.server.clientsupport;

import com.femtioprocent.propaganda.connector.Connector_Plain;
import static com.femtioprocent.propaganda.context.Config.getLogger;
import static com.femtioprocent.propaganda.data.AddrType.defaultAddrType;
import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.exception.PropagandaException;
import com.femtioprocent.propaganda.server.PropagandaServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class FederationServer {

    private String id;

    int port;
    private ServerSocket sso;

    public FederationServer(String id) {
        this.id = id;
    }

    public FederationServer(int federation_port) throws IOException {
        port = federation_port;
        startFederationServer();
    }

    public String getId() {
        return id;
    }

    public void sendToFederatedPropaganda(Datagram datagram) throws PropagandaException {
        System.out.println(">F> Send to federation: " + id + ' ' + datagram);
    }

    @Override
    public String toString() {
        return "FederationServer{" + id + "}";
    }

    private void startFederationServer() throws IOException {
        sso = new ServerSocket(port);
        Thread th;
        th = new Thread(() -> {
            try {
                for (;;) {
                    Socket so = sso.accept();
                    Thread sth = new Thread(() -> {
                        try {
                            InputStream is = so.getInputStream();
                            OutputStream os = so.getOutputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            PrintWriter pw = new PrintWriter(os, true);
                            NEXT_LINE:
                            for (;;) {
                                String s = br.readLine();
                                if (s == null) {
                                    is.close();
                                    br.close();
                                    return;
                                }
                                System.out.println("got (fed) " + s);
                                try {
                                    Datagram datagram = new Datagram(s);

                                    if (datagram.getSender() == defaultAddrType) {
                                        System.err.println("_ ignored: " + s);
                                        continue NEXT_LINE;
                                    }
                                    if (datagram.getReceiver() == defaultAddrType) {
                                        System.err.println("_ ignored: " + s);
                                        continue NEXT_LINE;
                                    }

                                    int dmCnt = PropagandaServer.getDefaultServer().dispatcher.dispatchMsg(null, datagram);

                                    String receipt = datagram.getReceipt();
                                    if (receipt != null) {
                                        //transmitReceipt(receipt + ',' + dmCnt);
                                    }
                                } catch (Exception ex) {
                                    getLogger().log(Level.SEVERE, "exception: [" + s + "];", ex);
                                }
                            }
                        } catch (IOException ex) {
                            System.err.println("Can't io " + ex);
                        }
                    });
                    sth.start();
                }
            } catch (IOException ex) {
                System.err.println("Can't run " + ex);
            }
        });
        th.start();
    }
}
