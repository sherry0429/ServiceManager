package mobi.bihu.crawler.sc.scnode;
/**
 * Created by tianyoupan on 16-11-15.
 */

/**
 * Description: all computers info like IP,PORT,Memory,CPU,etc.
 */

public class NodeInfo {
    private String IP;
    private int port;
    private Integer memory;

    public NodeInfo(){
        ;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }
}
