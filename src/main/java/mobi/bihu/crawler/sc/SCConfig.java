package mobi.bihu.crawler.sc; /**
 * Created by tianyoupan on 16-11-16.
 */

import mobi.bihu.crawler.config.Config;
import mobi.bihu.crawler.util.IPUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Description:
 */

class SCConfig extends Config {
    private static final Logger LOG = LoggerFactory.getLogger(SCConfig.class);
    private static final int THRIFT_THREAD_MAX = 200;

    private String IP;
    private int port;

    private int thriftThreadMax;

    private String zkServer;
    private int zkTimeout;
    private String jmxEnabled;
    private String[] initServiceList;

    SCConfig(String conf,String xml) throws Exception {
        super(conf);
        parseXML(xml);
    }

    private void parseXML(String xml){
        SAXReader reader = new SAXReader();
        File file = new File(xml);
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
            List childElements = root.elements("service");
            int index = 0;
            initServiceList = new String[childElements.size()];
            for (Object childElement : childElements) {
                Element elm = (Element)childElement;
                List elmList = elm.elements();
                StringBuilder builder = new StringBuilder();
                Element name = (Element)elmList.get(0);
                Element watchNode = (Element)elmList.get(1);
                Element policy = (Element)elmList.get(2);
                builder.append(name.getTextTrim());
                builder.append(",").append(watchNode.getTextTrim());
                builder.append(",").append(policy.getTextTrim());
                initServiceList[index] = builder.toString();
                LOG.info("init services by XML : info : {}",initServiceList[index]);
                index++;
            }
        }catch (Exception e){
            LOG.warn("XML parse failed. path : {}",xml);
        }

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
        //parseZookeeper(properties);

        zkServer = properties.getProperty("zk_server");
        if(zkServer == null) {
            throw new Exception("zk_server must be configured");
        } else {
            LOG.info("Conf: get zk_server {}", zkServer);
            String zkTimeoutStr = properties.getProperty("zk_timeout");
            if (zkTimeoutStr != null) {
                try {
                    zkTimeout = Integer.parseInt(zkTimeoutStr);
                    LOG.info("Conf: get zk_timeout {}", zkTimeoutStr);
                } catch (NumberFormatException var4) {
                    LOG.warn("Cannot parse zk_timeout({}), use default({}) instead", zkTimeoutStr, 3000);
                    this.zkTimeout_ = 3000;
                }
            }
        }

        jmxEnabled = properties.getProperty("jmx_enabled");
    }

    String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    int getThriftThreadMax() {
        return thriftThreadMax;
    }

    String getZkServer() {
        return zkServer;
    }

    int getZkTimeout() {
        return zkTimeout;
    }

    public String getJmxEnabled() {
        return jmxEnabled;
    }

    public String[] getInitServiceList() {
        return initServiceList;
    }
}
