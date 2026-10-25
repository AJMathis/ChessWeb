//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        GameState state = new GameState();
        //TEST 1
        System.out.println(state.getFEN());
        System.out.println(state.getFEN().equals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

        state.addMove(new Move('P', "e2", "e4"));

        System.out.println(state.getFEN());
        System.out.println(state.getFEN().equals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));

        state.addMove(new Move('P', "f2", "f4"));
    }
}