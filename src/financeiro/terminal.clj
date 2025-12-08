(ns financeiro.terminal
    (:require [financeiro.service :as svc]
              [financeiro.db :as db]))

  ;; ==============================
  ;;      CONSULTAR AÇÃO
  ;; ==============================
  (defn mostrar-consulta []
    (print "Digite o ticker (ex: PETR4): ")
    (flush)
    (let [ticker (read-line)
          acao (svc/consultar-acao ticker)]
      (println "\n==== CONSULTA DE AÇÃO ====")
      (if (:erro acao)
        (println "Erro:" (:erro acao))
        (doseq [[k v] acao]
          (println (str (name k) ": " v))))
      (println "==========================\n")))

  ;; ==============================
  ;;     REGISTRAR COMPRA
  ;; ==============================
  (defn registrar-compra []
    (print "Ticker(ex: PETR4, MGLU3, VALE3, ITUB4): ") (flush)
    (let [ticker (read-line)]
      (print "Quantidade: ") (flush)
      (let [quantidade (Integer/parseInt (read-line))]
        (print "Data (YYYY-MM-DD): ") (flush)
        (let [data (read-line)
              dados (svc/consultar-acao ticker)]
          (if (:erro dados)
            (println "Erro ao consultar ação:" (:erro dados))
            (let [preco (:preco dados)
                  total (* preco quantidade)
                  trans {:tipo "compra"
                         :data data
                         :ticker ticker
                         :quantidade quantidade
                         :valor total}]
              (db/add-transacao! trans)
              (println "\n=== COMPRA REGISTRADA ===")
              (println trans)
              (println "==========================\n")))))))

 ;; ==============================
 ;;     REGISTRAR VENDA
 ;; ==============================
 (defn registrar-venda []
   (print "Ticker(ex: PETR4, MGLU3, VALE3, ITUB4): ") (flush)
   (let [ticker (read-line)]
     (print "Quantidade: ") (flush)
     (let [quantidade (Integer/parseInt (read-line))]
       (print "Data (YYYY-MM-DD): ") (flush)
       (let [data (read-line)
             dados (svc/consultar-acao ticker)]
         (if (:erro dados)
           (println "Erro ao consultar ação:" (:erro dados))
           (let [preco (:preco dados)
                 total (* preco quantidade)
                 trans {:tipo "venda"
                        :data data
                        :ticker ticker
                        :quantidade quantidade
                        :valor total}]
             (db/add-transacao! trans)
             (println "\n=== VENDA REGISTRADA ===")
             (println trans)
             (println "==========================\n")))))))

;; ==============================
;;     EXTRATO POR PERÍODO
;; ==============================
(defn mostrar-extrato-por-periodo []
  (print "Data inicial (YYYY-MM-DD): ") (flush)
  (let [di (read-line)]
    (print "Data final (YYYY-MM-DD): ") (flush)
    (let [df (read-line)]
      (try
        (let [res (db/filtrar-por-periodo di df)]
          (println "\n==== EXTRATO POR PERÍODO ====")
          (if (empty? res)
            (println "Nenhuma transação encontrada nesse período.")
            (doseq [t res]
              (println t)))
          (println "==============================\n"))
        (catch Exception e
          (println "Erro ao filtrar período:" (.getMessage e)))))))

;; ==============================
;;     SALDO DA CARTEIRA
;; ==============================
(defn mostrar-saldo []
  (println "\n==== SALDO DA CARTEIRA ====")
  (println (format "%.2f" (db/obter-saldo)))
  (println "============================\n"))

;; ==============================
;;     MENU DO SISTEMA
;; ==============================
(defn menu []
  (println "==============================")
  (println "   GERENCIADOR DE AÇÕES")
  (println "==============================")
  (println "1) Consultar ação")
  (println "2) Registrar compra")
  (println "3) Registrar venda")
  (println "4) Extrato por período")
  (println "5) Saldo da carteira")
  (println "6) Sair")
  (print "> ") (flush))

