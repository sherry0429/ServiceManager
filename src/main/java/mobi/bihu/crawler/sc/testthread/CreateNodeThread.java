package mobi.bihu.crawler.sc.testthread;
/**
 * Created by tianyoupan on 16-11-23.
 */

import mobi.bihu.crawler.zookeeper.ZKClient;

import java.io.IOException;

/**
 * Description:
 */

public class CreateNodeThread extends Thread{
    private static ZKClient zkclient;
    public CreateNodeThread(){
        try {
            zkclient = new ZKClient("192.168.31.21:2181,192.168.31.22:2181,192.168.31.23:2181", 3000);
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        while(true){
            zkclient.createNodeEphemeralSequential("/crawler/appAPIs_ty","192.168.31.21:2181,appapi-01");
            try{
                sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
