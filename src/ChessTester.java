public class ChessTester {

    public static void testAddMove() {
        GameState state = new GameState();
        boolean valid = true;
        //Test
        valid &= (state.getFEN().equals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

        state.addMove(new Move('P', "e2", "e4"));
        valid &=(state.getFEN().equals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));

        state.addMove(new Move('p', "f7", "f5"));
        valid &=(state.getFEN().equals("rnbqkbnr/ppppp1pp/8/5p2/4P3/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 2"));
        System.out.println("testAddMove: " + (valid?"PASSED":"FAILED"));
    }
    public static void testEngineResponse() {
        GameState state = new GameState();
        ChessEngine engine = new ChessEngine(state, 4);
        boolean valid = true;

        valid &= (state.getFEN().equals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

        state.addMove(new Move('P', "e2", "e4"));
        valid &=(state.getFEN().equals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));

        engine.makeMove();
        valid &= (!state.getFEN().equals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));
        System.out.println("testEngineResponse: " + (valid?"PASSED":"FAILED") + " (Move Made: " + state.getHistory().getLast() + ")");
    }

    public static void main(String[] args) {
        testAddMove();
        testEngineResponse();
    }

}
