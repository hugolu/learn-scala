trait Defaulthandles extends Observable {
    type Handle = (this.type => Unit)
    protected def createHandle(callback: this.type => Unit): Handle =
        callback
}
