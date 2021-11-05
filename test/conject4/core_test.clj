(ns conject4.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [conject4.core :as sut]))

(def test-6x5-empty-game {::sut/width 6 ::sut/height 5 ::sut/board {} ::sut/game-log []})
(def test-6x5-laid-game  {::sut/width 6 ::sut/height 5 ::sut/board {0 ::sut/red 2 ::sut/yellow 5 ::sut/red 6 ::sut/yellow 7 ::sut/yellow} ::sut/game-log []})
(def test-4x5-laid-game  {::sut/width 4 ::sut/height 5 ::sut/board {0 ::sut/red 2 ::sut/yellow 5 ::sut/red 6 ::sut/yellow 7 ::sut/yellow} ::sut/game-log []})
(def test-4x3-laid-game  {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 4 ::sut/yellow 8 ::sut/red} ::sut/game-log []})

(deftest test-board-creation
  (testing "default game is empty regardless of size"
    (is (= {::sut/width 1 ::sut/height 1 ::sut/board {} ::sut/game-log []} (sut/create-empty-game 1 1)))
    (is (= {::sut/width 1 ::sut/height 2 ::sut/board {} ::sut/game-log []} (sut/create-empty-game 1 2)))
    (is (= {::sut/width 1 ::sut/height 3 ::sut/board {} ::sut/game-log []} (sut/create-empty-game 1 3)))
    (is (= {::sut/width 2 ::sut/height 1 ::sut/board {} ::sut/game-log []} (sut/create-empty-game 2 1)))
    (is (= {::sut/width 3 ::sut/height 1 ::sut/board {} ::sut/game-log []} (sut/create-empty-game 3 1)))
    (is (= {::sut/width 2 ::sut/height 2 ::sut/board {} ::sut/game-log []} (sut/create-empty-game 2 2)))
  )
  )

(deftest test-get-board-layout
  (testing "Prints board as expected"
    (is (= [[::sut/empty ::sut/empty ::sut/empty ::sut/empty]
            [::sut/empty ::sut/empty ::sut/empty ::sut/empty]] (sut/get-board-layout {::sut/height 2 ::sut/width 4 ::sut/board {}})))
    (is (= [[::sut/empty ::sut/red ::sut/yellow ::sut/yellow]
            [::sut/red ::sut/empty ::sut/yellow ::sut/empty]] (sut/get-board-layout {::sut/height 2 ::sut/width 4 ::sut/board {0 ::sut/red 2 ::sut/yellow 5 ::sut/red 6 ::sut/yellow 7 ::sut/yellow}})))
  )
)

(deftest test-get-piece-in-position
  (testing "returns empty if space is empty"
    (is (= ::sut/empty (sut/get-piece-in-position test-6x5-empty-game 2 2)))
    (is (= ::sut/empty (sut/get-piece-in-position test-6x5-laid-game 1 2)))
    (is (= ::sut/empty (sut/get-piece-in-position test-6x5-laid-game 2 2)))
    (is (= ::sut/empty (sut/get-piece-in-position test-6x5-laid-game 1 0)))
  )
  (testing "returns correct contents of space if space is not empty"
    (is (= ::sut/red (sut/get-piece-in-position test-6x5-laid-game 0 0)))
    (is (= ::sut/yellow (sut/get-piece-in-position test-6x5-laid-game 2 0)))
    (is (= ::sut/red (sut/get-piece-in-position test-6x5-laid-game 5 0)))
    (is (= ::sut/yellow (sut/get-piece-in-position test-6x5-laid-game 0 1)))
    (is (= ::sut/yellow (sut/get-piece-in-position test-6x5-laid-game 1 1)))
    (is (= ::sut/yellow (sut/get-piece-in-position test-4x5-laid-game 3 1)))
  )
  )


