package pilots.runtime;

import java.util.*;
import java.net.Socket;
import java.util.logging.*;

public class WriterManager {
    private static Logger LOGGER = Logger.getLogger(WriterManager.class.getName());
    
    class Host {
        String host = null;
        int port = -1;
        Socket sock = null;
        PrintWriter writer = null;

        Host(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    private Map<Integer, Host> hosts;

    public WriterManager() {
        hosts = new HashMap<>();
    }

    public void addHost(int i, String hostport) {
        int colon = hostport.indexOf(":");
        hosts.add(i, new Host(hostport.substring(0, colon),
                              Integer.parseInt(hostport.substring(colon + 1))));

    }

    public PrintWriter open(int i) {
        Host host = hosts.get(i);
        if (host == null) {
            LOGGER.warning("No host found for index " + i);
            return null;
        }

        if (sock != null && writer != null)
            return writer;
        
        sock = new Socket(host.host, host.port);
        writer = new PrintWriter(sock.getOutputStream(), true);
    }

    public PrintWriter get(int i) {
        Host host = hosts.get(i);
        if (host == null) {
            LOGGER.warning("No host found for index " + i);
            return null;
        }
        return host.writer;
    }

    public void close(int i) {
        Host host = hosts.get(i);
        if (host == null) {
            LOGGER.warning("No host found for index " + i);
            return;
        }

        if (host.writer != null)
            host.writer.close();

        if (host.sock != null) {
            try {
                host.sock.close();
            } catch (IOException ex) {
                LOGGER.warning(ex.toString());
            }
        }
    }

    public void closeAll() {
        for (Integer i : hosts.keySet())
            close(i);
    }

    /*
    public voiid print(int i, String str) {
        Host host = hostList.get(i);
        if (host == null) {
            LOGGER.warning("No host found for index " + i);
            return;
        }
        host.writer.print(str);
    }

    public voiid println(int i, String str) {
        Host host = hostList.get(i);
        if (host == null) {
            LOGGER.warning("No host found for index " + i);
            return;
        }
        host.writer.println(str);
    }

    public voiid flush(int i) {
        Host host = hostList.get(i);
        if (host == null) {
            LOGGER.warning("No host found for index " + i);
            return;
        }
        host.writer.flush();
    }
    */    
}
