FFmpegMC264Demo
===============

Use 'mc264' instead of 'libx264'.

FFmpegMC264 is a ffmpeg android library powered by MediaCodec HW accelated encoder.<br> 
Now use 'mc264' instead of 'libx264' in runing ffmpeg command in your code.

FFmpegMC264 library doesn't call ffmpeg executable file through exec().
Instead this library has embedded the ffmpeg as an API function.
So, you can call the ffmpeg like this : 
* int retcode = mMC264Encoder.ffmpegRun(cmdString);
  - cmdString example : 
  - ffmpeg -i INPUT -vcodec mc264 -acodec ac3 -f mp4 OUTPUT
  - ffmpeg -i INPUT -vcodec mc264 -b:v 2.0M -r 30 -g 15 -acodec ac3 -f mp4 OUTPUT
  - ffmpeg -i INPUT -vcodec mc264 -b:v 2.0M -r 30 -g 15 -acodec aac -b:a 64k -f mp4 OUTPUT
  - - aac : '-b:a 64k' recommended or AV sync problem on my device (LG Q6).
  - ffmpeg -re -i /sdcard/movie.avi -vcodec mc264 -acodec ac3 -f mpegts udp://192.168.0.12:5555?pkt_size=1316&buffer_size=655360
  - ffmpeg -i rtsp://192.168.0.10/videodevice -vcodec mc264 -an -f mpegts udp://192.168.0.12:5555?pkt_size=1316

  - generate TV test pattern : 
  - ffmpeg -f lavfi -i testsrc -pix_fmt yuv420p -vcodec mc264 -b:v 2.0M -r 30 -g 15 -an -f mp4 OUTPUT
  
For this, in C side, an encoder module - mc264.c - was added into ffmpeg libavcodec.<br>
In java side, an encoder controller class - MC264Encoder.java - was added over android MediaCodec (H.264 encoder only).

Enjoy ffmpeg powered by MediaCodec HW accelated encoder.


## The APIs you have to know : 

### prepare an instance
* mMC264Encoder = new MC264Encoder();
* mMC264Encoder.setYUVFrameListener(this, false);  - optional API

### prepare an AsyncTask
* ffmpeg_task = new MyTask(this);
* ffmpeg_task.execute( ffmpegCmdStringArray );

### run the instance in the task :: doInBackground()
* mMC264Encoder.H264MediaCodecReady();
* mMC264Encoder.ffmpegRun( strings );

### stop
* mMC264Encoder.ffmpegStop();
* mMC264Encoder.ffmpegForceStop(); - optional API to enforce stop

### post stop
* mMC264Encoder.reset();


## Supported Color Format :
* ffmpeg INPUT stream : yuv420p
* MediaCodec : YV12 [NV12] (MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)

The INPUT stream should have the color format YUV420Planar. Or use '-pix_fmt yuv420p' option with INPUT.<br>
The MediaCodec of your device should have the color format YV12 [NV12]. (Other formats will be supported later)

## Screenshot
<p align="center">
  <img src="./FFmpegMC264Demo-Screen.png" width="350" height="720">
</p>

## Referenced Links :
* MediaCodec example :
  - https://android.googlesource.com/platform/cts/+/jb-mr2-release/tests/tests/media/src/android/media/cts/EncodeDecodeTest.java
* Handling H.264 SPS / PPS :
  - https://github.com/Kickflip/kickflip-android-sdk/blob/master/sdk/src/main/java/io/kickflip/sdk/av/FFmpegMuxer.java
* https://stackoverflow.com/questions/24884827/possible-locations-for-sequence-picture-parameter-sets-for-h-264-stream/24890903#24890903
* https://stackoverflow.com/questions/20909252/calculate-pts-before-frame-encoding-in-ffmpeg
* http://leo.ugr.es/elvira/devel/Tutorial/Java/native1.1/implementing/index.html
* Color Format :
  - https://software.intel.com/en-us/ipp-dev-reference-pixel-and-planar-image-formats#FIG6-15
  - https://developer.android.com/reference/android/graphics/ImageFormat.html#YV12
  - https://en.wikipedia.org/wiki/YUV
* VideoKit :
  - https://github.com/inFullMobile/videokit-ffmpeg-android
  - https://github.com/IljaKosynkin/FFmpeg-Development-Kit
* Cross Compiling FFmpeg 4.0 for Android :
  - https://medium.com/@karthikcodes1999/cross-compiling-ffmpeg-4-0-for-android-b988326f16f2


