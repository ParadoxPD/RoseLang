package org.commandline;

import java.util.HashMap;
import java.util.Map;

import org.repl.*;

public class CommandLine {
    public static void main(String args[]) {

        Map<String, String> parsedArgs = new HashMap<String, String>();
        setDefaults(parsedArgs);

        for (String arg : args) {
            if (arg.contains("="))
                parsedArgs.put(arg.split("=")[0], arg.split("=")[1]);
            else
                parsedArgs.put(arg, "");
        }
        if (parsedArgs.containsKey("--repl")) {
            String arr[] = { parsedArgs.get("--debug") };
            REPL.main(arr);
        } else if (parsedArgs.containsKey("--compile")) {
            System.out.println("Compile");
        } else if (parsedArgs.containsKey("--help")) {
            printHelp();
        } else {
            System.out.println("You fucked up");
        }

    }

    static void printHelp() {
        String help = "--complile -> Compiles the Jork code\n--relp -> Start Jork REPL\n--output -> output file\n--debug -> Print Debug info";
        System.out.println(help);
    }

    static void setDefaults(Map<String, String> parsedArgs) {
        parsedArgs.put("--debug", "false");
    }
}
