package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-22.
 */

/**
 * Description:
 */

class ComputerStatus {
    private String memory;

    public ComputerStatus(){
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    //String to Object, make use easily.
    public double getMemory() {
        return Double.parseDouble(memory);
    }
}
