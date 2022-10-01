package com.github.haroldjcastillo.rsm;

import com.github.haroldjcastillo.rsm.counter.Counter;
import com.github.haroldjcastillo.rsm.counter.CounterDecrementAction;
import com.github.haroldjcastillo.rsm.counter.CounterIncrementAction;
import com.github.haroldjcastillo.rsm.store.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.ThreadLocalRandom;

class CounterStoreTest {
    private long initial;
    private Store<Counter> counterStore;
    private Flux<Long> subject;

    @BeforeEach
    void setUp() {
        initial = getRandomLong();
        counterStore = new Store<>(new Counter(initial));
        subject = counterStore.value().map(Counter::getValue);
    }

    private static long getRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    @Test
    void initialValue() {
        StepVerifier.create(subject.take(1))
                .expectNext(initial)
                .verifyComplete();
    }
    
    @Test
    void dispatchIncrementAction() {
        long delta = getRandomLong();
        StepVerifier.create(subject.take(2))
                .then(() -> counterStore.dispatch(new CounterIncrementAction(delta)))
                .expectNext(initial, initial + delta)
                .verifyComplete();
    }

    @Test
    void dispatchDecrementAction() {
        long delta = getRandomLong();
        StepVerifier.create(subject.take(2))
                .then(() -> counterStore.dispatch(new CounterDecrementAction(delta)))
                .expectNext(initial, initial - delta)
                .verifyComplete();
    }

    @Test
    void dispatchIncrementAndDecrementByDelta() {
        var delta1 = getRandomLong();
        var delta2 = getRandomLong();
        var delta3 = getRandomLong();
        var delta4 = getRandomLong();
        var delta5 = getRandomLong();
        StepVerifier.create(subject.take(6))
                .then(() -> {
                    counterStore.dispatch(new CounterDecrementAction(delta1));
                    counterStore.dispatch(new CounterIncrementAction(delta2));
                    counterStore.dispatch(new CounterIncrementAction(delta3));
                    counterStore.dispatch(new CounterIncrementAction(delta4));
                    counterStore.dispatch(new CounterDecrementAction(delta5));
                })
                .expectNext(initial, initial - delta1,
                        initial - delta1 + delta2,
                        initial - delta1 + delta2 + delta3,
                        initial - delta1 + delta2 + delta3 + delta4,
                        initial - delta1 + delta2 + delta3 + delta4 - delta5)
                .verifyComplete();
    }
}
