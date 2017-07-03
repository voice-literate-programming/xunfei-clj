# xunfei-clj

Clojure封装讯飞语音SDK,提供给Emacs语音调用接口, 通过[Cider](https://github.com/clojure-emacs/cider
)和Emacs通讯

## Usage

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
