package mobi.bihu.crawler.sc.scnode;
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
    private static ConcurrentHashMap<String,NodeInfo>NodeInfoMap;//String type : IP:PORT
    private static Ruler ruler;
    private int NodeConnectTimeout = 3000;
    private static Logger LOG = LoggerFactory.getLogger(NodeManager.class);
    private static NodeStatusUpdateThread timerUpdate;
    private int maxThread = 20;

    public NodeManager(){
        ruler = new Ruler();
        NodeInfoMap = new ConcurrentHashMap<>();
        timerUpdate = new NodeStatusUpdateThread(maxThread,NodeConnectTimeout);
    }

    public void init(String nodeList){
        LOG.info("NodeManager init, List : {}",nodeList);
        String nodes[] = nodeList.split(",");
        for (String node : nodes) {
            LOG.info("NodeManager add node : {}",node);
            addNode(node);
        }
    }

    public void setRuler(Ruler arg){
        ruler = arg;
    }

    public String getMaxLoadNode(){
        // TODO: 16-11-16 example for use ruler
        return ruler.findMax(NodeInfoMap);
    }

    public String getMinLoadNode(){
        // TODO: 16-11-16 example for use ruler
        return ruler.findMin(NodeInfoMap);
    }

    public NodeInfo getNode(String path){
        return NodeInfoMap.get(path);
    }

    /**
     * Description: add a <path,NodeInfo> in ConcurrentHashMap,when many threads run,should test it.
     * Input: ip:port
     * Output: add success & failed
     * Authers: tianyoupan
     */
    // TODO: 16-11-17 when many threads request this function,check the hashMap's situation.
    public boolean addNode(String ipWithPort){
        while(true){
            NodeInfo oldvalue = NodeInfoMap.get(ipWithPort);
            if(oldvalue == null){
                NodeInfo info = new NodeInfo();
                if(NodeInfoMap.putIfAbsent(ipWithPort,info) == null){
                    LOG.info("addNode first {}",ipWithPort);
                    break;
                }
            }else{
                /* Node has been added */
                LOG.warn("add a existed Node {}",ipWithPort);
                    break;
            }
        }

        return true;
    }

    /**
     * Description: delete Node from Map
     * Input: ip:port
     * Output: success & failed
     * Authers: tianyoupan
     */
    public boolean deleteNode(String ipWithPort){
        if(NodeInfoMap.remove(ipWithPort)!=null)
            return true;
        LOG.warn("DeleteNode failed {}",ipWithPort);
        return false;
    }

    /**
     * Description: this update function used by ServiceManager,when NodeChangeListener be called,This function will be called and clear & Update the map
     * Input: NodeChildrenList
     * Output:
     * Authers: tianyoupan
     */
    // TODO: 16-11-17 also,every time "map.clear",cost too many resource,think a better way.
    // TODO: 16-11-17 when ThreadA call RequestNodeInfo, it read map,ThreadB call UpdateNodeMap it clear map,Test it to make sure ConcurrentHashMap lock or unlock
    public void updateNodeMap(List<String> list){
        if(list == null)
            return;
        NodeInfoMap.clear();
        for (String s : list) {
            String args[] = s.split(",");
            addNode(args[0]);
        }
    }

    /**
     * Description: this update funcation used by NodeStatusUpdateThread class(Excutor),every thrift use one thread.
     * Input:
     * Output: success & failed.
     * Authers: tianyoupan
     */
    public boolean updateNodeInfoTiming(){
        for(Map.Entry<String,NodeInfo> e: NodeInfoMap.entrySet() ){
            timerUpdate.updateTimer(e.getKey(),e.getValue());
        }
        return true;
    }






}
