package org.pekall.ExClient.Protocol;

import org.jboss.netty.util.CharsetUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import static java.net.URLEncoder.encode;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.pekall.ExClient.Protocol.MIME.textMail.getMail;
import static org.pekall.ExClient.Util.GetData.*;
import static org.pekall.ExClient.configuration.RunTime.*;
import static org.pekall.ExClient.configuration.RunTime.getClientId;

/**
 * Created with IntelliJ IDEA.
 * User: next
 * Date: 13-8-22
 * Time: 上午11:26
 * To change this template use File | Settings | File Templates.
 */
public class BuildMessage {
    private BuildRequest.Type type;
    private String user;
    private static String policyType = "MS-EAS-Provisioning-WBXML";
    public BuildMessage(BuildRequest.Type type, String user) {
        this.type = type;
        this.user = user;
    }


    public String build() throws UnsupportedEncodingException {
        if (type.equals(BuildRequest.Type.Options)) {
            return "";
        }

        String message = buildWBXMLHeader();
        message += buildWBXMLBody();
        return message;
    }

    private static final int WBXMLVersion = 0x03;
    private String buildWBXMLHeader() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //version
        stream.write((byte)(WBXMLVersion & 0xff));

        //Public Identifier
        stream.write((byte)0x1);

        //Character set UTF8
        stream.write((byte)0x6a);

        //String table
        stream.write((byte)0x0);

        return stream.toString();
    }
    private String buildWBXMLBody() throws UnsupportedEncodingException {
        switch (type) {
            case Provision:
                return buildProvisionBody();
            case AckProvision:
                return buildAckProvisionBody();
            case SendMail:
                return buildSendMailBody();
            default:
                System.err.println("目前不支持WBXML类型.");
                return "";
        }
    }

    private String buildProvisionBody() throws UnsupportedEncodingException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Switch Page
        writeSwitchPage(stream, (byte) 0xe);

        //Provision
        stream.write((byte) 0x45);

        //Switch Page
        stream.write((byte) 0x0);
        stream.write((byte) 0x12);

        //DeviceInformation
        stream.write((byte) 0x56);

        //Set
        stream.write((byte) 0x48);

        //Write Device Information
        writeData(stream, (byte) 0x57, getModel());
        writeData(stream, (byte) 0x5a, getOS());
        writeData(stream, (byte) 0x5b, getLanguage());
        writeData(stream, (byte) 0x59, getFriendlyName(user));
        writeData(stream, (byte) 0x58, getDeviceId(user));
        writeData(stream, (byte) 0x5c, getPhoneNumber(user));

        //set end
        writeEnd(stream);

        //device information end
        writeEnd(stream);

        //Switch Page
        writeSwitchPage(stream, (byte)0xe);

        //Policies
        stream.write((byte) 0x46);
        //Policy
        stream.write((byte) 0x47);
        //Policy Type
        writeData(stream, (byte) 0x48, policyType);
        //Policy end
        writeEnd(stream);
        //Policies end
        writeEnd(stream);
        //Provision end
        writeEnd(stream);
        return stream.toString("utf-8");
    }

    private String buildAckProvisionBody() throws UnsupportedEncodingException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Switch Page
        writeSwitchPage(stream, (byte) 0xe);

        //Provision
        stream.write((byte) 0x45);

        //Policies
        stream.write((byte) 0x46);

        //Policy
        stream.write((byte) 0x47);

        //Policy Type
        writeData(stream, (byte)0x48, policyType);

        //Policy Key
        writeData(stream, (byte)0x49, String.valueOf(getPolicyKey(user)));

        //Status
        writeData(stream, (byte)0x4b, "1");

        //end Policy
        writeEnd(stream);

        //end Policies
        writeEnd(stream);

        //end Provision
        writeEnd(stream);

        return stream.toString("utf-8");
    }

    private String buildSendMailBody() throws UnsupportedEncodingException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //Switch Page
        writeSwitchPage(outputStream, (byte)0x15);

        //SendMail
        outputStream.write((byte)0x45);
        //ClientId
        writeData(outputStream, (byte)0x51, getClientId());

        //SaveInSentItems
        outputStream.write((byte)0x8);

        //MIME
        outputStream.write((byte)0x50);

        String tmp = getMail();
        outputStream.write(0xc3);
        try {
            outputStream.write(getOqaqueTypeLength(tmp.getBytes(UTF_8).length));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            outputStream.write(tmp.getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //MIME end
        writeEnd(outputStream);

        //SendMail end
        writeEnd(outputStream);

        return outputStream.toString("utf-8");
    }

    private void writeDataStart(ByteArrayOutputStream outputStream) {
        outputStream.write((byte)0x3);
    }

    private void writeDataEnd(ByteArrayOutputStream outputStream) {
        outputStream.write((byte)0x0);
        outputStream.write((byte)0x1);
    }

    private void writeEnd(ByteArrayOutputStream outputStream) {
        outputStream.write((byte)0x1);
    }

    private void writeSwitchPage(ByteArrayOutputStream stream, byte pageCode) {
        stream.write((byte)0x0);
        stream.write(pageCode);
    }

    private void writeData(ByteArrayOutputStream stream, byte cmd, String data) {
        stream.write(cmd);
        if (!data.isEmpty())  {
            writeDataStart(stream);
            try {
                stream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            writeDataEnd(stream);
        }else
            writeEnd(stream);
    }
}
