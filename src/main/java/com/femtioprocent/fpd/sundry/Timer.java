package com.femtioprocent.fpd.sundry;

import java.util.*;

/**
   A Timer report the time since it was created or last asked.
   The time reported are in milli seconds.
 */
public class Timer {
    /**
       Automatically updated via CVS operations.
     */
    static String build_date = "$Id: Timer.java,v 1.5 2003/03/21 09:34:48 lars Exp $";

    /**
       The start of the timer.
     */
    private long ct_start;

    /**
       The last time the Timer was reseted.
     */
    private long ct_last;

    /**
       Create a timer object and set the start time.
     */
    public Timer() {
	ct_start = ct_last = System.currentTimeMillis();
    }

    /**
       Return the time between now and when last reseted.
     */
    public int get() {
	long ct_now = System.currentTimeMillis();
	return (int)(ct_now - ct_last);
    }

    /**
       Return the time between now and when last reseted.
     */
    public long getTime() {
	long ct_now = System.currentTimeMillis();
	return ct_now - ct_last;
    }

    /**
       Return the time between now and when it was started.
     */
    public long getTimeFromStart() {
	long ct_now = System.currentTimeMillis();
	return ct_now - ct_start;
    }

    /**
       Return the time between now and when it was last reseted. Reset the timer also.
     */
    public long getTimeReset() {
	long ct_now = System.currentTimeMillis();
	long l = ct_now - ct_last;
	ct_last = ct_now;
	return l;
    }

    /**
       Return the time between now and when it was started and last reseted.
     */
    @Override
    public String toString() {
	long ct_now = System.currentTimeMillis();
	return "Timer{" + (ct_now - ct_start) + ',' + (ct_now - ct_last) + '}';
    }

    /**
       Write out on System.err the internal state.
     */
    private void dump(String prfx) {
	S.p(prfx + this + " getTime=" + getTime() +
	    " getTimeReset=" + getTimeReset() +
	    " getTimeFromStart=" + getTimeFromStart());
    }

    /**
       Return the start date as a Date object
     */
    public Date startAsDate() {
	return new Date(ct_start);
    }

    /**
       Simple formatter för displaying time in days, minutes ...
    */
    public String format(long millis) {
	int ms = (int)(millis % 1000);
	millis /= 1000;
	int s = (int)(millis % 60);
	millis /= 60;
	int m = (int)(millis % 60);
	millis /= 60;
	int h = (int)(millis % 24);
	millis /= 24;
	int d = (int)(millis);
	return "" + d + ' ' +
	    h + ' ' +
	    m + ' ' +
	    s + ' ' +
	    ms;
    }

    public static void main(String[] args) {
	Timer t = new Timer();

	t.dump("0 ");
	t.dump("0 ");
	int a = 0;
	for(int i = 0; i < 100000000; i++)
	    a++;
	t.dump("1 ");
	t.dump("1 ");
	for(int i = 0; i < 100000000; i++)
	    a++;
	t.dump("2 ");
	t.dump("2 ");

	S.flush();
    }
}

