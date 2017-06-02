package servicemanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:TianYouPan
 * Time:2017/6/1
 * Description:
 */
public class Group {
    private List<ServiceNode> Nodes = new ArrayList<ServiceNode>();
    private String serviceName = null;
    private String path = null;
    private String tag = null;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<ServiceNode> getNodes() {
        return Nodes;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setNodes(List<ServiceNode> nodes) {
        Nodes = nodes;
    }

    public void addNode(ServiceNode node){
        Nodes.add(node);
    }
}
