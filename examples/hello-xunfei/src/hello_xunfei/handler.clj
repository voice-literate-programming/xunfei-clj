(ns hello-xunfei.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [xunfei-clj.core :as xunfei]))

(def xunfei-init (xunfei/app-init "your-xunfei-appid"))

(defroutes app-routes
  (GET "/xunfei/:text" [text] (do (xunfei/text-to-player text) (str "读取成功:" text) ))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
