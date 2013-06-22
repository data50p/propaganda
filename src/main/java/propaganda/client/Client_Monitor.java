package propaganda.client;

import fpg.sundry.*;

import propaganda.data.*;
import propaganda.exception.*;

import static propaganda.context.Config.*;

public class Client_Monitor extends PropagandaClient
{
    public Client_Monitor(String name) 
    {
	super(name);
	init();
    }

    @Override
    protected void init() 
    {
	super.init();
	Thread th2 = new Thread(new Runnable() {
		public void run() 
		{
		    for(;;) {
			try {
			    if ( connector != null ) {
				Datagram datagram = connector.recvMsg();
				if ( datagram.getMessageType() == MessageType.ping ) {
				    sendMsg(new Datagram(getDefaultAddrType(), datagram.getSender(), MessageType.pong, datagram.getMessage()));
				    getLogger("monitor").finest("dgr: " + S.ct() + ' ' + name + " → " + datagram);
				}
				else if ( datagram.getMessageType() == MessageType.pong ) {
				    getLogger("monitor").finest("dgr: " + S.ct() + ' ' + name + " → " + datagram);
				}
				else
				    getLogger("monitor").fine("got: " + S.ct() + ' ' + name + " → " + datagram);
			    } else {
				S.m_sleep(200);
			    }
			}
			catch (PropagandaException ex) {
			    S.pL("Monitor: " + ex);
			}
		    }
		}
	    });
	th2.start();
    }
}
