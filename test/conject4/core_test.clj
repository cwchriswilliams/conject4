(ns conject4.core-test
  (:require [clojure.test :refer :all]
            [conject4.core :as sut]))

(def test-6x5-empty-board {:width 6 :height 5 :board {}})
(def test-6x5-laid-board  {:width 6 :height 5 :board {0 :red 2 :yellow 5 :red 6 :yellow 7 :yellow}})
(def test-4x5-laid-board  {:width 4 :height 5 :board {0 :red 2 :yellow 5 :red 6 :yellow 7 :yellow}})
(def test-4x3-laid-board  {:width 4 :height 3 :board {0 :red 4 :yellow 8 :red}})

(sut/print-board test-4x3-laid-board)

(deftest test-board-creation
  (testing "default num board is empty regardless of size"
    (is (= {:width 1 :height 1 :board {}} (sut/create-empty-board 1 1)))
    (is (= {:width 1 :height 2 :board {}} (sut/create-empty-board 1 2)))
    (is (= {:width 1 :height 3 :board {}} (sut/create-empty-board 1 3)))
    (is (= {:width 2 :height 1 :board {}} (sut/create-empty-board 2 1)))
    (is (= {:width 3 :height 1 :board {}} (sut/create-empty-board 3 1)))
    (is (= {:width 2 :height 2 :board {}} (sut/create-empty-board 2 2)))
  )
  )

(deftest print-board
  (testing "Prints board as expected"
    (is (= [[:empty :empty :empty :empty]
            [:empty :empty :empty :empty]] (sut/print-board {:height 2 :width 4 :board {}})))
    (is (= [[:empty :red :yellow :yellow]
            [:red :empty :yellow :empty]] (sut/print-board {:height 2 :width 4 :board {0 :red 2 :yellow 5 :red 6 :yellow 7 :yellow}})))
  )
)

(deftest test-get-piece-in-position
  (testing "returns empty if space is empty"
    (is (= :empty (sut/get-piece-in-position test-6x5-empty-board 2 2)))
    (is (= :empty (sut/get-piece-in-position test-6x5-laid-board 1 2)))
    (is (= :empty (sut/get-piece-in-position test-6x5-laid-board 2 2)))
    (is (= :empty (sut/get-piece-in-position test-6x5-laid-board 1 0)))
  )
  (testing "returns correct contents of space if space is not empty"
    (is (= :red (sut/get-piece-in-position test-6x5-laid-board 0 0)))
    (is (= :yellow (sut/get-piece-in-position test-6x5-laid-board 2 0)))
    (is (= :red (sut/get-piece-in-position test-6x5-laid-board 5 0)))
    (is (= :yellow (sut/get-piece-in-position test-6x5-laid-board 0 1)))
    (is (= :yellow (sut/get-piece-in-position test-6x5-laid-board 1 1)))
    (is (= :yellow (sut/get-piece-in-position test-4x5-laid-board 3 1)))
  )
  )


(deftest test-is-piece-in-position
  (testing "returns true if expected piece in position"
    (is (true? (sut/is-piece-in-position? test-6x5-laid-board 0 0 :red)))
    (is (true? (sut/is-piece-in-position? test-6x5-laid-board 2 0 :yellow)))
    (is (true? (sut/is-piece-in-position? test-4x5-laid-board 3 1 :yellow)))
    (is (true? (sut/is-piece-in-position? test-4x5-laid-board 0 1 :empty)))
  )
  (testing "returns false if expected piece not in position"
    (is (false? (sut/is-piece-in-position? test-6x5-laid-board 0 0 :empty)))
    (is (false? (sut/is-piece-in-position? test-6x5-laid-board 2 0 :red)))
    (is (false? (sut/is-piece-in-position? test-4x5-laid-board 0 1 :yellow)))
  )
  )

(deftest test-is-position-empty?
  (testing "returns true if position is empty"
    (is (true? (sut/is-position-empty? test-4x5-laid-board 0 1)))
    (is (true? (sut/is-position-empty? test-4x3-laid-board 1 2)))
  )
  (testing "returns false if position is not empty"
    (is (false? (sut/is-position-empty? test-4x5-laid-board 3 1)))
    (is (false? (sut/is-position-empty? test-6x5-laid-board 2 0)))
  )
)

