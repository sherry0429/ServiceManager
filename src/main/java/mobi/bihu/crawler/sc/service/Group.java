package mobi.bihu.crawler.sc.service;
/**
 * Created by tianyoupan on 16-11-21.
 */

import mobi.bihu.crawler.sc.ruler.Ruler;
import mobi.bihu.crawler.sc.thrift.sc_client;
import mobi.bihu.crawler.util.G;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Description:
 */

public class Group {
    private static final Logger LOG = LoggerFactory.getLogger(Group.class);
    private ArrayList<Item>serviceList;
    private int timeout;
    Ruler ruler;


    public Group(){
        ruler = new Ruler();
        serviceList = new ArrayList<>();
    }
    public void insert(Item item){
        serviceList.add(item);
    }

    public void clear(){
        serviceList.clear();
    }

    public void setRuler(Ruler ruler) {
        this.ruler = ruler;
    }

    public String getSuitable(){
        return ruler.findSuitable(serviceList);
    }

    public void update(){
        for (Item s : serviceList) {
            String IP = s.getIP();
            int port = s.getPort();

            TTransport transport = new TSocket(IP, port, timeout);
            TProtocol protocol = new TBinaryProtocol(transport);
            sc_client.Client client = new sc_client.Client(protocol);
            try {
                transport.open();
            } catch (TTransportException e) {
                LOG.warn("TTransportException when isSave {}", e.getMessage());
            }
            try{
                // TODO: 16-11-21 JSONRequest is empty.
                String jsonResponse,jsonRequest="";
                jsonResponse = client.requestServiceSituation(jsonRequest);
                LOG.info("doService ip : {} port {} response : {}",IP,port,jsonResponse);
                if (jsonResponse != null) {
                    HashMap map = G.gson().fromJson(jsonResponse, HashMap.class);
                    s.setMemory(Integer.parseInt(map.get("Memory").toString()));
                    // TODO: 16-11-17 here response,getValue maybe return null."not find this attribute",if this occur,this Node maybe have some questions.
                }else{
                    LOG.warn("TTransport do not receive response.ip : {}, port {}, response : {}",IP,port,jsonResponse);
                }
            }catch (TException e) {
                LOG.warn("TException when isSave {}", e.getMessage());
            }finally {
                transport.close();
            }
        }
    }
}
