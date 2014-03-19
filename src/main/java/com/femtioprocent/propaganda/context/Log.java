package com.femtioprocent.propaganda.context;

import com.femtioprocent.fpd.sundry.S;

import java.io.*;
import java.util.*;
import java.text.*;

import java.util.logging.*;

public class Log {

    private static class MyFormatter extends java.util.logging.Formatter {

        static DateFormat dformat = new SimpleDateFormat("dd/MM HH:mm:ss.SSS");
        long last = S.ct();

        @Override
        public String format(LogRecord record) {
            String s = record.getSourceClassName();
            s = s.substring(s.lastIndexOf('.') + 1);
            long ms = record.getMillis();
            Date dt = new Date(ms);
            String d = dformat.format(dt);
            int lt = (int) (ms - last);
            last = ms;

            String more = "";
            Throwable th = record.getThrown();
            if (th != null) {
                for (StackTraceElement st : th.getStackTrace()) {
                    more += "  " + st;
                }
            }

            return ""
                    + S.padRight("" + record.getLevel(), 10, ' ')
                    + d + ' '
                    + S.padLeft("" + lt, 5, ' ') + ' '
                    + S.padRight("" + s, 23, ' ') + ' '
                    + S.padRight("" + record.getSourceMethodName(), 18, ' ') + ' '
                    + record.getMessage() + more + '\n';
        }
    }
    private MyFormatter my_formatter = new MyFormatter();

    public java.util.logging.Logger logger;

    public Log(String name) {
        this(name, true);
    }

    public Log(String name, boolean on) {
        try {
            logger = java.util.logging.Logger.getLogger(name);
            //         logger.getParent().setLevel(Level.OFF);
            logger.setLevel(on ? Level.INFO : Level.OFF);
            File d = new File("logs");
            d.mkdir();
            FileHandler fh = new FileHandler("logs/" + name);
            fh.setFormatter(my_formatter);
            logger.addHandler(fh);
            //            logger.setUseParentHandlers(false);
        } catch (IOException ex) {
        } catch (NoClassDefFoundError ex) {
            logger = null;
        }
    }

    public java.util.logging.Logger getLogger() {
        return logger;
    }
}
