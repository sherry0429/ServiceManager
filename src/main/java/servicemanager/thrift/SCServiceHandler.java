package servicemanager.thrift; /**
 * Created by tianyoupan on 16-11-18.
 */

import servicemanager.loadbalance.ServiceManager;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: thrift service,return suitable up:port
 */

public class SCServiceHandler implements SCService.Iface, SCManager.Iface{

    private static final Logger LOG = LoggerFactory.getLogger(SCServiceHandler.class);
    private ServiceManager manager;
    private boolean debug = true;
    private String test = "call success.";

    public SCServiceHandler(ServiceManager manager){
        this.manager = manager;
    }

    @Override
    public String getService(String serviceName) throws TException {
        //request a Service result
        if(debug){
            return test;
        }
        return manager.getService(serviceName);
    }

    @Override
    public String registerService(String serviceName, String watchNode) throws TException {
        if(debug){
            return test;
        }
        return null;
    }

    @Override
    public String removeService(String serviceName) throws TException {
        if(debug){
            return test;
        }
        return null;
    }

    @Override
    public String getServiceNodes(String serviceName) throws TException {
        if(debug){
            return test;
        }
        return null;
    }

    @Override
    public String removeNodes(String nodePath) throws TException {
        if(debug){
            return test;
        }
        return null;
    }
}
