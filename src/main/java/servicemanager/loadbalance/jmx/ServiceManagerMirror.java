package servicemanager.loadbalance.jmx;
/**
 * Created by tianyoupan on 16-11-25.
 */

import servicemanager.loadbalance.Node;
import servicemanager.loadbalance.NodesGroup;
import servicemanager.model.Group;
import servicemanager.model.Groups;
import servicemanager.model.TestParams;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 */

public class ServiceManagerMirror implements ServiceManagerMirrorMBean{

    public ServiceManagerMirror(ConcurrentHashMap<String, NodesGroup> managerMap){

    }


    @Override
    public int thriftConnection() {
        return TestParams.getThrift_connections();
    }

    @Override
    public int thriftCall() {
        return TestParams.getThrift_call();
    }

    @Override
    public int nodes_number() {
        return TestParams.getNodes_number();
    }

    @Override
    public int service_number() {
        return TestParams.getServices_number();
    }

    @Override
    public int group_number() {
        return TestParams.getGroups_number();
    }

    @Override
    public String[] service_list() {
        return TestParams.getServices_list();
    }

    @Override
    public String[] node_list() {
        return TestParams.getNode_list();
    }
}
