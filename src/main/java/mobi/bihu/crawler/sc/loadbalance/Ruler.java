package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Ruler decide client get service or not
 */

class Ruler {

    Ruler(){

    }

    // TODO: 16-11-18 need Node's info,to select suitable algorithm
    private String findMax(ConcurrentHashMap<Item,ComputerStatus> list){
        return null;
    }

    private String findMin(ConcurrentHashMap<Item,ComputerStatus> list){
        return null;
    }

    String findSuitable(ConcurrentHashMap<Item,ComputerStatus> list){
        double min = Double.MAX_VALUE;
        String path = null;
        Item minItem = null;
        for (Map.Entry<Item, ComputerStatus> entry : list.entrySet()) {
            double value = entry.getValue().getMemory();
            if(value < min){
                min = value;
                minItem = entry.getKey();
            }
        }
        if(minItem != null){
            path = minItem.getIP() + ":" + minItem.getPort();
        }
        return path;
    }
}
