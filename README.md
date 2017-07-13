# xunfei-clj

Clojure封装讯飞语音SDK, 可提供给Emacs/Vim编辑器使用,或者命令行, 实现语音朗读提醒/语音识别/语音转为命令等

*目前只支持Linux和Windows系统,因为讯飞官方SDK暂未支持Mac*

## Usage: [查看使用示例hello-xunfei](./examples/hello-xunfei)
```clojure
;; 1. add to project.clj.
[xunfei-clj "0.1.4-SNAPSHOT"]

;; 2. add Msc.jar to project's lib path, then add `:resource-paths` option.
:resource-paths ["lib/Msc.jar"]

;; 3. copy libmsc64.so(windows: msc64.dll) & libmsc32.so(windows: msc32.dll) to your project root path.

;; 4. core.clj:
(ns hello-xunfei.core
  (:require [xunfei-clj.core :as xunfei]))

;; 讯飞初始化
(xunfei/app-init "your-xunfei-appid") ;; 可以自行到讯飞开放平台注册一个appid

;; 语音朗读
(defn xunfei-say-hi
  [text]
  (xunfei/text-to-player text))

;; 语音识别
(def regcog-res (atom (list)))
(xunfei/record-voice-to-text (fn [] (xunfei/m-reco-listener #(swap! regcog-res conj %))) )

```

## Develop

```bash
$ lein repl 

;; 讯飞初始化
xunfei-clj.core> (xunfei/app-init "your-xunfei-appid")
```

```clojure
;; 语音朗读
xunfei-clj.core> (text-to-player "什么语音文学驱动编程?")

;; 语音识别
xunfei-clj.core> (def regcog-res (atom (list)))
xunfei-clj.core> (record-voice-to-text (fn [] (m-reco-listener #(swap! regcog-res conj %))) )

```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
