# LiteLimbo
A lightweight and high performance multi-version Minecraft limbo server.

Currently supporting 1.7.2-1.15.2, work in progress for 1.16+

# Summary
The goal of this project is to make a high performance and lightwight limbo server, which enables users to hold thousands of players on a single instance.
The way this limbo server works is not by running a regular Minecraft server in the background.
Instead, it only sends a few packets to a players to allow their client to log in.
After the player has logged in, keep-alive packets are sent to keep the connection alive.
