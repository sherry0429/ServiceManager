package mobi.bihu.crawler.sc.testthread;

import org.junit.Test;

/**
 * Created by tianyoupan on 16-11-23.
 */
public class RequestComputerThreadTest {

    //how to use:
    //1.run the SCMain first
    //2.run this test
    @Test
    public void run() throws Exception {
        RequestComputerThread requestPc = new RequestComputerThread();
        requestPc.start();
        try {
            requestPc.sleep(100000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}