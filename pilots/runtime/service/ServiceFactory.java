package pilots.runtime.service;


public class ServiceFactory {
	// Service implementations
    private static CurrentLocationTimeService currLocTime_ = null;
    
    // Default service classes
    private static String currLocTimeClass_ = "pilots.runtime.service.SimpleTimeService";

    // Modify settings for ServiceFactory
    public synchronized static void setCurrClass( String currLocTimeClass ) {
        ServiceFactory.currLocTimeClass_ = currLocTimeClass;
    }

    public synchronized static CurrentLocationTimeService getCurrentLocationTime() {
        if (currLocTime_ == null) {
            String className = null;
            className = System.getProperty( "currLocTime" );
            if (className == null) className = currLocTimeClass_;

            try {
                currLocTime_ = (CurrentLocationTimeService)Class.forName( className ).newInstance();
            } 
            catch (Exception e) {
                System.err.println( e );
            }
        }

        return currLocTime_;
    }
}
