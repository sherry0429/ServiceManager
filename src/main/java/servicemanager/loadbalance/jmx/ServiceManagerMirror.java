package servicemanager.loadbalance.jmx;
/**
 * Created by tianyoupan on 16-11-25.
 */

import servicemanager.loadbalance.NodesGroup;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 */

public class ServiceManagerMirror implements  ServiceManagerMirrorMBean{
    private ConcurrentHashMap<String, NodesGroup> managerMap;
    private ConcurrentHashMap<String, String> serviceNodeMap;//serviceName - Node

    public ServiceManagerMirror(ConcurrentHashMap<String, NodesGroup> managerMap,ConcurrentHashMap<String, String> serviceNodeMap){
        this.managerMap = managerMap;
        this.serviceNodeMap = serviceNodeMap;
    }

    @Override
    public String[] getServices() {
        String[] str = new String[serviceNodeMap.size()];
        int index = 0;
        for (Map.Entry<String, String> e : serviceNodeMap.entrySet()) {
            if (e != null) {
                str[index++] = e.getKey() + "   " + e.getValue();
            }
        }
        return str;
    }

    @Override
    public String[] getNodes() {
        String[] str = new String[serviceNodeMap.size()];
        int index = 0;
        for (Map.Entry<String, String> e : serviceNodeMap.entrySet()) {
            if (e != null) {
                String serviceNode = e.getValue();
                NodesGroup nodesGroup = managerMap.get(e.getKey());
                StringBuffer strBuff = new StringBuffer();
                strBuff.append(serviceNode + "     ");
                strBuff.append(Arrays.toString(nodesGroup.getAllNodes()) + '\n');
                str[index++] = strBuff.toString();
            }
        }
        return str;
    }

}
