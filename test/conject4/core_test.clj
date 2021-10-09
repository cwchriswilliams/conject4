(ns conject4.core-test
  (:require [clojure.test :refer :all]
            [conject4.core :as sut]))

(def test-6x5-empty-board {:width 6 :height 5 :board {}})
(def test-6x5-laid-board  {:width 6 :height 5 :board {0 :red 2 :yellow 5 :red 6 :yellow 7 :yellow}})
(def test-4x5-laid-board  {:width 4 :height 5 :board {0 :red 2 :yellow 5 :red 6 :yellow 7 :yellow}})
(def test-4x3-laid-board  {:width 4 :height 3 :board {0 :red 4 :yellow 8 :red}})

(sut/print-board test-4x3-laid-board)

(deftest test-board-creation
  "default num board is empty regardless of size"
  (is (= (sut/create-empty-board 1 1) {:width 1 :height 1 :board {}}))
  (is (= (sut/create-empty-board 1 2) {:width 1 :height 2 :board {}}))
  (is (= (sut/create-empty-board 1 3) {:width 1 :height 3 :board {}}))
  (is (= (sut/create-empty-board 2 1) {:width 2 :height 1 :board {}}))
  (is (= (sut/create-empty-board 3 1) {:width 3 :height 1 :board {}}))
  (is (= (sut/create-empty-board 2 2) {:width 2 :height 2 :board {}}))
  )

(deftest print-board
  "Prints board as expected"
  (is (= (sut/print-board {:height 2 :width 4 :board {}}) [[:empty :empty :empty :empty]
                                                               [:empty :empty :empty :empty]]))
  (is (= (sut/print-board {:height 2 :width 4 :board {0 :red 2 :yellow 5 :red 6 :yellow 7 :yellow}}) [[:empty :red :yellow :yellow]
                                                                                                          [:red :empty :yellow :empty]]))
  )

(deftest test-get-piece-in-position
  "returns empty if space is empty"
  (is (= (sut/get-piece-in-position test-6x5-empty-board 2 2) :empty))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 1 2) :empty))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 2 2) :empty))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 1 0) :empty))
  "returns correct contents of space if space is not empty"
  (is (= (sut/get-piece-in-position test-6x5-laid-board 0 0) :red))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 2 0) :yellow))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 5 0) :red))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 0 1) :yellow))
  (is (= (sut/get-piece-in-position test-6x5-laid-board 1 1) :yellow))
  (is (= (sut/get-piece-in-position test-4x5-laid-board 3 1) :yellow))
  )


(deftest test-is-piece-in-position
  "returns true if expected piece in position"
  (is (true? (sut/is-piece-in-position? test-6x5-laid-board 0 0 :red)))
  (is (true? (sut/is-piece-in-position? test-6x5-laid-board 2 0 :yellow)))
  (is (true? (sut/is-piece-in-position? test-4x5-laid-board 3 1 :yellow)))
  (is (true? (sut/is-piece-in-position? test-4x5-laid-board 0 1 :empty)))
  "returns false if expected piece not in position"
  (is (false? (sut/is-piece-in-position? test-6x5-laid-board 0 0 :empty)))
  (is (false? (sut/is-piece-in-position? test-6x5-laid-board 2 0 :red)))
  (is (false? (sut/is-piece-in-position? test-4x5-laid-board 0 1 :yellow)))
  )

(deftest test-is-position-empty?
  "returns true if position is empty"
  (is (true? (sut/is-position-empty? test-4x5-laid-board 0 1)))
  (is (true? (sut/is-position-empty? test-4x3-laid-board 1 2)))
  "returns false if position is not empty"
  (is (false? (sut/is-position-empty? test-4x5-laid-board 3 1)))
  (is (false? (sut/is-position-empty? test-6x5-laid-board 2 0)))
  )

