package mobi.bihu.crawler.sc.testthread;
/**
 * Created by tianyoupan on 16-11-23.
 */

import mobi.bihu.crawler.sc.thrift.SCService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Description:
 */

public class RegisterThread extends Thread{

    public RegisterThread(){
        ;
    }

    @Override
    public void run() {
        TTransport transport = new TSocket("192.168.31.103",10151, 3000);
        TProtocol protocol = new TBinaryProtocol(transport);

        // TMultiplexedProtocol
        // 按名称获取服务端注册的service
        SCService.Client client = new SCService.Client(protocol);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        try{
            String Response = client.registerService("appapi","/crawler/appAPIs_ty");
            System.out.println(Response);
        }catch (TException e) {
            e.printStackTrace();
        }finally {
            transport.close();
        }
    }
}
