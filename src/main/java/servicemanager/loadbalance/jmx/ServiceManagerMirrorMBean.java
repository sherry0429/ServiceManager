package servicemanager.loadbalance.jmx;
/**
 * Created by tianyoupan on 16-11-25.
 */

import servicemanager.model.Group;

import java.util.Map;

/**
 * Description:
 */

public interface ServiceManagerMirrorMBean {
    int thriftConnection();
    int thriftCall();
    int nodes_number();
    int service_number();
    int group_number();
    String[] service_list();
    String[] node_list();
}
