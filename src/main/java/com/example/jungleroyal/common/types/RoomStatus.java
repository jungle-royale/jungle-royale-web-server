package com.example.jungleroyal.common.types;

public enum RoomStatus {
    WAITING, // Waiting for players
    RUNNING, // Game is ongoing
    END // Game has ended
    ;

    public boolean canShow() {
        // 게임이 끝난건 표시할 필요가 없다.
        // 게임 중인건 reconnection이 가능하기 때문에 보여야 한다.
        return this != END;
    }
}
