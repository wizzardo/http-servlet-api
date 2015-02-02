package com.wizzardo.servlet;

import java.io.File;

/**
 * Created by wizzardo on 02.02.15.
 */
public class Runner {

    private static final String OPTION_WAR = "war";
    private static final String OPTION_WORKERS = "workers";
    private static final String OPTION_IO_WORKERS = "io-workers";
    private static final String OPTION_PORT = "port";
    private static final String OPTION_HOST = "host";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("options:");
            System.out.println("\t" + OPTION_WAR + "=path_to_war_file");
            System.out.println("\t" + OPTION_WORKERS + "=10 (default - 10)");
            System.out.println("\t" + OPTION_IO_WORKERS + "=4 (default - number of cpu cores)");
            System.out.println("\t" + OPTION_PORT + "=8080 (default - 8080)");
            System.out.println("\t" + OPTION_HOST + "=localhost (default - 0.0.0.0)");
            return;
        }

        int port = 8080;
        int workers = 10;
        int ioWorkers = -1;
        String host = "0.0.0.0";
        File war = null;

        for (String option : args) {
            if (checkOption(option, OPTION_WAR))
                war = new File(getStringOption(option, OPTION_WAR));
            else if (checkOption(option, OPTION_WORKERS))
                workers = getIntOption(option, OPTION_WORKERS);
            else if (checkOption(option, OPTION_IO_WORKERS))
                ioWorkers = getIntOption(option, OPTION_IO_WORKERS);
            else if (checkOption(option, OPTION_PORT))
                port = getIntOption(option, OPTION_PORT);
            else if (checkOption(option, OPTION_HOST))
                host = getStringOption(option, OPTION_HOST);
        }

        ServletServer server = new ServletServer(host, port, workers);
        if (ioWorkers != -1)
            server.setIoThreadsCount(ioWorkers);

        server.registerWar(war, "");
        server.start();
    }

    private static String getStringOption(String src, String name) {
        return src.substring(name.length() + 1);
    }

    private static int getIntOption(String src, String name) {
        return Integer.parseInt(getStringOption(src, name));
    }

    private static boolean checkOption(String src, String name) {
        return src.startsWith(name + "=");
    }
}
