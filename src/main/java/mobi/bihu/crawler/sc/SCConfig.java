package mobi.bihu.crawler.sc; /**
 * Created by tianyoupan on 16-11-16.
 */

import mobi.bihu.crawler.config.Config;
import mobi.bihu.crawler.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Description:
 */

public class SCConfig extends Config {
    private static final Logger LOG = LoggerFactory.getLogger(SCConfig.class);
    private static final int THRIFT_THREAD_MAX = 200;

    private String IP;
    private int port;

    private int thriftThreadMax;

    private String zkServer;
    private int zkTimeout;
    private String zkNode;
    private String zkList;//zkServerName:zkNode1,zkNode2,zkNode3.....
    private String zkNodeList;

    private String name;

    public SCConfig(String conf) throws Exception {
        super(conf);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void parse(Properties properties) throws Exception {
        //ip and port config
        IP = properties.getProperty("IP");
        if (IP == null) throw new Exception("IP must be configured");
        LOG.info("Conf: get IP {}", IP);
        if (!IPUtils.getLocalIPList().contains(IP)) {
            LOG.warn("IP({}) in conf may not be a local IP", IP);
        }

        String portStr = properties.getProperty("port");
        if (portStr == null) throw new Exception("port must be configured");
        try {
            port = Integer.parseInt(portStr);
        }
        catch (NumberFormatException e) {
            throw new Exception("Cannot parse port({})".replace("{}", portStr));
        }
        if (port == 0) throw new Exception("Worker cannot listen on port 0");
        LOG.info("Conf: get port {}", port);

        //thrift config
        String thriftThreadMaxStr = properties.getProperty("thrift_thread_max");
        if (thriftThreadMaxStr != null) {
            try {
                thriftThreadMax = Integer.parseInt(thriftThreadMaxStr);
                LOG.info("Conf: get thrift_thread_max {}", thriftThreadMax);
            } catch (NumberFormatException e) {
                LOG.warn("Conf: cannot parse thrift_thread_max {}", thriftThreadMaxStr);
            }
            if (thriftThreadMax <= 0) {
                thriftThreadMax = THRIFT_THREAD_MAX;
            }
        }
        else {
            thriftThreadMax = THRIFT_THREAD_MAX;
        }

        //zookeeper config
        parseZookeeper(properties);
        zkServer = zkServer_;
        zkTimeout = zkTimeout_;
        zkNode = zkNode_;

        //zookeeper new conf
        zkList = properties.getProperty("zk_list");
        LOG.info("Conf: get zklist {}", zkList);

        zkNodeList = properties.getProperty("zk_nodelist");
        LOG.info("Conf: get zkNodelist {}", zkNodeList);

        name = properties.getProperty("name");
        if (name == null) throw new Exception("Name must be configured");
        LOG.info("Conf: get Name {}", name);



    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getThriftThreadMax() {
        return thriftThreadMax;
    }

    public String getZkServer() {
        return zkServer;
    }

    public int getZkTimeout() {
        return zkTimeout;
    }

    public String getZkNode() {
        return zkNode;
    }

    public String getName() {
        return name;
    }

    public String getZkList() {
        return zkList;
    }

    public void setZkList(String zkList) {
        this.zkList = zkList;
    }

    public void setZkNode(String zkNode) {
        this.zkNode = zkNode;
    }

    public String getZkNodeList() {
        return zkNodeList;
    }
}
