package com.aau.wizard;

public class GameExceptions {

    public static class GameNotFoundException extends RuntimeException {
        public GameNotFoundException(String message) {
            super(message);
        }
    }

    public static class GameStartException extends RuntimeException {
        public GameStartException(String message) {
            super(message);
        }
    }

    public static class InvalidPredictionException extends RuntimeException {
        public InvalidPredictionException(String message) {
            super(message);
        }
    }

    public static class PlayerNotFoundException extends RuntimeException {
        public PlayerNotFoundException(String message) {
            super(message);
        }
    }

    public static class RoundLogicException extends RuntimeException {
        public RoundLogicException(String message) {
            super(message);
        }
    }

    public static class InvalidTurnException extends RuntimeException {
        public InvalidTurnException(String message) {
            super(message);
        }
    }

    public static class CardNotInHandException extends RuntimeException {
        public CardNotInHandException(String message) {
            super(message);
        }
    }

    public static class GameAlreadyEndedException extends RuntimeException {
        public GameAlreadyEndedException(String message) {
            super(message);
        }
    }

    public static class GameNotActiveException extends RuntimeException {
        public GameNotActiveException(String message) {
            super(message);
        }
    }

    public static class RoundProgressionException extends RuntimeException {
        public RoundProgressionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
