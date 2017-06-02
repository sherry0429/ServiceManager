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
    private float powerMemory;
    private float powerCPU;
    private float powerNetUsg;
    private float powerDisk;
    private String type;

    Ruler(String type) {
        this.type = type;
        setPowerCount(type);
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
                setPowerCountCustom(0.7f,0.1f,0.1f,0.1f);
                break;
            }
            case "MEMORYMORE":{
                setPowerCountCustom(0.1f,0.7f,0.1f,0.1f);
                break;
            }
            case "DISKMORE":{
                setPowerCountCustom(0.1f,0.1f,0.7f,0.1f);
                break;
            }
            case "NETUSGMORE":{
                setPowerCountCustom(0.1f,0.1f,0.1f,0.7f);
                break;
            }
            case "AVERAGE":{
                setPowerCountCustom(0.25f,0.25f,0.25f,0.25f);
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
    private void setPowerCountCustom(float powerCPU,float powerMemory,float powerDisk,float powerNetUsg){
        this.powerCPU = powerCPU;
        this.powerDisk = powerDisk;
        this.powerNetUsg = powerNetUsg;
        this.powerMemory = powerMemory;
    }

    Node findSuitable(ArrayList<Node> list) {
        switch (type){
            case "RANDOM":
                Random random = new Random();
                return list.get(random.nextInt(list.size()));
            default:
                float score = 0;
                int index = 0;
                float min_score = 10000;
                for (int i = 0; i < list.size(); i++) {
                    if(score < min_score) {
                        min_score = score;
                        index = i;
                    }
                    score = list.get(i).getCpu() * powerCPU +
                            list.get(i).getMem() * powerMemory +
                            list.get(i).getNet() * powerNetUsg +
                            list.get(i).getDisk() * powerDisk;
                }
                return list.get(index);
        }
    }
}
