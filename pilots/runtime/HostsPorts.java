package pilots.runtime;

import java.util.*;
import java.net.*;

public class HostsPorts {
    private List<String> hosts;
    private List<Integer> ports;

    public HostsPorts() {
        hosts = new ArrayList<>();
        ports = new ArrayList<>();
    }

    public void addHostPort(String host, int port) {
        //System.out.println("addHostPort, host=" + host + ", port=" + port);
        hosts.add(host);
        ports.add(port);
    }

    public String getHost(int index) {
        return hosts.get(index);
    }
    
    public int getPort(int index) {
        Integer port = ports.get(index);
        return port.intValue();
    }

    public int getSize() {
        return hosts.size();  // must be equal to portssize()
    }

    public String toString(int index) {
        return hosts.get(index) + ":" + ports.get(index);
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < hosts.size(); i++) 
            str += toString(i) + " ";
        return str;
    }
            
}
