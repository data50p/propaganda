package com.femtioprocent.propaganda.data;

import static java.lang.Long.parseLong;
import static com.femtioprocent.propaganda.context.Config.getLogger;
import static com.femtioprocent.propaganda.data.AddrType.createAddrType;
import com.femtioprocent.fpd.sundry.S;

public class Datagram {

    String receipt;
    AddrType sender, receiver;
    MessageType message_type;
    String message_type_if_bad;  // store message type here if it was bad (unknown or whatever)
    String message_type_arg;
    long time;
    Message msg;
    String datagram_string;
    Status status;

    public enum Status {

        OK, BAD, IGNORE
    };

    public Datagram(Datagram dgr) {
        this.sender = dgr.sender;
        this.receiver = dgr.receiver;
        this.msg = dgr.msg;
        this.message_type = dgr.message_type;
        this.message_type_if_bad = dgr.message_type_if_bad;
        this.message_type_arg = dgr.message_type_arg;
        this.time = dgr.time;
        this.datagram_string = dgr.datagram_string;
        status = dgr.status;
    }

    public Datagram(AddrType sender, AddrType receiver, Message msg) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.message_type = MessageType.plain;
        this.time = S.ct();
        this.datagram_string = "" + sender.getAddrTypeString() + ' '
                + receiver.getAddrTypeString() + ' ' + getTimeAsString() + "; "
                + msg.getText();
        status = Status.OK;
    }

    public Datagram(AddrType sender, AddrType receiver, MessageType message_type, Message msg) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.message_type = message_type;
        this.time = S.ct();
        this.datagram_string = "" + sender.getAddrTypeString() + ' '
                + receiver.getAddrTypeString() + ' '
                + getCompleteMessageTypeAsString() + ' '
                + getTimeAsString() + "; "
                + msg.getText();
        status = Status.OK;
    }

    public Datagram(AddrType sender, AddrType receiver, MessageType message_type, long time, Message msg) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.message_type = message_type;
        this.time = time;
        this.datagram_string = "" + sender.getAddrTypeString() + ' '
                + receiver.getAddrTypeString() + ' '
                + getCompleteMessageTypeAsString() + ' '
                + getTimeAsString() + "; "
                + msg.getText();
        status = Status.OK;
    }

    public Datagram(String datagram_string) {
        // First remova and remember any receipt "[receipt] envelop; message"

        if (datagram_string.startsWith("[")) {
            int index2 = datagram_string.indexOf("]");
            if (index2 == -1) {
                return;
            }
            this.receipt = datagram_string.substring(1, index2);
            datagram_string = datagram_string.substring(index2 + 1).trim(); // remove receipt from string
        }

        int index = datagram_string.indexOf(';');
        if (index == -1) {
            datagram_string += ";";
            index = datagram_string.indexOf(';');
        }

        String envelop = datagram_string.substring(0, index);
        String content = datagram_string.substring(index + 1);

        String[] envelop_arr = envelop.split(" ");

        if (envelop_arr.length < 2) {
            status = Status.IGNORE;
            return;
        }

        this.sender = createAddrType(envelop_arr[0]);
        this.receiver = createAddrType(envelop_arr[1]);
        this.msg = new Message(content);

        getLogger().finest("parse: " + this + '[' + envelop + ']' + S.a2s(envelop_arr));

        String s2 = envelop_arr.length > 2 ? envelop_arr[2] : null;

        if (s2 == null) { // only sender, recv in envelop
            this.message_type = MessageType.plain;
            this.time = S.ct();
            this.datagram_string = "" + sender.getAddrTypeString() + ' '
                    + receiver.getAddrTypeString() + ' '
                    + getTimeAsString() + "; "
                    + msg.getText();
        } else { // more than sender, recv in envelop
            try {
                this.time = parseLong(s2); // expect catch NumberFormatException if s2 is messagetype
                // sender, receiver and time in envelop
                this.message_type = MessageType.plain;
                this.datagram_string = "" + sender.getAddrTypeString() + ' '
                        + receiver.getAddrTypeString() + ' '
                        + getTimeAsString() + "; "
                        + msg.getText();
            } catch (NumberFormatException ex) { // sender, recv, messagetype and maybee time in envelop
                try {
                    this.message_type = parseMessageType(s2);
                    if (this.message_type == MessageType.RM) {
                        String mt_arg = envelop_arr[2].length() > 2 ? envelop_arr[2].substring(3) : "";
                        message_type_arg = mt_arg;
                    }

                    String ts = envelop_arr.length > 3 ? envelop_arr[3] : null;
                    try {
                        this.time = ts == null ? S.ct() : parseLong(ts);
                    } catch (NumberFormatException ex2) {
                        getLogger().warning("parseLong: ts " + ts);
                        this.time = S.ct();
                    }

                    this.datagram_string = "" + sender.getAddrTypeString() + ' '
                            + receiver.getAddrTypeString() + ' '
                            + getCompleteMessageTypeAsString() + ' '
                            + getTimeAsString() + "; "
                            + msg.getText();
                } catch (IllegalArgumentException ex2) {
                    this.message_type = MessageType.bad;
                    this.message_type_if_bad = s2;
                    this.datagram_string = datagram_string;
                    status = Status.BAD;
                    getLogger().finest("parse2: " + this + '[' + envelop + ']' + S.a2s(envelop_arr));
                    return;
                }
            }
        }
        status = Status.OK;
        getLogger().finest("parse3: " + this + '[' + envelop + ']' + S.a2s(envelop_arr));
    }

    private MessageType parseMessageType(String s) {
        if (s.startsWith("RM:")) // this type contains 'RM:group:duration'
        {
            return MessageType.RM;
        }

        return Enum.valueOf(MessageType.class, s);
    }

    public void setReceipt(String s) {
        receipt = s;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getMessageTypeArg() {
        return message_type_arg;
    }

    public String getCompleteMessageTypeAsString() {
        if (message_type == MessageType.bad) {
            return message_type_if_bad;
        }

        if (message_type == MessageType.RM) {
            return "" + message_type + ':' + message_type_arg;
        } else {
            return "" + message_type;
        }
    }

    public AddrType getSender() {
        return sender;
    }

    private void recreateDatagramString() {
        if (message_type == MessageType.plain && status != Status.BAD) {
            this.datagram_string = "" + sender.getAddrTypeString() + ' '
                    + receiver.getAddrTypeString() + ' '
                    + getTimeAsString() + "; " + // 		/* time + */ "; " +
                    msg.getText();
        } else if (message_type == MessageType.plain) {
            this.datagram_string = "" + sender.getAddrTypeString() + ' '
                    + receiver.getAddrTypeString() + ' '
                    + getTimeAsString() + "; "
                    + msg.getText();
        } else {
            this.datagram_string = "" + sender.getAddrTypeString() + ' '
                    + receiver.getAddrTypeString() + ' '
                    + getCompleteMessageTypeAsString() + ' '
                    + getTimeAsString() + "; " + msg.getText();
        }
    }

    public void setSender(AddrType sender) {
        this.sender = sender;
        recreateDatagramString();
    }

    public AddrType getReceiver() {
        return receiver;
    }

    public void setReceiver(AddrType receiver) {
        this.receiver = receiver;
        recreateDatagramString();
    }

    public MessageType getMessageType() {
        return message_type;
    }

    public Message getMessage() {
        return msg;
    }

    public long getTime() {
        return time;
    }

    public String getTimeAsString() {
        return time == 0 ? "" : "" + time;
    }

    public String getDatagramString() {
        return datagram_string;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status == Status.BAD ? "" + status + ';' + getDatagramString() : getDatagramString();
    }
}
