package com.femtioprocent.propaganda.server.federation;

import com.femtioprocent.propaganda.data.Datagram;
import com.femtioprocent.propaganda.server.PropagandaServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.femtioprocent.propaganda.context.Config.getLogger;

public class FederationClient {

    PropagandaServer server;

    int port;
    String host;
    private ServerSocket serverSocket;
    private Socket socket = null;
    private PrintWriter printWriter = null;

    public FederationClient(PropagandaServer server, String federation_join, int federation_port) throws IOException {
        port = federation_port;
        host = federation_join;
        this.server = server;
        startFederationClient();
        System.err.println("new com.femtioprocent.propaganda.server.federation.FederationClient: " + federation_port);
    }

    public String getId() {
        return "FederationClient-" + this.hashCode();
    }

    private void startFederationClient() {
        System.err.println("Start FedClient");
        Thread sth = new Thread(() -> {
            try {
                socket = new Socket(host, port);
                System.err.println("FedClient connected: " + socket);
                socket.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                try {
                    if (printWriter == null) {
                        printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
                    }
                    System.err.println("sendToFed: " + printWriter);
                } catch (IOException ex) {
                    getLogger().severe("no-socket: " + socket.toString());
                }

                for (; ; ) {
                    String sin = rd.readLine();
                    System.err.println("FedClient got: " + sin);
                    if (sin == null) {
                        break;
                    }
                    Datagram datagram = new Datagram(sin);
                    System.err.println("FedClient dispatch: " + datagram);
                    server.dispatcher.dispatchMsg(null, datagram, printWriter);
                }
            } catch (Exception ex) {
                System.err.println("FedClient ex: " + ex);
            }
        });
        sth.start();
    }

    public void sendToFed(String s, PrintWriter avoid) {
        System.err.println("sendToFed fso: " + socket);
        if (socket == null) {
            return;
        }

        if (printWriter != avoid) {
            if (printWriter != null) {
                try {
                    synchronized (printWriter) {
                        printWriter.println(s);
                        printWriter.flush();
                        System.err.println("sendToFed send: " + s);
                    }
                } catch (Exception ex) {
                    System.err.println("sendToFed send: " + ex);
                }
            }
        }
    }
}
