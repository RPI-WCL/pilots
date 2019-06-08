package pilots.runtime;

import java.util.Date;
import java.util.logging.Logger;


public class ServiceFactory {
    private static Logger LOGGER = Logger.getLogger(ServiceFactory.class.getName());
    
	// Service implementations
    private static CurrentLocationTimeService currLocTime = null;

    // Default service classes
    private static String currLocTimeClass = "pilots.runtime.SimpleTimeService";    

    // Modify settings for ServiceFactory
    public synchronized static void setCurrClass(String currLocTimeClass) {
        ServiceFactory.currLocTimeClass = currLocTimeClass;
    }

    public synchronized static CurrentLocationTimeService getCurrentLocationTime() {
        if (currLocTime == null) {
            String className = null;
            className = System.getProperty("currLocTime");

            if (className == null)
                className = currLocTimeClass;

            try {
                currLocTime = (CurrentLocationTimeService)Class
                    .forName(className)
                    .getDeclaredConstructor()
                    .newInstance();
            } 
            catch (Exception ex) {
                System.err.println(ex);
            }

            Date currTime = currLocTime.getTime();
            LOGGER.finest("currTime=" + currTime + ", className=" + className);
        }

        return currLocTime;
    }
}
    




