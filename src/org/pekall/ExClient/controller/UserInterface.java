package org.pekall.ExClient.controller;

import static org.pekall.ExClient.configuration.Configs.setRumTimes;
import static org.pekall.ExClient.configuration.RunTime.initClientId;
import static org.pekall.ExClient.controller.TestCounter.*;

/**
 * Created with IntelliJ IDEA.
 * User: next
 * Date: 13-8-26
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
public class UserInterface {
    private static Booter booter;
    public static void sendMail(int times)  {
        if (!isTesting())  {
            setRumTimes(times);
            clearTestCounter();
            initClientId();
            SendRequest.SendMail();
        }else  {
            System.out.println("Your Test is Running.Pls do this test latter.");
        }
    }

    private static void clearStatus() {
        clearTestCounter();
        initClientId();
        HandleChannel.releaseExternalResources();
    }

    public static void stop()  {
        if (!isTesting())  {
            if (booter == null)  {
                System.out.println("Booter is null.Can't controll it.");
            }else  {
                clearStatus();
                booter.stop();
            }
        }else  {
            System.out.println("Your Test is Running.Pls quit latter.");
        }
    }

    public static void start()  {
        if (!isTesting())  {
            if (booter == null)  {
                System.out.println("Booter is null.Can't controll it.");
            }else
                booter.start();
        }else  {
            System.out.println("Your Test is Running.Pls quit latter.");
        }
    }

    public static void restart()  {
        if (!isTesting())  {
            if (booter == null)  {
                System.out.println("Booter is null.Can't controll it.");
            }else  {
                clearStatus();
                booter.restart();
            }
        }else  {
            System.out.println("Your Test is Running.Pls quit latter.");
        }
    }

    public static void setBooter(Booter booter) {
        UserInterface.booter = booter;
    }
}
