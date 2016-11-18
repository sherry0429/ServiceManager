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
    private static ConcurrentHashMap<String,Node>nodeInfoMap;//String type : IP:PORT
    private static ConcurrentHashMap<String,Integer>nodeKeyTimeMap;//String type : IP:PORT

    private static Ruler ruler;
    private int NodeConnectTimeout = 1000;
    private static Logger LOG = LoggerFactory.getLogger(NodeManager.class);
    private int maxThread = 20;
    private int ChangeTimes = 0;

    public NodeManager(){
        ruler = new Ruler();
        nodeInfoMap = new ConcurrentHashMap<>();
        nodeKeyTimeMap = new ConcurrentHashMap<>();
    }

    public void init(String nodeList){
        LOG.info("NodeManager init, List : {}",nodeList);
        String nodes[] = nodeList.split(",");
        for (String node : nodes) {
            LOG.info("NodeManager add node : {}",node);
            addNode(node,ChangeTimes);
        }
    }

    public void setRuler(Ruler arg){
        ruler = arg;
    }

    public String getSuitable(){
        // TODO: 16-11-16 example for use ruler
        return ruler.findSuitable(nodeInfoMap);
    }

    public Node getNode(String path){
        return nodeInfoMap.get(path);
    }

    /**
     * Description: add a <path,Node> in ConcurrentHashMap,when many threads run,should test it.
     * Input: ip:port
     * Output: add success & failed
     * Authers: tianyoupan
     */
    // TODO: 16-11-17 when many threads request this function,check the hashMap's situation.
    public synchronized boolean addNode(String ipWithPort,int ChangeTimes) {
        Node oldvalue = nodeInfoMap.get(ipWithPort);
        String args[] = ipWithPort.split(":");
        if (oldvalue == null) {
            Node info = new Node(args[0],Integer.parseInt(args[1]));
            if (nodeInfoMap.putIfAbsent(ipWithPort, info) == null) {
                if(nodeKeyTimeMap.get(ipWithPort) == null){
                    nodeKeyTimeMap.putIfAbsent(ipWithPort,0);
                }
                LOG.info("addNode first {}", ipWithPort);
            }
        } else {
                /* Node has been added */
            LOG.warn("add a existed Node {}", ipWithPort);
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
        if(nodeInfoMap.remove(ipWithPort)!=null){
            return true;
        }
        LOG.warn("DeleteNode failed {}",ipWithPort);
        return false;
    }

    /**
     * Description: this update function used by ServiceManager,when NodeChangeListener be called,This function will be called and clear & Update the map
     * Input: NodeChildrenList
     * Output:
     * Authers: tianyoupan
     */
    public void updateNodeMap(List<String> list){
        if(list.isEmpty() || list == null)
            return;
        LOG.info("ChangedNode Example : {} ",list.get(0));
        ChangeTimes++;
        for (String s : list) {
            nodeKeyTimeMap.putIfAbsent(s,ChangeTimes);
        }
        //here use algorithm replace "deleteAll" opertaion.
        //1.define a map to save every node's changeTimes,like Generation,one by one.
        //2.ChangeTimes means Generations,use this variable also can record NodeChangeFunction called times.
        //3.ChangeTimes will be n or n+1,if n+1,that is "ChangedNode",if "n",is Origin Nodes.
        String oldEntry = null;
        for (Map.Entry<String, Integer> entry : nodeKeyTimeMap.entrySet()) {
            if(oldEntry != null){
                nodeKeyTimeMap.remove(oldEntry);
                oldEntry = null;
            }
            String node = entry.getKey();
            Integer generation = entry.getValue();
            if(generation == ChangeTimes){
                if(nodeInfoMap.get(node) == null){
                    addNode(node,ChangeTimes);
                }
            }
            if(generation == (ChangeTimes - 1)){
                nodeInfoMap.remove(entry.getKey());
                oldEntry = node;
            }
        }
    }

    /**
     * Description: this update funcation used by NodeStatusUpdate class(Excutor),every thrift use one thread.
     * Input:
     * Output: success & failed.
     * Authers: tianyoupan
     */
    public boolean updateNodeInfoTiming(){
        for(Map.Entry<String,Node> e: nodeInfoMap.entrySet() ){
            if(e!=null){
                e.getValue().request(NodeConnectTimeout);
            }
        }
        return true;
    }






}