(defn iniciar []
  (println "Iniciando terminal financeiro...")
  (loop []
    (menu)
    (let [op (read-line)]
      (cond
        (= op "1") (mostrar-consulta)
        (= op "2") (registrar-compra)
        (= op "3") (registrar-venda)
        (= op "4") (mostrar-extrato-por-periodo)
        (= op "5") (mostrar-saldo)
        (= op "6") (println "Saindo hehe\n" "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀                   ⢀⣠⠖⣚⣋⣉⡻⠶⡄⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡰⠋⣠⠞⠁⠀⠀⠛⡆⠹⡄⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡞⠑⡶⠁⠀⠀⠀⠀⠀⢸⠀⡇⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠇⢸⠁⢠⡀⠀⠀⠀⠀⡸⢸⠁⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠚⠫⢟⣮⣧⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣘⡦⢌⣓⡤⣹⡆⠀⠀⣰⢣⠏⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡴⠛⠛⠛⠲⣄⣸⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠞⣡⠔⠒⠒⠮⡹⠚⢯⣉⣵⠏⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⡏⠁⠀⠁⢀⡤⠚⠉⠙⡍⢧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⡰⠁⠀⠀⠀⠀⠈⢆⠀⢳⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡀⠀⠀⠀⠀⣤⢀⠀⢹⠈⢧⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣟⣲⠁⠀⠀⠀⠀⢀⣀⣸⠀⡞⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢧⡔⠀⠀⢀⣽⣿⠁⠀⡇⠸⡄⠀⠀⠀⠀⠀⠀⠀⣸⣅⡇⡏⠑⠄⠀⠀⠀⠀⣸⢣⡼⠁⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣇⠀⠀⠸⡿⠛⠀⣾⣉⣶⣃⠀⠀⠀⠀⠀⠀⡿⣿⠿⣆⡼⡄⠀⠀⠀⠀⡴⠁⡸⠃⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣆⡀⠀⠀⣀⡤⠋⠀⣁⣨⣇⠤⠤⠤⠤⠤⣾⣿⣭⣀⠈⠉⠢⢄⣀⡞⠁⡴⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣯⢉⣉⣥⣔⠾⠛⠉⠁⠀⠀⠀⠀⠀⠰⣿⣟⡏⠻⣕⠢⢤⣀⣀⣠⠞⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⣠⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠋⠀⠀⠙⢶⡋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠋⠀⠀⠀⡜⠀⠀⡴⠀⠀⠀⠰⣄⠀⠀⠀⠀⠀⠀⠀⠻⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡼⠁⠀⠀⣠⡼⣤⡤⣞⠀⠀⠀⠀⠀⠘⣆⣀⣀⡇⠀⣸⠀⠀⠙⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣸⠃⠀⢰⣞⡕⠉⠁⠈⠙⢷⡤⠃⠐⢤⣠⠾⠓⠒⠫⣝⢅⠀⡀⠀⢿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⠀⢸⡜⢀⡴⠚⠉⠒⢜⣆⡀⠀⢠⢇⡠⠤⠤⣀⠈⢟⡏⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣸⢀⡈⠉⢿⣃⣜⠰⣶⣰⠀⡸⠯⠕⠒⠿⡏⠀⡄⠠⠈⣆⢸⡏⠁⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⠋⠀⠀⠉⠻⣺⣄⣈⡠⠞⠃⢀⣀⠀⠀⠙⢦⡙⠞⠀⣾⣿⠁⠀⢰⢀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡄⠀⠀⢠⠆⠀⣡⠂⣀⠴⠊⠁⠀⠉⠙⢲⠠⣍⠑⠚⠊⠉⠉⠀⡏⣼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢷⡄⢀⠇⠀⡞⠁⠀⣹⠂⠀⠀⠀⠀⢀⡞⠀⠈⢳⠀⠀⠀⠀⢰⡿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢻⡿⢤⢻⠁⠀⡀⡝⠋⠉⠉⠓⠢⡎⠀⢀⠀⠀⠇⠀⠀⢀⣼⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣷⡈⣞⠲⠼⢻⣀⣀⠀⠀⢀⡼⠁⠀⣼⣆⠀⠐⢠⡶⣯⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡟⢌⠪⣝⡒⠼⣀⣉⣹⡳⠿⠒⢓⡖⠉⣓⡶⣗⣟⣷⡟⢀⡤⠤⢤⣤⣤⣄⣀⣀⠀⠀⠀⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⣈⠉⠷⢬⣵⡞⠛⠒⡟⠛⠛⣯⠟⠛⣷⣯⢠⠿⢷⡿⠋⠀⠀⠀⠈⠛⣶⣤⡈⡹⢦⡄⠀⠀⠀
                   ⠀⠀⠀⠀⠀⠀⠀⢀⣤⡶⠒⠛⠛⠒⢾⡿⠦⣀⠀⠀⠹⠤⣨⠧⠒⠟⢧⣀⠞⠁⠘⠃⣠⢿⡁⠀⠀⠀⠀⠀⠀⠈⢳⡈⠳⡄⠙⣆⠀⠀
                   ⠀⠀⠀⠀⣀⣠⣴⡏⠀⢷⡄⠀⠀⠀⠀⣿⠦⠈⠓⡤⢄⣀⣄⣀⣀⣀⣀⣀⣠⠤⠴⡞⠓⠚⣇⠀⠀⠀⠀⠀⠀⠀⠀⢧⠀⠘⡄⢸⡄⠀
                   ⠀⢀⡴⢛⡽⠚⡿⢷⣀⡼⠃⢠⡦⠀⠀⡿⠒⠁⠸⣅⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣇⠀⠀⠘⣆⠀⠀⠀⠀⠀⠀⠀⢸⠀⢠⠇⢰⠷⡀
                   ⠀⡾⢡⡏⠀⣠⣷⡀⡞⠀⠀⢯⠉⢦⣴⠁⠀⠀⠀⡞⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡆⠀⠀⠈⠳⣄⡀⠀⠀⠀⢠⠏⣠⠾⢚⡟⠀⢧
                   ⢸⠁⡏⡇⠀⢧⣈⢻⣇⠀⠀⠀⢷⡽⠁⠀⠀⠀⣠⢧⠀⠀⠀⠀⠀⣀⠀⠀⠀⠀⠀⠸⡅⠀⠀⠀⣠⣼⠛⠒⠶⣾⠛⠚⣡⡴⠋⠀⠀⢸
                   ⡾⠀⠣⠽⡦⠒⠁⠀⢈⡷⠦⢤⡞⠀⠀⠀⣀⠔⠁⣸⠀⠀⠀⣠⢴⡏⠳⢄⡀⠀⣀⣠⠗⠀⠠⣶⣾⠍⢠⠒⡆⢹⠎⠉⠁⠀⠀⡇⠀⢸
                   ⢷⠀⠀⠀⠘⠦⠟⠉⠉⠀⠀⡼⠀⠀⠀⠈⠁⠀⠀⠘⠉⠉⠉⠁⠀⠀⠀⠀⠹⢏⠀⠀⠐⠁⠀⠁⠺⠶⢋⣀⠓⠻⡄⠀⠀⠀⠈⠀⠀⡎
                   ⠘⠆⠀⠀⠀⠀⠀⠀⠀⠀⣸⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣸⠂⠀⠀⠀⠀⠀⠀⠀⠈⠉⠀⠀⢳⡀⠀⠀⠀⢀⠼⠀")
                   
        :else (println "Opção inválida."))

      (when (not= op "6")
        (recur)))))

(defn -main []
  (iniciar))