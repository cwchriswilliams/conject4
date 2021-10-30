(ns conject4.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  []
  (println "Hello, World!"))

(defn create-empty-board 
  "Creates an empty board of the specified size
  Arguments:
    - board width
    - board height
  Returns:
    - A map containing the attributes [width, height board]"
  [x-size y-size]
  {:height y-size :width x-size :board {}})

(defn get-board-height 
  "Gets the height of the provided board
  Arguments:
    - A game board
  Returns:
    - An integer representing the height of the board"
  [{height :height}]
  height)

(defn get-board-width 
"Gets the width of the provided board
  Arguments:
    - A game board
  Returns:
    - An integer representing the width of the board"
  [{width :width}]
  width)

(defn get-position-index 
  "Gets the zero-indexed index of the provided position in the board
  Arguments:
    - A game board
    - A target x position
    - A target y position
  Returns:
    - An integer representing the zero-index index of the provided position"
  [board x-pos y-pos]
  (+ (* y-pos (get-board-width board)) x-pos))

(defn get-piece-in-position 
  "Gets the current piece in the provided position in the board
  Arguments:
    - A game board
    - A target x position
    - A target y position
  Returns:
    - A keyword representing the current piece in position. One of [:empty :red :yellow]"
  [{current-layout :board :as board} x-pos y-pos]
  (get current-layout (get-position-index board x-pos y-pos) :empty)
  )

(defn is-piece-in-position?
  "Returns a value indicating whether the provided piece is in the provided position
  Arguments:
    - A game board
    - A target x position
    - A target y position
    - A keyword representing the piece to check for. One of [:empty :red :yellow]
  Returns:
    - true or false"
  [board x-pos y-pos piece]
  (= (get-piece-in-position board x-pos y-pos) piece))

(defn is-position-empty? 
  "Returns a value indicating whether the provided position is empty
  Arguments:
    - A game board
    - A target x position
    - A target y position
  Returns:
    - true or false"
  [board x-pos y-pos]
  (is-piece-in-position? board x-pos y-pos :empty))

(def is-position-filled?
  "Returns a value indicating whether the provided position is one of [:red :yellow]
  Arguments:
    - A game board
    - A target x position
    - A target y position
  Returns:
    - true or false"
 (complement is-position-empty?))

(defn is-position-valid?
  "Returns a value indicating whether a piece can be placed in provided column
  Arguments:
    - A game board
    - A target x column
  Returns:
    - true or false"
 [board x-pos]
  (and
    (not (neg? x-pos))
    (< x-pos (get-board-width board))
    (is-position-empty? board x-pos (dec (get-board-height board)))
    )
  )

(defn place-counter
  "Places the specified counter colour in the specified position (ignoring validating position)
  Arguments:
    - A game board
    - A target x position
    - A target y position
    - A keyword representing the piece to place. One of [:empty :red :yellow]
  Returns:
    - A new game board with the specified piece placed (or removed for :empty)"
  [board x-pos y-pos counter-colour]
  (if (= counter-colour :empty)
    (update board :board dissoc (get-position-index board x-pos y-pos))
    (assoc-in board [:board (get-position-index board x-pos y-pos)] counter-colour))
  )

(defn get-lowest-empty-in-column
  "Returns the lowest empty position in a column x (ignoring validation)
  Arguments:
    - A game board
    - A target x column
  Returns:
    - An integer representing a y-position which is :empty in the specified column"
  [board x-pos]
  (loop [current-row-index 0]
    (if (is-position-empty? board x-pos current-row-index)
      current-row-index
      (recur (inc current-row-index)))))

(defn place-counter-in-column
  "Places a counter in the specified column (ignoring validation)
  Arguments:
    - A game board
    - A target x column
    - A keyword representing the piece to place. One of [:empty :red :yellow]
  Returns:
    - A new game board with the specified piece placed (or removed for :empty)"
    [board x-pos counter-colour]
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
  (reduce (fn [agg-board new-pos](:board (apply-move agg-board new-pos)))
          board
          (map vector x-positions (next-counter start-counter))))

(defn find-valid-moves [board]
  (filter (partial is-position-valid? board) (range (get-board-width board)))
  )

(defn print-board [{:keys [width height] :as full-board}]
  (reverse
    (map
      (fn [y-pos] (map
                    (fn [x-pos] (get-piece-in-position full-board x-pos y-pos))
                    (range width)))
      (range height)))
  )
