package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-21.
 */

/**
 * Description:
 */

public class Node {
    private String data;
    private String IP;
    private int port;
    private float mem;
    private float cpu;
    private float Net;// send/receive
    private float disk;// read/write
    private String path;

    Node(String ip, int port, String data, String path){
        this.IP = ip;
        this.port = port;
        this.data = data;
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = obj instanceof Node;
        if(!flag){
            return false;
        }
        Node emp = (Node)obj;
        return this.getPath().equals(emp.getPath());
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
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

    float getMem() {
        return mem;
    }

    float getCpu() {
        return cpu;
    }

    public float getDisk() {
        return disk;
    }

    public float getNet() {
        return Net;
    }

    void setNet(float net) {
        Net = net;
    }

    void setDisk(float disk) {
        this.disk = disk;
    }

    void setCpu(float cpu) {
        this.cpu = cpu;
    }

    void setMem(float mem) {
        this.mem = mem;
    }
}
