package com.maple.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * @author lfy
 * @date 2019/12/6 15:56
 **/
public class CrossClientSideDispatchByteBuffEncoder extends MessageToByteEncoder<ByteBuf> {
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {

	}
}
