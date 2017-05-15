(r "接下来要做的是什么呢?
1. Todos 设置时间选择范围
2. 词性的标注用map nature, n星的名词都可以,只显示名词
3. mcmc 生成泊松过程和伽马分布图
4. hanlping 加入静态的warn提示,来加上type
")

;; 直接可以输入的任意内容
(defmacro r1
  [& lst]
  (r (str lst)))
;; (r1 接下来要做的是什么 Todos 设置时间选择范围) ;;=> "(接下来要做的是什么 Todos 设置时间选择范围)"

