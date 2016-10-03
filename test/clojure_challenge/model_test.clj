(ns clojure-challenge.model-test
  (:require [clojure.test :refer :all]
            [clojure-challenge.model :refer :all]))

(def test-filename "resources/example.txt")

(deftest load-customer-tree-test

  (testing "Testing load tree"
    (load-customer-tree test-filename)
    (is (= 2.5 (get-score 1)))
    (is (= (count @customer-tree) 6))
    (add-invitation 6 9)
    (is (= 2.75 (get-score 1)))
    (is (= (count @customer-tree) 7))
    (process-line "9 11")
    (is (= (count @customer-tree) 8))
  
  (testing "Testing inviter?"
    (is (= false (inviter? 1)))
    (is (= true (inviter? 2)))
    (is (= true (inviter? 9))))
  
  (testing "Testing get-inviter"
     (is (= (get-inviter 3) 1))
     (is (not (= (get-inviter 4) 2)))
     (is (= (get-inviter 4) 3))
     (is (= (get-inviter 9) 6))
     (add-invitation 9 12)
     (is (= (get-inviter 12) 9))
     (is (= (count @customer-tree) 9)))
  
  (testing "Testing validate-invitation"
    (is (not (validate-invitation "4" "4"))) 
    (is (validate-invitation "6" "10"))
    (is (not (validate-invitation "7" "8")))
    (is (validate-invitation "6" "7"))
    (is (not (validate-invitation "5" "4")))
    (add-invitation 6 7)
    (is (validate-invitation "7" "8"))
    (is (= (count @customer-tree) 10))
    (is (= 2.875 (get-score 1)))))
  
  )

