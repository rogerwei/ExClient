package org.pekall.ExClient.Util;

/**
 * Created with IntelliJ IDEA.
 * User: next
 * Date: 13-8-23
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
public class GetData {

    public static byte[] subBytes(byte[] bytes, int from, int length)  {
        byte[] data = new byte[length];
        System.arraycopy(bytes, from, data, 0, length);

        return data;
    }

    public static byte[] getOqaqueTypeLength(int len) {
        byte[] bytes = new byte[4];
        int i = 0;

        do {  //covert into bytes
            byte data = (byte)(len & 0x7f);
            len >>=7;
            if (i > 0)
                data += (byte)0x80;
            bytes[i] = data;
            i++;
        } while (len > 0);

        byte[]  res = new byte[i];
        int j = 0;
        for (i--; i >=0;i --)  {  //rollback
            res[j++] = (bytes[i]);
        }

        return res;
    }
}
