package mobi.bihu.crawler.sc;
/**
 * Created by tianyoupan on 16-11-18.
 */

import mobi.bihu.crawler.sc.loadbalance.ServiceManager;
import mobi.bihu.crawler.sc.thrift.SCService;
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
    public String getSuitable(String serviceName) throws TException {
        //request is appApiName,here are many kinds of appApisName
        return manager.getSuitable(serviceName);
    }

    @Override
    public String registerService(String serviceName, String watchNode, String selectType) throws TException {
        boolean result = manager.updateWatch(serviceName,watchNode,selectType);
        if(result){
            return serviceName + "@" + watchNode + " Ruler : " + selectType + " register Success.";
        }else{
            return serviceName + "@" + watchNode + " Ruler : " + selectType + " register Failed !";
        }
    }



}