(deftest test-is-piece-in-position
  (testing "returns true if expected piece in position"
    (is (true? (sut/is-piece-in-position? test-6x5-laid-game 0 0 ::sut/red)))
    (is (true? (sut/is-piece-in-position? test-6x5-laid-game 2 0 ::sut/yellow)))
    (is (true? (sut/is-piece-in-position? test-4x5-laid-game 3 1 ::sut/yellow)))
    (is (true? (sut/is-piece-in-position? test-4x5-laid-game 0 1 ::sut/empty)))
  )
  (testing "returns false if expected piece not in position"
    (is (false? (sut/is-piece-in-position? test-6x5-laid-game 0 0 ::sut/empty)))
    (is (false? (sut/is-piece-in-position? test-6x5-laid-game 2 0 ::sut/red)))
    (is (false? (sut/is-piece-in-position? test-4x5-laid-game 0 1 ::sut/yellow)))
  )
  )

(deftest test-is-position-empty?
  (testing "returns true if position is empty"
    (is (true? (sut/is-position-empty? test-4x5-laid-game 0 1)))
    (is (true? (sut/is-position-empty? test-4x3-laid-game 1 2)))
  )
  (testing "returns false if position is not empty"
    (is (false? (sut/is-position-empty? test-4x5-laid-game 3 1)))
    (is (false? (sut/is-position-empty? test-6x5-laid-game 2 0)))
  )
)

(deftest test-is-position-filled?
  (testing "returns true if position is empty"
    (is (false? (sut/is-position-filled? test-4x5-laid-game 0 1)))
    (is (false? (sut/is-position-filled? test-4x3-laid-game 1 2)))
  )
  (testing "returns false if position is not empty"
    (is (true? (sut/is-position-filled? test-4x5-laid-game 3 1)))
    (is (true? (sut/is-position-filled? test-6x5-laid-game 2 0)))
    (is (true? (sut/is-position-filled? test-4x3-laid-game 0 2)))
  )
  )

(deftest test-is-position-valid?
  (testing "is-position-valid? returns false for negative numbers"
    (is (false? (sut/is-position-valid? test-6x5-laid-game -1)))
    (is (false? (sut/is-position-valid? test-6x5-laid-game -50)))
  )
  (testing "is-position-valid? returns false for numbers bigger than the board (-1 for 0-indexing)"
    (is (false? (sut/is-position-valid? test-6x5-laid-game 6)))
    (is (false? (sut/is-position-valid? test-6x5-laid-game 7)))
    (is (false? (sut/is-position-valid? test-6x5-laid-game 60)))
    (is (false? (sut/is-position-valid? test-4x3-laid-game 4)))
  )
  (testing "is-position-valid? returns true for numbers within the board (-1 for 0-indexing)"
    (is (true? (sut/is-position-valid? test-4x3-laid-game 1)))
    (is (true? (sut/is-position-valid? test-4x3-laid-game 2)))
  )
  (testing "is-position-valid? returns false for when the column is full"
    (is (false? (sut/is-position-valid? test-4x3-laid-game 0)))
  )
  )

(deftest test-update-move-list
  (testing "Adds new move to empty game log"
    (is (= [1] (::sut/game-log (sut/update-move-list {} 1))))
    (is (= [3] (::sut/game-log (sut/update-move-list {} 3))))
    )
  (testing "Adds new move to non-empty game log"
    (is (= [1 2] (::sut/game-log (sut/update-move-list {::sut/game-log [1]} 2))))
    (is (= [3 4] (::sut/game-log (sut/update-move-list {::sut/game-log [3]} 4))))
    (is (= [3 4 5 6] (::sut/game-log (sut/update-move-list {::sut/game-log [3 4 5]} 6))))
    )
  (testing "Adds new move to non-empty game log when position duplicated"
    (is (= [1 1] (::sut/game-log (sut/update-move-list {::sut/game-log [1]} 1))))
    (is (= [3 3] (::sut/game-log (sut/update-move-list {::sut/game-log [3]} 3))))
    )
    )

