public class TennisGameFixed {

    private static final int SCORE_LOVE = 0;
    private static final int SCORE_FIFTEEN = 1;
    private static final int SCORE_THIRTY = 2;
    private static final int SCORE_FORTY = 3;

    public static String getScore(String player1Name, String player2Name, int p1Score, int p2Score) {
        if (isTied(p1Score, p2Score)) {
            return getTiedScore(p1Score);
        }

        if (isEndGame(p1Score, p2Score)) {
            return getEndGameScore(p1Score, p2Score);
        }

        return getRegularScore(p1Score) + "-" + getRegularScore(p2Score);
    }

    private static boolean isTied(int p1Score, int p2Score) {
        return p1Score == p2Score;
    }

    private static boolean isEndGame(int p1Score, int p2Score) {
        return p1Score >= 4 || p2Score >= 4;
    }

    private static String getTiedScore(int score) {
        return switch (score) {
            case SCORE_LOVE -> "Love-All";
            case SCORE_FIFTEEN -> "Fifteen-All";
            case SCORE_THIRTY -> "Thirty-All";
            case SCORE_FORTY -> "Forty-All";
            default -> "Deuce";
        };
    }

    private static String getEndGameScore(int p1Score, int p2Score) {
        int scoreDifference = p1Score - p2Score;

        if (scoreDifference == 1) return "Advantage player1";
        if (scoreDifference == -1) return "Advantage player2";
        if (scoreDifference >= 2) return "Win for player1";
        return "Win for player2";
    }

    private static String getRegularScore(int score) {
        return switch (score) {
            case SCORE_LOVE -> "Love";
            case SCORE_FIFTEEN -> "Fifteen";
            case SCORE_THIRTY -> "Thirty";
            case SCORE_FORTY -> "Forty";
            default -> "";
        };
    }
}