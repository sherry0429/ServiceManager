package mobi.bihu.crawler.sc.manager;
/**
 * Created by tianyoupan on 16-11-15.
 */

import mobi.bihu.crawler.sc.service.Group;
import mobi.bihu.crawler.sc.service.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Machine Information Manager.
 */

public class NodeManager {
    private static ConcurrentHashMap<String,Group>managerMap;
    private int NodeConnectTimeout = 1000;
    private static Logger LOG = LoggerFactory.getLogger(NodeManager.class);

    public NodeManager(){
        managerMap = new ConcurrentHashMap<>();
    }

    public void init(String zkList){
        String services[] = zkList.split("\\|");
        String service[];
        String ipWithPorts[];
        String args[];
        for (String s : services) {
            service = s.split("@");
            ipWithPorts = service[1].split(",");
            Group group = new Group();
            Item item = new Item();
            for (String ipWithPort : ipWithPorts) {
                args = ipWithPort.split(":");
                item.setIP(args[0]);
                item.setPort(Integer.parseInt(args[1]));
                group.insert(item);
            }
            managerMap.putIfAbsent(service[0], group);
        }
    }

    public String getSuitable(String serviceName){
        // TODO: 16-11-16 example for use ruler
        return managerMap.get(serviceName).getSuitable();
    }

    /**
     * Description: this update function used by ServiceManager,when NodeChangeListener be called,This function will be called and clear & Update the map
     * Input: NodeChildrenList
     * Output:
     * Authers: tianyoupan
     */
    public void updateNodeMap(String serviceName,List<String> list){
        if(list == null || list.isEmpty())
            return;
        LOG.info("ChangedNode Example : {} ",list.get(0));

        managerMap.get(serviceName).clear();
        for (String s : list) {
            String args[] = s.split(":");
            Item item = new Item();
            item.setIP(args[0]);
            item.setPort(Integer.parseInt(args[1]));
            managerMap.get(serviceName).insert(item);
        }
    }

    /**
     * Description: this update funcation used by NodeStatusUpdate class(Excutor),every thrift use one thread.
     * Input:
     * Output: success & failed.
     * Authers: tianyoupan
     */
    public boolean updateNodeInfoTiming(){
        for(Map.Entry<String,Group> e: managerMap.entrySet() ){
            if(e!=null){
                e.getValue().update();
            }
        }

        return true;
    }






}
