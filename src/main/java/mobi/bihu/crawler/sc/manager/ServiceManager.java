package mobi.bihu.crawler.sc.manager;

/**
 * Created by tianyoupan on 16-11-15.
 */

/**
 * Description: manage Zookpeer/response thrift service
 */

/** unprepared, do not use this package. thanks */

import mobi.bihu.crawler.sc.SCConfig;
import mobi.bihu.crawler.sc.handler.SCServiceHandler;
import mobi.bihu.crawler.sc.thrift.sc_server;
import mobi.bihu.crawler.zookeeper.ZKCallback;
import mobi.bihu.crawler.zookeeper.ZKClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

/** zookper.. Singleton or not? thinking... */

public class ServiceManager {
    private static NodeManager Nodes;
    private static ZKClient zkclient;
    private static int internal = 1000;
    private String zkServer;
    private int zkTimeout;
    private SCConfig config;
    private static Logger LOG = LoggerFactory.getLogger(ServiceManager.class);
    private NodeMainService cpRunable;
    private Thread cpThread;

    /**
     * Description: Main thread make a loop,check machine every times,update Machine infos.
     * Input: 
     * Output:
     * Authers: tianyoupan
     */
    private class NodeMainService implements Runnable{
        @Override
        public void run() {
            while(true){
                try {
                    //warning: this internal > Nodes.internal
                    if(Nodes != null)
                        Nodes.updateNodeInfoTiming();
                    Thread.sleep(internal);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("PRC"));
    }

    public ServiceManager(SCConfig conf, NodeManager manager) {
        if (conf == null || manager == null) {
            LOG.error("NULL Config or NULL NodeManager.");
            System.exit(1);
        }else{
            config = conf;
            Nodes = manager;
        }

    }

    public boolean initManager(){
        zkServer = config.getZkServer();
        zkTimeout = config.getZkTimeout();
        String data = config.getIP() + ":" + config.getPort() + "," +config.getName();
        String node = config.getZkNode();
        String zkNodeList = config.getZkNodeList();

        try {
            zkclient = new ZKClient(zkServer,zkTimeout);
        } catch (IOException e) {
            System.exit(-1);
        }
        zkclient.setExpiredListener(new ZKCallback.ExpiredListener() {
            @Override
            public boolean shouldReconnect() {
                return true;
            }
        });
        zkclient.setReconnectedListener(new ZKCallback.ReconnectedListener() {
            @Override
            public void onReconnected(ZKClient zkClient) {
                String path = null;
                while(path == null) {
                    path = zkClient.createNodeEphemeralSequential(node, data);
                    if (path == null) {
                        System.out.println("Failed to register appAPI({}) to Zookeeper, try again 1s later" + data);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    else {
                        System.out.println("Register appAPI({}) to ZooKeeper node({})" + data + path);
                    }
                }
            }
        });
        //every service have a watcher.
        String[] nodelist = zkNodeList.split("\\|");
        for (String s : nodelist) {
            String[] args = s.split("@");
            zkclient.watchNodeChildrenChanged(args[1], new ZKCallback.ChildrenChangedListener() {
                @Override
                public void onChildrenChanged(List<String> list) {
                    Nodes.updateNodeMap(args[0],list);
                }
            });

        }
        return true;
    }

    public boolean startManager(){
        //start loop thread for Nodes
        cpRunable = new NodeMainService();
        cpThread = new Thread(cpRunable);
        if(cpThread == null || cpRunable == null){
            LOG.error("NodeService start failed,Runable : {},Thread : {}",cpRunable,cpThread);
        }else{
            cpThread.start();
        }

        //start thrift Server
        SCServiceHandler handler = new SCServiceHandler(config,this);
        sc_server.Processor<SCServiceHandler> processor = new sc_server.Processor<SCServiceHandler>(handler);
        TServerSocket transport = null;
        try {
            transport = new TServerSocket(config.getPort());
        } catch (TTransportException e) {
            LOG.error("TTransportException: {}", e.getMessage());
            System.exit(-1);
        }

        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(transport);
        tArgs.processor(processor)
                .protocolFactory(new TBinaryProtocol.Factory())
                .maxWorkerThreads(config.getThriftThreadMax());
        TServer server = new TThreadPoolServer(tArgs);
        server.serve();

        return true;
    }
    
    public String getSuitableNode(String serviceName){
        return Nodes.getSuitable(serviceName);
    }

}