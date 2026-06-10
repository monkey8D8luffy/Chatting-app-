package com.kizuna.webrtc

import android.content.Context
import org.webrtc.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class WebRTCManager(private val context: Context) {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var rootEglBase: EglBase? = null

    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null

    private val _iceCandidates = MutableSharedFlow<IceCandidate>(replay = 10)
    val iceCandidates: SharedFlow<IceCandidate> = _iceCandidates

    private val _offers = MutableSharedFlow<SessionDescription>(replay = 1)
    val offers: SharedFlow<SessionDescription> = _offers

    private val _answers = MutableSharedFlow<SessionDescription>(replay = 1)
    val answers: SharedFlow<SessionDescription> = _answers

    private val _remoteVideoTrack = MutableSharedFlow<VideoTrack>(replay = 1)
    val remoteVideoTrack: SharedFlow<VideoTrack> = _remoteVideoTrack

    fun initialize() {
        rootEglBase = EglBase.create()
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        val options = PeerConnectionFactory.Options()
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(rootEglBase?.eglBaseContext, true, true))
            .createPeerConnectionFactory()

        createPeerConnection()
    }

    fun getEglBaseContext(): EglBase.Context? {
        return rootEglBase?.eglBaseContext
    }

    private fun createPeerConnection() {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

        peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onIceCandidate(candidate: IceCandidate) {
                _iceCandidates.tryEmit(candidate)
            }
            override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<out MediaStream>) {
                val track = receiver.track()
                if (track is VideoTrack) {
                    _remoteVideoTrack.tryEmit(track)
                }
            }
            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
            override fun onIceConnectionReceivingChange(p0: Boolean) {}
            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
            override fun onDataChannel(p0: DataChannel?) {}
            override fun onRenegotiationNeeded() {}
            override fun onAddStream(p0: MediaStream?) {}
            override fun onRemoveStream(p0: MediaStream?) {}
        })
    }

    fun createOffer() {
        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                peerConnection?.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        _offers.tryEmit(sdp)
                    }
                    override fun onCreateSuccess(p0: SessionDescription?) {}
                    override fun onCreateFailure(p0: String?) {}
                    override fun onSetFailure(p0: String?) {}
                }, sdp)
            }
            override fun onCreateFailure(p0: String?) {}
            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String?) {}
        }, MediaConstraints())
    }

    fun handleOffer(offer: SessionDescription) {
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                peerConnection?.createAnswer(object : SdpObserver {
                    override fun onCreateSuccess(answer: SessionDescription) {
                        peerConnection?.setLocalDescription(object : SdpObserver {
                            override fun onSetSuccess() {
                                _answers.tryEmit(answer)
                            }
                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                            override fun onSetFailure(p0: String?) {}
                        }, answer)
                    }
                    override fun onCreateFailure(p0: String?) {}
                    override fun onSetSuccess() {}
                    override fun onSetFailure(p0: String?) {}
                }, MediaConstraints())
            }
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }, offer)
    }

    fun handleAnswer(answer: SessionDescription) {
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {}
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }, answer)
    }

    fun handleIceCandidate(candidate: IceCandidate) {
        peerConnection?.addIceCandidate(candidate)
    }

    fun startLocalVideo(context: Context, surfaceViewRenderer: SurfaceViewRenderer) {
        val videoCapturer = createVideoCapturer(context) ?: return
        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase?.eglBaseContext)
        val videoSource = peerConnectionFactory?.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource?.capturerObserver)
        videoCapturer.startCapture(1280, 720, 30)

        localVideoTrack = peerConnectionFactory?.createVideoTrack("local_video", videoSource)
        localVideoTrack?.addSink(surfaceViewRenderer)

        peerConnection?.addTrack(localVideoTrack)

        // Setup Audio
        val audioSource = peerConnectionFactory?.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory?.createAudioTrack("local_audio", audioSource)
        peerConnection?.addTrack(localAudioTrack)
    }

    private fun createVideoCapturer(context: Context): VideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        return null
    }

    fun disconnect() {
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        peerConnection?.close()
        peerConnectionFactory?.dispose()
        rootEglBase?.release()
    }
}