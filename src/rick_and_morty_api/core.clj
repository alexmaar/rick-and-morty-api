(ns rick-and-morty-api.core
  (:require [compojure.api.sweet :refer :all]
            [clj-http.client :as client]
            [ring.util.http-response :refer :all]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]))

(def characters (ref {})) ;map of characters which were searched
(def users (ref {})) ;map of users and amout of their requests

(defn get-by-number [number]
  (if (<= 0 (Integer/parseInt number) 591)
    (if-let [character (get @characters number)]
      (merge {:success true :source "db"} character)
      (let [body (:body (client/get (str "https://rickandmortyapi.com/api/character/" number)))
            content (json/read-str body :key-fn keyword)
           {name :name episode :episode} content
           character {number {:name name :episode episode}}]
        (dosync (alter characters conj character))
        (merge {:success true :source "api"} character)))
   {:success false :error-message "wrong number"}))

(defn get-by-number-and-nick [number nick]
  (dosync
   (if (get @users nick)
    (alter users update nick inc)
    (alter users conj {nick 1})))
  (get-by-number number))

(defn requests-number [nick]
  (if-let [requests-no (get @users nick)]
    {:success true :result requests-no}
    {:success false :error-message "user does not exists"}))

(def app
  (api
   (GET "/character" [number]
     (ok  (get-by-number number)))
   (GET "/character-and-nick" [number nick]
     (ok (get-by-number-and-nick number nick)))
   (GET "/requests-number" [nick]
     (ok (requests-number nick)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 3000}))