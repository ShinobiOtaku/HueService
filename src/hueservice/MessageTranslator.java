/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hueservice;

import proto.messages.UpdateLightCommandOuterClass.UpdateLightCommand;

/**
 *
 * @author simonanderson
 */
public class MessageTranslator {
    private final ConnectionManager _connectionManager;

    MessageTranslator(ConnectionManager cm) {
        _connectionManager = cm;
        
    }
    
    public void Translate(UpdateLightCommand command)
    {
        _connectionManager.Session.UpdateLight(
                command.getLightId(),
                command.getRgbRed(),
                command.getRgbGreen(),
                command.getRgbRed(),
                command.getIsOn());
    }
}