(deftest test-is-position-filled?
  "returns true if position is empty"
  (is (false? (sut/is-position-filled? test-4x5-laid-board 0 1)))
  (is (false? (sut/is-position-filled? test-4x3-laid-board 1 2)))
  "returns false if position is not empty"
  (is (true? (sut/is-position-filled? test-4x5-laid-board 3 1)))
  (is (true? (sut/is-position-filled? test-6x5-laid-board 2 0)))
  (is (true? (sut/is-position-filled? test-4x3-laid-board 0 2)))
  )

(deftest test-is-position-valid?
  "is-position-valid? returns false for negative numbers"
  (is (false? (sut/is-position-valid? test-6x5-laid-board -1)))
  (is (false? (sut/is-position-valid? test-6x5-laid-board -50)))
  "is-position-valid? returns false for numbers bigger than the board (-1 for 0-indexing)"
  (is (false? (sut/is-position-valid? test-6x5-laid-board 6)))
  (is (false? (sut/is-position-valid? test-6x5-laid-board 7)))
  (is (false? (sut/is-position-valid? test-6x5-laid-board 60)))
  (is (false? (sut/is-position-valid? test-4x3-laid-board 4)))
  "is-position-valid? returns true for numbers within the board (-1 for 0-indexing)"
  (is (true? (sut/is-position-valid? test-4x3-laid-board 1)))
  (is (true? (sut/is-position-valid? test-4x3-laid-board 2)))
  "is-position-valid? returns false for when the column is full"
  (is (false? (sut/is-position-valid? test-4x3-laid-board 0)))
  )

(deftest test-place-counter
  "Places in the location specified on empty board"
  (is (= (sut/place-counter test-6x5-empty-board 0 0 :red) {:width 6 :height 5 :board {0 :red}}))
  (is (= (sut/place-counter test-6x5-empty-board 1 0 :yellow) {:width 6 :height 5 :board {1 :yellow}}))
  (is (= (sut/place-counter test-6x5-empty-board 0 1 :yellow) {:width 6 :height 5 :board {6 :yellow}}))
  "Places in the location specified on non-empty board"
  (is (= (sut/place-counter test-4x3-laid-board 1 1 :yellow) {:width 4 :height 3 :board {0 :red 4 :yellow 8 :red 5 :yellow}}))
  (is (= (sut/place-counter test-4x3-laid-board 2 1 :yellow) {:width 4 :height 3 :board {0 :red 4 :yellow 8 :red 6 :yellow}}))
  "Replaces the piece in position"
  (is (= (sut/place-counter test-4x3-laid-board 0 0 :yellow) {:width 4 :height 3 :board {0 :yellow 4 :yellow 8 :red}}))
  (is (= (sut/place-counter test-4x3-laid-board 4 0 :red) {:width 4 :height 3 :board {0 :red 4 :red 8 :red}}))
  "Removes the piece if empty selected"
  (is (= (sut/place-counter test-4x3-laid-board 0 0 :empty) {:width 4 :height 3 :board {4 :yellow 8 :red}}))
  (is (= (sut/place-counter test-4x3-laid-board 4 0 :empty) {:width 4 :height 3 :board {0 :red 8 :red}}))
  )

(deftest test-apply-move
  "apply-move returns map with is-valid set to false if move is not valid"
  (is (false? (:is-valid-move (sut/apply-move test-4x3-laid-board -1 :red))))
  "apply-move returns map with board set to input board if move is not valid"
  (is (= (:board (sut/apply-move test-4x3-laid-board -1 :red)) test-4x3-laid-board))
  "apply-move returns map with is-valid set to true if move is valid"
  (is (true? (:is-valid-move (sut/apply-move test-4x3-laid-board 1 :red))))
  "apply move returns map with board set to the updated board if move is valid"
  (is (= (:board (sut/apply-move test-4x3-laid-board 1 :red)) {:width 4 :height 3 :board {0 :red 1 :red 4 :yellow 8 :red}}))
  )