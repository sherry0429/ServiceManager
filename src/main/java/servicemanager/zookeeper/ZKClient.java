package servicemanager.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;

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

    public ZKClient(String path, ZKCallBack callback){
        try {
            zkCallBack = callback;
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(path, retryPolicy);
            TreeCache cache = new TreeCache(client, "/zookeeper");
            cache.getListenable().addListener(new CustomPathListener());
            cache.start();
            client.start();
        } catch (Exception e) {
            LOG.warn("Zookeeper init failed. Msg : {}", e.getMessage());
        }
    }

    public boolean CreateNode(String path, String data, boolean isGroup){
        try{
            if(client.checkExists() != null){
                client.create().inBackground().forPath(path, data.getBytes());
                return true;
            }
        }catch (Exception e){
            LOG.warn("Zookeeper Node create failed. Msg : {}", e.getMessage());
        }
        return false;
    }

    public boolean DeleteNode(String path){
        try{
            if(client.checkExists() != null){
                client.delete().deletingChildrenIfNeeded().inBackground().forPath(path);
                return true;
            }
        }catch (Exception e){
            LOG.warn("Zookeeper Node create failed. Msg : {}", e.getMessage());
        }
        return false;
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


    class CustomPathListener implements TreeCacheListener{
        @Override
        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
            switch (event.getType()){
                case NODE_ADDED:
                    zkCallBack.onChildAdd(event.getData().getPath(), new String(event.getData().getData()));
                    break;
                case NODE_REMOVED:
                    zkCallBack.onChildDelete(event.getData().getPath());
                    break;
                case NODE_UPDATED:
                    zkCallBack.onChildUpdate(event.getData().getPath(), new String(event.getData().getData()));
                    break;
            }
        }
    }
}
