package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-21.
 */

/**
 * Description:
 */

class Node {
    private String name;
    private String IP;
    private int port;

    Node(String ip, int port, String name){
        this.IP = ip;
        this.port = port;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getName() {
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
