package mobi.bihu.crawler.sc.scservice;
/**
 * Created by tianyoupan on 16-11-18.
 */

import mobi.bihu.crawler.SCConfig;
import mobi.bihu.crawler.sc.ServiceManager;
import mobi.bihu.crawler.thrift.CenterManageService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: thrift service,return suitalbe up:port
 */

public class SCServiceHandler implements CenterManageService.Iface{

    private static final Logger LOG = LoggerFactory.getLogger(SCServiceHandler.class);
    private static ServiceManager manager;

    private String serviceName = null;

    public SCServiceHandler(SCConfig config,ServiceManager manager){
        this.serviceName = config.getName();
        this.manager = manager;
    }

    @Override
    public String getSuitableNode(String serviceName) throws TException {
        //request is appApiName,here are many kinds of appApisName
        switch (serviceName){
            case "appapi":{
                return manager.getSuitableNode();
            }
        }
        return null;
    }
}
