package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-21.
 */

import mobi.bihu.crawler.sc.thrift.SCClientService;
import mobi.bihu.crawler.util.G;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 */

class Group {
    private static final Logger LOG = LoggerFactory.getLogger(Group.class);
    private ArrayList<Item>serviceList;
    private ConcurrentHashMap<Item,ComputerStatus>itemStatusMap;
    //here if response data is big, time out need be added.
    private int timeout = 2000;
    Ruler ruler = null;


    Group(){
        serviceList = new ArrayList<>();
        itemStatusMap = new ConcurrentHashMap<>();
    }
    void insert(Item item){
        serviceList.add(item);
    }

    void clear(){
        serviceList.clear();
        itemStatusMap.clear();
        ruler = null;
    }

    void setRuler(Ruler ruler) {
        this.ruler = ruler;
    }

    String getSuitable(){
        return ruler.findSuitable(itemStatusMap);
    }

    void update(){
        for (Item s : serviceList) {
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
    }
}