(deftest test-is-position-filled?
  (testing "returns true if position is empty"
    (is (false? (sut/is-position-filled? test-4x5-laid-board 0 1)))
    (is (false? (sut/is-position-filled? test-4x3-laid-board 1 2)))
  )
  (testing "returns false if position is not empty"
    (is (true? (sut/is-position-filled? test-4x5-laid-board 3 1)))
    (is (true? (sut/is-position-filled? test-6x5-laid-board 2 0)))
    (is (true? (sut/is-position-filled? test-4x3-laid-board 0 2)))
  )
  )

(deftest test-is-position-valid?
  (testing "is-position-valid? returns false for negative numbers"
    (is (false? (sut/is-position-valid? test-6x5-laid-board -1)))
    (is (false? (sut/is-position-valid? test-6x5-laid-board -50)))
  )
  (testing "is-position-valid? returns false for numbers bigger than the board (-1 for 0-indexing)"
    (is (false? (sut/is-position-valid? test-6x5-laid-board 6)))
    (is (false? (sut/is-position-valid? test-6x5-laid-board 7)))
    (is (false? (sut/is-position-valid? test-6x5-laid-board 60)))
    (is (false? (sut/is-position-valid? test-4x3-laid-board 4)))
  )
  (testing "is-position-valid? returns true for numbers within the board (-1 for 0-indexing)"
    (is (true? (sut/is-position-valid? test-4x3-laid-board 1)))
    (is (true? (sut/is-position-valid? test-4x3-laid-board 2)))
  )
  (testing "is-position-valid? returns false for when the column is full"
    (is (false? (sut/is-position-valid? test-4x3-laid-board 0)))
  )
  )

(deftest test-place-counter
  (testing "Places in the location specified on empty board"
    (is (= {:width 6 :height 5 :board {0 :red}} (sut/place-counter test-6x5-empty-board 0 0 :red)))
    (is (= {:width 6 :height 5 :board {1 :yellow}} (sut/place-counter test-6x5-empty-board 1 0 :yellow)))
    (is (= {:width 6 :height 5 :board {6 :yellow}} (sut/place-counter test-6x5-empty-board 0 1 :yellow)))
  )
  (testing "Places in the location specified on non-empty board"
    (is (= {:width 4 :height 3 :board {0 :red 4 :yellow 8 :red 5 :yellow}} (sut/place-counter test-4x3-laid-board 1 1 :yellow)))
    (is (= {:width 4 :height 3 :board {0 :red 4 :yellow 8 :red 6 :yellow}} (sut/place-counter test-4x3-laid-board 2 1 :yellow)))
  )
  (testing "Replaces the piece in position"
    (is (= {:width 4 :height 3 :board {0 :yellow 4 :yellow 8 :red}} (sut/place-counter test-4x3-laid-board 0 0 :yellow)))
    (is (= {:width 4 :height 3 :board {0 :red 4 :red 8 :red}} (sut/place-counter test-4x3-laid-board 4 0 :red)))
  )
  (testing "Removes the piece if empty selected"
    (is (= {:width 4 :height 3 :board {4 :yellow 8 :red}} (sut/place-counter test-4x3-laid-board 0 0 :empty)))
    (is (= {:width 4 :height 3 :board {0 :red 8 :red}} (sut/place-counter test-4x3-laid-board 4 0 :empty)))
  )
  )

(deftest get-lowest-empty-in-column
  (testing "Returns 0 for empty column"
    (is (zero? (sut/get-lowest-empty-in-column test-4x3-laid-board 1)))
  )
  (testing "throws for filled column"
    ;(is (thrown? Exception (sut/get-lowest-empty-in-column test-4x3-laid-board 1)))
  )
  (testing "Returns expected value for various columns"
    (is (= 1 (sut/get-lowest-empty-in-column {:width 1 :height 5 :board {0 :red}} 0)))
    (is (= 2 (sut/get-lowest-empty-in-column {:width 1 :height 5 :board {0 :red 1 :yellow}} 0)))
    (is (= 3 (sut/get-lowest-empty-in-column {:width 1 :height 5 :board {0 :red 1 :yellow 2 :red}} 0)))
  )
  )

