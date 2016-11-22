package mobi.bihu.crawler.sc;
/**
 * Created by tianyoupan on 16-11-18.
 */

import mobi.bihu.crawler.sc.thrift.SCService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: thrift service,return suitalbe up:port
 */

class SCServiceHandler implements SCService.Iface{

    private static final Logger LOG = LoggerFactory.getLogger(SCServiceHandler.class);
    private ServiceManager manager;
    private SCConfig config;

    //for test
    SCServiceHandler(){
        ;
    }

    SCServiceHandler(SCConfig config, ServiceManager manager){
        this.manager = manager;
        this.config = config;
    }

    @Override
    public String getSuitable(String serviceName) throws TException {
        //request is appApiName,here are many kinds of appApisName
        switch (serviceName){
            case "appapi":{
                return manager.getSuitableNode(serviceName);
            }
        }
        return null;
    }

    @Override
    public String registerService(String serviceName, String watchPath) throws TException {
        manager.updateWatch(serviceName,watchPath);
        return null;
    }


}
