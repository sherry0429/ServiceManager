package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import servicemanager.loadbalance.jmx.ServiceManagerMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicemanager.zookeeper.ZKCallBack;
import servicemanager.zookeeper.ZKClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Machine Information Manager.
 */

public class ServiceManager implements ZKCallBack{
    private static ConcurrentHashMap<String, NodesGroup> managerMap;
    private static ConcurrentHashMap<String, String> serviceNodeMap;//serviceName - Node\
    private int NodeConnectTimeout = 1000;
    private static int internal = 5000;
    private static Logger LOG = LoggerFactory.getLogger(ServiceManager.class);
    private NodeMainService cpRunable;
    private Thread cpThread;
    private ZKClient zkClient;
    private ServiceManagerMirror mirror;
    private String rulerType = "";

    @Override
    public void onChildAdd(String path, String data) {
        String datas[] = data.split(",");
        if (managerMap.containsKey(datas[2])) {
            managerMap.get(datas[2]).clear();
            managerMap.remove(datas[2]);
            serviceNodeMap.remove(datas[2]);
        }
        NodesGroup nodesGroup = new NodesGroup();
        nodesGroup.setRuler(switchRuler(rulerType));

        Node node = new Node(datas[0], Integer.parseInt(datas[1]), datas[2]);
        nodesGroup.insert(node);
        managerMap.put(datas[2], nodesGroup);
        serviceNodeMap.put(datas[2],path);
    }

    @Override
    public void onChildDelete(String path, String data) {
        String datas[] = data.split(",");
        if (managerMap.containsKey(datas[2])) {
            managerMap.get(datas[2]).clear();
            managerMap.remove(datas[2]);
            serviceNodeMap.remove(datas[2]);
        }

    }

    @Override
    public void onChildUpdate(String path, String data) {
    }

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

    public boolean StartManager(String zkServer,int zkTimeout,String []initServiceList) {
        zkClient = null;
        try {
            zkClient = new ZKClient(zkServer);
        } catch (Exception e) {
            LOG.error("Zookeeper start failed. msg : {}",e.toString());
            System.exit(-1);
        }

        //init Watch
        for (String s : initServiceList) {
            String args[] = s.split(",");
            /* path and data */
            zkClient.CreateNode(args[0],args[1]);
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

    public boolean registerService(String serviceName, String path, String data){
        return zkClient.CreateNode(path, serviceName + data);
    }

    public String getService(String serviceName) {
        if(managerMap.containsKey(serviceName)){
            if(!managerMap.get(serviceName).isEmpty()) {
                return managerMap.get(serviceName).getService();
            }
            else{
                LOG.warn("Service NodeList is empty");
                return null;
            }
        }else{
            LOG.warn("Unregister service");
            return null;
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
