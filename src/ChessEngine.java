public class ChessEngine {
    GameState gameState;
    GameState future;
    public ChessEngine(GameState gameState) { this.gameState = gameState;
    }
    public void makeMove() {
        Move move = bestMove();
        gameState.addMove(move);
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

    private double evaluation(GameState future) {
        return GameState.getEval();
    }

    private void simulateMove(Move move) {
        future.addMove(move);
    }

    private void undoMove(Move move) {
        future.undoMove(move);
    }
}
