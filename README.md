# xunfei-clj

Clojure封装讯飞语音SDK, 可提供给Emacs/Vim编辑器使用,或者命令行, 实现语音朗读提醒/语音识别/语音转为命令等

*目前只支持Linux和Windows系统,因为讯飞官方SDK暂未支持Mac*

## Usage: [查看使用示例hello-xunfei](./examples/hello-xunfei)
```clojure
;; 1. add to project.clj.
[xunfei-clj "0.1.0-SNAPSHOT"]

;; 2. add Msc.jar to project's lib path, then add `:resource-paths` option.
:resource-paths ["lib/Msc.jar"]

;; 3. copy libmsc64.so(windows: msc64.dll) & libmsc32.so(windows: msc32.dll) to your project root path.

;; 4. core.clj:
(ns hello-xunfei.core
  (:require [xunfei-clj.core :as xunfei]))

(defn xunfei-say-hi
  [text]
  (xunfei/text-to-player text))
```

## Develop

```bash
$ lein repl 

xunfei-clj.core> 
```

```clojure
;; 语音朗读
xunfei-clj.core> (r "什么语音文学驱动编程?")

;; 语音识别
xunfei-clj.core> (record-voice-to-text)

```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
