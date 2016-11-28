package mobi.bihu.crawler.sc.testthread;

import mobi.bihu.crawler.sc.thrift.SCService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by tianyoupan on 16-11-23.
 */
public class RegisterThreadTest {

    //how to use:
    //1.run the SCMain first
    //2.run this test
//    @Test
    public void run() throws Exception {

        Thread thread = new Thread(new Runnable() {
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
                    String Response = client.registerService("appapi","/crawler/appAPIs_ty","RANDOM");
                    System.out.println(Response);
                }catch (TException e) {
                    e.printStackTrace();
                }finally {
                    transport.close();
                }
            }
        });
        thread.start();
        while(true)
        {
            ;//防止线程迅速停止
        }

    }

}