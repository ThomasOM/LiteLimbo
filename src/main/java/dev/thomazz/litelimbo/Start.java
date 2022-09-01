package dev.thomazz.litelimbo;

import dev.thomazz.litelimbo.util.ColorConverter;
import dev.thomazz.litelimbo.util.Constants;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

public final class Start {
	public static void main(String[] args) {
		try {
			// Detect netty memory leaks
			System.setProperty("io.netty.leakDetection.level", "ADVANCED");

			LiteLimbo liteLimbo = new LiteLimbo();

			// Read config
			int port;
			try (InputStream input = LiteLimbo.class.getClassLoader().getResourceAsStream("config.properties")) {
				Properties config = new Properties();
				config.load(input);

				liteLimbo.welcomeMessage(ColorConverter.convert(config.getProperty("welcome.message")));
				liteLimbo.motdMessage(ColorConverter.convert(config.getProperty("motd.message")));
				liteLimbo.onlineMode(Boolean.parseBoolean(config.getProperty("online.mode")));
				port = Integer.parseInt(config.getProperty("server.port"));
			} catch (Exception e) {
				LiteLimbo.LOGGER.error("Could not read config file!");
				throw e;
			}

			// Start and add shutdown hook
			liteLimbo.start(new InetSocketAddress(Constants.LOCAL_IP, port));
			Runtime.getRuntime().addShutdownHook(new Thread(liteLimbo::die, "limbo-shutdown"));
		} catch (Exception e) {
			LiteLimbo.LOGGER.info("Could not start LiteLimbo!");
			e.printStackTrace();
		}
	}
}
