package mobi.bihu.crawler.sc.scnode;
/**
 * Created by tianyoupan on 16-11-15.
 */

import mobi.bihu.crawler.thrift.RequestComputerService;
import mobi.bihu.crawler.util.G;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Description: all computers info like IP,PORT,Memory,CPU,etc.
 */

public class Node {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private String IP;
    private int port;
    private Integer memory;
    private TTransport transport;
    private TProtocol protocol;
    private RequestComputerService.Client client;
    public Node(String ip, int port){
        IP = ip;
        this.port = port;
    }

    private void openSocket(int timeout) {

        transport = new TSocket(IP, port, timeout);
        protocol = new TBinaryProtocol(transport);
        client = new RequestComputerService.Client(protocol);
        try {
            transport.open();
        } catch (TTransportException e) {
            LOG.warn("TTransportException when isSave {}", e.getMessage());
        }
    }

    private void doService(){
        try{
        String jsonResponse,jsonRequest="";
        jsonResponse = client.requestComputerSituation(jsonRequest);
            LOG.info("doService ip : {} port {} response : {}",IP,port,jsonResponse);
        if (jsonResponse != null) {
            HashMap map = G.gson().fromJson(jsonResponse, HashMap.class);
            IP = map.get("ip").toString();
            port = Integer.parseInt(map.get("port").toString());
            memory = Integer.parseInt(map.get("Memory").toString());
            // TODO: 16-11-17 here response,getValue maybe return null."not find this attribute",if this occur,this Node maybe have some questions.
        }else{
            LOG.warn("TTransport do not receive response.ip : {}, port {}, response : {}",IP,port,jsonResponse);
        }
        }catch (TException e) {
            LOG.warn("TException when isSave {}", e.getMessage());
        }
    }

    public void request(int timeout){
        if(transport == null){
            openSocket(timeout);
        }else if(transport != null){
            if(!transport.isOpen()) {
                try {
                    transport.open();
                }catch (TTransportException e){
                    LOG.warn("transport reopen failed,ip {},port {},Msg {}",IP,port,e.toString());
                }
            }
        }
        doService();
    }

    public void closeSocket(){
        transport.close();
    }



}
