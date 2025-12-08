(ns financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [financeiro.db :as db]
            [financeiro.service :as svc]))

(defroutes app-routes

  ;; 1. Consultar ação
  (GET "/acao/:ticker" [ticker]
    (resp/response (svc/consultar-acao ticker)))

  ;; 2. Registrar compra
  (POST "/compra" {body :body}
    (let [{:keys [data ticker quantidade preco-total]} body
          trans {:tipo "compra"
                 :data data
                 :ticker ticker
                 :quantidade quantidade
                 :valor preco-total}]
      (db/add-transacao! trans)
      (resp/response {:ok true :transacao trans})))

  ;; 3. Registrar venda
  (POST "/venda" {body :body}
    (let [{:keys [data ticker quantidade preco-total]} body
          trans {:tipo "venda"
                 :data data
                 :ticker ticker
                 :quantidade quantidade
                 :valor preco-total}]
      (db/add-transacao! trans)
      (resp/response {:ok true :transacao trans})))

  ;; 4. Extrato por período via API
  (GET "/extrato" [data-inicial data-final]
    (resp/response (db/filtrar-por-periodo data-inicial data-final)))

  ;; 5. Saldo da carteira
  (GET "/saldo" []
    (resp/response {:saldo (db/saldo)}))

  (route/not-found {:error "Rota não encontrada"}))
