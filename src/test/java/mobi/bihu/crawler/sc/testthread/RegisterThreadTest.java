package mobi.bihu.crawler.sc.testthread;

import org.junit.Test;

/**
 * Created by tianyoupan on 16-11-23.
 */
public class RegisterThreadTest {

    //how to use:
    //1.run the SCMain first
    //2.run this test
    @Test
    public void run() throws Exception {

        RegisterThread register = new RegisterThread();
        register.start();
        try {
            register.sleep(100000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}