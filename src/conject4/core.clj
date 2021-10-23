(ns conject4.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  []
  (println "Hello, World!"))

(defn create-empty-board [x-size y-size]
  {:height y-size :width x-size :board {}})

(defn get-board-height [board]
  (:height board))

(defn get-board-width [board]
  (:width board))

(defn get-position-index [board x-pos y-pos]
  (+ (* y-pos (get-board-width board)) x-pos))

(defn get-piece-in-position [board x-pos y-pos]
  (get (:board board) (get-position-index board x-pos y-pos) :empty)
  )

(defn is-piece-in-position? [board x-pos y-pos piece]
  (= (get-piece-in-position board x-pos y-pos) piece))

(defn is-position-empty? [board x-pos y-pos]
  (is-piece-in-position? board x-pos y-pos :empty))

(def is-position-filled? (complement is-position-empty?))


(defn is-position-valid? [board x-pos]
  (not
    (or
      (neg? x-pos)
      (>= x-pos (get-board-width board))
      (is-position-filled? board x-pos (dec (get-board-height board)))
      )
    )
  )

(defn place-counter [board x-pos y-pos counter-colour]
  (if (= counter-colour :empty)
    (update-in board [:board] dissoc (get-position-index board x-pos y-pos))
    (assoc-in board [:board (get-position-index board x-pos y-pos)] counter-colour))
  )

(defn get-lowest-empty-in-column [board x-pos]
  (loop [current-row-index 0]
    (if (is-position-empty? board x-pos current-row-index)
      current-row-index
      (recur (inc current-row-index)))))

(defn place-counter-in-column [board x-pos counter-colour]
  (place-counter board x-pos (get-lowest-empty-in-column board x-pos) counter-colour))

(defn apply-move
  ([board x-pos counter-colour]
  (if (is-position-valid? board x-pos)
    {:board (place-counter-in-column board x-pos counter-colour) :is-valid-move true}
    {:board board :is-valid-move false}
    ))
  ([board [x-pos counter-colour]]
   (apply-move board x-pos counter-colour))
  )

(defn next-counter [starts-with]
  (lazy-seq (cycle [starts-with (if (= starts-with :red) :yellow :red)])))

(defn apply-moves [board x-positions start-counter]
  (reduce #(:board (apply-move %1 %2))
          board
          (map vector x-positions (next-counter start-counter))))

(defn find-valid-moves [board]
  (filter #(is-position-valid? board %) (range (get-board-width board)))
  )

(defn print-board [{:keys [width height] :as full-board}]
  (reverse
    (map
      (fn [y-pos] (map
                    (fn [x-pos] (get-piece-in-position full-board x-pos y-pos))
                    (range width)))
      (range height)))
  )
