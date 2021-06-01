package mia.core;

import java.util.*;

/***
 * This class handles everything concerning the CommandBus.
 * Nodes that want to listen for a specific command need to be registered via 'register()' and need to implement {@link IMiaCommandBusNode}
 * Nodes can emit a command on the CommandBus via 'emit()'
 */
public final class MiaCommandBus {

    private final Map<String, List<IMiaCommandBusNode>> registeredNodes = new HashMap<>();

    /***
     * Register a node to the event bus that listens to specific commands.
     * @param node the node to register
     * @param channelsToListenTo the channels the node will listen on
     */
    public void register(IMiaCommandBusNode node, List<String> channelsToListenTo){
        channelsToListenTo.forEach((String channel) -> {
            if(!registeredNodes.containsKey(channel)) {
                Mia.getLogger().logInfo("A node just subscribed to channel: " + channel);
                List<IMiaCommandBusNode> nodeList = new ArrayList<>();
                nodeList.add(node);
                registeredNodes.put(channel, nodeList);
            }
            else
                registeredNodes.get(channel).add(node);
        });
    }

    /***
     * Emits a command on a channel of the command bus with additional data in a String list.
     * @param channel the command will be emitted on
     * @param cmd the command to be emitted
     * @param additionalData additional data as a List of Strings
     */
    public void emit(String channel, String cmd, List<String> additionalData){
        if(registeredNodes.containsKey(channel))
            registeredNodes.get(channel).forEach((node) -> node.receive(cmd, additionalData));
        else
            Mia.getLogger().logInfo("Command " + cmd + " emitted on channel " + channel + " but no node is listening to this channel!");
    }

    /***
     * Emits a command on a channel of the command bus.
     * @param channel the command will be emitted on
     * @param cmd the command to be emitted
     * @param additionalData additional data as individual Strings
     */
    public void emit(String channel, String cmd, String... additionalData){
        if(registeredNodes.containsKey(channel))
            registeredNodes.get(channel).forEach((node) -> node.receive(cmd, Arrays.stream(additionalData).toList()));
        else
            Mia.getLogger().logInfo("Command " + cmd + " emitted on channel " + channel + " but no node is listening to this channel!");
    }

}
