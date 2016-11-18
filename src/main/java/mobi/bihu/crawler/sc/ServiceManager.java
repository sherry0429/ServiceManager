package mobi.bihu.crawler.sc;

/**
 * Created by tianyoupan on 16-11-15.
 */

/**
 * Description: manage Zookpeer/response thrift service
 */

/** unprepared, do not use this package. thanks */

import mobi.bihu.crawler.SCConfig;
import mobi.bihu.crawler.sc.scnode.NodeManager;
import mobi.bihu.crawler.sc.scservice.SCServiceHandler;
import mobi.bihu.crawler.thrift.CenterManageService;
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
    private static int internal = 5000;
    private String zkServer;
    private int zkTimeout;
    private SCConfig config;
    private static Logger LOG = LoggerFactory.getLogger(ServiceManager.class);
    final String node;
    final String data;
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
                    zkclient.deleteNode("");
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
        config = conf;
        zkServer = config.getZkServer();
        zkTimeout = config.getZkTimeout();
        data = config.getIP() + ":" + config.getPort() + "," +config.getName();
        node = config.getZkNode();
        this.initManager(zkServer,zkTimeout,manager);
    }

    public boolean initManager(String zkServer,int timeout,NodeManager arg){
        if(zkclient != null)
            return true;
        if (arg != null) {
            Nodes = arg;
        }else{
            Nodes = new NodeManager();
            LOG.warn("NodeManager is Null, create a default NodeManager zkServer:{}",zkServer);
        }
        try {
            zkclient = new ZKClient(zkServer,timeout);
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
        // TODO: 16-11-18 make /crawler/appAPIs_ty as a variable
        zkclient.watchNodeChildrenChanged("/crawler/appAPIs_ty", new ZKCallback.ChildrenChangedListener() {
            @Override
            public void onChildrenChanged(List<String> list) {
                Nodes.updateNodeMap(list);
            }
        });
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
        CenterManageService.Processor<SCServiceHandler> processor = new CenterManageService.Processor<SCServiceHandler>(handler);
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
    
    public String getSuitableNode(){
        return Nodes.getSuitable();
    }

}