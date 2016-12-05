package mobi.bihu.crawler.sc.testthread;

import mobi.bihu.crawler.sc.thrift.SCService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;

/**
 * Created by tianyoupan on 16-11-23.
 */
public class RequestThreadTest {

    //how to use:
    //1.run the SCMain first
    //2.run this test
    @Test
    public void run() throws Exception {
        Thread thread[] = new Thread[2];
        for (Thread thread1 : thread) {
            thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        TTransport transport = new TSocket("192.168.31.14",10120, 3000);
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
                            //long startTime = System.currentTimeMillis();
                            String Response = client.getService("mobi.bihu.appapi");
                            System.out.println(Response);
                            //long endTime = System.currentTimeMillis();
                        }catch (TException e) {
                            e.printStackTrace();
                        }finally {
                            transport.close();
                        }
                        try {
                            Thread.sleep(2000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread1.start();
        }
        while(true)
        {

        }
    }

}