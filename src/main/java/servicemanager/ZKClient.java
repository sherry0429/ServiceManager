package servicemanager;

import servicemanager.zookeeper.ZKCallBack;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:TianYouPan
 * Time:2017/2/21
 * Description:
 * zookeeper java client
 */
public class ZKClient {

    private static final Logger LOG = LoggerFactory.getLogger(ZKClient.class);
    private static ZooKeeper zk;
    private static List<String>zkChild = new ArrayList<>();
    private static ZKCallBack zkCallBack;

    public ZKClient(String host,int timeOut){
        try {
            zk = new ZooKeeper(host, timeOut, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    try {
                        zkChild = zk.getChildren(event.getPath(),true);
                        LOG.info("child : {}",zkChild.toString());
                        zkCallBack.onChildChanged();

                    } catch (KeeperException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            LOG.warn("Zookeeper init failed. Msg : {}",e.getMessage());
        }
    }




}
