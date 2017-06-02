package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import com.alibaba.fastjson.JSON;
import servicemanager.loadbalance.jmx.ServiceManagerMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicemanager.model.Group;
import servicemanager.model.Groups;
import servicemanager.model.ServiceNode;
import servicemanager.model.TestParams;
import servicemanager.zookeeper.ZKCallBack;
import servicemanager.zookeeper.ZKClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Machine Information Manager.
 */

public class ServiceManager implements ZKCallBack{
    private static ConcurrentHashMap<String, NodesGroup> serviceMap;
    private static ConcurrentHashMap<String, ArrayList<String>> GroupsMap;
    private int NodeConnectTimeout = 1000;
    private static int internal = 2000;
    private static Logger LOG = LoggerFactory.getLogger(ServiceManager.class);
    private NodeMainService cpRunable;
    private Thread cpThread;
    private ZKClient zkClient;
    private ServiceManagerMirror mirror;
    private String rulerType = "RANDOM";

    private void updateTestParams(){
        int node_count = 0;
        ArrayList<String> nodes = new ArrayList<>();
        int index = 0;
        for (NodesGroup group : serviceMap.values()) {
            node_count += group.getServiceList().size();
            for (Node node : group.getServiceList()) {
                nodes.add(node.getPath());
            }
        }
        TestParams.setNodes_number(node_count);
        TestParams.setGroups_number(GroupsMap.size());
        TestParams.setServices_number(serviceMap.size());

        TestParams.setNode_list((String[])nodes.toArray(new String[node_count]));

        String[] arr = new String[serviceMap.keySet().size()];
        serviceMap.keySet().toArray(arr);
        TestParams.setServices_list(arr);
    }

    @Override
    public void onChildAdd(String path, String datas) {
        if(datas.charAt(1) == '@'){
            //group
            NodesGroup nodesGroup = new NodesGroup();
            nodesGroup.setRuler(switchRuler(rulerType));
            nodesGroup.setPath(path);
            serviceMap.put(datas.substring(2), nodesGroup);
        }
        else if(datas.charAt(0) == '@'){
            //groups
            // TODO: 2017/6/2 未实现
            GroupsMap.put(datas.substring(1), new ArrayList<>());
            return;
        }
        String data_ip_port[] = datas.split("-");
        if(data_ip_port.length != 3){
            return;
        }
        String data = data_ip_port[0];
        String ip = data_ip_port[1];
        int port = Integer.parseInt(data_ip_port[2]);
        Node node = new Node(ip, port, data, path);
        serviceMap.get(data).insert(node);
    }

    @Override
    public void onChildDelete(String path) {
        String delete_key = null;
        for(Map.Entry<String,NodesGroup> entry : serviceMap.entrySet()){
            System.out.println("key : " + entry.getKey());
            if(path.contains(entry.getKey())){
                delete_key = entry.getKey();
                break;
            }
        }
        if(delete_key != null) {
            if(serviceMap.containsKey(delete_key)) {
                serviceMap.remove(delete_key);
            }
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
                while(zkClient != null && serviceMap != null){
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
        serviceMap = new ConcurrentHashMap<>();
        GroupsMap = new ConcurrentHashMap<>();
        mirror = new ServiceManagerMirror(serviceMap);
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
            zkClient = new ZKClient(zkServer, this);
        } catch (Exception e) {
            LOG.error("Zookeeper start failed. msg : {}",e.toString());
            System.exit(-1);
        }

        //init Watch
//        for (String s : initServiceList) {
//            String args[] = s.split(",");
//            /* path and data */
//            zkClient.CreateNode(args[0],args[1]);
//        }

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

    public String registerService(String serviceName, String jsonString) {
        Groups groups = JSON.parseObject(jsonString, Groups.class);
        String groupsPath = groups.getPath();
        String tag = groups.getTag();
        if(!zkClient.CreateNode(groupsPath,tag,true)){
            return "failed.";
        }
        for (Group group : groups.getGroups()) {
            String name = group.getServiceName();
            String group_path = group.getPath();
            if(!zkClient.CreateNode(group_path, group.getTag(), true)){
                return "failed.";
            }
            for (ServiceNode service : group.getNodes()) {
                String ip = service.getIp();
                int port = Integer.parseInt(service.getPort());
                String data = name + "-" + ip + "-" + port;
                String path = service.getPath();
                if(!zkClient.CreateNode(path, data, true)) {
                    return "some node create failed.";
                }
            }
        }
        return "register success.";
    }

    public String getService(String serviceName) {
        if(serviceMap.containsKey(serviceName)){
            if(!serviceMap.get(serviceName).isEmpty()) {
                return serviceMap.get(serviceName).getService();
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

    public String deleteService(String serviceName){
        if (serviceMap.containsKey(serviceName)) {
            NodesGroup group = serviceMap.get(serviceName);
            for (Node node : group.getServiceList()) {
                System.out.println(node.getPath());
                if (!zkClient.DeleteNode(node.getPath())) {
                    return "delete some node failed.";
                }
            }
            if (!zkClient.DeleteNode(group.getPath())) {
                return "delete some node failed.";
            }
            return "delete success.";
        }else{
            return "no this service.";
        }

    }

    public String deleteNode(String path){
        if(zkClient.DeleteNode(path)){
           return "Success.";
        }
        return "Failed.";
    }

    public String getServiceNodes(String serviceName){
        if(serviceMap.containsKey(serviceName)){
            int size = serviceMap.get(serviceName).getServiceList().size();
            String nodes[] = new String[size];
            for (int i = 0; i < size; i++) {
                nodes[i] = serviceMap.get(serviceName).getServiceList().get(i).getPath();
            }
            return Arrays.toString(nodes);
        }
        return "not find this service.";
    }

    /**
     * Description: this update funcation used by NodeStatusUpdate class(Excutor),every thrift use one thread.
     * Input:
     * Output: success & failed.
     * Authers: tianyoupan
     */
    private boolean updateNodeInfoTiming() {
        for (Map.Entry<String, NodesGroup> e : serviceMap.entrySet()) {
            if (e != null) {
                e.getValue().update();
            }
        }
        //Update JMX
        updateTestParams();
        return true;
    }


}
