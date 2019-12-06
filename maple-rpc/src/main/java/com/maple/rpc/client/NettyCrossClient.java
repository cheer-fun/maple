package com.maple.rpc.client;

import com.maple.rpc.handler.*;
import com.maple.rpc.packet.DispatchPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lfy
 * @date 2019/12/6 15:35
 **/
@Slf4j
public class NettyCrossClient implements CrossClient {

	// TODO 需要优化，放到父类
	/**
	 * 客户端缓存
	 */
	private static ConcurrentHashMap<String, CrossClient> clientMap = new ConcurrentHashMap<>();

	private static final AtomicInteger threadNum = new AtomicInteger();

	private static EventLoopGroup workGroup = new NioEventLoopGroup();
	// TODO 需要优化
	/**
	 * 客户端执行线程池
	 */
	private static ExecutorService crossClientExecuteService = Executors.newCachedThreadPool(r -> {
		String threadName = "cross-service-thread-" + threadNum.incrementAndGet();
		return new Thread(r, threadName);
	});

	private static  AtomicInteger clientIDGenerator = new AtomicInteger();

	//----------static end;
	/**
	 * 客户端id生成器
	 */

	private final OnSocketCloseHandler onSocketCloseHandler;

	private final int id;

	private BlockingQueue<DispatchPacket> packetQueue = new LinkedTransferQueue<>();

	private volatile boolean running = true;

	private String ip;

	private int port;

	private volatile Channel channel;


	//-------------static method

	/**
	 * 根据服务器信息获取Client
	 *
	 * @return
	 */
	public CrossClient getCrossClientByServerInfo(ServerInfo serverInfo) {
		String ipAndPort = serverInfo.getIpAndPort();
		CrossClient crossClient = clientMap.get(ipAndPort);
		if (crossClient == null) {
			synchronized (this) {
				crossClient = clientMap.get(ipAndPort);
				if (crossClient == null) {
					crossClient = new NettyCrossClient(serverInfo.getIp(), serverInfo.getPort(), crossClient1 -> {
						if (clientMap.remove(ipAndPort, crossClient1)) {
							log.info("CrossClient key:{} is removed", ipAndPort);
						} else {
							log.info("CrossClient key:{} remove fail maybe have a new connection replaced curClient", ipAndPort);
						}
					});
					CrossClient oldClient = clientMap.putIfAbsent(ipAndPort, crossClient);
					if (oldClient != null) {
						crossClient = oldClient;
					} else {
						crossClientExecuteService.submit(crossClient);
					}

				}
			}
		}
		return crossClient;
	}


	public NettyCrossClient(String ip, int port) {
		this(ip, port, null);
	}

	public NettyCrossClient(String ip, int port, OnSocketCloseHandler onSocketCloseHandler) {
		super();
		this.id = clientIDGenerator.incrementAndGet();
		this.ip = ip;
		this.port = port;
		this.onSocketCloseHandler = onSocketCloseHandler;

	}


	@Override
	public boolean sendPacket(DispatchPacket packet) {
		return packetQueue.add(packet);
	}


	@Override
	public boolean isActive() {
		return channel != null && channel.isActive();
	}


	public ChannelFuture connectRemoteServer(EventLoopGroup workGroup) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_TIMEOUT, 5000)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						socketChannel.pipeline().addLast(CrossClientSideIdeHandler.createNew());
						socketChannel.pipeline().addLast(
								new CrossClientSideDispatchEncoder(),
								new CrossClientSideDispatchByteBuffEncoder(),
								new CrossClientSideResponseDecoder(),
								new CrossClientSideResponseHandler()
						);
					}
				});
		ChannelFuture future = bootstrap.connect(ip, port);
		this.channel = future.channel();

		if (onSocketCloseHandler != null) {
			channel.closeFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
					try {
						int size = packetQueue.size();
						if (size > 0) {
							log.error("CrossClient {}:{} remove msgLost queueSize:{}", ip, port, size);
						}
					} catch (Exception e) {
						log.error("error", e);
					}
				}
			});
		}
		return future;
	}


	public void close() {
		try {
			if (channel != null) {
				ChannelFuture close = channel.close();
				close.sync();
			}
			onSocketCloseHandler.onClose(this);
			this.running = false;
		} catch (Exception e) {
			log.error("close failure ", e);
		}
		log.info("crossClient close success {}:{},", ip, port);
	}

	@Override
	public void run() {
		try {
			init();
		} catch (Exception e) {
			log.error("connect failure ", e);
		}

		while (running) {
			if (StringUtils.isEmpty(ip) || port <= 0) {
				packetQueue.clear();
				log.error("cross connect serverInfo is null");
				break;
			}
			try {
				DispatchPacket take = packetQueue.take();
				init();
				ChannelFuture future = channel.writeAndFlush(packetQueue);
				future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			} catch (Exception e) {
				log.error("connect failure ", e);
			}
		}
	}


	private void init() throws InterruptedException {
		if (channel == null || !channel.isActive()) {
			log.error("try (re)connect cross server {}:{}", ip, port);
			ChannelFuture future = connectRemoteServer(workGroup);
			future.sync();
			if (future.isSuccess()) {
				log.info("cross connect success {}:{}", ip, port);
			} else {
				log.warn("cross connect failure {}:{}", ip, port);
			}
			ByteBuf buffer = Unpooled.buffer(64);
			// TODO 添加AUTH_KEY,PLATFORM,SERVER_ID;
			buffer.writeByte('c');
			buffer.writeByte('r');
			buffer.writeByte('o');
			buffer.writeByte('c');
			ChannelFuture future1 = channel.writeAndFlush(buffer);
			future1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		}
	}

	public int getId() {
		return this.id;
	}
}
