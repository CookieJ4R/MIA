package mia.core;

import java.util.List;

public interface ICommandBusNode {

    void receive(String cmd, List<String> data);

}
