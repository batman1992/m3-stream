/*
 * Flazr <http://flazr.com> Copyright (C) 2009  Peter Thomas.
 *
 * This file is part of Flazr.
 *
 * Flazr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flazr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flazr.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.flazr.rtmp.server;

import android.util.Log;

import com.flazr.rtmp.RtmpHandshake;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class RtmpServerHandshakeHandler extends FrameDecoder implements ChannelDownstreamHandler {
    
    private boolean rtmpe;
    private RtmpHandshake handshake;
    private boolean partOneDone;
    private boolean handshakeDone;

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer in) {        
        if(!partOneDone) {
            handshake = new RtmpHandshake();
            if(in.readableBytes() < RtmpHandshake.HANDSHAKE_SIZE + 1) {
                return null;
            }
            handshake.decodeClient0And1(in);
            rtmpe = handshake.isRtmpe();
            ChannelFuture future = Channels.succeededFuture(channel);
            Channels.write(ctx, future, handshake.encodeServer0());
            Channels.write(ctx, future, handshake.encodeServer1());
            Channels.write(ctx, future, handshake.encodeServer2());
            partOneDone = true;
        }
        if(!handshakeDone) {
            if(in.readableBytes() < RtmpHandshake.HANDSHAKE_SIZE) {
                return null;
            }
            handshake.decodeClient2(in);
            handshakeDone = true;
            Log.i(this.getClass().getName(), "handshake done, rtmpe: "+ rtmpe);
            if(!rtmpe) {
                channel.getPipeline().remove(this);
            }
        }
        if(in.readable()) {
            Channels.fireMessageReceived(channel, in);
        }
        return null;
    }

    @Override
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent ce) throws Exception {        
        if (!handshakeDone || !rtmpe || !(ce instanceof MessageEvent)) {
            super.handleUpstream(ctx, ce);
            return;
        }
        final MessageEvent me = (MessageEvent) ce;
        if(me.getMessage() instanceof RtmpServerHandler.RtmpServerEvent) {
            super.handleUpstream(ctx, ce);
            return;
        }
        final ChannelBuffer in = (ChannelBuffer) ((MessageEvent) ce).getMessage();
        handshake.cipherUpdateIn(in);
        Channels.fireMessageReceived(ctx, in);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent ce) {        
        if (!handshakeDone || !rtmpe || !(ce instanceof MessageEvent)) {
            ctx.sendDownstream(ce);
            return;
        }
        final ChannelBuffer in = (ChannelBuffer) ((MessageEvent) ce).getMessage();
        handshake.cipherUpdateOut(in);
        ctx.sendDownstream(ce);
    }

}
