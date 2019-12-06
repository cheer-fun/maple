package com.maple.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author lfy
 * @date 2019/12/6 15:57
 **/
public class CrossClientSideResponseDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

	}
}
