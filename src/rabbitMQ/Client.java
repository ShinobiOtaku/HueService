/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitMQ;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import hueservice.MessageTranslator;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import proto.messages.UpdateLightCommandOuterClass.UpdateLightCommand;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simonanderson
 */
public class Client {
    
    private final static String QUEUE_NAME = "HUECOMMANDS";
    private Channel _channel = null;
    private Connection _connection = null;
    private final MessageTranslator _translator;
    
    public Client(MessageTranslator translator) throws IOException, TimeoutException
    {
        _translator = translator;
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("54.69.168.245");
        _connection = factory.newConnection();
        _channel = _connection.createChannel();

        _channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        Consumer consumer = new DefaultConsumer(_channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws IOException {
              ConsumeMessage(body);
            }
        };
        _channel.basicConsume(QUEUE_NAME, true, consumer);
    }
    
    private void ConsumeMessage(byte[] data) throws InvalidProtocolBufferException
    {
        System.out.printf("Message received %s %n", new Date().getTime());
        UpdateLightCommand command = UpdateLightCommand.parseFrom(data);
        _translator.Translate(command);
        
        System.out.println("Setting off timer");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    PostMessage(command.getLightId());
                } catch (IOException ex) {
                    System.out.println("Failed to turn light off");
                }
            }
          }, 30*1000);
    }
    
    public void PostMessage(String id) throws IOException
    {
        System.out.println("Turning light off");
        
        UpdateLightCommand cmd = UpdateLightCommand
                .newBuilder()
                .setIsOn(false)
                .setLightId(id)
                .setMsgVersion(1)
                .setRgbRed(255)
                .setRgbGreen(255)
                .setRgbBlue(255).build();
        
        _translator.Translate(cmd);
    }
    
    public void Dispose() throws IOException, TimeoutException
    {
        _channel.close();
        _connection.close();
    }
}
