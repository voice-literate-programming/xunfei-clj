(ns xunfei-clj.core
  (:import [com.iflytek.cloud.speech
            SpeechRecognizer
            SpeechConstant
            SpeechUtility
            SpeechSynthesizer
            SynthesizerListener]
           [org.json JSONArray JSONObject JSONTokener]
           ))

(def appid (str SpeechConstant/APPID "=59145fb0"))

(def app-init (SpeechUtility/createUtility appid))

;; 设置合成监听器,对SynthesizerListener进行proxy,添加对象属性控制
;; (.onSpeakProgress (mSynListenerGen) 1 1 1) ;;=> nil
(defn mSynListenerGen
  []
  (proxy [SynthesizerListener] []
    (onCompleted [_])
    (onBufferProgress [^Integer percent  ^Integer beginPos  ^Integer endPos ^String info])
    (onSpeakBegin [])
    (onSpeakPaused [])
    (onSpeakProgress [^Integer percent ^Integer beginPos ^Integer endPos])
    (onSpeakResumed [])
    )
  )

;; (read-text-as-voice "输入文本,用讯飞语音合成器, 合成发音播放")
(defn read-text-as-voice
  [text]
  (let [mTts (SpeechSynthesizer/createSynthesizer)
        _ (.setParameter mTts SpeechConstant/VOICE_NAME "xiaoyan")
        _ (.setParameter mTts SpeechConstant/SPEED "50")
        _ (.setParameter mTts SpeechConstant/VOLUME "80")
        _ (.setParameter mTts SpeechConstant/TTS_AUDIO_PATH "./tts_test.pcm")
        ]
    ;; Ubuntu: 开始合成, 测试文本,合成读音ok:-)
    (.startSpeaking mTts text (mSynListenerGen))
    )
  )
