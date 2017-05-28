

package com.jatin.mapsdemo;

import com.jatin.mapsdemo.model.Direction;

public interface DirectionCallback {
    void onDirectionSuccess(Direction direction, String rawBody);
    void onDirectionFailure(Throwable t);
}
