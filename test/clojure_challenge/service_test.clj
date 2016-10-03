(ns clojure-challenge.service-test
  (:require [clojure.test :refer :all]
            [clojure-challenge.service :refer :all]
            [clojure-challenge.model :refer :all]
            [ring.mock.request :as mock]))

(deftest api-handler-test  
  (reset! customer-tree {})
 
  (testing "Testing RANK SCORE API Handler"
    (is (= (api-dev (mock/request :get "/api/rank"))
        {:status  200
         :headers {"Content-Type" "application/json"}
         :body "[]"}))
   (is (= (count @customer-tree) 0)))
  
  (testing "Testing Redirect RANK SCORE API Handler"
    (is (= (api-dev (mock/request :get "/"))
        {:status  301
         :headers {"Location" "/api/rank"}
         :body ""})))

  (testing "Testing INVITE API POST Handler"
    (is (= (api-dev (mock/request :post "/api/invite/1/2"))
         {:status  201
          :headers {"Content-Type" "text/html ; charset=utf-8"}
          :body sucess-msg }))
    (is (= (count @customer-tree) 2)))
  
  (testing "Testing Invalid INVITE API POST Handler"
    (is (= (api-dev (mock/request :post "/api/invite/-1/a"))
         {:status  400
          :headers {"Content-Type" "text/html ; charset=utf-8"}
          :body inviter-error-msg })))
  
  (testing "Testing Page not found Handler"
    (is (= (api-dev (mock/request :get "/foo/bar"))
         {:status  404
          :headers {"Content-Type" "text/html ; charset=utf-8"}
          :body page-not-found })))
  )