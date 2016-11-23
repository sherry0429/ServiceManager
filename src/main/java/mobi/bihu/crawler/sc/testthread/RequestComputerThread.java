package mobi.bihu.crawler.sc.testthread;
/**
 * Created by tianyoupan on 16-11-23.
 */

import mobi.bihu.crawler.sc.thrift.SCClientService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Description:
 */

public class RequestComputerThread extends Thread{
    public RequestComputerThread(){

    }

    @Override
    public void run() {
        TTransport transport = new TSocket("192.168.31.103",10152, 3000);
        TProtocol protocol = new TBinaryProtocol(transport);

        // TMultiplexedProtocol
        TMultiplexedProtocol multiplexedProtocol = new  TMultiplexedProtocol(protocol,"sc");
        // 按名称获取服务端注册的service
        SCClientService.Client client = new SCClientService.Client(multiplexedProtocol);
        try {
            transport.open();
        } catch (TTransportException e) {
        }
        try{
            String jsonResponse=null,jsonRequest="simple";
            jsonResponse = client.requestServiceSituation(jsonRequest);
            if (jsonResponse != null) {
                try {
                    System.out.println(jsonResponse);
                }catch (Exception e){
                }
            }
        }catch (TException e) {
        }finally {
            transport.close();
        }
    }
}
