package hueservice;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import java.awt.Color;
import java.util.List;
import java.util.Map;

interface ISession {
    void UpdateLight(String lightId, int hue, int sat, int brightness, boolean isOn);
}

class NullSession implements ISession {

    @Override
    public void UpdateLight(String lightId, int hue, int sat, int brightness, boolean isOn)
    { }
    
}

/**
 *
 * @author simonanderson
 */
class BridgeSession implements ISession{
    private final PHBridge _phb;
    private final Map<String, PHLight> _lights;

    BridgeSession(PHBridge phb) {
        _phb = phb;
        
        _lights = phb.getResourceCache().getLights();
    }
    
    @Override
    public void UpdateLight(String lightId, int hue, int sat, int brightness, boolean isOn)
    {
        if(_lights.containsKey(lightId))
        {
            Color white = Color.white;
            List<Float> xy = RGBtoXYConverter.getRGBtoXY(white.getRed(), white.getGreen(), white.getBlue());
            PHLightState newState = new PHLightState();
            newState.setOn(isOn);
            newState.setX(xy.get(0));
            newState.setY(xy.get(1));
        
            PHLight l = _lights.get(lightId);
            _phb.updateLightState(l, newState);
            
            System.out.printf("Light state updated %s %n", lightId);
        }
    }
}
