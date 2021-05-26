package mia.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MiaShutdownManager {

    private final List<ShutdownableNode> shutdownableNodes = new ArrayList<>();

    public void addShutdownableNode(IMiaShutdownable node, int priority){
        shutdownableNodes.add(new ShutdownableNode(node, priority));
    }

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

class ShutdownableNode implements Comparable<ShutdownableNode>{
    IMiaShutdownable node;
    int priority;

    public ShutdownableNode(IMiaShutdownable node, int priority){
        this.node = node;
        this.priority = priority;
    }


    @Override
    public int compareTo(@NotNull ShutdownableNode o) {
        if(this.priority == o.priority) return 0;
        if(this.priority > o.priority) return 1;
        else return -1;
    }
}
