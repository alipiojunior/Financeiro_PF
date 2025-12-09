(ns financeiro.db
  (:import (java.time LocalDate Instant ZoneId)
           (java.time.format DateTimeParseException)))

(def transacoes (atom []))
(def saldo (atom 0.0))

(defn parse-data
  "Tenta converter s (string ou LocalDate) para java.time.LocalDate.
   Aceita 'YYYY-MM-DD' e timestamps ISO como '2025-12-08T14:15:00.000Z'.
   Retorna nil se não conseguir parsear."
  [s]
  (cond
    (nil? s) nil
    (instance? LocalDate s) s
    :else
    (try 
      
      (LocalDate/parse (str s))
      (catch Exception _
        (try
          (-> (Instant/parse (str s))
              (.atZone (ZoneId/systemDefault))
              (.toLocalDate))
          (catch Exception _
            nil))))))

(defn parse-valor
  "Converte diferentes tipos de :valor para double. Se falhar, retorna 0.0."
  [v]
  (cond
    (nil? v) 0.0
    (number? v) (double v)
    (string? v) (try (Double/parseDouble v) (catch Exception _ 0.0))
    :else 0.0))


(defn add-transacao!
  "Adiciona transação ao átomo transacoes e atualiza o saldo.
   t deve ter chaves: :tipo (\"compra\" ou \"venda\"), :data (string ou LocalDate),
   :ticker, :quantidade, :valor (número ou string). Retorna a transação gravada."
  [t]
  (let [valor (parse-valor (:valor t))
        data-ld (or (parse-data (:data t)) 
                    nil)
        trans (-> t
                  (assoc :valor valor)
                  (assoc :data (if data-ld (.toString data-ld) (:data t))))]
    
    (swap! transacoes conj trans)
    
    (swap! saldo (fn [s]
                   (case (:tipo trans)
                     "compra" (- s valor)
                     "venda"  (+ s valor)
                     s)))
    trans))

(defn listar-transacoes
  "Retorna todas as transações (vector)."
  []
  @transacoes)

(defn obter-saldo
  "Retorna o saldo atual (double)."
  []
  @saldo)

(defn filtrar-por-periodo
  "Filtra transações pelo intervalo [data-inicial, data-final] (inclusive).
   data-inicial e data-final podem ser strings 'YYYY-MM-DD' ou ISO timestamps.
   Lança ex-info se as datas forem inválidas."
  [data-inicial data-final]
  (let [di (parse-data data-inicial)
        df (parse-data data-final)]
    (when (or (nil? di) (nil? df))
      (throw (ex-info "Formato de data inválido. Use YYYY-MM-DD ou ISO-8601."
                      {:data-inicial data-inicial :data-final data-final})))
    (->> @transacoes
         (filter (fn [t]
                   (let [td (parse-data (:data t))]
                     (when td
                       (and (not (.isBefore td di))
                            (not (.isAfter td df)))))))
         vec)))

(defn reset-db!
  "Reseta transacoes e saldo (útil para testes)."
  []
  (reset! transacoes [])
  (reset! saldo 0.0)
  {:ok true})

(defn carregar-exemplo!
  "Insere algumas transações de exemplo (útil para testar extrato e saldo)."
  []
  (reset-db!)
  (add-transacao! {:tipo "compra"  :data "2025-12-03" :ticker "PETR4" :quantidade 100 :valor "3163.0"})
  (add-transacao! {:tipo "venda"   :data "2025-12-06" :ticker "PETR4" :quantidade 50  :valor "1581.5"})
  (add-transacao! {:tipo "compra"  :data "2025-12-08T14:15:00.000Z" :ticker "VALE3" :quantidade 10 :valor 300.0})
  {:ok true})
