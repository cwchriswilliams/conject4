(ns conject4.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  []
  (println "Hello, World!"))

(defn create-empty-game 
  "Creates an empty game of the specified size
  Arguments:
    - board width
    - board height
  Returns:
    - A map containing the attributes [width, height board]"
  [x-size y-size]
  {:height y-size :width x-size :board {} :game-log []})

(defn get-board-height 
  "Gets the height of the provided board
  Arguments:
    - A game
  Returns:
    - An integer representing the height of the board"
  [{height :height}]
  height)

(defn get-board-width 
"Gets the width of the provided board
  Arguments:
    - A game
  Returns:
    - An integer representing the width of the board"
  [{width :width}]
  width)

(defn get-position-index 
  "Gets the zero-indexed index of the provided position in the board
  Arguments:
    - A game
    - A target x position
    - A target y position
  Returns:
    - An integer representing the zero-index index of the provided position"
  [game x-pos y-pos]
  (+ (* y-pos (get-board-width game)) x-pos))

(defn get-piece-in-position 
  "Gets the current piece in the provided position in the board
  Arguments:
    - A game
    - A target x position
    - A target y position
  Returns:
    - A keyword representing the current piece in position. One of [:empty :red :yellow]"
  [{current-layout :board :as game} x-pos y-pos]
  (get current-layout (get-position-index game x-pos y-pos) :empty)
  )

(defn is-piece-in-position?
  "Returns a value indicating whether the provided piece is in the provided position
  Arguments:
    - A game
    - A target x position
    - A target y position
    - A keyword representing the piece to check for. One of [:empty :red :yellow]
  Returns:
    - true or false"
  [game x-pos y-pos piece]
  (= (get-piece-in-position game x-pos y-pos) piece))

(defn is-position-empty? 
  "Returns a value indicating whether the provided position is empty
  Arguments:
    - A game 
    - A target x position
    - A target y position
  Returns:
    - true or false"
  [game x-pos y-pos]
  (is-piece-in-position? game x-pos y-pos :empty))

(def is-position-filled?
  "Returns a value indicating whether the provided position is one of [:red :yellow]
  Arguments:
    - A game
    - A target x position
    - A target y position
  Returns:
    - true or false"
 (complement is-position-empty?))

(defn is-position-valid?
  "Returns a value indicating whether a piece can be placed in provided column
  Arguments:
    - A game
    - A target x column
  Returns:
    - true or false"
 [game x-pos]
  (and
    (not (neg? x-pos))
    (< x-pos (get-board-width game))
    (is-position-empty? game x-pos (dec (get-board-height game)))
    )
  )

(defn update-move-list
  "Returns a game with the game log updated with the provided move
  Arguments:
    - A game
    - A target x column
  Returns:
    - A game with the game log updated with the provided move"
  [game new-column-position]
  (update game :game-log conj new-column-position))

(defn place-counter
  "Places the specified counter colour in the specified position (ignoring validating position)
  Arguments:
    - A game
    - A target x position
    - A target y position
    - A keyword representing the piece to place. One of [:empty :red :yellow]
  Returns:
    - A new game with the specified piece placed (or removed for :empty)"
  [game x-pos y-pos counter-colour]
  (if (= counter-colour :empty)
    (update game :board dissoc (get-position-index game x-pos y-pos))
    (assoc-in game [:board (get-position-index game x-pos y-pos)] counter-colour))
  )

(defn get-lowest-empty-in-column
  "Returns the lowest empty position in a column x (ignoring validation)
  Arguments:
    - A game
    - A target x column
  Returns:
    - An integer representing a y-position which is :empty in the specified column"
  [game x-pos]
  (loop [current-row-index 0]
    (if (is-position-empty? game x-pos current-row-index)
      current-row-index
      (recur (inc current-row-index)))))

(defn place-counter-in-column
  "Places a counter in the specified column (ignoring validation)
  Arguments:
    - A game
    - A target x column
    - A keyword representing the piece to place. One of [:empty :red :yellow]
  Returns:
    - A new game board with the specified piece placed (or removed for :empty)"
    [game x-pos counter-colour]
  (-> 
   (place-counter game x-pos (get-lowest-empty-in-column game x-pos) counter-colour)
   (update-move-list x-pos)
  ))

(defn apply-move
  "Places a counter in the specified column or returns with is-valid-move? set to false
  Arguments:
    - A game
    - A target x column
    - A keyword representing the piece to place. One of [:empty :red :yellow]
  Arguments:
    - A game
    - A pair of x column and counter-colour  
  Returns:
    - A new game board with the specified piece placed (or removed for :empty)"
  ([game x-pos counter-colour]
  (if (is-position-valid? game x-pos)
    {:board (place-counter-in-column game x-pos counter-colour) :is-valid-move? true}
    {:board game :is-valid-move? false}
    ))
  ([game [x-pos counter-colour]]
   (apply-move game x-pos counter-colour))
  )

(defn next-counter
  "Gets an infinite sequence of alternating counters
  Arguments:
    - The counter colour to start with
  Returns:
    - An infinite sequence of alternating counters"
  [starts-with]
  (cycle [starts-with (if (= starts-with :red) :yellow :red)]))

(defn apply-moves
  "Applies the provided moves in order with alternating counters starting with the provided counter
  Arguments:
    - A game
    - A sequence of x-positions to apply counters to
    - The counter colour to start with
  Returns:
    - An updated game"
  [game x-positions start-counter]
  (reduce (fn [agg-game new-pos](:board (apply-move agg-game new-pos)))
          game
          (map vector x-positions (next-counter start-counter))))

(defn find-valid-moves
  "Finds a list of all valid moves on the board
   Arguments:
    - A game
   Returns:
    - A list of columns which can be placed in"
  [game]
  (filter (partial is-position-valid? game) (range (get-board-width game)))
  )

(defn get-board-layout 
  "Translates a game into a 2D collection of the board layout
   Arguments:
    - A game
   Returns:
    - 2D collection of the board layout"
  [{:keys [width height] :as full-board}]
  (reverse
    (map
      (fn [y-pos] (map
                    (fn [x-pos] (get-piece-in-position full-board x-pos y-pos))
                    (range width)))
      (range height)))
  )
