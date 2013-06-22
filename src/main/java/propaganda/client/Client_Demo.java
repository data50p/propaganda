package propaganda.client;

import static java.lang.System.exit;
import static propaganda.context.Config.getLogger;
import static propaganda.data.AddrType.createAddrType;

import java.util.logging.Level;

import propaganda.connector.PropagandaConnector;
import propaganda.connector.PropagandaConnectorFactory;
import propaganda.data.AddrType;
import propaganda.data.Datagram;
import propaganda.data.Message;
import propaganda.data.MessageType;
import propaganda.exception.PropagandaException;
import propaganda.server.PropagandaServer;
import fpg.sundry.S;


public class Client_Demo extends PropagandaClient
{
    public Client_Demo(String name)
    {
	super(name);
	init();
    }

    @Override
    protected void init()
    {
	final PropagandaConnector connector;

	connector = PropagandaConnectorFactory.create("Queue", name, PropagandaServer.getDefaultServer(), this);
	try {
	    register("DEMO");
	}
	catch (PropagandaException ex) {
	    getLogger().log(Level.SEVERE, "register: ", ex);
	    exit(0);
	}

	Thread th = new Thread(new Runnable() {
		public void run() {
		    try {
			AddrType receiver_at = createAddrType("*@DEMO");

			int cnt = 0;
			for(;;) {
			    S.m_sleep(S.rand(cnt == 0 ? 2000 : 20000));
			    sendMsg(new Datagram(getDefaultAddrType(), receiver_at, new Message("hello-" + name + '-' + cnt++)));
			}
		    }
		    catch (PropagandaException ex) {
			S.pL("Demo: " + ex);
		    }
		}
	    });
	th.start();

	Thread th2 = new Thread(new Runnable() {
		public void run()
		{
		    try {
			for(;;) {
			    Datagram datagram = connector.recvMsg();
			    if ( datagram.getMessageType() == MessageType.ping ) {
				sendMsg(new Datagram(getDefaultAddrType(), datagram.getSender(), MessageType.pong, datagram.getMessage()));
				getLogger().finest("datagram: " + S.ct() + ' ' + name + " =----> PING " + datagram);
			    }
			    else if ( datagram.getMessageType() == MessageType.pong ) {
				getLogger().finest("datagram: " + S.ct() + ' ' + name + " =----> PONG " + datagram);
			    }
			    else
				getLogger().finest("datagram: " + S.ct() + ' ' + name + " =----> " + datagram);
			}
		    }
		    catch (PropagandaException ex) {
			S.pL("Demo " + ex);
		    }
		}
	    });
	th2.start();
    }

    public void run()
    {
    }
}
