(ns clojure-challenge.service
  (:use compojure.core)
  (:use ring.middleware.params)
  (:require [clojure-challenge.model :as model]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.util.response :refer [response redirect content-type not-found status]]
            [compojure.route :as route]
            [compojure.handler :as handler]))


;; Sucessful invitation
(def sucess-msg "Invitation sucessful")

;; Error processing
(def error-msg "Error processing request")

;; Inviter is not customer
(def inviter-error-msg "Invalid invitation request")

;; Default page not found message
(def page-not-found "Page Not Found")

;; RESTful API Definition

;; Handler to route REST API
(defroutes api-route 
  ;; Simple redirect from context root to Rank Score
  (GET "/" []
       (redirect "/api/rank" 301))
  
  ;; GET METHOD  to calculate rank of input file
  (GET "/api/rank" [] 
       (content-type (response (model/rank)) "application/json"))

  ;; POST METHOD to add a new invitation
  (POST "/api/invite/:inviter/:invitee" {params :params} []
        (let [ inviter (get params :inviter)
               invitee (get params :invitee)]
          (do
            (cond (model/validate-invitation inviter invitee) 
              (when true 
                (model/add-invitation-thread (model/parse-number inviter) (model/parse-number invitee))
                (status 
                  (content-type (response sucess-msg) "text/html ; charset=utf-8") 
                  201))
              :else (status 
                      (content-type 
                        (response inviter-error-msg) "text/html ; charset=utf-8") 
                      400)))))
    
  ;; ROUTE/not-found
  (route/not-found 
    (content-type (not-found page-not-found) "text/html ; charset=utf-8")))

;; Define exception handler
(defn wrap-exception-handling [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (prn e)
        (response error-msg)))))

;; API Handler (compojure.handler)
(def api-handler 
  (-> api-route handler/api))

;; API - Load from core.main
(defn api [args]
  (model/load-customer-tree (first args)) ;; Load customer tree with first args (default-filename args is nil)
  (wrap-params (-> api-handler 
                 (wrap-json-params)
                 (wrap-json-response)
                 (wrap-exception-handling))))

;; API - dev ring server and tests
(def api-dev
  (wrap-params (-> api-handler 
                 (wrap-json-params)
                 (wrap-json-response)
                 (wrap-exception-handling))))