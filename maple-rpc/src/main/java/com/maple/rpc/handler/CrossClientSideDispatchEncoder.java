package com.maple.rpc.handler;

import com.maple.rpc.packet.DispatchPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author lfy
 * @date 2019/12/6 15:55
 **/
public class CrossClientSideDispatchEncoder extends MessageToByteEncoder<DispatchPacket> {
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, DispatchPacket packet, ByteBuf byteBuf) throws Exception {

	}
}
