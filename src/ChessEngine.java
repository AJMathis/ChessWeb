public class ChessEngine {
    GameState gameState;
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
        GameState future = new GameState(gameState);
        for(Move move : gameState.getAllMoves()) {
            System.out.println(move.toString() + " turn=" + future.turn);
            future.addMove(move);
            double eval = minimax(this.depth, false, future);
            if(bestMove == null || eval > bestEval) {
                bestMove = move;
                bestEval = eval;
            }
            future = new GameState(gameState);
            //future.undoMove(move);//WHAT IF FUTURE=GAMESTATE THIS STOPS THE NEED FOR UNDO MOVE
        }
        return bestMove;
    }

    private double minimax(int depth, boolean maxPlayer, GameState currentState) {
        if(depth <= 0 || currentState.gameOver()) {
            return evaluation(currentState);
        }

        if(maxPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for(Move move : currentState.getAllMoves()){
                GameState nextState = new GameState(currentState);
                nextState.addMove(move);
                double eval = minimax(depth-1, false, nextState);
                maxEval = Math.max(eval, maxEval);
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for(Move move : currentState.getAllMoves()){
                GameState nextState = new GameState(currentState);
                nextState.addMove(move);
                double eval = minimax(depth-1, true, nextState);
                minEval = Math.min(eval, minEval);
            }
            return minEval;
        }
    }

    private double evaluation(GameState gameState) {
        return gameState.getEval();
    }
}
