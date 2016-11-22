package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Machine Information Manager.
 */

public class NodeManager {
    private static ConcurrentHashMap<String, Group> managerMap;
    private int NodeConnectTimeout = 1000;
    private static Logger LOG = LoggerFactory.getLogger(NodeManager.class);

    public NodeManager() {
        managerMap = new ConcurrentHashMap<>();
    }

    public String getSuitable(String serviceName) {
        // TODO: 16-11-16 example for use ruler
        return managerMap.get(serviceName).getSuitable();
    }

    /**
     * Description: this update function used by ServiceManager,when NodeChangeListener be called,This function will be called and clear & Update the map
     * Input: NodeChildrenList
     * Output:
     * Authers: tianyoupan
     */
    public void updateNodeMap(String serviceName, List<String> list) {
        if (list == null) {
            return;
        }
        //if all the nodes removed. remove the service in managerMap.
        if(list.isEmpty()){
            if(managerMap.containsKey(serviceName)){
                managerMap.get(serviceName).clear();
                managerMap.remove(serviceName);
            }
        }
        if(managerMap.containsKey(serviceName)){
            managerMap.get(serviceName).clear();
        }
        Group group = new Group();
        int index = 0;
        for (String s : list) {
            String args[] = s.split(":");
            Item item = new Item(args[0], Integer.parseInt(args[1]), serviceName + "-" + index);
            index++;
            group.insert(item);
        }
        managerMap.putIfAbsent(serviceName,group);
    }

    /**
     * Description: this update funcation used by NodeStatusUpdate class(Excutor),every thrift use one thread.
     * Input:
     * Output: success & failed.
     * Authers: tianyoupan
     */
    public boolean updateNodeInfoTiming() {
        for (Map.Entry<String, Group> e : managerMap.entrySet()) {
            if (e != null) {
                e.getValue().update();
            }
        }

        return true;
    }


}
