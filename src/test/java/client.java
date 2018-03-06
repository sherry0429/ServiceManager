import com.alibaba.fastjson.JSON;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import servicemanager.model.Group;
import servicemanager.model.Groups;
import servicemanager.model.ServiceNode;
import servicemanager.thrift.SCService;

import java.util.Random;

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
        boolean manager = true;
        TTransport transport = new TSocket(IP, port, timeout);
        TProtocol protocol = new TBinaryProtocol(transport);
        SCService.Client client = null;
        client = new SCService.Client(protocol);//初始化Thrift Client
        try {
            transport.open();
//           String response = client.registerService("unUsed",buildGroup("@TEST","test"));
//           String response = client.removeService("test0");
//           String response = client.removeNodes("/zookeeper/@TEST");
//            String response = client.getServiceNodes("test1");
            String response = client.getService("test2");
            System.out.println(response);
        } catch (TException e) {
            e.printStackTrace();
        }finally {
            transport.close();
        }
    }

    static String buildGroup(String tag, String serviceName){
        Groups groups = new Groups();
        groups.setTag(tag);
        groups.setPath("/zookeeper/" + tag);
        int groupMax = 5;
        int ServiceMax = 5;
        Random random = new Random();
        int groupNum = random.nextInt(groupMax) + 1;
        for (int i = 0; i < groupNum; i++){
            Group group = new Group();
            group.setServiceName(serviceName + String.valueOf(i));
            group.setPath(groups.getPath() + "/" +group.getServiceName());
            group.setTag("@@" + group.getServiceName());
            int serviceNum = random.nextInt(ServiceMax) + 1;
            for(int j = 0; j < serviceNum; j++){
                ServiceNode node = new ServiceNode();
                node.setData(serviceName + "-127.0.0.1-2181");
                node.setIp("127.0.0.1");
                node.setPort("2181");
                node.setPath(group.getPath() + "/zk-" + String.valueOf(j));
                group.addNode(node);
            }
            groups.addGroup(group);
        }
        String jsonString = JSON.toJSONString(groups);
        return jsonString;
    }
}
