package servicemanager.model;

/**
 * Author:TianYouPan
 * Time:2017/6/1
 * Description:
 */
public class ServiceNode {
    private String ip = null;
    private String port = null;
    private String data = null;
    private String path = null;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getData() {
        return data;
    }

    public String getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
