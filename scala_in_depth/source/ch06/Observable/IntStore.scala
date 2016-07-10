class IntStore(private var value: Int) extends Observable with DefaultHandles {
    def get: Int = value
    def set(newValue: Int): Unit = {
        value = newValue
        notifyListeners()
    }

     override def toString: String = "IntStore(" + value + ")"
}
