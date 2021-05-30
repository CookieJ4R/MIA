package mia.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * This class handles everything concerning the CommandBus.
 * Nodes that want to listen for a specific command need to be registered via 'register()' and need to implement {@link IMiaCommandBusNode}
 * Nodes can emit a command on the CommandBus via 'emit()'
 */
public final class MiaCommandBus {

    private final Map<String, IMiaCommandBusNode> registeredNodes = new HashMap<>();

    /***
     * Register a node to the event bus that listens to specific commands.
     * @param node the node to register
     * @param cmdsToListenTo the commands the node will listen for
     */
    public void register(IMiaCommandBusNode node, List<String> cmdsToListenTo){
        cmdsToListenTo.forEach((String cmd) -> {
            if(!registeredNodes.containsKey(cmd))
                registeredNodes.put(cmd, node);
            else
                System.out.println("Command " + cmd + " is already registerd to a different Node");
        });
    }

    /***
     * Emits a command on the command bus. You can pass additional Data as a String list.
     * @param cmd the command to be emitted
     * @param additionalData additional data that can be processed by the receiver.
     */
    public void emit(String cmd, List<String> additionalData){
        if(registeredNodes.containsKey(cmd))
            registeredNodes.get(cmd).receive(cmd, additionalData);
        else
            System.out.println("Command " + cmd + " is not registerd to any listener");
    }

}
