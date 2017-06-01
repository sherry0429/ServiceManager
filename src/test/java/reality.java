import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicemanager.zookeeper.ZKClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Author:TianYouPan
 * Time:2017/5/21
 * Description:用于仿真，模拟业务增删，服务宕机
 */
public class reality {

    static ZKClient zkClient = new ZKClient("/");
    private static final Logger LOG = LoggerFactory.getLogger(reality.class);
    private static final ArrayList<String> services = new ArrayList<>();
    static void NewService(String path, String data){
        zkClient.CreateNode(path, data);
    }

    static void DeleteService(String path){
        zkClient.DeleteNode(path);
    }

    static void ComputerDown(){
        //删除所有zk节点
        zkClient.DeleteNode("/");
    }

    static void ComputerRelive(){
        //新建一些zk节点
        for (String s : services) {
            String pathWithData[] = s.split(",");
            zkClient.CreateNode(pathWithData[0],pathWithData[1]);
        }

    }

    public static void main(String[] args) {
        String conf = "src/main/conf/sc.conf";
        if (args.length >= 1) {
            conf = args[0];
        }
        SCConfig config = null;
        try {
            config = new SCConfig(conf);
        } catch (Exception e) {
            LOG.error("Fail to parse conf file {}, {}", conf, e.getMessage());
            System.exit(-1);
        }
        String serverlist[] = config.getServerList();
        for (String s : serverlist) {
            String pathWithData[] = s.split(",");
            services.add(s);
            /* path and data */
            zkClient.CreateNode(pathWithData[0],pathWithData[1]);
        }
        Random random = new Random();
        while(true){
            try {
                Thread.sleep(1000);
                int tag = random.nextInt(4);
                int index = random.nextInt(services.size());
                String []pathWithData = services.get(index).split(",");
                switch (tag){
                    case 0:
                        NewService(pathWithData[0], pathWithData[1]);
                        break;
                    case 1:
                        DeleteService(pathWithData[0]);
                        break;
                    case 2:
                        ComputerDown();
                        break;
                    case 3:
                        ComputerRelive();
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
