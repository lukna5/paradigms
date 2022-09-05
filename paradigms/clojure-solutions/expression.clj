
(defn operateArg [f] (fn [& g]
                       (fn [lets]
                         (apply f (mapv #(%1 lets) g)))))
(defn constant [num] (constantly num))
(defn variable [letter] (fn [lets] (lets letter)))
(def add (operateArg +))
(def subtract (operateArg -))
(def multiply (operateArg *))
(def divide (operateArg #(/ (double %1) %2)))
(def cos (operateArg #(Math/cos %1)))
(def sin (operateArg #(Math/sin %1)))
(def negate (operateArg #(* -1 %1)))
(def operations2Arg {'+ add, '- subtract, '* multiply,'/ divide})
(def operations1Arg {'negate negate, 'sin sin, 'cos cos})
(defn parseOperation [exp] (cond
                             (number? exp) (constant exp)
                             (symbol? exp) (variable (str exp))
                             (contains? operations1Arg (first exp)) ((operations1Arg (first exp)) (parseOperation (nth exp 1)))
                             (contains? operations2Arg (first exp)) (apply (operations2Arg (first exp)) (mapv parseOperation (rest exp)))
                             ))
(defn parseFunction [exp] (parseOperation (read-string exp)))

(defn pget [obj key]
  (cond
    (contains? obj key) (obj key)
    (contains? obj :prototype) (pget (obj :prototype) key)
    :else nil))

(defn pcall [obj key & args]
  (apply (pget obj key) obj args))

(defn field [key]
  #(pget % key))

(defn method  [key]
  #(apply pcall %1 key %&))

(def toString (method :toString))
(def evaluate (method :evaluate))
(def diff (method :diff))

(def Constant)
(def Add)
(def Subtract)
(def Multiply)
(def Divide)
(def Negate)
(def Cos)
(def Sin)

(def ConstantPrototype
  {
   :toString (fn [this] (format "%.1f"((field :value) this)))
   :evaluate (fn [this _] ((field :value) this))
   :diff (fn [& args] (Constant 0.0))
   })

(defn Constant [number]
  {
   :prototype ConstantPrototype
   :value number
   })

(def VariablePrototype
  {
   :toString (fn [this] ((field :letter) this))
   :evaluate (fn [this letter] (letter ((field :letter) this)))
   :diff (fn [this letter] (if (= letter ((field :letter) this)) (Constant 1.0) (Constant 0.0)))
   })

(defn Variable [letter]
  {
   :prototype VariablePrototype
   :letter letter
   })

(def diffFunctions
  {
   "negate" (fn [f letter] (Negate (diff (first f) letter)))
   "sin" (fn [f letter] (Multiply
                          (Cos (first f))
                          (diff (first f) letter)))
   "cos" (fn [f letter] (Negate
                          (Multiply
                            (Sin (first f))
                            (diff (first f) letter))))
   "+" (fn [f letter] (Add
                        (diff (first f) letter)
                        (diff (second f) letter)))
   "-" (fn [f letter] (Subtract
                        (diff (first f) letter)
                        (diff (second f) letter)))
   "*" (fn [f letter] (Add (Multiply (diff (first f) letter) (second f))
                           (Multiply (first f) (diff (second f) letter))))
   "/" (fn [f letter] (Divide
                        (Subtract
                          (Multiply (diff (first f) letter) (second f))
                          (Multiply (first f) (diff (second f) letter)))
                        (Multiply (second f) (second f))))
   })
(def OperationPrototype
  {
   :toString (fn [this]
               (str "("
                    ((field :sign) this) " "
                    (clojure.string/join " " (mapv toString ((field :f) this)))
                    ")" ))
   :evaluate (fn [this letter] (apply ((field :op) this)
                                      (mapv #(evaluate %1 letter) ((field :f) this))
                                      ))
   :diff (fn [this letter] ((get diffFunctions ((field :sign) this))
                            (vector (first ((field :f) this)) (second ((field :f) this))) letter))
   }
  )

(defn createOp [sign op]
  (fn [& args]
    {
     :prototype OperationPrototype
     :sign sign
     :f (vec args)
     :op op
     }
    )
  )

(def Negate (createOp "negate" #(* %1 -1)))
(def Add (createOp "+" + ))
(def Subtract (createOp "-" - ))
(def Multiply (createOp "*" * ))
(def Divide (createOp "/" #(/ (double %1) %2)))
(def Sin (createOp "sin" #(Math/sin %1)))
(def Cos (createOp "cos" #(Math/cos %1)))
(def Operations2Arg {'+ Add, '- Subtract, '* Multiply,'/ Divide})
(def Operations1Arg {'negate Negate 'sin Sin, 'cos Cos})

(defn parseOperationObject [exp] (cond
                                   (number? exp) (Constant exp)
                                   (symbol? exp) (Variable (str exp))
                                   (contains? Operations1Arg (first exp)) ((Operations1Arg (first exp))
                                                                           (parseOperationObject (nth exp 1)))
                                   (contains? Operations2Arg (first exp)) (apply (Operations2Arg (first exp))
                                                                                 (mapv parseOperationObject (rest exp)))
                                   ))
(defn parseObject [exp] (parseOperationObject (read-string exp)))
