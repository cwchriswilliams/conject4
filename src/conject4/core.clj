(ns conject4.core
  (:require [clojure.spec.alpha :as spec])
  (:gen-class))

(comment
  (require '[clojure.spec.test.alpha :as stest])
  (require '[clojure.spec.gen.alpha :as sgen]))
;; specs in this file are for the purpose of experimentation only
;; Q: Where should these be placed?
;; Q: How much is too much spec?
;; Q: How much can the auto-generated spec doc replace documentation of args and rets?

(def valid-player-counters #{::red ::yellow})
(def valid-counters (conj valid-player-counters ::empty))

(spec/def ::game-size (spec/and pos-int? #(< % 1000)))
(spec/def ::game-pos ::game-size)
(spec/def ::board (spec/map-of nat-int? valid-player-counters))
(spec/def ::game-log (spec/coll-of nat-int? :kind vector? :min-count 1))
(spec/def ::height ::game-size)
(spec/def ::width ::game-size)
(spec/def ::game (spec/keys
                  :req [::height ::width ::board ::game-log]))
(spec/def ::empty-game (spec/and ::game
                                 #(and (empty? (::board %)) (map? (::board %)))
                                 #(and (empty? (::game-log %)) (vector (::game-log %)))))



(spec/fdef create-empty-game
  :args (spec/cat :x-size ::game-size :y-size ::game-size)
  :fn (spec/and #(= (-> % :args :y-size) (-> % :ret ::height))
                #(= (-> % :args :x-size) (-> % :ret ::width)))
  :ret ::empty-game)

(defn create-empty-game
  "Creates an empty game of the specified size
  Arguments:
    - board width
    - board height
  Returns:
    - A map containing the attributes [width, height board]"
  [x-size y-size]
  {::height y-size ::width x-size ::board {} ::game-log []})


(comment
  (stest/check `create-empty-game))

(spec/fdef get-position-index
  :args (spec/cat :game (spec/keys :req [::width]) :x-pos ::game-pos :y-pos ::game-pos)
  :ret nat-int?)

(defn get-position-index
  "Gets the zero-indexed index of the provided position in the board
  Arguments:
    - A game
    - A target x position
    - A target y position
  Returns:
    - An integer representing the zero-index index of the provided position"
  [game x-pos y-pos]
  (+ (* y-pos (::width game)) x-pos))


(comment
  (println "Hello World")
  (stest/check `get-position-index))

(spec/fdef get-piece-in-position
  :args (spec/and (spec/cat :game ::game :x-pos ::game-pos :y-pos ::game-pos)
                  #(< (:x-pos %) (-> % :game ::width))
                  #(< (:y-pos %) (-> % :game ::height)))
  :ret valid-counters)

(defn get-piece-in-position
  "Gets the current piece in the provided position in the board
  Arguments:
    - A game
    - A target x position
    - A target y position
  Returns:
    - A keyword representing the current piece in position. One of [::empty ::red ::yellow]"
  [{current-layout ::board :as game} x-pos y-pos]
  (get current-layout (get-position-index game x-pos y-pos) ::empty))

(comment
  ; This is very slow right now.
  (stest/check `get-piece-in-position))

(spec/fdef is-piece-in-position?
  :args (spec/and (spec/cat :game ::game :x-pos ::game-pos :y-pos ::game-pos :piece valid-counters)
                  #(< (:x-pos %) (-> % :game ::width))
                  #(< (:y-pos %) (-> % :game ::height)))
  :ret boolean?)

(defn is-piece-in-position?
  "Returns a value indicating whether the provided piece is in the provided position
  Arguments:
    - A game
    - A target x position
    - A target y position
    - A keyword representing the piece to check for. One of [::empty ::red ::yellow]
  Returns:
    - true or false"
  [game x-pos y-pos piece]
  (= (get-piece-in-position game x-pos y-pos) piece))


(comment
  ; This is very slow right now.
  (stest/check `is-piece-in-position?))


(spec/fdef is-position-empty?
  :args (spec/and (spec/cat :game ::game :x-pos ::game-pos :y-pos ::game-pos)
                  #(< (:x-pos %) (-> % :game ::width))
                  #(< (:y-pos %) (-> % :game ::height)))
  :ret boolean?)

(defn is-position-empty?
  "Returns a value indicating whether the provided position is empty
  Arguments:
    - A game 
    - A target x position
    - A target y position
  Returns:
    - true or false"
  [game x-pos y-pos]
  (is-piece-in-position? game x-pos y-pos ::empty))

(comment
  ; This is very slow right now.
  (stest/check `is-position-empty?))

(spec/fdef is-position-filled?
  :args (spec/and (spec/cat :game ::game :x-pos ::game-pos :y-pos ::game-pos)
                  #(< (:x-pos %) (-> % :game ::width))
                  #(< (:y-pos %) (-> % :game ::height)))
  :ret boolean?)

(def is-position-filled?
  "Returns a value indicating whether the provided position is one of [::red ::yellow]
  Arguments:
    - A game
    - A target x position
    - A target y position
  Returns:
    - true or false"
  (complement is-position-empty?))

(comment
  ; This is very slow right now.
  (stest/check `is-position-filled?))

(spec/fdef is-position-valid?
  :args (spec/cat :game ::game :x-pos ::game-pos)
  :ret boolean?)

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
   (< x-pos (::width game))
   (is-position-empty? game x-pos (dec (::height game)))))

(comment
  ; This is very slow right now.
  (stest/check `is-position-filled?))

(spec/fdef update-move-list
           :args (spec/cat :game (spec/keys :req [::game-log]) :new-column-position ::game-pos)
           :ret (spec/keys :req [::game-log]))

(defn update-move-list
  "Returns a game with the game log updated with the provided move
  Arguments:
    - A game
    - A target x column
  Returns:
    - A game with the game log updated with the provided move"
  [game new-column-position]
  (update game ::game-log conj new-column-position))

(comment
  (stest/check `update-move-list))

(defn place-counter
  "Places the specified counter colour in the specified position (ignoring validating position)
  Arguments:
    - A game
    - A target x position
    - A target y position
    - A keyword representing the piece to place. One of [::empty ::red ::yellow]
  Returns:
    - A new game with the specified piece placed (or removed for ::empty)"
  [game x-pos y-pos counter-colour]
  (if (= counter-colour ::empty)
    (update game ::board dissoc (get-position-index game x-pos y-pos))
    (assoc-in game [::board (get-position-index game x-pos y-pos)] counter-colour)))

(defn get-lowest-empty-in-column
  "Returns the lowest empty position in a column x (ignoring validation)
  Arguments:
    - A game
    - A target x column
  Returns:
    - An integer representing a y-position which is ::empty in the specified column"
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
    - A keyword representing the piece to place. One of [::empty ::red ::yellow]
  Returns:
    - A new game board with the specified piece placed (or removed for ::empty)"
  [game x-pos counter-colour]
  (->
   (place-counter game x-pos (get-lowest-empty-in-column game x-pos) counter-colour)
   (update-move-list x-pos)))

(defn apply-move
  "Places a counter in the specified column or returns with is-valid-move? set to false
  Arguments:
    - A game
    - A target x column
    - A keyword representing the piece to place. One of [::empty ::red ::yellow]
  Arguments:
    - A game
    - A pair of x column and counter-colour  
  Returns:
    - A new game board with the specified piece placed (or removed for ::empty)"
  ([game x-pos counter-colour]
   (if (is-position-valid? game x-pos)
     {::board (place-counter-in-column game x-pos counter-colour) ::is-valid-move? true}
     {::board game ::is-valid-move? false}))
  ([game [x-pos counter-colour]]
   (apply-move game x-pos counter-colour)))

(defn next-counter
  "Gets an infinite sequence of alternating counters
  Arguments:
    - The counter colour to start with
  Returns:
    - An infinite sequence of alternating counters"
  [starts-with]
  (cycle [starts-with (if (= starts-with ::red) ::yellow ::red)]))

(defn apply-moves
  "Applies the provided moves in order with alternating counters starting with the provided counter
  Arguments:
    - A game
    - A sequence of x-positions to apply counters to
    - The counter colour to start with
  Returns:
    - An updated game"
  [game x-positions start-counter]
  (reduce (fn [agg-game new-pos] (::board (apply-move agg-game new-pos)))
          game
          (map vector x-positions (next-counter start-counter))))

(defn find-valid-moves
  "Finds a list of all valid moves on the board
   Arguments:
    - A game
   Returns:
    - A list of columns which can be placed in"
  [game]
  (filter (partial is-position-valid? game) (range (::width game))))

(defn get-board-layout
  "Translates a game into a 2D collection of the board layout
   Arguments:
    - A game
   Returns:
    - 2D collection of the board layout"
  [{:keys [::width ::height] :as full-board}]
  (reverse
   (map
    (fn [y-pos] (map
                 (fn [x-pos] (get-piece-in-position full-board x-pos y-pos))
                 (range width)))
    (range height))))
