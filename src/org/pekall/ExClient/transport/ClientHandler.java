package org.pekall.ExClient.transport;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.ssl.SslHandler;
import org.pekall.ExClient.configuration.Configs;
import org.pekall.ExClient.controller.HandleResponse;
import org.pekall.ExClient.test.Report;

import static org.pekall.ExClient.controller.HandleChannel.BuildPeer;

/**
 * Created with IntelliJ IDEA.
 * User: next
 * Date: 13-8-21
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Report.connectServer(true);

        if (Configs.isSslEnabled())   {
            SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
            ChannelFuture f= sslHandler.handshake();

            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess())
                    {
                        BuildPeer(future.getChannel());

                    } else {
                        future.getChannel().close();
                    }
                }
            });
        }else
            BuildPeer(e.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpResponse)  {
            HttpResponse response = (HttpResponse) e.getMessage();
            Channel channel = e.getChannel();
            HandleResponse.handle(response, channel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
