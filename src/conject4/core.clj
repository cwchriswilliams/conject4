(ns conject4.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
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

(defn apply-move [board x-pos counter-colour]
  (if (is-position-valid? board x-pos)
    {:board (place-counter board x-pos (get-lowest-empty-in-column board x-pos) counter-colour) :is-valid-move true}
    {:board board :is-valid-move false}
    ))

(defn print-board [board]
  (loop [board board
         current-idx 0
         printable-board []
         board-width (get-board-width board)
         max-idx (* (get-board-height board) board-width)]
    (if (= current-idx max-idx)
      (vec (reverse printable-board))
      (if (= (mod current-idx board-width) 0)
        (recur board
               (inc current-idx)
               (conj printable-board [(get (:board board) current-idx :empty)])
               board-width
               max-idx)
        (recur board
               (inc current-idx)
               (update printable-board (- (count printable-board) 1) #(conj % (get (:board board) current-idx :empty)))
               board-width
               max-idx))))
  )
