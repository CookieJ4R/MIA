package mia.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/***
 * This class manages the shutdown of the program
 * Every node that needs to be cleanly shutdown can be added via 'addShutdownableNode()'
 */
public class MiaShutdownManager {

    private final List<ShutdownableNode> shutdownableNodes = new ArrayList<>();

    /***
     * Adds a shutdownable node to the list. All nodes get their shutdown method called on system shutdown
     * @param node the node to add
     * @param priority the priority with which the shutdown method will be called on the node (0=highest)
     */
    public void addShutdownableNode(IMiaShutdownable node, int priority){
        shutdownableNodes.add(new ShutdownableNode(node, priority));
    }

    /***
     * Shuts the program down by calling all registered shutdownable nodes shutdown method
     */
    public void shutdownSystem(){
        new Thread(() -> {
            try {
                Thread.sleep(200);
                shutdownableNodes.sort(Comparator.naturalOrder());
                shutdownableNodes.forEach(n -> {
                    Mia.getLogger().logInfo("Shutting down " + n.node.getClass().getSimpleName() + "...");
                    n.node.shutdown();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();
    }

}

/***
 * This class provides a simple wrapper around a IMiaShutdownable to associate it with a priority
 */
class ShutdownableNode implements Comparable<ShutdownableNode>{
    IMiaShutdownable node;
    int priority;

    public ShutdownableNode(IMiaShutdownable node, int priority){
        this.node = node;
        this.priority = priority;
    }


    @Override
    public int compareTo(@NotNull ShutdownableNode o) {
        return Integer.compare(this.priority, o.priority);
    }
}
