package dev.thomazz.litelimbo.network;

import dev.thomazz.litelimbo.LiteLimbo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.FastThreadLocalThread;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;

public abstract class Connection {
	protected final Queue<Channel> boundChannels = new ConcurrentLinkedQueue<>();
	protected final boolean epoll;

	protected final EventLoopGroup bossGroup;
	protected final EventLoopGroup workerGroup;

	public Connection() {
		NettyThreadFactory threadFactory = new NettyThreadFactory();

		if (this.epoll = Epoll.isAvailable()) {
			LiteLimbo.LOGGER.info("Creating connection using Epoll");
			this.bossGroup = new EpollEventLoopGroup(0, threadFactory);
			this.workerGroup = new EpollEventLoopGroup(0, threadFactory);
		} else {
			LiteLimbo.LOGGER.info("Creating connection using NIO");
			this.bossGroup = new NioEventLoopGroup(0, threadFactory);
			this.workerGroup = new NioEventLoopGroup(0, threadFactory);
		}
	}

	public void start(InetSocketAddress address) {
		ServerBootstrap bootstrap = new ServerBootstrap()
				.channel(this.epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
				.childHandler(this.channelInitializer())
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.group(this.bossGroup, this.workerGroup)
				.localAddress(address);

		bootstrap.bind().addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				this.boundChannels.add(future.channel());
				LiteLimbo.LOGGER.info("Listening for connections at " + address);
			} else {
				LiteLimbo.LOGGER.error("Failed to bind channel: " + future.cause());
			}
		});
	}

	public void die() {
		Channel channel;
		while ((channel = this.boundChannels.poll()) != null) {
			try {
				channel.close().sync();
			} catch (InterruptedException e) {
				LiteLimbo.LOGGER.info("Thread interrupt while closing " + channel.localAddress());
				e.printStackTrace();
			}
		}

		this.bossGroup.shutdownGracefully();
		this.workerGroup.shutdownGracefully();
	}

	protected abstract ChannelInitializer<Channel> channelInitializer();

	public static class NettyThreadFactory implements ThreadFactory {
		private int threadId;

		public Thread newThread(@Nonnull Runnable runnable) {
			return new FastThreadLocalThread(runnable, "Netty Thread #" + this.threadId++);
		}
	}
}
