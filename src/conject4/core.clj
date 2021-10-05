(ns conject4.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn create-empty-board [x-size y-size]
  (vec (repeat y-size (vec (repeat x-size :empty)))))

(defn get-board-height [board]
  (count board))

(defn get-board-width [board]
  (count (first board)))

(defn get-piece-in-position [board x-pos y-pos]
  (get (get (vec (reverse board)) y-pos) x-pos)
  )

(defn is-piece-in-position? [board x-pos y-pos piece]
  (= (get-piece-in-position board x-pos y-pos) piece)
  )

(defn is-position-empty? [board x-pos y-pos]
  (is-piece-in-position? board x-pos y-pos :empty))

(defn is-position-valid? [board x-pos]
  (not
    (or
      (neg? x-pos)
      (>= x-pos (get-board-width board))
      (not= :empty (get (get board 0) x-pos))
      )
    )
  )

(defn place-counter [board x-pos y-pos counter-colour]
  (assoc board (- (get-board-height board) y-pos 1) (assoc (get board x-pos) x-pos counter-colour))
  )

(defn get-lowest-empty-in-column [board x-pos]
  (loop [current-row-index 0]
    (if (is-position-empty? board x-pos current-row-index)
      current-row-index
      (recur (inc current-row-index))))
  )

(defn apply-move [board x-pos counter-colour]
  (if (is-position-valid? board x-pos)
    {:board (place-counter board x-pos (get-lowest-empty-in-column board x-pos) counter-colour) :is-valid-move true}
    {:board board :is-valid-move false}
    ))
