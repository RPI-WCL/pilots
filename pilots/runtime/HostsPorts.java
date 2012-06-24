package pilots.runtime;

import java.util.Vector;
import java.net.*;

public class HostsPorts {
    private Vector<String> hosts_;
    private Vector<Integer> ports_;

    public HostsPorts() {
        hosts_ = new Vector<String> ();
        ports_ = new Vector<Integer> ();
    }

    public void addHostPort( String host, int port ) {
        //System.out.println( "addHostPort, host=" + host + ", port=" + port );
        hosts_.add( host );
        ports_.add( new Integer( port ) );
    }

    public String getHost( int index ) {
        return hosts_.get( index );
    }
    
    public int getPort( int index ) {
        Integer port = ports_.get( index );
        return port.intValue();
    }

    public int getSize() {
        return hosts_.size();  // must be equal to ports_size()
    }
}