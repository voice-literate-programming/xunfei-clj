(ns xunfei-clj.core
  (:require [cheshire.core :as cjson])
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
;; 存放语音识别的结果列表,异步消费remove掉,这里只是暂存的中间过程
(def regcog-res (atom (list)))
;; 例子=> ({"sn" 2, "ls" true, "bg" 0, "ed" 0, "ws" [{"bg" 0, "cw" [{"sc" 0.0, "w" "。"}]}]} {"sn" 1, "ls" false, "bg" 0, "ed" 0, "ws" [{"bg" 0, "cw" [{"sc" 0.0, "w" "哈"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "哈哈"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "，"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "这里"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "新人"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "吃"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "蒂"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "夫"}]} {"bg" 0, "cw" [{"sc" 0.0, "w" "乔布斯"}]}]})

;; 路由监听器
(defn mRecoListener
  []
  (proxy [RecognizerListener] []
    (onResult [^RecognizerResult results ^Boolean isLast]
      (let [res (-> results .getResultString cjson/parse-string)]
        (println "识别语音结果:=>" res)
        (swap! regcog-res conj res)
        )
      )
    (onError [^SpeechError error] (.getPlainDescription error true) )
    (onBeginOfSpeech [])
    (onVolumeChanged [^Integer volume])
    (onEndOfSpeech [])
    (onEvent [^Integer eventType ^Integer arg1 ^Integer arg2 ^String msg])
    )
  )

;; (record-voice-to-text)
;; 语音识别生成文本打印出来
(defn record-voice-to-text
  []
  (let [mIat (SpeechRecognizer/createRecognizer)
        _ (.setParameter mIat SpeechConstant/DOMAIN "iat")
        _ (.setParameter mIat SpeechConstant/LANGUAGE "zh_cn")
        _ (.setParameter mIat SpeechConstant/ACCENT "mandarin")]
    (.startListening mIat (mRecoListener))
    )
  )

;; For Emacs API ;;;;;;;;;;;
(defn r [text] (text-to-player text))

;; (read-do (r "中线是什么") (r "什么数学算法模型"))
;; 读=> "00000下面是:第1段 中线是什么11111本段结束 00000下面是:第2段 什么数学算法模型11111本段结束"
(defmacro read-do
  [& lst]
  (r
   (clojure.string/join
    " "
    (flatten
     (map-indexed
      (fn [inx# li#]
        (list
         (str "00000下面是:第" (+ inx# 1) "段")
         (str (last li#) "本段结束11111" ))) lst)))))

;; 读任何的输入: (r1 接下来要做的是什么 Todos 设置时间选择范围) 
(defmacro r1
  [& lst]
  (r (str lst)))

