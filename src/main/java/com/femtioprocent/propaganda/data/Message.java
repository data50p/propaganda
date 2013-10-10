package com.femtioprocent.propaganda.data;

public class Message
{
    String addendum;
    String message;
    String text;

    public Message(String text) {
	this.text = text.trim();
	int ix = this.text.indexOf(" ");
	if ( ix == -1 ) {
	    this.message = this.text;
	    this.addendum = null;
	}
	else {
	    this.message = this.text.substring(0, ix).trim();
	    this.addendum = this.text.substring(ix + 1).trim();
	}
    }

    public Message(String message, String addendum) {
	if ( addendum == null )
	    ;//this.text = text;
	else
	    this.text = message + ' ' + addendum;
	this.message = message;
	this.addendum = addendum;
    }

    public String getText() {
	return text;
    }

    public String getTrimmedText() {
	return text == null ? "" : text.trim();
    }

    public String getAddendum() {
	return addendum;
    }

    public String getMessage() {
	return message;
    }

    @Override
    public String toString() 
    {
	return "Message{" + getText() + "}";
    }
}
