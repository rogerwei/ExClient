package org.pekall.ExClient.transport;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.pekall.ExClient.configuration.Configs;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: next
 * Date: 13-8-20
 * Time: 下午6:13
 * To change this template use File | Settings | File Templates.
 */
public class ClientProxy {
    private NioClientSocketChannelFactory factory;
    private ClientBootstrap bootstrap1 = null;
    private boolean running = false;
    private int times = 0;

    public ClientProxy(int m)  {
        times = m;
    }

    public void start()  {
        if (running)  {
            System.out.println(running? "All Client is Running." : "No Client Bootstrap built!");
            return;
        }
        Executor executor = Executors.newCachedThreadPool();

        factory = new NioClientSocketChannelFactory(executor, executor);
        bootstrap1 = new ClientBootstrap(factory);
        bootstrap1.setPipelineFactory(new PipelineFactory());

        for (int i =0;i < times;i++)
            bootstrap1.connect(new InetSocketAddress(Configs.getHost(), Configs.getPort()));

        System.out.println("测试客户端已经启动!");
        running = true;
    }

    public void stop() {
        if (!running)  {
            System.out.println(running? "All Client is Stopped." : "No Client Bootstrap built!");
            return;
        }

        bootstrap1.releaseExternalResources();
        running = false;
        System.out.println("测试客户端已经停止!");
    }
}
