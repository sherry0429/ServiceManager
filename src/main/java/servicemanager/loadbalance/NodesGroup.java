package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-21.
 */

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicemanager.thrift.LoadBalanceInterface;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 */

public class NodesGroup {
    private static final Logger LOG = LoggerFactory.getLogger(NodesGroup.class);
    private ArrayList<Node>serviceList;
    //here if response data is big, time out need be added.
    private int timeout = 2000;
    private Ruler ruler = null;
    private String path;

    NodesGroup(){
        serviceList = new ArrayList<>();
    }
    void insert(Node node){
        serviceList.add(node);
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    void clear(){
        serviceList.clear();
        ruler = null;
    }

    void deleteNode(String path){
        int index = 0;
        for (Node node : serviceList) {
            if(node.getPath().equals(path)){
                break;
            }
            index++;
        }
        serviceList.remove(index);
    }

    boolean isExist(String ip, int port){
        for (Node node : serviceList) {
            if(node.getIP().equals(ip)){
                if(node.getPort() == port){
                    return true;
                }
            }
        }
        return false;
    }

    boolean isEmpty(){
        return serviceList.isEmpty();
    }

    void setRuler(Ruler ruler) {
        this.ruler = ruler;
    }

    String getService(){
        Node node =  ruler.findSuitable(serviceList);
        return node.getData();
    }

    public ArrayList<Node> getServiceList() {
        return serviceList;
    }

    void update(){
        for (Node s : serviceList) {

            String IP = s.getIP();
            int port = 9090;
            TTransport transport = new TSocket(IP, port, timeout);
            TProtocol protocol = new TBinaryProtocol(transport);

            // TMultiplexedProtocol
//            TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, "sc");
            // 按名称获取服务端注册的service
            LoadBalanceInterface.Client client = new LoadBalanceInterface.Client(protocol);
            try {
                transport.open();
            } catch (TTransportException e) {
                LOG.warn("TTransportException when isSave {}", e.getMessage());
            }
            try {
                String response = null;
                response = client.requestServiceSituation("test");
                if (response != null) {
                    try {
                        String status[] = response.split("@");
                        s.setMem(Float.parseFloat(status[0]));
                        s.setCpu(Float.parseFloat(status[1]));
                        s.setDisk(Float.parseFloat(status[2]));
                        s.setNet(Float.parseFloat(status[3]));
                    } catch (Exception e) {
                        LOG.warn("get machine status failed. ip : {}, port : {}", s.getIP(), s.getPort());
                    }
                } else {
                    LOG.warn("TTransport response is null. ip : {}, port {}", IP, port);
                }
            } catch (TException e) {
                LOG.warn("TException when isSave {}", e.getMessage());
            } finally {
                transport.close();
            }
        }
    }


}
