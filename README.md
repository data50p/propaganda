propaganda
==========

Propaganda is a messaging system.

A message is one line of text consisting of an envelop and a arbitrary utf-8 text. The envelop contains at least a sender address and a receiver address. The first ';' separates envelop and the payload text.
Messages can be sent thru a variaty of protocols.

* Simple socket
* HTTP
* MQTT
* Web Service (soap)


Building instructions
---------------------

    mvn clean install assembly:assembly
In target there will be an executable jar file. 


Connecting via socket
---------------------

1. Start the server (runServer) (on default port 8899)

2. In your client program, make a TCP connection to server on port 8899

3. Register yourself as a member in propaganda group by sending this to the server

       . @ register; request-id <myId>@DEMO
  
  	As response you get:
  
       @ aa@DEMO 1334489038115; registered aa@DEMO @[DEMO]

4. Send a message server and wait for the response.

	   _ *@DEMO ;This is my one line message

5. All other registred on @DEMO will reveive the message (including yourself):

       aa@DEMO *@DEMO plain 1334489087597;This is my message


Note
=====

* Each message is only ONE line of text.

* Message consist of: <envelop>;<message-text>

* envelop consit of: <sender-address> <receiver-address> [timestamp]

* sender/receiver-address: <uniq-id>@<group>

* special address:
	
Address  | Meaning
---------| :----------------
@        | the message system it self
*@group  | all in the group
.        | anonymous
_        | my default address (first registred, or automatic)
