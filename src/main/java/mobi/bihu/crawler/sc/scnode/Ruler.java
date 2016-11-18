package mobi.bihu.crawler.sc.scnode;
/**
 * Created by tianyoupan on 16-11-15.
 */

import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Ruler decide client get service or not
 */

public class Ruler {

    public Ruler(){

    }

    // TODO: 16-11-18 need Node's info,to select suitable algorithm
    private String findMax(ConcurrentHashMap<String,Node> info){
        return null;
    }

    private String findMin(ConcurrentHashMap<String,Node> info){
        return null;
    }

    public String findSuitable(ConcurrentHashMap<String,Node> info){return null;}
}
