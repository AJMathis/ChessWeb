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
        for(Move move : gameState.getAllMoves()) {
            System.out.print(move.toString() + ", ");
            gameState.addMove(move, true);
            double eval = minimax(this.depth, false, gameState);
            if(bestMove == null || ((gameState.turn == 'w' && eval > bestEval) || (gameState.turn == 'b' && eval < bestEval))) {
                bestMove = move;
                bestEval = eval;
            }
            gameState.undoLastMove();
            //future.undoMove(move);//WHAT IF FUTURE=GAMESTATE THIS STOPS THE NEED FOR UNDO MOVE
        }
        System.out.println();
        return bestMove;
    }

    private double minimax(int depth, boolean maxPlayer, GameState currentState) {
        if(depth <= 0 || currentState.gameOver()) {
            return evaluation(currentState);
        }

        if(maxPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for(Move move : currentState.getAllMoves()){
                currentState.addMove(move, true);
                double eval = minimax(depth-1, false, currentState);
                maxEval = Math.max(eval, maxEval);
                currentState.undoLastMove();
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for(Move move : currentState.getAllMoves()){
                currentState.addMove(move, true);
                double eval = minimax(depth-1, true, currentState);
                minEval = Math.min(eval, minEval);
                currentState.undoLastMove();
            }
            return minEval;
        }
    }

    private double evaluation(GameState gameState) {
        return gameState.getEval();
    }
}
