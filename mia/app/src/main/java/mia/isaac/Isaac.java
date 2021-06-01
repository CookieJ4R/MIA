package mia.isaac;

import mia.core.IMiaCommandBusNode;
import mia.core.IMiaExtensionModule;
import mia.core.Mia;

import java.util.List;

/***
 * The main Isaac module file
 */
public class Isaac implements IMiaExtensionModule, IMiaCommandBusNode {

    private IsaacScriptLoader isaacScriptLoader;
    private IsaacScriptExecutor isaacScriptExecutor;

    @Override
    public void initModule() {
        isaacScriptLoader = new IsaacScriptLoader();
        isaacScriptLoader.loadScripts();
        isaacScriptExecutor = new IsaacScriptExecutor();


        Mia.getCommandBus().register(this, List.of("mqtt:isaac","isaac"));
    }

    @Override
    public void addShutdownHooks() {
        Mia.getShutdownManager().addShutdownableNode(isaacScriptExecutor, 0);
    }

    @Override
    public void receive(String cmd, List<String> data) {
        if ("runScriptByName".equals(cmd)) {
            isaacScriptExecutor.runScript(isaacScriptLoader.getScript(data.get(0)));
        }
    }
}
