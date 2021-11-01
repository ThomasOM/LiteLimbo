# LiteLimbo
A lightweight and high performance multi-version Minecraft limbo server.

Currently supporting 1.7.2-1.15.2, work in progress for 1.16+

# Summary
The goal of this project is to make a high performance and lightweight limbo server, which enables users to hold thousands of players on a single instance.
The way this limbo server works is not by running a regular Minecraft server in the background.
Instead, it only sends a few packets to players to allow their client to log in.
After the player has logged in, keep-alive packets are sent to keep the connection alive.

Feel free to fork and/or use this project however you desire.

A huge thanks to VelocityPowered for their packet utility and id mapping. Without their project this would have been a lot more time consuming.
Please check out their amazing project [here](https://github.com/VelocityPowered/Velocity)
