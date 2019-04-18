FFmpegMC264Demo
===============

FFmpegMC264 is a ffmpeg android library built on MediaCodec.
This library doesn't call ffmpeg executable file through exec().
Instead this library has embedded the ffmpeg as souce code.
So, you can call the ffmpeg through the API - ffmpeg_main(int argc, char **argv) - not exec().

In C side, an encoder module - mc264.c - was added into ffmpeg libavcodec.
In java side, an encoder controller class - MC264Encode.java - was added upon android MediaCodec (H.264 encoder only).

You can run any ffmpeg command on your Java code using mc264 encoder.

Example : ffmpeg -i INPUT -vcodec mc264 -b:v 2.0M -r 30 -g 15 -acodec copy -f mp4 OUTPUT


