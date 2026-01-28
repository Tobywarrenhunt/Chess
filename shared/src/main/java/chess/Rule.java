package chess;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

public class Rule {
    private final boolean isSliding;
    private final int[][] directions;

    public Rule(boolean isSliding, int[][] directions) {
        this.isSliding = isSliding;
        this.directions = directions;

    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition fromPos, ChessPiece piece) {
        Set<ChessMove> moves = new HashSet<>();

        int fromRow = fromPos.getRow();
        int fromCol = fromPos.getColumn();

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int steps = 1;

            while (true) {
                int newRow = fromRow + dx * steps;
                int newCol = fromCol + dy * steps;

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) break;

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(newPos);

                if (target == null) {
                    moves.add(new ChessMove(fromPos, newPos, null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(fromPos, newPos, null));
                    }
                    break;
                }

                if (!isSliding) break;
                steps++;
            }
        }

        return moves;

    }

}
