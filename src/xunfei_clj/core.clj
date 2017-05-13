(ns xunfei-clj.core
  (:import [com.iflytek.cloud.speech
            SpeechRecognizer
            SpeechConstant
            SpeechUtility
            SpeechSynthesizer
            SynthesizerListener
            SynthesizeToUriListener
            SpeechError
            RecognizerListener
            RecognizerResult]
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

;; (read-text-as-voice "输入文本,用讯飞语音合成器, 合成发音播放" (fn [mTts text] ...播放或者是保存到音频文件...) )
(defn read-text-to-voice
  [text output-fn]
  (let [mTts (SpeechSynthesizer/createSynthesizer)
        _ (.setParameter mTts SpeechConstant/VOICE_NAME "xiaoyan")
        _ (.setParameter mTts SpeechConstant/SPEED "50")
        _ (.setParameter mTts SpeechConstant/VOLUME "80")
        _ (.setParameter mTts SpeechConstant/TTS_AUDIO_PATH "./tts_test.pcm")]
    ;; Ubuntu: 开始合成, 测试文本,合成读音ok:-)
    (output-fn mTts text)
    )
  )

;; (text-to-player "这里是文本播放语音")
(defn text-to-player
  [text]
  (read-text-to-voice
   text
   (fn [mTts text] (.startSpeaking mTts text (mSynListenerGen)))))

;; 将text合成的语音保存到文件的合成器
(defn synthesizeToUriListener
  []
  (proxy [SynthesizeToUriListener] []
    (onBufferProgress [^Integer progress])
    (onSynthesizeCompleted [^String uri ^SpeechError error])
    )
  )

;; (text-to-vfile "将text合成的语音保存到文件" "testest.wav")
;;todos: 可生成testest.wav文件,并且输入文本越长则文件越大,不知为什么不能播放
(defn text-to-vfile
  [text url]
  (read-text-to-voice
   text
   (fn [mTts text]
     (.synthesizeToUri mTts text url (synthesizeToUriListener)))))

;; =======>>>> 下面是语音识别生成文本 ====>>>>>
;; 路由监听器
(defn mRecoListener
  []
  (proxy [RecognizerListener] []
    (onResult [^RecognizerResult results ^Boolean isLast]
      (println "识别语音结果:=>" (.getResultString results)) )
    (onError [^SpeechError error] (.getPlainDescription error true) )
    (onBeginOfSpeech [])
    (onVolumeChanged [^Integer volume])
    (onEndOfSpeech [])
    (onEvent [^Integer eventType ^Integer arg1 ^Integer arg2 ^String msg])
    )
  )

;; (read-text-to-voice)
;; 语音识别生成文本打印出来
(defn read-text-to-voice
  []
  (let [mIat (SpeechRecognizer/createRecognizer)
        _ (.setParameter mIat SpeechConstant/DOMAIN "iat")
        _ (.setParameter mIat SpeechConstant/LANGUAGE "zh_cn")
        _ (.setParameter mIat SpeechConstant/ACCENT "mandarin")]
    (.startListening mIat (mRecoListener))
    )
  )
