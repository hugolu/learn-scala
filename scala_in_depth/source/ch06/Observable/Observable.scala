trait Observable {
    type Handle

    protected var callbacks = Map[Handle, this.type => Unit]()

    def observe(callback: this.type => Unit): Handle = {
        val handle = createHandle(callback)
        callbacks += (handle -> callback)
        handle
    }

    def unobserve(handle: Handle): Unit = {
        callbacks -= handle
    }

    protected def createHandle(callback: this.type => Unit): Handle 

    protected def notifyListeners(): Unit =
        for(callback <- callbacks.values) callback(this)
}
