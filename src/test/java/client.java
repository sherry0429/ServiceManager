import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import servicemanager.thrift.SCManager;

/**
 * Author:TianYouPan
 * Time:2017/5/21
 * Description:
 */
public class client {

    public static void main(String[] args) {
        String IP = "127.0.0.1";
        int port = 8000;
        int timeout = 3000;
        TTransport transport = new TSocket(IP, port, timeout);
        TProtocol protocol = new TBinaryProtocol(transport);

        SCManager.Client client = new SCManager.Client(protocol);//初始化Thrift Client
        try {
            transport.open();
            //Thrift调用
            String response = client.registerService("test","/zookeeper/pty/test");
            System.out.println(response);
        } catch (TException e) {
            e.printStackTrace();
        }

    }
}
