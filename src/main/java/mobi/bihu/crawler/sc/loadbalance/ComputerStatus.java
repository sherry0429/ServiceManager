package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-22.
 */

/**
 * Description:
 * LoadInfo
 */

class ComputerStatus {
    private String memory;
    private String CPU;
    private String disk;
    private String netUsg;

    public ComputerStatus(){
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    //String to Object, make use easily.
    double getMemory() {
        return Double.parseDouble(memory);
    }

    double getCPU() {
        return Double.parseDouble(CPU);
    }

    double getDisk() {
        return Double.parseDouble(disk);
    }

    double getNetUsg() {
        return Double.parseDouble(netUsg);
    }
}
