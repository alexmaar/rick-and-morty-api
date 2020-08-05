(ns rick-and-morty-api.core
  (:require [compojure.api.sweet :refer :all]
            [clj-http.client :as client]
            [ring.util.http-response :refer :all]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]))

(def characters (ref {}))
(def users (ref {}))


(defn get-by-number [number body]
  (if-let [character (get @characters number)]
    (str "Character's name from data-base: " (:name character) ". \nEpisodes: " (:episode character) ". ")
    (let [b (json/read-str (body (client/get (str "https://rickandmortyapi.com/api/character/" number))))
        name (get b "name")
        episode (get b "episode")]
      (dosync (alter characters conj {number {:name name :episode episode}}))
      (str "Character's name: " name ". \nEpisodes: " episode ". "))))

(defn get-by-number-and-nick [number nick body]
  (if-let [req-no (get @users nick)]
    (dosync 
     (alter users dissoc nick)
     (alter users conj {nick (inc req-no)})
    (dosync (alter users conj {nick 1})))
  (get-by-number number body)))


(def app
  (api
   (GET "/number" [number]
     (ok  (get-by-number number :body)))
   (GET "/number-and-nick" [number nick]
     (ok (get-by-number-and-nick number nick :body)))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 3000}))