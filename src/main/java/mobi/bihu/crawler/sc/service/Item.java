package mobi.bihu.crawler.sc.service;
/**
 * Created by tianyoupan on 16-11-21.
 */

/**
 * Description:
 */

public class Item {
    private String IP;
    private int port;
    private int memory;

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getMemory() {
        return memory;
    }
}
