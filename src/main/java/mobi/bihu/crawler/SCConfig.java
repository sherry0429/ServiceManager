package mobi.bihu.crawler; /**
 * Created by tianyoupan on 16-11-16.
 */

import mobi.bihu.crawler.config.Config;
import mobi.bihu.crawler.util.IPUtils;
import mobi.bihu.crawler.util.ObjectUtils;
import mobi.bihu.crawler.util.ShellUtils;
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

    private String phantomjs;

    private int thriftThreadMax;

    private String nlpHost;
    private int nlpPort;

    private String regionUrl;

    private String captchaHost;
    private int captchaPort;
    private int captchaHttpPort;

    private String zkServer;
    private int zkTimeout;
    private String zkNode;

    private int proxyQuality;

    private String pproxyUrl;

    private String name;

    private String proxyServiceHost;
    private int proxyServicePort;

    private String overseasProxyHost;
    private int overseasProxyPort;

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
        //phantomjs config
        phantomjs = properties.getProperty("phantomjs");
        if (ObjectUtils.isEmpty(phantomjs)) throw new Exception("phantomjs must be configured");
        ShellUtils.CommandResult result = ShellUtils.execCommand(phantomjs + " -v");
        if (result.result != 0 || ObjectUtils.isEmpty(result.successMsg)) {
            LOG.warn("Failed to exec '{} -v', retCode: {}, {}", phantomjs, result.result, result.errorMsg);
            throw new Exception("phantomjs is configured error");
        }
        LOG.info("Conf: get phantomjs {}, version: {}", phantomjs, result.successMsg);
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

        nlpHost = properties.getProperty("nlp_host");
        if (nlpHost == null) throw new Exception("nlp_host must be configured");
        LOG.info("Conf: get nlp_host {}", nlpHost);

        String nlpPortStr = properties.getProperty("nlp_port");
        if (nlpPortStr == null) throw new Exception("nlp_port must be configured");
        try {
            nlpPort = Integer.valueOf(nlpPortStr);
        } catch (NumberFormatException e) {
            throw new Exception("nlp_port must be number");
        }
        LOG.info("Conf: get nlp_port {}", nlpPort);

        regionUrl = properties.getProperty("region_url");
        if (regionUrl == null) throw new Exception("region_url must be configured");
        LOG.info("Conf: get region_url {}", regionUrl);

        captchaHost = properties.getProperty("captcha_host");
        if (captchaHost == null) throw new Exception("captcha_host must be configured");
        LOG.info("Conf: get captcha_host {}", captchaHost);

        String captchaPortStr = properties.getProperty("captcha_port");
        if (captchaPortStr == null) throw new Exception("captcha_port must be configured");
        try {
            captchaPort = Integer.valueOf(captchaPortStr);
        } catch (NumberFormatException e) {
            throw new Exception("captcha_port must be number");
        }
        LOG.info("Conf: get captcha_port {}", captchaPort);

        String captchaHttpPortStr = properties.getProperty("captcha_http_port");
        if (captchaHttpPortStr == null) throw new Exception("captcha_http_port must be configured");
        try {
            captchaHttpPort = Integer.valueOf(captchaHttpPortStr);
        } catch (NumberFormatException e) {
            throw new Exception("captcha_http_port must be number");
        }
        LOG.info("Conf: get captcha_http_port {}", captchaHttpPort);

        //zookeeper config
        parseZookeeper(properties);
        zkServer = zkServer_;
        zkTimeout = zkTimeout_;
        zkNode = zkNode_;

        name = properties.getProperty("name");
        if (name == null) throw new Exception("name must be configured");
        LOG.info("Conf: get name {}", name);

        String proxyQualityStr = properties.getProperty("proxy_quality");
        if (!ObjectUtils.isEmpty(proxyQualityStr)) {
            try {
                proxyQuality = Integer.valueOf(proxyQualityStr);
                LOG.info("Conf: get proxy_quality {}", proxyQuality);
            } catch (NumberFormatException e) {
                throw new Exception("proxy_quality must be number");
            }
        }
        else {
            proxyQuality = 0;
        }

        pproxyUrl = properties.getProperty("pproxy_url");
        if (!ObjectUtils.isEmpty(pproxyUrl)) {
            LOG.info("Conf: get pproxy_url {}", pproxyUrl);
        }

        proxyServiceHost = properties.getProperty("proxy_service_host");
        if (!ObjectUtils.isEmpty(proxyServiceHost)) {
            LOG.info("Conf: get proxy_service_host {}", proxyServiceHost);
        }
        String proxyServicePortStr = properties.getProperty("proxy_service_port");
        if (!ObjectUtils.isEmpty(proxyServicePortStr)) {
            try {
                proxyServicePort = Integer.parseInt(proxyServicePortStr);
                LOG.info("Conf: get proxy_service_port {}", proxyServicePort);
            } catch (NumberFormatException e) {
                throw new Exception("proxy_service_port must be number");
            }
        }
        else {
            proxyServicePort = 0;
        }

        overseasProxyHost = properties.getProperty("overseas_proxy_host");
        if (ObjectUtils.isEmpty(overseasProxyHost)) throw new Exception("overseas_proxy_host must be configured");
        LOG.info("Conf: get overseas_proxy_host {}", overseasProxyHost);

        String overseasProxyPortStr = properties.getProperty("overseas_proxy_port");
        if (ObjectUtils.isEmpty(overseasProxyPortStr)) throw new Exception("overseas_proxy_port must be configured");
        try {
            overseasProxyPort = Integer.parseInt(overseasProxyPortStr);
            LOG.info("Conf: get overseas_proxy_port {}", overseasProxyPort);
        } catch (NumberFormatException e) {
            throw new Exception("overseas_proxy_port must be number");
        }
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

    public String getPhantomjs() {
        return phantomjs;
    }

    public void setPhantomjs(String phantomjs) {
        this.phantomjs = phantomjs;
    }

    public int getThriftThreadMax() {
        return thriftThreadMax;
    }

    public void setThriftThreadMax(int thriftThreadMax) {
        this.thriftThreadMax = thriftThreadMax;
    }

    public String getNlpHost() {
        return nlpHost;
    }

    public int getNlpPort() {
        return nlpPort;
    }

    public String getRegionUrl() {
        return regionUrl;
    }

    public String getCaptchaHost() {
        return captchaHost;
    }

    public int getCaptchaPort() {
        return captchaPort;
    }

    public int getCaptchaHttpPort() {
        return captchaHttpPort;
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

    public int getProxyQuality() {
        return proxyQuality;
    }

    public String getPproxyUrl() {
        return pproxyUrl;
    }

    public String getProxyServiceHost() {
        return proxyServiceHost;
    }

    public int getProxyServicePort() {
        return proxyServicePort;
    }

    public String getOverseasProxyHost() {
        return overseasProxyHost;
    }

    public int getOverseasProxyPort() {
        return overseasProxyPort;
    }
}