(deftest test-place-counter
  (testing "Places in the location specified on empty board"
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {0 ::sut/red} ::sut/game-log []} (sut/place-counter test-6x5-empty-game 0 0 ::sut/red)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {1 ::sut/yellow} ::sut/game-log []} (sut/place-counter test-6x5-empty-game 1 0 ::sut/yellow)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {6 ::sut/yellow} ::sut/game-log []} (sut/place-counter test-6x5-empty-game 0 1 ::sut/yellow)))
  )
  (testing "Places in the location specified on non-empty board"
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 4 ::sut/yellow 8 ::sut/red 5 ::sut/yellow} ::sut/game-log []} (sut/place-counter test-4x3-laid-game 1 1 ::sut/yellow)))
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 4 ::sut/yellow 8 ::sut/red 6 ::sut/yellow} ::sut/game-log []} (sut/place-counter test-4x3-laid-game 2 1 ::sut/yellow)))
  )
  (testing "Replaces the piece in position"
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/yellow 4 ::sut/yellow 8 ::sut/red} ::sut/game-log []} (sut/place-counter test-4x3-laid-game 0 0 ::sut/yellow)))
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 4 ::sut/red 8 ::sut/red} ::sut/game-log []} (sut/place-counter test-4x3-laid-game 4 0 ::sut/red)))
  )
  (testing "Removes the piece if empty selected"
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {4 ::sut/yellow 8 ::sut/red} ::sut/game-log []} (sut/place-counter test-4x3-laid-game 0 0 ::sut/empty)))
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 8 ::sut/red} ::sut/game-log []} (sut/place-counter test-4x3-laid-game 4 0 ::sut/empty)))
  )
  )

(deftest get-lowest-empty-in-column
  (testing "Returns 0 for empty column"
    (is (zero? (sut/get-lowest-empty-in-column test-4x3-laid-game 1)))
  )
  (testing "throws for filled column"
    ;(is (thrown? Exception (sut/get-lowest-empty-in-column test-4x3-laid-game 1)))
  )
  (testing "Returns expected value for various columns"
    (is (= 1 (sut/get-lowest-empty-in-column {::sut/width 1 ::sut/height 5 ::sut/board {0 ::sut/red}} 0)))
    (is (= 2 (sut/get-lowest-empty-in-column {::sut/width 1 ::sut/height 5 ::sut/board {0 ::sut/red 1 ::sut/yellow}} 0)))
    (is (= 3 (sut/get-lowest-empty-in-column {::sut/width 1 ::sut/height 5 ::sut/board {0 ::sut/red 1 ::sut/yellow 2 ::sut/red}} 0)))
  )
  )

(deftest test-apply-move
  (testing "apply-move returns map with is-valid set to false if move is not valid"
    (is (false? (::sut/is-valid-move? (sut/apply-move test-4x3-laid-game -1 ::sut/red))))
  )
  (testing "apply-move returns map with board set to input board if move is not valid"
    (is (= test-4x3-laid-game (::sut/board (sut/apply-move test-4x3-laid-game -1 ::sut/red))))
  )
  (testing "apply-move returns map with is-valid set to true if move is valid"
    (is (true? (::sut/is-valid-move? (sut/apply-move test-4x3-laid-game 1 ::sut/red))))
  )
  (testing "apply move returns map with board set to the updated board if move is valid"
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 1 ::sut/red 4 ::sut/yellow 8 ::sut/red} ::sut/game-log [1]} (::sut/board (sut/apply-move test-4x3-laid-game 1 ::sut/red))))
  )
  (testing "apply move can take pair of position and colour"
    (is (= {::sut/width 4 ::sut/height 3 ::sut/board {0 ::sut/red 1 ::sut/red 4 ::sut/yellow 8 ::sut/red} ::sut/game-log [1]} (::sut/board (sut/apply-move test-4x3-laid-game [1 ::sut/red]))))
  )
  )

(deftest test-next-counter
  (testing "Next counter returns infinite sequence of alternating ::sut/red ::sut/yellow counters"
    (is (= [::sut/yellow] (take 1 (sut/next-counter ::sut/yellow))))
    (is (= [::sut/red] (take 1 (sut/next-counter ::sut/red))))
    (is (= [::sut/yellow ::sut/red] (take 2 (sut/next-counter ::sut/yellow))))
    (is (= [::sut/red ::sut/yellow] (take 2 (sut/next-counter ::sut/red))))
    (is (= [::sut/red ::sut/yellow ::sut/red ::sut/yellow] (take 4 (sut/next-counter ::sut/red))))
  )
  )

