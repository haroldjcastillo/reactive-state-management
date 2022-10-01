package com.github.haroldjcastillo.rsm.counter;

import com.github.haroldjcastillo.rsm.store.Action;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CounterIncrementAction implements Action<Counter> {
    private final long delta;

    public Counter apply(Counter counter) {
        return new Counter(counter.getValue() + this.delta);
    }
}
