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

(def face-detections (atom []))

(defn create-classifier []
  (-> "lbpcascade_frontalface.xml" clojure.java.io/resource .getPath CascadeClassifier.))

(defn load-image [path]
  (Highgui/imread path))

(defn detect-faces! [classifier image]
  (.detectMultiScale classifier image @face-detections))

(defn draw-bounding-boxes! [image to-file]
  (doall (map (fn [rect]
                (Core/rectangle image
                                (Point. (.x rect) (.y rect))
                                (Point. (+ (.x rect) (.width rect))
                                        (+ (.y rect) (.height rect)))
                                (Scalar. 0 255 0)))
              (.toArray @face-detections)))
  (Highgui/imwrite to-file image))

(defn process-and-save-image! [in out]
  (let [image (load-image in)]
    (detect-faces! (create-classifier) image)
    (draw-bounding-boxes! image out)))

(defn find [name]
  (let [path "/Users/anton/Desktop/"]
    (main (str path "dest/" name ".jpg") (str path "out/" name ".png"))))

(defn main [fin fout]
  (clojure.lang.RT/loadLibrary Core/NATIVE_LIBRARY_NAME)
  (reset! face-detections (MatOfRect.))
  (process-and-save-image! fin fout))
