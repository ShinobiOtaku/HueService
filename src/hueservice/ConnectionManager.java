package hueservice;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import hueservice.HueService.Config;
import java.util.List;

class ConnectionManager implements PHSDKListener {
    private final PHHueSDK _hue;

    ConnectionManager(PHHueSDK hue) throws InterruptedException {
        _hue = hue;
        //System.out.println("Searching for Bridge");
        //PHBridgeSearchManager sm = (PHBridgeSearchManager) hue.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        //sm.search(true, true);
        Session = new NullSession();
        Connect();
    }
    
    public ISession Session;
    
    public final void Connect() throws InterruptedException
    {
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(Config.Address);
        accessPoint.setUsername(Config.Username);
        
        _hue.connect(accessPoint);
    }
    
    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phb) {
        System.out.println("Cache updated");//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onBridgeConnected(PHBridge phb, String string) {        
        System.out.printf("Bridge connected %s %n", string);
         
        Session = new BridgeSession(phb);
        
        PHBridgeResourcesCache cache = _hue.getSelectedBridge().getResourceCache();
        
        //cache.getAllSensors().forEach((PHSensor sensor) -> System.out.printf("Sensor: %s %n", sensor.getBaseState().toString()));
        cache.getAllLights().forEach((PHLight light) -> System.out.printf("Light %s %n", light.toString()));
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phap) {
        System.out.println("Authentication required, press push link button");
         _hue.startPushlinkAuthentication(phap);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
         System.out.println("Access point found");
         
         list.forEach((PHAccessPoint x) -> {
             System.out.println(x.getIpAddress());
             x.setUsername("2f0e47e5bd9b6e71b4cebb114ab5e13");
             _hue.connect(x);
        });
    }

    @Override
    public void onError(int i, String string) {
         System.out.printf("Error %s %s %n", string, i);//To change body of generated methods, choose Tools | Templates.
         
         //TODO: void session and reconnect
         
    }

    @Override
    public void onConnectionResumed(PHBridge phb) {
        System.out.println("Connection resumed");//To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onConnectionLost(PHAccessPoint phap) {
        System.out.println("Connection lost");//To change body of generated methods, choose Tools | Templates.
        
        //Connect();
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        System.out.println("Parsing errors");//To change body of generated methods, choose Tools | Templates.
    }
    
}
