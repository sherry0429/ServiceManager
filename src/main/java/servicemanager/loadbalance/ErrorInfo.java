package servicemanager.loadbalance;
/**
 * Created by tianyoupan on 16-11-25.
 */

/**
 * Description:
 */

public class ErrorInfo {
    private String errmsg;
    private String errcode;

    /*
    code = 0 success
    code = 1 unRegister
     */
    public ErrorInfo(String msg,String code){
        errmsg = msg;
        errcode = code;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getErrcode() {
        return errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }
}
