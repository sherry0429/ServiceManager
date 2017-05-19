package servicemanager;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import servicemanager.zookeeper.ZKCallBack;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author:TianYouPan
 * Time:2017/2/21
 * Description:
 * zookeeper java client
 */
public class ZKClient {

    private static final Logger LOG = LoggerFactory.getLogger(ZKClient.class);
    private static CuratorFramework client;
    private static List<String>zkChild = new ArrayList<>();
    private static ZKCallBack zkCallBack;
    private static HashMap<String,PathChildrenCache> listenMap = new HashMap<>();
    private ZKCallBack callBack;

    public ZKClient(String path){
        try {

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(path, retryPolicy);
            client.start();
        } catch (Exception e) {
            LOG.warn("Zookeeper init failed. Msg : {}", e.getMessage());
        }
    }


    public boolean CreateNode(String path, String data){
        try{
            if(client.checkExists() != null){
                client.create().inBackground().forPath(path);
                PathChildrenCache cache = new PathChildrenCache(client, path, true);
                cache.getListenable().addListener(new CustomPathListener());
                cache.start();
                return true;
            }
        }catch (Exception e){
            LOG.warn("Zookeeper Node create failed. Msg : {}", e.getMessage());
        }
        return false;
    }

    public void DeleteNode(String path){
        try{
            if(client.checkExists() != null){
                client.delete().inBackground().forPath(path);
            }
        }catch (Exception e){
            LOG.warn("Zookeeper Node create failed. Msg : {}", e.getMessage());
        }
    }

    public void GetNodeData(String path){
        try{
            if(client.checkExists() != null){
                client.getData().inBackground().forPath(path);
            }
        }catch (Exception e){
            LOG.warn("Zookeeper Node create failed. Msg : {}", e.getMessage());
        }
    }

    public void SetNodeData(String path){
        try{
            if(client.checkExists() != null){
                client.setData().inBackground().forPath(path);
            }
        }catch (Exception e){
            LOG.warn("Zookeeper Node create failed. Msg : {}", e.getMessage());
        }
    }


    class CustomPathListener implements PathChildrenCacheListener{
        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            switch (event.getType()){
                case CHILD_ADDED:
                    callBack.onChildAdd(event.getData().getPath(), new String(event.getData().getData()));
                    break;
                case CHILD_REMOVED:
                    callBack.onChildDelete(event.getData().getPath(), new String(event.getData().getData()));
                    break;
                case CHILD_UPDATED:
                    callBack.onChildUpdate(event.getData().getPath(), new String(event.getData().getData()));
                    break;
                case CONNECTION_LOST:
                    break;
                case CONNECTION_RECONNECTED:
                    break;
                case CONNECTION_SUSPENDED:
                    break;
                case INITIALIZED:
                    break;
            }
        }
    }


}
