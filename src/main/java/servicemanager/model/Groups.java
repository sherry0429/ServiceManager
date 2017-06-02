package servicemanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:TianYouPan
 * Time:2017/6/1
 * Description:
 */
public class Groups {
    private List<Group> groups = new ArrayList<Group>();
    private String tag = null;
    private String path = null;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public String getTag() {
        return tag;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group g){
        groups.add(g);
    }
}
