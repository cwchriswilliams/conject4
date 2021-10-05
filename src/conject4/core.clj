(ns conject4.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn create-empty-board [x-size y-size]
  (vec (repeat y-size (vec (repeat x-size :empty)))))

(defn is-position-valid? [board x-pos]
  (not
    (or
      (neg? x-pos)
      (>= x-pos (count board))
      (not= :empty (get (get board 0) x-pos))
      )
    )
  )

(defn place-counter [board x-pos y-pos]
  (assoc board y-pos (assoc (get board x-pos) x-pos :red))
  )

(defn apply-move [board x-pos]
  (if (is-position-valid? board x-pos)
    {:board board :is-valid-move true}
    {:board board :is-valid-move false}
    ))
