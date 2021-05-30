package mia.core;

import java.util.List;

/***
 * This interface needs to be implemented on every node that want to listen on the CommandBus
 */
public interface IMiaCommandBusNode {

    /**
     * This method gets called when a command is emitted on the eventbus which this node is listening for
     * @param cmd the cmd that got emitted
     * @param data the data associated with the command
     */
    void receive(String cmd, List<String> data);

}
