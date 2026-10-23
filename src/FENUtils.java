import java.util.ArrayList;

public class FENUtils {

    public static char[][] parseBoard(String fenBoard) {
        char[][] board = new char[8][8];
        String[] ranks = fenBoard.split("/");
        for (int i = 0; i < ranks.length; i++) {
            int j = 0;
            for (char pce : ranks[i].toCharArray()) {
                if (Character.isDigit(pce)) {
                    for (int k = 0; k < (pce - '0'); k++) {
                        board[j++][7-i] = '.';
                    }
                } else {
                    board[j++][7-i] = pce;
                }
            }
        }
        return board;
    }

    public static String fenBoard(char[][] board) {
        ArrayList<String> fenBoard = new ArrayList<>();
        int empty = 0;
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                if (board[file][rank] == '.') empty++;
                else {
                    if (empty > 0) {
                        fenBoard.add(String.valueOf(empty));
                        empty = 0;
                    }
                    fenBoard.add(String.valueOf(board[file][rank]));
                }
            }
            if (empty > 0) {
                fenBoard.add(String.valueOf(empty));
                empty = 0;
            }
            fenBoard.add("/");
        }
        fenBoard.removeLast();
        return String.join("", fenBoard);

    }

}