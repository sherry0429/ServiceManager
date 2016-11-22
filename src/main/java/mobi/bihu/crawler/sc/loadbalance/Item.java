package mobi.bihu.crawler.sc.loadbalance;
/**
 * Created by tianyoupan on 16-11-21.
 */

/**
 * Description:
 */

class Item {
    public String name;
    private String IP;
    private int port;

    Item(String ip,int port,String name){
        this.IP = ip;
        this.port = port;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setIP(String IP) {
        this.IP = IP;
    }

    void setPort(int port) {
        this.port = port;
    }

    String getIP() {
        return IP;
    }

    int getPort() {
        return port;
    }
}
