(ns financeiro.service
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]))

(def API_KEY "nnKP65aYYmdJQaNZdzffGu")
(def BASE_URL "https://brapi.dev/api/quote/")

(defn- normalize-quote [q]
  {:ticker               (:symbol q)
   :nome                 (:longName q)
   :preco                (:regularMarketPrice q)
   :abertura             (:regularMarketOpen q)
   :maxima               (:regularMarketDayHigh q)
   :minima               (:regularMarketDayLow q)
   :fechamento-anterior  (:regularMarketPreviousClose q)
   :variacao             (:regularMarketChange q)
   :variacao-percentual  (:regularMarketChangePercent q)
   :volume               (:regularMarketVolume q)
   :data                 (:regularMarketTime q)})

(defn consultar-acao [ticker]
  (let [ticker (-> ticker str/trim str/upper-case)
        url (str BASE_URL ticker "?token=" API_KEY)
        resp (http/get url {:throw-exceptions false :as :string})
        status (:status resp)
        body (:body resp)]

    (println "\n===== DEBUG API =====")
    (println "GET" url "HTTP" status)
    (println body)
    (println "=====================\n")

    (cond
      (not= status 200)
      {:erro (str "HTTP " status)}

      :else
      (let [m (json/parse-string body true)
            results (:results m)]
        (if (seq results)
          (normalize-quote (first results))
          {:erro "Nenhum dado retornado"})))))
