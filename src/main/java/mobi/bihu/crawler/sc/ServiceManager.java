package mobi.bihu.crawler.sc;

/**
 * Created by tianyoupan on 16-11-15.
/**
 * Description: manage Zookpeer/response thrift service
 */

/** unprepared, do not use this package. thanks */

import mobi.bihu.crawler.sc.loadbalance.NodeManager;
import mobi.bihu.crawler.sc.thrift.SCService;
import mobi.bihu.crawler.sc.thrift.SelectType;
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

class ServiceManager {
    private static NodeManager Nodes;
    private static ZKClient zkclient;
    private static int internal = 5000;
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
    private class NodeMainService implements Runnable {
        @Override
        public void run() {
            while (Nodes != null) {
                try {
                    Nodes.updateNodeInfoTiming();
                    Thread.sleep(internal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("PRC"));
    }

    ServiceManager(SCConfig conf, NodeManager manager) {
        if (conf == null || manager == null) {
            LOG.error("NULL Config or NULL NodeManager.");
            System.exit(1);
        } else {
            config = conf;
            Nodes = manager;
        }

    }

    boolean initManager() {
        zkServer = config.getZkServer();
        zkTimeout = config.getZkTimeout();
        String zkNodeList = config.getZkNodeList();

        try {
            zkclient = new ZKClient(zkServer, zkTimeout);
        } catch (IOException e) {
            System.exit(-1);
        }
        //every service have a watcher.

        /*
        String[] nodelist = zkNodeList.split("\\|");
        for (String s : nodelist) {
            String[] args = s.split("@");
            zkclient.watchNodeChildrenChanged(args[1], new ZKCallback.ChildrenChangedListener() {
                @Override
                public void onChildrenChanged(List<String> list) {
                    Nodes.updateNodeMap(args[0], list);
                }
            });

        }
        */
        return true;
    }

    boolean updateWatch(String serviceName,String watchPath){
        LOG.info("new service register. Name : {}, Path : {}",serviceName,watchPath);
        try {
            zkclient.watchNodeChildrenChanged(watchPath, new ZKCallback.ChildrenChangedListener() {
                @Override
                public void onChildrenChanged(List<String> list) {
                    LOG.info("node changed. Path : {}, list : {}",watchPath,list.toString());
                    Nodes.updateNodeMap(serviceName, list);
                }
            });
        }catch (Exception e){
            LOG.warn("add Watch Failed. serviceName : {} watchPath : {} Msg: {}",serviceName,watchPath,e.toString());
            return false;
        }
        return true;
    }

    boolean startManager() {
        //start loop thread for Nodes
        cpRunable = new NodeMainService();
        cpThread = new Thread(cpRunable);
        try {
            cpThread.start();
        }catch (Exception e){
            LOG.error("update thread start failed. msg : {}",e.toString());
        }

        //start thrift Server
        SCServiceHandler handler = new SCServiceHandler(this);
        SCService.Processor<SCServiceHandler> processor = new SCService.Processor<SCServiceHandler>(handler);
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

    String getSuitableNode(String serviceName,SelectType selectType) {
        return Nodes.getSuitable(serviceName,selectType);
    }


}