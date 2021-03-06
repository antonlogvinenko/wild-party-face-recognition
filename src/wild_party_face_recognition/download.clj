(ns wild-party-face-recognition.download
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]))

(defn- get-image-urls [photoreport-url]
  (let [as (-> photoreport-url
               slurp java.io.StringReader. html/html-resource
               (html/select [:#eventPeriodsPhotoreportImages (html/attr? :href)]))]
    (->> as (map :attrs) (map :href))))

(defn- get-image-bytes [image-url dest name]
  (Thread/sleep 2000)
  (with-open [in (io/input-stream image-url)
              out (io/output-stream (str dest name ".jpg"))]
    (io/copy in out)))

(defn download-images [photoreport-url dest]
  (let [image-urls (get-image-urls photoreport-url)]
    (map #(get-image-bytes (nth image-urls %) dest %) 
     (-> image-urls count range))))
