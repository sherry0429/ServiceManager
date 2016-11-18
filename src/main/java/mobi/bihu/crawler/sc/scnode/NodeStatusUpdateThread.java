package mobi.bihu.crawler.sc.scnode;
/**
 * Created by tianyoupan on 16-11-17.
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: timeing,update ComputerInfoMap
 */

public class NodeStatusUpdateThread {

    private static final Logger LOG = LoggerFactory.getLogger(NodeStatusUpdateThread.class);
    private int timeout = 3000;//thrift time out
    private ScheduledExecutorService schedule;
    private int threadNumber;

    public NodeStatusUpdateThread(int threadNum, int time){
        threadNumber = threadNum;
        timeout = time;
        schedule = new ScheduledThreadPoolExecutor(threadNumber);
    }

    /**
     * Description: use path & Node,start a new thread to thrift machine and get it's status,Update the Map
     * Input: path,node
     * Output: 
     * Authers: tianyoupan
     */
    // TODO: 16-11-17  check the Node is actually Map's Node
    public void updateTimer(String path,NodeInfo Node){
        schedule.schedule(new Runnable() {
            @Override
            public void run() {
                String args[] = path.split(":");//arg[0] ip arg[1] port
                TTransport transport = new TSocket(args[0],Integer.parseInt(args[1]),timeout);//3000 is time out
                // TODO: 16-11-17 here can make a TSocket Pool,But seem that is do not needed now.
                TProtocol protocol = new TBinaryProtocol(transport);
                RequestComputerService.Client client = new RequestComputerService.Client(protocol);
                try {
                    transport.open();
                    String jsonResponse,jsonRequest="";
                    jsonResponse = client.requestComputerSituation(jsonRequest);
                    HashMap map = G.gson().fromJson(jsonResponse, HashMap.class);
                    if (jsonResponse != null) {
                        Node.setIP(map.get("ip").toString());
                        Node.setPort(Integer.parseInt(map.get("port").toString()));
                        Node.setMemory(Integer.parseInt(map.get("Memory").toString()));
                        // TODO: 16-11-17 here response,getValue maybe return null."not find this attribute",if this occur,this Node maybe have some questions.
                    }else{
                        LOG.warn("TTransport do not receive response.ip : {}, port {}",args[0],args[1]);
                    }

                } catch (TTransportException e) {
                    LOG.warn("TTransportException when isSave {}",e.getMessage());
                } catch (TException e) {
                    LOG.warn("TException when isSave {}", e.getMessage());
                }finally {
                    transport.close();
                }
            }
        },0, TimeUnit.MILLISECONDS);
    }

}
