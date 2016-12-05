package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-21.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 */

public class NodesGroup {
    private static final Logger LOG = LoggerFactory.getLogger(NodesGroup.class);
    private ArrayList<Node>serviceList;
    private ConcurrentHashMap<Node,ComputerStatus>itemStatusMap;
    //here if response data is big, time out need be added.
    private int timeout = 2000;
    private Ruler ruler = null;

    NodesGroup(){
        serviceList = new ArrayList<>();
        itemStatusMap = new ConcurrentHashMap<>();
    }
    void insert(Node node){
        serviceList.add(node);
    }

    void clear(){
        serviceList.clear();
        itemStatusMap.clear();
        ruler = null;
    }

    boolean isEmpty(){
        return serviceList.isEmpty();
    }

    void setRuler(Ruler ruler) {
        this.ruler = ruler;
    }

    String getService(){
        //如果iteamStatusMap不可用 按照serviceList，返回RANDOM
        if(itemStatusMap == null || itemStatusMap.isEmpty() || ruler.getType() == "RANDOM"){
            return ruler.findMinByRandom(serviceList);
        }
        return ruler.findSuitable(itemStatusMap);
    }

    public String[] getAllNodes(){
        String[] nodes = new String[serviceList.size()];
        int index = 0;
        for (Node node : serviceList) {
            nodes[index++]= node.getIP() + ":" + node.getPort() + "," + node.getName();
        }
        return nodes;
    }

    void update(){
        return;
        /*
        for (Node s : serviceList) {

            String IP = s.getIP();
            int port = s.getPort();
            TTransport transport = new TSocket(IP,port, timeout);
            TProtocol protocol = new TBinaryProtocol(transport);

            // TMultiplexedProtocol
            TMultiplexedProtocol multiplexedProtocol = new  TMultiplexedProtocol(protocol,"sc");
            // 按名称获取服务端注册的service
            SCClientService.Client client = new SCClientService.Client(multiplexedProtocol);
            try {
                transport.open();
            } catch (TTransportException e) {
                LOG.warn("TTransportException when isSave {}", e.getMessage());
            }
            try{
                String jsonResponse=null,jsonRequest="all";
                jsonResponse = client.requestServiceSituation(jsonRequest);
                if (jsonResponse != null) {
                    try {
                        ComputerStatus status = G.gson().fromJson(jsonResponse, ComputerStatus.class);
                        itemStatusMap.put(s,status);
                    }catch (Exception e){
                        LOG.warn("jsonResponse fromJson method failed. ip : {} , port : {}, msg : {}",IP,port,e.toString());
                    }
                }else{
                    LOG.warn("TTransport response is null. ip : {}, port {}",IP,port);
                }
            }catch (TException e) {
                LOG.warn("TException when isSave {}", e.getMessage());
            }finally {
                transport.close();
            }

        }
        */
    }


}
