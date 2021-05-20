package mia.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBus {

    public static CommandBus INSTANCE = new CommandBus();

    private final Map<String, ICommandBusNode> registeredNodes = new HashMap();

    public void register(ICommandBusNode node, List<String> cmdsToListenTo){
        cmdsToListenTo.forEach((String cmd) -> {
            if(!registeredNodes.containsKey(cmd))
                registeredNodes.put(cmd, node);
            else
                System.out.println("Command " + cmd + " is already registerd to a different Node");
        });
    }

    public void emit(String cmd, List<String> additionalData){
        if(registeredNodes.containsKey(cmd))
            registeredNodes.get(cmd).receive(cmd, additionalData);
        else
            System.out.println("Command " + cmd + " is not registerd to any listener");
    }

}
