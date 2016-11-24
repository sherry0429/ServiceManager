package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import mobi.bihu.crawler.sc.thrift.SelectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Ruler decide client get service or not
 */

class Ruler {
    private double powerMemory;
    private double powerCPU;
    private double powerNetUsg;
    private double powerDisk;
    private SelectType type;

    Ruler(SelectType type) {
        this.type = type;
        setPowerCount(type);
    }

    private String findMinDefault(ConcurrentHashMap<Item, ComputerStatus> list) {
        return null;
    }

    /**
     * Description: setPowerCount by the type define in powerType
     * Input: powerType
     * Output: 
     * Authers: tianyoupan
     */
    private void setPowerCount(SelectType type){
        switch (type){
            case CPUMORE:{
                setPowerCountCustom(0.7,0.1,0.1,0.1);
                break;
            }
            case MEMORYMORE:{
                setPowerCountCustom(0.1,0.7,0.1,0.1);
                break;
            }
            case DISKMORE:{
                setPowerCountCustom(0.1,0.1,0.7,0.1);
                break;
            }
            case NETUSGMORE:{
                setPowerCountCustom(0.1,0.1,0.1,0.7);
                break;
            }
            case AVERAGE:{
                setPowerCountCustom(0.25,0.25,0.25,0.25);
                break;
            }
            case RANDOM:{
                break;
            }
        }
    }
    
    /**
     * Description: set powerCount by custom.them sum need to be 1.
     * Input: power Params
     * Output: 
     * Authers: tianyoupan
     */
    private void setPowerCountCustom(double powerCPU,double powerMemory,double powerDisk,double powerNetUsg){
        this.powerCPU = powerCPU;
        this.powerDisk = powerDisk;
        this.powerNetUsg = powerNetUsg;
        this.powerMemory = powerMemory;
    }

    /**
     * Description: findMin by Weighted,powerCount must defined by setPowerCount method first.
     * Input: map
     * Output: min node's path,ip:port,name
     * Authers: tianyoupan
     */
    private String findMinByPowerCount(ConcurrentHashMap<Item, ComputerStatus> list) {
        double min = Double.MAX_VALUE;
        String path = null;
        Item minItem = null;
        for (Map.Entry<Item, ComputerStatus> entry : list.entrySet()) {
            ComputerStatus value = entry.getValue();
            double powerCount = value.getMemory() * powerMemory + value.getCPU() * powerCPU + value.getDisk() * powerDisk + value.getNetUsg() * powerNetUsg;
            if (powerCount < min) {
                min = powerCount;
                minItem = entry.getKey();
            }
        }
        if (minItem != null) {
            path = minItem.getIP() + ":" + minItem.getPort() + "," + minItem.getName();
        }
        return path;
    }

    private String findMinByRandom(ConcurrentHashMap<Item, ComputerStatus> list){
        Random random = new Random();
        int index = random.nextInt(list.size());
        List<Item> arr = new ArrayList<Item>(list.keySet());
        Item item = arr.get(index);
        String path = null;
        if (item != null) {
            path = item.getIP() + ":" + item.getPort() + "," + item.getName();
        }
        return path;
    }


    String findSuitable(ConcurrentHashMap<Item, ComputerStatus> list) {
        if(type == SelectType.RANDOM){
            return findMinByRandom(list);
        }
        return findMinByPowerCount(list);
    }
}