(deftest test-apply-move
  (testing "apply-move returns map with is-valid set to false if move is not valid"
    (is (false? (:is-valid-move (sut/apply-move test-4x3-laid-board -1 :red))))
  )
  (testing "apply-move returns map with board set to input board if move is not valid"
    (is (= test-4x3-laid-board (:board (sut/apply-move test-4x3-laid-board -1 :red))))
  )
  (testing "apply-move returns map with is-valid set to true if move is valid"
    (is (true? (:is-valid-move (sut/apply-move test-4x3-laid-board 1 :red))))
  )
  (testing "apply move returns map with board set to the updated board if move is valid"
    (is (= {:width 4 :height 3 :board {0 :red 1 :red 4 :yellow 8 :red}} (:board (sut/apply-move test-4x3-laid-board 1 :red))))
  )
  (testing "apply move can take pair of position and colour"
    (is (= {:width 4 :height 3 :board {0 :red 1 :red 4 :yellow 8 :red}} (:board (sut/apply-move test-4x3-laid-board [1 :red]))))
  )
  )

(deftest test-next-counter
  (testing "Next counter returns infinite sequence of alternating :red :yellow counters"
    (is (= [:yellow] (take 1 (sut/next-counter :yellow))))
    (is (= [:red] (take 1 (sut/next-counter :red))))
    (is (= [:yellow :red] (take 2 (sut/next-counter :yellow))))
    (is (= [:red :yellow] (take 2 (sut/next-counter :red))))
    (is (= [:red :yellow :red :yellow] (take 4 (sut/next-counter :red))))
  )
  )

(deftest test-apply-moves
  (testing "apply-moves returns input board when no moves provided"
    (is (= test-4x3-laid-board (sut/apply-moves test-4x3-laid-board [] :yellow)))
  )
  (testing "apply-moves will apply the provided counter first"
    (is (= {:width 6 :height 5 :board {2 :yellow}} (sut/apply-moves test-6x5-empty-board [2] :yellow)))
    (is (= {:width 6 :height 5 :board {2 :red}} (sut/apply-moves test-6x5-empty-board [2] :red)))
  )
  (testing "apply-moves will apply the moves flipping counter each turn"
    (is (= {:width 6 :height 5 :board {2 :yellow 3 :red}} (sut/apply-moves test-6x5-empty-board [2 3] :yellow)))
    (is (= {:width 6 :height 5 :board {2 :red 3 :yellow}} (sut/apply-moves test-6x5-empty-board [2 3] :red)))
    (is (= {:width 6 :height 5 :board {1 :yellow 2 :red}} (sut/apply-moves test-6x5-empty-board [2 1] :red)))
    (is (= {:width 6 :height 5 :board {1 :yellow 7 :red}} (sut/apply-moves test-6x5-empty-board [1 1] :yellow)))
    (is (= {:width 6 :height 5 :board {1 :red 7 :yellow}} (sut/apply-moves test-6x5-empty-board [1 1] :red)))
    (is (= {:width 6 :height 5 :board {1 :red 7 :yellow 13 :red}} (sut/apply-moves test-6x5-empty-board [1 1 1] :red)))
  )
  )

(deftest test-find-valid-moves
  (testing "Returns empty col when there are no valid moves"
    (is (empty? (sut/find-valid-moves {:width 1 :height 1 :board {0 :red}})))
    (is (empty? (sut/find-valid-moves {:width 2 :height 1 :board {0 :red 1 :red}})))
    (is (empty? (sut/find-valid-moves {:width 1 :height 2 :board {0 :red 1 :red}})))
    (is (empty? (sut/find-valid-moves {:width 2 :height 2 :board {0 :red 1 :red 2 :red 3 :red}})))
  )

  (testing "Returns only valid move if only one move available"
    (is (= [0] (sut/find-valid-moves {:width 1 :height 1 :board {}})))
    (is (= [0] (sut/find-valid-moves {:width 2 :height 1 :board {1 :red}})))
    (is (= [1] (sut/find-valid-moves {:width 2 :height 1 :board {0 :red}})))
    (is (= [0] (sut/find-valid-moves {:width 1 :height 2 :board {0 :red}})))
    (is (= [0] (sut/find-valid-moves {:width 2 :height 2 :board {0 :red 1 :red 3 :red}})))
    (is (= [1] (sut/find-valid-moves {:width 2 :height 2 :board {0 :red 1 :red 2 :red}})))
  )

  (testing "Returns multiple valid moves if multiple moves available"
    (is (= [0 1] (sut/find-valid-moves {:width 2 :height 1 :board {}})))
    (is (= [0 1 2] (sut/find-valid-moves {:width 3 :height 1 :board {}})))
    (is (= [1 2] (sut/find-valid-moves {:width 3 :height 1 :board {0 :red}})))
    (is (= [0 1] (sut/find-valid-moves {:width 2 :height 2 :board {0 :red 1 :red}})))
  )
)