object RTConsole_v2 {
  def getString = { state: WorldState => (state.nextState, Console.readLine) }
  def putString(s: String) = { state: WorldState => (state.nextState, Console.print(s)) }
}
