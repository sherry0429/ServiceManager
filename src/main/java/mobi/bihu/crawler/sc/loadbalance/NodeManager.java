package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import mobi.bihu.crawler.sc.thrift.SelectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static mobi.bihu.crawler.sc.thrift.SelectType.*;

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

    public String getSuitable(String serviceName,SelectType selectType) {
        if(managerMap.get(serviceName).ruler == null){
            Ruler ruler = null;
            switch (selectType){
                case MEMORYMORE:{
                    ruler = new Ruler(MEMORYMORE);
                    break;
                }case CPUMORE:{
                    ruler = new Ruler(CPUMORE);
                    break;
                }case DISKMORE:{
                    ruler = new Ruler(DISKMORE);
                    break;
                }case NETUSGMORE:{
                    ruler = new Ruler(NETUSGMORE);
                    break;
                }case AVERAGE:{
                    ruler = new Ruler(AVERAGE);
                    break;
                }case RANDOM:{
                    ruler = new Ruler(RANDOM);
                    break;
                }
            }
            managerMap.get(serviceName).setRuler(ruler);
        }
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
            return;
        }
        if(managerMap.containsKey(serviceName)){
            managerMap.get(serviceName).clear();
        }
        Group group = new Group();
        for (String s : list) {
            String args[] = s.split(",");
            String ipWithPorts[] = args[0].split(":");
            Item item = new Item(ipWithPorts[0], Integer.parseInt(ipWithPorts[1]), args[1]);
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
