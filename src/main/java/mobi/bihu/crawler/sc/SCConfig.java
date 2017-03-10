package mobi.bihu.crawler.sc; /**
 * Created by tianyoupan on 16-11-16.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Description:
 */

class SCConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SCConfig.class);

    private String serverIP;
    private int serverPort;
    private String zkServer;
    private String[] servers;
    private int zkPort;
    private int zkTimeOut;
    private String JMXEnabled;
    private int thriftThreadMax;

    SCConfig(String conf) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        parse(properties);
    }

    private void parse(Properties properties) throws Exception {
        //ip and port config
        serverIP = properties.getProperty("serverIP");
        serverPort = Integer.valueOf(properties.getProperty("serverPort"));
        zkServer = properties.getProperty("zkServer");
        zkPort = Integer.valueOf(properties.getProperty("zkPort"));
        JMXEnabled = properties.getProperty("JMXEnabled");
        String serverList = properties.getProperty("serverList");
        zkTimeOut = Integer.valueOf(properties.getProperty("zkTimeOut"));
        thriftThreadMax = Integer.valueOf(properties.getProperty("thriftThreadMax"));
        servers = serverList.split(",");
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getZkPort() {
        return zkPort;
    }

    public String getZkServer() {
        return zkServer;
    }

    public String getJMXEnabled() {
        return JMXEnabled;
    }

    public String[] getServerList() {
        return servers;
    }

    public int getZkTimeOut() {
        return zkTimeOut;
    }

    public int getThriftThreadMax() {
        return thriftThreadMax;
    }
}
