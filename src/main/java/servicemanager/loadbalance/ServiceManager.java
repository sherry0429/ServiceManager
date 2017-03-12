package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import servicemanager.ZKClient;
import servicemanager.loadbalance.jmx.ServiceManagerMirror;
import mobi.bihu.crawler.util.G;
import mobi.bihu.crawler.zookeeper.ZKCallback;
import mobi.bihu.crawler.zookeeper.ZKClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Machine Information Manager.
 */

public class ServiceManager{
    private static ConcurrentHashMap<String, NodesGroup> managerMap;
    private static ConcurrentHashMap<String, String> serviceNodeMap;//serviceName - Node
    private int NodeConnectTimeout = 1000;
    private static int internal = 5000;
    private static Logger LOG = LoggerFactory.getLogger(ServiceManager.class);
    private NodeMainService cpRunable;
    private Thread cpThread;
    private ZKClient zkClient;
    private ServiceManagerMirror mirror;

    /**
     * Description: Main thread make a loop,check machine every times,update Machine infos.
     * Input:
     * Output:
     * Authers: tianyoupan
     */
    private class NodeMainService implements Runnable {
        @Override
        public void run() {
            try {
                while(zkClient != null && managerMap != null){
                    updateNodeInfoTiming();
                    Thread.sleep(internal);
                }
            } catch (InterruptedException e) {
                LOG.warn("updateNodeInfoTiming Interrupted. default RANDOM style started. msg : {}", e.toString());
                System.exit(-1);
            }
        }
    }

    public ServiceManagerMirror getMirror() {
        return mirror;
    }

    public ServiceManager() {
        managerMap = new ConcurrentHashMap<>();
        serviceNodeMap = new ConcurrentHashMap<>();
        mirror = new ServiceManagerMirror(managerMap,serviceNodeMap);
    }

    private Ruler switchRuler(String selectType){
        Ruler ruler = null;
        switch (selectType) {
            case "MEMORYMORE": {
                ruler = new Ruler("MEMORYMORE");
                break;
            }
            case "CPUMORE": {
                ruler = new Ruler("CPUMORE");
                break;
            }
            case "DISKMORE": {
                ruler = new Ruler("DISKMORE");
                break;
            }
            case "NETUSGMORE": {
                ruler = new Ruler("NETUSGMORE");
                break;
            }
            case "AVERAGE": {
                ruler = new Ruler("AVERAGE");
                break;
            }
            case "RANDOM": {
                ruler = new Ruler("RANDOM");
                break;
            }
        }
        return ruler;
    }

    public boolean updateWatch(String serviceName, String watchPath, String selectType) {
        LOG.info("new service register. Name : {}, Path : {} Type : {}", serviceName, watchPath,selectType);
        try {
            // TODO: 2017/3/12 here need think a better way
            zkClient.watchNodeChildrenChanged(watchPath, new ZKCallback.ChildrenChangedListener() {
                @Override
                public void onChildrenChanged(List<String> list) {
                    LOG.info("node changed. Path : {}, list : {}", watchPath, list.toString());
                    updateNodeMap(serviceName, watchPath, selectType, list);
                }
            });
        } catch (Exception e) {
            LOG.warn("add Watch Failed. serviceName : {} watchPath : {} Msg: {}", serviceName, watchPath, e.toString());
            return false;
        }
        return true;
    }

    public boolean StartManager(String zkServer,int zkTimeout,String []initServiceList) {
        zkClient = null;
        try {
            zkClient = new ZKClient(zkServer, zkTimeout);
        } catch (IOException e) {
            LOG.error("Zookeeper start failed. msg : {}",e.toString());
            System.exit(-1);
        }

        //init Watch
        for (String s : initServiceList) {
            String args[] = s.split(",");
            updateWatch(args[0],args[1],args[2]);
        }

        //start loop thread for Nodes
        cpRunable = new NodeMainService();
        cpThread = new Thread(cpRunable);
        try {
            cpThread.start();
        } catch (Exception e) {
            LOG.error("update Service info's thread failed. msg : {}", e.toString());
            return false;
        }
        return true;
    }

    public String getService(String serviceName) {
        if(managerMap.containsKey(serviceName)){
            if(!managerMap.get(serviceName).isEmpty()) {
                return managerMap.get(serviceName).getService();
            }
            else{
                return G.gson().toJson(new ErrorInfo("Service NodeList is empty","2"));
            }
        }else{
            return G.gson().toJson(new ErrorInfo("Unregister service","1"));
        }
    }

    /**
     * Description: this update function used by ServiceManager,when NodeChangeListener be called,This function will be called and clear & Update the map
     * Input: NodeChildrenList
     * Output:
     * Authers: tianyoupan
     */
    private synchronized void updateNodeMap(String serviceName, String watchPath, String selectType, List<String> list) {
        if(list == null)
            return;
        if (managerMap.containsKey(serviceName)) {
            managerMap.get(serviceName).clear();
            managerMap.remove(serviceName);
            serviceNodeMap.remove(serviceName);
        }
        NodesGroup nodesGroup = new NodesGroup();
        nodesGroup.setRuler(switchRuler(selectType));
        for (String s : list) {
            String args[] = s.split(",");
            String ipWithPorts[] = args[0].split(":");
            Node node = new Node(ipWithPorts[0], Integer.parseInt(ipWithPorts[1]), args[1]);
            nodesGroup.insert(node);
        }
        managerMap.put(serviceName, nodesGroup);
        serviceNodeMap.put(serviceName,watchPath);
    }

    /**
     * Description: this update funcation used by NodeStatusUpdate class(Excutor),every thrift use one thread.
     * Input:
     * Output: success & failed.
     * Authers: tianyoupan
     */
    private boolean updateNodeInfoTiming() {
        for (Map.Entry<String, NodesGroup> e : managerMap.entrySet()) {
            if (e != null) {
                e.getValue().update();
            }
        }

        return true;
    }


}
