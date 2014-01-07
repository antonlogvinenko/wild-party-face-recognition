(ns wild-party-face-recognition.detect
  (:import
   org.opencv.core.Core
   org.opencv.core.Mat
   org.opencv.core.MatOfRect
   org.opencv.core.Point
   org.opencv.core.Rect
   org.opencv.core.Scalar
   org.opencv.highgui.Highgui
   org.opencv.objdetect.CascadeClassifier))

;;Basic face detection - http://nils-blum-oeste.net/image-analysis-with-clojure-up-and-running-with-opencv/
;;+ eyes http://docs.opencv.org/doc/tutorials/objdetect/cascade_classifier/cascade_classifier.html

(clojure.lang.RT/loadLibrary Core/NATIVE_LIBRARY_NAME)

(defn create-resource [name]
  (do  (-> name clojure.java.io/resource .getPath CascadeClassifier.)))

;lbpcascade_frontalface.xml
;haarcascade_frontalface_alt2.xml
;haarcascade_frontalface_default.xml
(def faces-classifier (create-resource "haarcascade_frontalface_default.xml"))

;haarcascade_eye.xml
(def eyes-classifier (create-resource  "haarcascade_eye.xml"))

(defn detect-faces [image]
  (let [face-detections (MatOfRect.)]
    (.detectMultiScale faces-classifier image face-detections)
    face-detections))

(defn make-rect [image rect x y color]
  (Core/rectangle image
                  (Point. (+ x (.x rect)) (+ y (.y rect)))
                  (Point. (+ x (.x rect) (.width rect))
                          (+ y (.y rect) (.height rect)))
                  (Scalar. 0 color 0)))

(defn cake [image [f-rect e-rect]]
  (make-rect image f-rect 0 0 250)
  (doall (for [eye (-> e-rect .toArray seq)]
           (make-rect image eye (.x f-rect) (.y f-rect) 100))))

(defn draw-bounding-boxes [image to-file face-detections]
  (doall (map (partial cake image) face-detections))
  (Highgui/imwrite to-file image))

(defn detect-eyes [image rect]
  (let [eyes-detections (MatOfRect.)
        face-img (.submat image rect)]
    (.detectMultiScale eyes-classifier face-img eyes-detections)
    [rect eyes-detections]))

(defn process-and-save-image [in out]
  (let [image (Highgui/imread in)]
    (->> image
         detect-faces
         .toArray
         (map (partial detect-eyes image))
         (draw-bounding-boxes image out))))

(defn main [fin fout]
  (process-and-save-image fin fout))

(defn find [name]
  (let [path "/Users/anton/Desktop/"]
    (main (str path "dest/" name ".jpg") (str path "out/" name ".png"))))
