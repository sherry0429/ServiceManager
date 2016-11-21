package mobi.bihu.crawler.sc;

/**
 * Created by tianyoupan on 16-11-16.
 */

import mobi.bihu.crawler.sc.scnode.NodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

/**
 * Description:
 */

public class SCMain {
    private static final Logger LOG = LoggerFactory.getLogger(SCMain.class);
    private static final String DEFAULT_CONF_FILE = "conf/app_api.conf";

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("PRC"));
    }

    public static void main(String[] args) {

        String conf = DEFAULT_CONF_FILE;
        if (args.length >= 1) {
            conf = args[0];
        }
        SCConfig config = null;
        try {
            config = new SCConfig(conf);
        } catch (Exception e) {
            LOG.error("Fail to parse conf file {}, {}", conf, e.getMessage());
            System.exit(-1);
        }
        /** init Center */
        NodeManager nodeManager = new NodeManager();
        nodeManager.init(config.getZkServer());
        ServiceManager serviceManager = new ServiceManager(config, nodeManager);

        boolean status = serviceManager.startManager();
        if (!status) {
            LOG.error("ServiceManager start failed.");
            System.exit(-1);
        }
        LOG.info("ServiceManager start success.");
    }
}
