//
// Created by feiyu on 2019/2/13.
//
#include <jni.h>
#include <string>
#include "./header/native-ffmpeg.h"
#include <jni.h>
#include <string>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <unistd.h>
#include <android/log.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"TAG",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"TAG",FORMAT,##__VA_ARGS__);

extern "C" {
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libavfilter/avfilter.h"
#include "libavutil/imgutils.h"
#include "libavutil/avutil.h"
#include "libavfilter/buffersink.h"
#include "libavfilter/buffersrc.h"
#include "libavcodec/avcodec.h"
//重采样
#include "libswresample/swresample.h"
}

#define MAX_AUDIO_FRME_SIZE 48000 * 4

extern "C" JNIEXPORT void JNICALL
Java_com_charles_crazyguy_activities_PlayVideoActivity_playVideo(
        JNIEnv *env,
        jobject jthiz, jstring path_, jobject view, jstring output_) {
    LOGE("%s", "play()");
    const char *path = env->GetStringUTFChars(path_, 0);
    LOGE("%s", path);
    //注册所有的编解码器
    LOGE("%s", "av_register_all()");
    av_register_all();
    int ret;
    //封装格式上线文
    AVFormatContext *fmt_ctx = avformat_alloc_context();
    //打开输入流并读取头文件。此时编解码器还没有打开
    LOGE("%s", "start avformat_open_input");
    int err_code;
    char *buf = new char[1024];
    if ((err_code = avformat_open_input(&fmt_ctx, path, NULL, NULL)) < 0) {
        av_strerror(err_code, buf, 1024);
        LOGE("Couldn't open file %s: %d(%s)", path, err_code, buf);
        return;
    }
    LOGE("%s", "avformat_open_input success()");
    //获取信息
    if (avformat_find_stream_info(fmt_ctx, NULL) < 0) {
        return;
    }
    //获取视频流的索引位置
    int video_stream_index = -1;
    int audio_stream_index = -1;
    for (int i = 0; i < fmt_ctx->nb_streams; i++) {
        if (fmt_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream_index = i;
            LOGE("找到视频流索引位置video_stream_index=%d", video_stream_index);
        } else if (fmt_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audio_stream_index = i;
            LOGE("找到音频流流索引位置video_stream_index=%d", video_stream_index);
        }
    }
    if (video_stream_index == -1) {
        LOGE("未找到视频流索引");
    }

    if(audio_stream_index == -1) {
        LOGE("未找到音频流索引");
    }
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, view);
    if (nativeWindow == NULL) {
        LOGE("ANativeWindow_fromSurface error");
        return;
    }
    //绘制时候的缓冲区
    ANativeWindow_Buffer outBuffer;
    //获取视频流解码器
    AVCodecContext *codec_ctx = avcodec_alloc_context3(NULL);
    avcodec_parameters_to_context(codec_ctx, fmt_ctx->streams[video_stream_index]->codecpar);
    AVCodec *avCodec = avcodec_find_decoder(codec_ctx->codec_id);
    //打开解码器
    if ((ret = avcodec_open2(codec_ctx, avCodec, NULL)) < 0) {
        ret = -3;
        return;
    }

    //获取视频流解码器
    AVCodecContext *audio_codec_ctx = avcodec_alloc_context3(NULL);
    avcodec_parameters_to_context(audio_codec_ctx, fmt_ctx->streams[audio_stream_index]->codecpar);
    AVCodec *audioAcCodec = avcodec_find_decoder(audio_codec_ctx->codec_id);
    //打开解码器
    if ((ret = avcodec_open2(audio_codec_ctx, audioAcCodec, NULL)) < 0) {
        ret = -3;
        return;
    }

    //循环从文件读取一帧压缩数据
    //开始读取视频
    int y_size = codec_ctx->width * codec_ctx->height;
    AVPacket *pkt = (AVPacket *) malloc(sizeof(AVPacket));//分配一个packet
    av_new_packet(pkt, y_size);//分配packet的数据
    AVFrame *yuvFrame = av_frame_alloc();
    AVFrame *rgbFrame = av_frame_alloc();
    //解压缩数据
    AVFrame *frame = av_frame_alloc();
    // 颜色转换器
    SwsContext *m_swsCtx = sws_getContext(codec_ctx->width, codec_ctx->height, codec_ctx->pix_fmt,
                                          codec_ctx->width,
                                          codec_ctx->height, AV_PIX_FMT_RGBA, SWS_BICUBIC, NULL,
                                          NULL, NULL);


    LOGE("开始解码");
    int index = 0;
    //frame->16bit 44100 PCM 统一音频采样格式与采样率
    SwrContext *swrCtx = swr_alloc();
    //16bit 44100 PCM 数据
    uint8_t *out_buffer = (uint8_t *)av_malloc(MAX_AUDIO_FRME_SIZE);
    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO;
    //输出采样格式16bit PCM
    enum AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16;
    //输入采样率
    int in_sample_rate = audio_codec_ctx->sample_rate;
    //输出采样率
    int out_sample_rate = in_sample_rate;
    LOGE("sample rate : %d", out_sample_rate)
    uint64_t in_ch_layout = audio_codec_ctx->channel_layout;
    //输入的采样格式
    enum AVSampleFormat in_sample_fmt = audio_codec_ctx->sample_fmt;
    swr_alloc_set_opts(swrCtx,
                       out_ch_layout,out_sample_fmt,out_sample_rate,
                       in_ch_layout,in_sample_fmt,in_sample_rate,
                       0, NULL);
    swr_init(swrCtx);
    //输出的声道个数
    int out_channel_nb = av_get_channel_layout_nb_channels(out_ch_layout);
    //JasonPlayer
    jclass player_class = env->GetObjectClass(jthiz);
    jmethodID create_audio_track_mid = env->GetMethodID(player_class,"createAudioTrack","(II)Landroid/media/AudioTrack;");
    jobject audio_track = env->CallObjectMethod(jthiz,create_audio_track_mid,out_sample_rate,out_channel_nb);

    //调用AudioTrack.play方法
    jclass audio_track_class = env->GetObjectClass(audio_track);
    jmethodID audio_track_play_mid = env->GetMethodID(audio_track_class,"play","()V");
    env->CallVoidMethod(audio_track, audio_track_play_mid);

    //AudioTrack.write
    jmethodID audio_track_write_mid = env->GetMethodID(audio_track_class,"write","([BII)I");
    //JNI end------------------
    const char* output_cstr = env->GetStringUTFChars(output_,NULL);
    FILE *fp_pcm = fopen(output_cstr,"wb");

    while (1) {
        if (av_read_frame(fmt_ctx, pkt) < 0) {
            //这里就认为视频读完了
            break;
        }
        if (pkt->stream_index == video_stream_index) {
            //视频解码
            ret = avcodec_send_packet(codec_ctx, pkt);
            if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGE("avcodec_send_packet ret=%d", ret);
                av_packet_unref(pkt);
                continue;
            }
            //从解码器返回解码输出数据
            int ret = avcodec_receive_frame(codec_ctx, yuvFrame);
            if (ret < 0 && ret != AVERROR_EOF) {
                LOGE("avcodec_receive_frame ret=%d", ret);
                av_packet_unref(pkt);
                continue;
            }
            //avcodec_decode_video2(codec_ctx,yuvFrame,&got_pictue,&pkt);
            sws_scale(m_swsCtx, (const uint8_t *const *) yuvFrame->data, yuvFrame->linesize, 0,
                      codec_ctx->height, rgbFrame->data, rgbFrame->linesize);
            //设置缓冲区的属性
            ANativeWindow_setBuffersGeometry(nativeWindow, codec_ctx->width, codec_ctx->height,
                                             WINDOW_FORMAT_RGBA_8888);
            ret = ANativeWindow_lock(nativeWindow, &outBuffer, NULL);
            if (ret != 0) {
                LOGE("ANativeWindow_lock error");
                return;
            }
            av_image_fill_arrays(rgbFrame->data, rgbFrame->linesize,
                                 (const uint8_t *) outBuffer.bits, AV_PIX_FMT_RGBA,
                                 codec_ctx->width, codec_ctx->height, 1);
            //fill_ANativeWindow(&outBuffer,outBuffer.bits,rgbFrame);
            //将缓冲区数据显示到surfaceView
            ret = ANativeWindow_unlockAndPost(nativeWindow);
            if (ret != 0) {
                LOGE("ANativeWindow_unlockAndPost error");
                return;
            }
            LOGE("成功显示到缓冲区%d次", ++index);
            av_packet_unref(pkt);
            usleep(1000 * 8);
        } else if(pkt->stream_index == audio_stream_index){
            ret = avcodec_send_packet(audio_codec_ctx, pkt);
            if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGE("avcodec_send_packet ret=%d", ret);
                av_packet_unref(pkt);
                continue;
            }
            //解码
            ret = avcodec_receive_frame(audio_codec_ctx,frame);
            if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGE("avcodec_send_packet ret=%d", ret);
                av_packet_unref(pkt);
                continue;
            }

            //解码一帧成功
            LOGE("解码：%d",index++);
            swr_convert(swrCtx, &out_buffer, MAX_AUDIO_FRME_SIZE,(const uint8_t **)frame->data,frame->nb_samples);
            //获取sample的size
            LOGE("av_samples_get_buffer_size : %d, %d, %d", out_channel_nb, frame->nb_samples, out_sample_fmt);
            int out_buffer_size = av_samples_get_buffer_size(NULL, out_channel_nb,
                                                             frame->nb_samples==0?1:frame->nb_samples, out_sample_fmt, 1);
            LOGE("out buffer size : %d", out_buffer_size)
            fwrite(out_buffer,1,out_buffer_size,fp_pcm);

            //out_buffer缓冲区数据，转成byte数组
            jbyteArray audio_sample_array = env->NewByteArray(out_buffer_size);
            jbyte* sample_bytep = env->GetByteArrayElements(audio_sample_array,NULL);
            //out_buffer的数据复制到sampe_bytep
            memcpy(sample_bytep,out_buffer,out_buffer_size);
            //同步
            env->ReleaseByteArrayElements(audio_sample_array,sample_bytep,0);

            //AudioTrack.write PCM数据
            env->CallIntMethod(audio_track,audio_track_write_mid,
                                  audio_sample_array,0,out_buffer_size);
            //释放局部引用
            env->DeleteLocalRef(audio_sample_array);

            av_packet_unref(pkt);
            usleep(1000 * 8);
        }
    }

    av_frame_free(&rgbFrame);
    avcodec_close(codec_ctx);
    avcodec_close(audio_codec_ctx);
    sws_freeContext(m_swsCtx);
    avformat_close_input(&fmt_ctx);
    ANativeWindow_release(nativeWindow);
    env->ReleaseStringUTFChars(path_, path);
    LOGI("解析完成");
}

extern "C" JNIEXPORT void JNICALL
Java_com_charles_crazyguy_activities_PlayVideoActivity_stopVideo(
        JNIEnv *env,
        jobject /* this */) {

}