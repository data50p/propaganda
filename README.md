propaganda
==========

Propaganda is a messaging system.

mvn clean install assembly:assembly

-------------------------------

Connecting via socket
---------------------

Step 1)

Start the server (runServer) (on default port 8899)

Step 2)

In your client program, make a TCP connection to server on port 8899

Step 3)

Register yourself as a member in propaganda group by sending this to the server

	 . @ register; request-id <myId>@DEMO

where <myId> is your unique id. Expect a response like:

      @ aa@DEMO 1334489038115; registered aa@DEMO @[DEMO]

Step 4)

Send a message server and read the response.

	_ *@DEMO ;This is my message

All other registred on @DEMO will reveive the message (including yourself):

	aa@DEMO *@DEMO plain 1334489087597;This is my message


Note
=====

* Each message is only ONE line of text.

* Message consist of: <envelop>;<message-text>

* envelop consit of: <sender-address> <receiver-address> [timestamp]

* sender/receiver-address: <uniq-id>@<group>

* special address:
			@ -> the message system it self
			*@<group> -> all in the group
			. -> anonymous
			_ -> my default address (first registred)
