(ns rick-and-morty-api.core
  (:require [compojure.api.sweet :refer :all]
            [clj-http.client :as client]
            [ring.util.http-response :refer :all]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]))

(def data-base (atom []))

(defn request-handler [number body]
  
  (let [character (first (filter #(= (:id %) number) @data-base))]
    (if character
    (str "Character's name from data-base: " (:name character) ". \nEpisodes: " (:episode character) ". ")
    (let [b (json/read-str (body (client/get (str "https://rickandmortyapi.com/api/character/" number))))
        name (get b "name")
        episode (get b "episode")]
      (swap! data-base conj {:id number :name name :episode episode})
      (str "Character's name: " name ". \nEpisodes: " episode ". ")))))
     
(def app
  (api
   (GET "/" [number]
     (ok  (request-handler number :body)))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 3000}))