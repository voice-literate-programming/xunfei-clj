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
           [org.json JSONArray JSONObject JSONTokener]))

;; 讯飞初始化: (app-init "59145fb0") , 可以自行到讯飞开放平台注册一个appid, 或者用本人的测试
(defn app-init
  [appid]
  (let [appid (str SpeechConstant/APPID "=" appid)]
    (SpeechUtility/createUtility appid)))

;; 设置合成监听器,对SynthesizerListener进行proxy,添加对象属性控制
(defn m-syn-listener-gen
  []
  (proxy [SynthesizerListener] []
    (onCompleted [_])
    (onBufferProgress [^Integer percent  ^Integer begin-pos  ^Integer end-pos ^String info])
    (onSpeakBegin [])
    (onSpeakPaused [])
    (onSpeakProgress [^Integer percent ^Integer begin-pos ^Integer end-pos])
    (onSpeakResumed [])))

;; (read-text-as-voice "输入文本,用讯飞语音合成器, 合成发音播放" (fn [mTts text] ...播放或者是保存到音频文件...) )
(defn read-text-to-voice
  [text output-fn]
  (let [m-tts (doto (SpeechSynthesizer/createSynthesizer)
                (.setParameter SpeechConstant/VOICE_NAME "xiaoyan")
                (.setParameter SpeechConstant/SPEED "50")
                (.setParameter SpeechConstant/VOLUME "80")
                (.setParameter SpeechConstant/TTS_AUDIO_PATH "./tts_test.pcm"))]
    (output-fn m-tts text)))

;; (text-to-player "这里是文本播放语音")
(defn text-to-player
  [text]
  (read-text-to-voice
   text
   (fn [m-tts text] (.startSpeaking m-tts text (m-syn-listener-gen)))))

;; 将text合成的语音保存到文件的合成器
(defn synthesize-to-uri-listener
  []
  (proxy [SynthesizeToUriListener] []
    (onBufferProgress [^Integer progress])
    (onSynthesizeCompleted [^String uri ^SpeechError error])))

;; (text-to-vfile "将text合成的语音保存到文件" "testest.wav")
(defn text-to-vfile
  [text url]
  (read-text-to-voice
   text
   (fn [m-tts text]
     (.synthesizeToUri m-tts text url (synthesize-to-uri-listener)))))

;; =======>>>> 下面是语音识别生成文本 ====>>>>>

;; 语音识别监听器Usage:
;; (def regcog-res (atom (list)))
;; (m-reco-listener #(swap! regcog-res conj %))
(defn m-reco-listener
  [result-fn]
  (proxy [RecognizerListener] []
    (onResult [^RecognizerResult results ^Boolean is-last]
      (let [res (-> results .getResultString cjson/parse-string)]
        (println "识别语音结果:=>" res)
        (result-fn res)))
    (onError [^SpeechError error] (.getPlainDescription error true) )
    (onBeginOfSpeech [])
    (onVolumeChanged [^Integer volume])
    (onEndOfSpeech [])
    (onEvent [^Integer eventType ^Integer arg1 ^Integer arg2 ^String msg])))

;; (record-voice-to-text)
(defn record-voice-to-text
  []
  (let [m-iat
        (doto (SpeechRecognizer/createRecognizer)
          (.setParameter SpeechConstant/DOMAIN "iat")
          (.setParameter SpeechConstant/LANGUAGE "zh_cn")
          (.setParameter SpeechConstant/ACCENT "mandarin"))]
    (.startListening m-iat (m-reco-listener))))
