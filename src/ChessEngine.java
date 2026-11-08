public class ChessEngine {
    GameState gameState;
    GameState future;
    int depth;

    public ChessEngine(GameState gameState, int depth) {
        this.gameState = gameState;
        this.depth = depth;
    }
    public void makeMove() {
        Move move = bestMove();
        gameState.addMove(move);
    }

    public Move bestMove() {
        Move bestMove = null;
        double bestEval = Double.NEGATIVE_INFINITY;
        future = this.gameState;
        for(Move move : future.getAllMoves()) {
            System.out.println(move.toString() + " turn=" + future.turn);
            future.addMove(move);
            double eval = minimax(this.depth, false);
            if(bestMove == null || eval > bestEval) {
                bestMove = move;
                bestEval = eval;
            }
            future.undoMove(move);
        }
        return bestMove;
    }

    private double minimax(int depth, boolean maxPlayer) {
        if(future == null) future = this.gameState;
        if(depth <= 0 || future.gameOver()) {
            return evaluation(future);
        }

        if(maxPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for(Move move : gameState.getAllMoves()){
                simulateMove(move);
                double eval = minimax(depth-1, false);
                maxEval = Math.max(eval, maxEval);
                undoMove(move);
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for(Move move : gameState.getAllMoves()){
                simulateMove(move);
                double eval = minimax(depth-1, true);
                minEval = Math.min(eval, minEval);
                undoMove(move);
            }
            return minEval;
        }
    }

    private double evaluation(GameState game) {
        return game.getEval();
    }

    private void simulateMove(Move move) {
        future.addMove(move);
    }

    private void undoMove(Move move) {
        future.undoMove(move);
    }
}
