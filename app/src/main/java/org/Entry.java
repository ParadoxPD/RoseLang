package org;

import org.commandline.CommandLine;

public class Entry {
    public static void main(String[] args) {
        try {
            // System.out.println("Nothing Happening....");
            // for (String arg : args)
            //  System.out.println(arg);
            CommandLine.main(args);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace(System.out);
            // main(args);
        }
    }
}
