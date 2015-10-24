package hueservice;

import com.philips.lighting.hue.sdk.PHHueSDK;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import rabbitMQ.Client;

public class HueService {

    public static class Config{
        public static String Username = "2f0e47e5bd9b6e71b4cebb114ab5e13";
        public static String Address = "192.168.1.75";
    }
    
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        PHHueSDK hue = PHHueSDK.getInstance();
        
        ConnectionManager cm = new ConnectionManager(hue);
        
        hue.getNotificationManager().registerSDKListener(cm);
        
        MessageTranslator mt = new MessageTranslator(cm);
        
        //Thread.sleep(1000);
        
        Client rmqClient = new Client(mt);
  
        //rmqClient.PostMessage();
    }
}
