package pilots.runtime;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.*;

public class ConnectionManager {
    private static Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());

    public static final int NULL = -1;
    public static final int NOT_CONNECTED = 0;    
    public static final int CONNECTED = 1;
    
    class Connection {
        String host = null;
        int port = -1;
        Socket sock = null;
        PrintWriter writer = null;
        int state = ConnectionManager.NULL;

        Connection(String host, int port) {
            this.host = host;
            this.port = port;
            this.state = NOT_CONNECTED;
        }
    }

    private Map<Integer, Connection> connections;   // key:connId, val:Connection

    public ConnectionManager() {
        connections = new HashMap<>();
    }

    public void create(int connId, String hostport) {
        // connId is an integer specified by the PILOTS appplication
        int colon = hostport.indexOf(":");
        connections.put(
            connId,
            new Connection(hostport.substring(0, colon),
                           Integer.parseInt(hostport.substring(colon + 1))));
    }

    public void destroy(int connId) {
        Connection conn = connections.get(connId);
        if (conn != null && conn.state == ConnectionManager.NOT_CONNECTED)
            connections.remove(connId);
    }

    public boolean isCreated(int connId) {
        // returns if associated Connection instance is created
        return connections.get(connId) != null;
    }

    public boolean isConnected(int connId) {
        Connection conn = connections.get(connId);
        return conn != null && conn.state == ConnectionManager.CONNECTED;
    }
    
    public PrintWriter open(int connId) {
        Connection conn = connections.get(connId);
        if (conn == null) {
            LOGGER.warning("No connection found for connId: " + connId);
            return null;
        }

        if (conn.sock != null && conn.writer != null)
            return conn.writer;

        try {
            conn.sock = new Socket(conn.host, conn.port);
            if (conn.sock != null) {
                conn.writer = new PrintWriter(conn.sock.getOutputStream(), true);
                conn.state = ConnectionManager.CONNECTED;
            }
        } catch (UnknownHostException ex) {
            LOGGER.warning(ex.toString());
        } catch (IOException ex) {
            LOGGER.warning(ex.toString());
        }

        return conn.writer;
    }

    public PrintWriter get(int connId) {
        Connection conn = connections.get(connId);
        if (conn == null) {
            LOGGER.warning("No conn found for connId: " + connId);            
            return null;
        }
        return conn.writer;
    }

    public void close(int connId) {
        Connection conn = connections.get(connId);
        if (conn == null) {
            LOGGER.warning("No conn found for connId: " + connId);
            return;
        }

        if (conn.writer != null)
            conn.writer.close();

        if (conn.sock != null) {
            try {
                conn.sock.close();
            } catch (IOException ex) {
                LOGGER.warning(ex.toString());
            }
        }
    }

    public void closeAll() {
        for (Integer connId : connections.keySet())
            close(connId);
    }
}
