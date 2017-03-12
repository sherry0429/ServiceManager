package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-15.
 */

import java.util.ArrayList;
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
    private String type;

    Ruler(String type) {
        this.type = type;
        setPowerCount(type);
    }

    String getType() {
        return type;
    }

    private String findMinDefault(ConcurrentHashMap<Node, ComputerStatus> list) {
        return null;
    }

    /**
     * Description: setPowerCount by the type define in powerType
     * Input: powerType
     * Output: 
     * Authers: tianyoupan
     */
    private void setPowerCount(String type){
        switch (type){
            case "CPUMORE":{
                setPowerCountCustom(0.7,0.1,0.1,0.1);
                break;
            }
            case "MEMORYMORE":{
                setPowerCountCustom(0.1,0.7,0.1,0.1);
                break;
            }
            case "DISKMORE":{
                setPowerCountCustom(0.1,0.1,0.7,0.1);
                break;
            }
            case "NETUSGMORE":{
                setPowerCountCustom(0.1,0.1,0.1,0.7);
                break;
            }
            case "AVERAGE":{
                setPowerCountCustom(0.25,0.25,0.25,0.25);
                break;
            }
            case "RANDOM":{
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
    private String findMinByPowerCount(ConcurrentHashMap<Node, ComputerStatus> list) {
        double min = Double.MAX_VALUE;
        String path = null;
        Node minNode = null;
        for (Map.Entry<Node, ComputerStatus> entry : list.entrySet()) {
            ComputerStatus value = entry.getValue();
            double powerCount = value.getMemory() * powerMemory + value.getCPU() * powerCPU + value.getDisk() * powerDisk + value.getNetUsg() * powerNetUsg;
            if (powerCount < min) {
                min = powerCount;
                minNode = entry.getKey();
            }
        }
        if (minNode != null) {
            path = minNode.getIP() + ":" + minNode.getPort() + "," + minNode.getName();
        }
        return path;
    }

    String findMinByRandom(ArrayList<Node> list){
        Random random = new Random();
        int index = random.nextInt(list.size());
        Node node = list.get(index);
        String path = null;
        if (node != null) {
            path = node.getIP() + ":" + node.getPort() + "," + node.getName();
        }
        return path;
    }


    String findSuitable(ConcurrentHashMap<Node, ComputerStatus> list) {
        return findMinByPowerCount(list);
    }
}
