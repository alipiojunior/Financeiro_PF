(ns financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [financeiro.db :as db]
            [financeiro.service :as svc]))

(defroutes app-routes

  
  (GET "/acao/:ticker" [ticker]
    (resp/response (svc/consultar-acao ticker)))

  
  (POST "/compra" {body :body}
    (let [{:keys [data ticker quantidade preco-total]} body
          trans {:tipo "compra"
                 :data data
                 :ticker ticker
                 :quantidade quantidade
                 :valor preco-total}]
      (db/add-transacao! trans)
      (resp/response {:ok true :transacao trans})))

  
  (POST "/venda" {body :body}
    (let [{:keys [data ticker quantidade preco-total]} body
          trans {:tipo "venda"
                 :data data
                 :ticker ticker
                 :quantidade quantidade
                 :valor preco-total}]
      (db/add-transacao! trans)
      (resp/response {:ok true :transacao trans})))

  
  (GET "/extrato" [data-inicial data-final]
    (resp/response (db/filtrar-por-periodo data-inicial data-final)))

  
  (GET "/saldo" []
    (resp/response {:saldo (db/saldo)}))

  (route/not-found {:error "Rota não encontrada"}))
