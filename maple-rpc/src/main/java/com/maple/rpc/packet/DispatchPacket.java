package com.maple.rpc.packet;

import com.maple.rpc.constant.RpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

/**
 * 跨服包
 *
 * @author lfy
 * @date 2019/12/6 15:20
 **/
public class DispatchPacket implements ReferenceCounted {


	private short packetId;

	private ByteBuf byteBuf;

	public DispatchPacket(short packetId, ByteBuf byteBuf) {
		super();
		this.packetId = packetId;
		this.byteBuf = byteBuf;
	}


	public short getPacketId() {
		return packetId;
	}

	public void setPacketId(short packetId) {
		this.packetId = packetId;
	}

	public ByteBuf getByteBuf() {
		return byteBuf;
	}

	public void setByteBuf(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}


	public void write(ByteBuf out) {
		out.writeInt(byteBuf.readableBytes());
		out.writeByte(RpcConstant.CROSS_MSG_TYPE_MSG);
		// TODO 需要玩家id
		out.writeShort(packetId);
		out.writeBytes(byteBuf);
	}

	@Override
	public int refCnt() {
		return byteBuf.refCnt();
	}

	@Override
	public ReferenceCounted retain() {
		return byteBuf.retain();
	}

	@Override
	public ReferenceCounted retain(int i) {
		return byteBuf.retain();
	}

	@Override
	public ReferenceCounted touch() {
		return byteBuf.touch();
	}

	@Override
	public ReferenceCounted touch(Object o) {
		return byteBuf.touch(o);
	}

	@Override
	public boolean release() {
		return byteBuf.release();
	}

	@Override
	public boolean release(int i) {
		return byteBuf.release(i);
	}
}
