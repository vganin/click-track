To generate raw PCM file for testing, use the following example command for ffmpeg which generates signed 16-bit little-endian encoded file from the source WAV:
```bash
ffmpeg -i source_u8.wav -f s16le s16le.pcm
```

For full list of available PCM formats, use the following command:
```bash
ffmpeg -codecs | grep pcm_
```
