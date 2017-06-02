package servicemanager.zookeeper;

/**
 * Author:TianYouPan
 * Time:2017/2/21
 * Description:
 */
public interface ZKCallBack {
    void onChildAdd(String path, String data);
    void onChildDelete(String path);
    void onChildUpdate(String path, String data);
}
