(ns conject4.core-test
  (:require [clojure.test :refer :all]
            [conject4.core :as sut]))

(deftest test-board-creation
  "default board is empty regardless of size"
  (is (= (sut/create-empty-board 1 1) [[:empty]]))
  (is (= (sut/create-empty-board 1 2) [[:empty] [:empty]]))
  (is (= (sut/create-empty-board 1 3) [[:empty] [:empty] [:empty]]))
  (is (= (sut/create-empty-board 2 1) [[:empty :empty]]))
  (is (= (sut/create-empty-board 3 1) [[:empty :empty :empty]]))
  (is (= (sut/create-empty-board 2 2) [[:empty :empty] [:empty :empty]]))
  )

(def test-board [[:empty :empty :empty :empty]
                 [:empty :empty :empty :empty]
                 [:empty :empty :empty :empty]
                 [:empty :empty :empty :empty]
                 [:empty :empty :empty :empty]])

(def test-board-with-red-column [[:empty :red :empty :empty]
                                  [:empty :red :empty :empty]
                                  [:empty :red :empty :empty]
                                  [:empty :red :empty :empty]
                                  [:empty :red :empty :empty]])

(def test-board-with-yellow-column [[:empty :empty :yellow :empty]
                                  [:empty :empty :yellow :empty]
                                  [:empty :empty :yellow :empty]
                                  [:empty :empty :yellow :empty]
                                  [:empty :empty :yellow :empty]])

(deftest test-is-position-valid?
  "is-position-valid? returns false for negative numbers"
  (is (false? (sut/is-position-valid? test-board -1)))
  (is (false? (sut/is-position-valid? test-board -50)))
  "is-position-valid? returns false for numbers bigger than the board (-1 for 0-indexing)"
  (is (false? (sut/is-position-valid? test-board 5)))
  (is (false? (sut/is-position-valid? test-board 6)))
  (is (false? (sut/is-position-valid? test-board 60)))
  "is-position-valid? returns true for numbers within the board (-1 for 0-indexing)"
  (is (true? (sut/is-position-valid? test-board 2)))
  (is (true? (sut/is-position-valid? test-board 3)))
  (is (true? (sut/is-position-valid? test-board 0)))
  "is-position-valid? returns false for when the column is full"
  (is (false? (sut/is-position-valid? test-board-with-red-column 1)))
  (is (false? (sut/is-position-valid? test-board-with-yellow-column 2)))
  )

(deftest test-apply-move
  "apply-move returns map with is-valid set to false if move is not valid"
  "apply-move returns map with board set to input board if move is not valid"
  (is (= (:board (sut/apply-move test-board -1)) test-board))
  (is (false? (:is-valid-move (sut/apply-move test-board -1))))
  "apply-move returns map with is-valid set to true if move is valid"
  (is (true? (:is-valid-move (sut/apply-move test-board 3))))
  )