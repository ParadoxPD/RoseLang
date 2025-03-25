package org;

import org.commandline.CommandLine;

public class Entry {
    public static void main(String[] args) {
        System.out.println("Nothing Happening....");
        for (String arg : args)
            System.out.println(arg);
        CommandLine.main(args);
    }
}
