(ns rick-and-morty-api.core
  (:require [compojure.api.sweet :refer :all]
            [clj-http.client :as client]
            [ring.util.http-response :refer :all]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]))

(def characters (ref {})) ;map of characters which were searched
(def users (ref {})) ;map of users and amout of their requests

(defn get-by-number [number]
  (if (<= 0 number 591)
  ;; (if (<= 0 (Integer/parseInt number) 591)
    (if-let [character-data (get @characters number)]
      (merge {:success true :source "db"}  {number character-data})
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
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Rick and Morty API"
                   :description "Compojure Api example"}
            :tags [{:name "api", :description "Rick and Morty API"}]
            :consumes ["application/json"]
            :produces ["application/json"]}}}
   (context "/api" []
     :tags ["api"]
     (GET "/character" []
       :query-params [id :- Long]
       (ok  (get-by-number id)))
     (GET "/character-and-nick" []
       :query-params [id :- Long, nick :- String]
       (ok (get-by-number-and-nick id nick)))
     (GET "/requests-number" []
       :query-params [nick :- String]
       (ok (requests-number nick))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 3000}))

