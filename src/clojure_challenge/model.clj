(ns clojure-challenge.model
  (:use [clojure.java.io])
  (:require [clojure.math.numeric-tower :as math])
  (:require [clojure.string :as str]))

;; Initial input file to load customers 
;; used if we call lein run with no args
(def default-filename "resources/example.txt")

;; TREE DATA STRUCTURE

;; Atom to refer to customer-tree
(def customer-tree (atom {}))

;; UTILITIES FUNCTIONS 

;; Print customer-tree - Debug stuff
(defn print-customer-tree []
  (doseq [[key value] @customer-tree]
    (prn (str " Key => " key " : Value => " value))))
;; Get customer id
(defn get-cid [inviter]
  (get-in @customer-tree [inviter :cid]))
;; Get Inviter
(defn get-inviter [inviter]
  (get-in @customer-tree [inviter :inviter]))
;; Has Inviter
(defn inviter? [inviter]
  (not (nil? (get-in @customer-tree [inviter :inviter]))))
;; Is inviter confirmed
(defn confirmed? [inviter]
  (get-in @customer-tree [inviter :confirmed]))
;; Get score of inviter
(defn get-score [inviter]
  (get-in @customer-tree [inviter :score]))
;; Get level of inviter
(defn get-level [inviter]
  (get-in @customer-tree [inviter :level]))
;; Is inviter a customer
(defn customer? [inviter]
  (if (= (get-in @customer-tree [inviter :cid]) nil)
    false
    true))

;; RULES FUNCTIONS

;; Rank sorted by score - Build a map from customer-tree atom to send as response to rank endpoint
(defn rank []
  (let [customers (vals @customer-tree)]
    (if (nil? customers)
      []
      (map #(select-keys % [:cid :score])
           (sort-by :score > customers)))))

;; Increment score of node based on rule (1/2)^k - 
;; where k is the distance from root - Start from 0 for children
(defn inc-score [inviter-level invitee-level]
  (math/expt 0.5 (- (- invitee-level 1) inviter-level)))

;; Score increment recursion
(defn score-inc [inviter invitee]
  (swap! customer-tree update-in [inviter :score] + (inc-score (get-level inviter) (get-level invitee)))
  (if (inviter? inviter) ;; If it's not root score-inc recursively up to root
    (score-inc (get-inviter inviter) invitee)))   

;; Confirm invitation
(defn confirm-invitation [inviter]
  (if (not (confirmed? inviter))
    (if (inviter? inviter) ;; score-inc if inviter isn't root
      (score-inc (get-inviter inviter) inviter)))
  (swap! customer-tree assoc-in [inviter :confirmed] true))

;; Invitee = (inc inviter-level)
(defn inc-level [inviter]
  (inc (get-level inviter)))

;; Add new invitation into the customer-tree
(defn add-invitation [inviter invitee]
  (if (nil? (get-cid inviter)) ;; Inviter is nil - create root node (:inviter nil) and add to the tree 
    (swap! customer-tree assoc inviter {:cid inviter :score 0 :level 0 :inviter nil :confirmed true})) 
  (if (nil? (get-cid invitee)) ;; Invitee is nil - add invitee to the tree with increased level from its inviter
    (let [inc-level (inc-level inviter)]
	     (swap! customer-tree assoc invitee {:cid invitee :score 0 :level inc-level :inviter inviter :confirmed false})
      (if (inviter? inviter) ;; Verify if inviter has parent
        (confirm-invitation inviter)))
    (confirm-invitation  inviter)))

;; Execute add invitation from REST API into sync new thread
(defn add-invitation-thread [inviter invitee]
  ;;(prn (str "Adding new invitation from REST Service -> [" inviter "," invitee "]"))
  (future (dosync (add-invitation inviter invitee))))

;; VALIDATION FUNCTIONS

;; Parse input string to accept only positive integer number
(defn parse-number [s]
  (if (re-find #"^[1-9]\d*$" s)
    (read-string s)))

;; Inviter cannot invite his own inviter
(defn invalid-invitation [inviter invitee]
  (let [ invitee-cust (get-cid (parse-number invitee))
         inviter-invitee (get-inviter (parse-number inviter))]
    (if (and (not (nil? invitee-cust)) 
             (not (nil? inviter-invitee))
             (= invitee-cust inviter-invitee))
      false
      true)))

;; Validate invitation
(defn validate-invitation [inviter invitee]
  (if (and (not (= (parse-number inviter) (parse-number invitee))) ;; and inviter does not invite himself
           (or (customer? (parse-number inviter)) 
               (empty? @customer-tree)) ;; Or customer-tree is empty , inviter is customer
           (invalid-invitation inviter invitee)) ;; Inviter cannot invite his own inviter
    true
    false))

;; INITIAL LOAD - first args or default-filename

;; Process each line of file (splitting single spaces)
(defn process-line [line] 
  (let [[inviter invitee] (str/split line #"\s+")]
    (if (validate-invitation inviter invitee)
      (add-invitation (parse-number inviter) (parse-number invitee))))) 
      ;;(prn (str "Invalid invitation -> [" inviter "," invitee "]")) 

;; Load customer-tree with filename
(defn load-customer-tree [filename]
  (if (not (nil? filename)) ;; Verify param - Nil process default filename
    (doseq [line (line-seq (reader filename))] (process-line line))
    (doseq [line (line-seq (reader default-filename))] (process-line line))));; Process each file line [inviter invitee]
;;(print-customer-tree))