(deftest test-apply-moves
  (testing "apply-moves returns input game when no moves provided"
    (is (= test-4x3-laid-game (sut/apply-moves test-4x3-laid-game [] ::sut/yellow)))
  )
  (testing "apply-moves will apply the provided counter first"
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {2 ::sut/yellow} ::sut/game-log [2]} (sut/apply-moves test-6x5-empty-game [2] ::sut/yellow)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {2 ::sut/red} ::sut/game-log [2]} (sut/apply-moves test-6x5-empty-game [2] ::sut/red)))
  )
  (testing "apply-moves will apply the moves flipping counter each turn"
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {2 ::sut/yellow 3 ::sut/red} ::sut/game-log [2 3]} (sut/apply-moves test-6x5-empty-game [2 3] ::sut/yellow)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {2 ::sut/red 3 ::sut/yellow} ::sut/game-log [2 3]} (sut/apply-moves test-6x5-empty-game [2 3] ::sut/red)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {1 ::sut/yellow 2 ::sut/red} ::sut/game-log [2 1]} (sut/apply-moves test-6x5-empty-game [2 1] ::sut/red)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {1 ::sut/yellow 7 ::sut/red} ::sut/game-log [1 1]} (sut/apply-moves test-6x5-empty-game [1 1] ::sut/yellow)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {1 ::sut/red 7 ::sut/yellow} ::sut/game-log [1 1]} (sut/apply-moves test-6x5-empty-game [1 1] ::sut/red)))
    (is (= {::sut/width 6 ::sut/height 5 ::sut/board {1 ::sut/red 7 ::sut/yellow 13 ::sut/red} ::sut/game-log [1 1 1]} (sut/apply-moves test-6x5-empty-game [1 1 1] ::sut/red)))
  )
  )

(deftest test-find-valid-moves
  (testing "Returns empty col when there are no valid moves"
    (is (empty? (sut/find-valid-moves {::sut/width 1 ::sut/height 1 ::sut/board {0 ::sut/red}})))
    (is (empty? (sut/find-valid-moves {::sut/width 2 ::sut/height 1 ::sut/board {0 ::sut/red 1 ::sut/red}})))
    (is (empty? (sut/find-valid-moves {::sut/width 1 ::sut/height 2 ::sut/board {0 ::sut/red 1 ::sut/red}})))
    (is (empty? (sut/find-valid-moves {::sut/width 2 ::sut/height 2 ::sut/board {0 ::sut/red 1 ::sut/red 2 ::sut/red 3 ::sut/red}})))
  )

  (testing "Returns only valid move if only one move available"
    (is (= [0] (sut/find-valid-moves {::sut/width 1 ::sut/height 1 ::sut/board {}})))
    (is (= [0] (sut/find-valid-moves {::sut/width 2 ::sut/height 1 ::sut/board {1 ::sut/red}})))
    (is (= [1] (sut/find-valid-moves {::sut/width 2 ::sut/height 1 ::sut/board {0 ::sut/red}})))
    (is (= [0] (sut/find-valid-moves {::sut/width 1 ::sut/height 2 ::sut/board {0 ::sut/red}})))
    (is (= [0] (sut/find-valid-moves {::sut/width 2 ::sut/height 2 ::sut/board {0 ::sut/red 1 ::sut/red 3 ::sut/red}})))
    (is (= [1] (sut/find-valid-moves {::sut/width 2 ::sut/height 2 ::sut/board {0 ::sut/red 1 ::sut/red 2 ::sut/red}})))
  )

  (testing "Returns multiple valid moves if multiple moves available"
    (is (= [0 1] (sut/find-valid-moves {::sut/width 2 ::sut/height 1 ::sut/board {}})))
    (is (= [0 1 2] (sut/find-valid-moves {::sut/width 3 ::sut/height 1 ::sut/board {}})))
    (is (= [1 2] (sut/find-valid-moves {::sut/width 3 ::sut/height 1 ::sut/board {0 ::sut/red}})))
    (is (= [0 1] (sut/find-valid-moves {::sut/width 2 ::sut/height 2 ::sut/board {0 ::sut/red 1 ::sut/red}})))
  )
)