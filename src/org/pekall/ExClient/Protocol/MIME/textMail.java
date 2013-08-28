package org.pekall.ExClient.Protocol.MIME;

import java.io.IOException;

import static org.pekall.ExClient.Util.File.readFile;

/**
 * Created with IntelliJ IDEA.
 * User: next
 * Date: 13-8-27
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
public class textMail {
    private static final String mailFile = "mail.txt";

    public static String getMail()  {
        try {
            return readFile(mailFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return  "";
        }
    }
}
