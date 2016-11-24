package mobi.bihu.crawler.sc;
/**
 * Created by tianyoupan on 16-11-18.
 */

import mobi.bihu.crawler.sc.thrift.SCService;
import mobi.bihu.crawler.sc.thrift.SelectType;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: thrift service,return suitable up:port
 */

class SCServiceHandler implements SCService.Iface{

    private static final Logger LOG = LoggerFactory.getLogger(SCServiceHandler.class);
    private ServiceManager manager;

    SCServiceHandler(ServiceManager manager){
        this.manager = manager;
    }

    @Override
    public String getSuitable(String serviceName,SelectType selectType) throws TException {
        //request is appApiName,here are many kinds of appApisName
        switch (serviceName){
            case "appapi":{
                return manager.getSuitableNode(serviceName,selectType);
            }
        }
        return null;
    }

    @Override
    public String registerService(String serviceName, String watchPath) throws TException {
        // TODO: 16-11-23 update config or not?
        boolean result = manager.updateWatch(serviceName,watchPath);
        if(result){
            return serviceName + "@" + watchPath + " register Success.";
        }else{
            return serviceName + "@" + watchPath + " register Failed !";
        }
    }


}
