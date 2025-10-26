package chess.model.http;

import java.util.Collection;

public record ListGamesResult(Collection<GameInfo> games) {}