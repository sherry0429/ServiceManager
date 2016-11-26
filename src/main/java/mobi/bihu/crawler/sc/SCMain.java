package mobi.bihu.crawler.sc;

/**
 * Created by tianyoupan on 16-11-16.
 */

import mobi.bihu.crawler.sc.loadbalance.ServiceManager;
import mobi.bihu.crawler.sc.thrift.SCService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.TimeZone;

/**
 * Description:
 */

public class SCMain {
    private static final Logger LOG = LoggerFactory.getLogger(SCMain.class);
    private static final String DEFAULT_CONF_FILE = "conf/sc.conf";
    private static final String DEFAULT_XML_FILE = "conf/servicelist.xml";

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("PRC"));
    }

    public static void main(String[] args) {

        String conf = DEFAULT_CONF_FILE;
        String xml = DEFAULT_XML_FILE;
        if (args.length >= 1) {
            conf = args[0];
        }
        SCConfig config = null;
        try {
            config = new SCConfig(conf,xml);
        } catch (Exception e) {
            LOG.error("Fail to parse conf file {}, {}", conf, e.getMessage());
            System.exit(-1);
        }

        //Start Manager
        ServiceManager manager = new ServiceManager();
        boolean status = manager.StartManager(config.getZkServer(),config.getZkTimeout(),config.getInitServiceList());
        if (!status) {
            LOG.error("ServiceManager start failed.");
            System.exit(-1);
        }
        LOG.info("ServiceManager start success.");


        if(config.getJmxEnabled().equals("true")){
            //start JMX
            try {

                // Get the Platform MBean Server
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

                // Construct the ObjectName for the MBean we will register
                ObjectName name = new ObjectName("mobi.bihu.crawler.sc.loadbalance.JMX:type=ServiceManagerMirror");

                // Register the Hello World MBean
                mbs.registerMBean(manager.getMirror(), name);
            }catch (Exception e){
                LOG.warn("JMX start failed, msg : {}",e.toString());
            }
        }

        //start thrift Server
        SCServiceHandler handler = new SCServiceHandler(manager);
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


    }
}
