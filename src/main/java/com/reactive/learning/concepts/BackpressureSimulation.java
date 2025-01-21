package com.reactive.learning.concepts;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author : Houssam KOURDACHE
 *
 * There are 4 ways to manage backpressure with Webflux:
 * -----------------------------------------------------
 * 1) - the consumer limit the number of data capable to handle
 * 2) - if producer produce too much data than consumer can handle, webflux is able to store overflowed data in a buffer
 *     waiting to consumer to be ready to handle them
 * 3) - ignore overflowed data, not the best solution because it leads to data loss
 * 4) - by timeout: if some data are not handled, they can be ignored.
 *      Ex :
 *        flux.timeout(Duration.ofSeconds(5))
 *          .onErrorResume(e -> Flux.empty())
 *          .subscribe();
 * 5) - keep only the latest values produced (the olded ones are abandonned).
 *
 *
 * Backpressure is too much important because consumer can be overflowed by an important data that producer is pouring
 * in the stream.
 *
 */

public class BackpressureSimulation {


    /**
     * normal case without any backpressure
     */
    private Flux<Long> createNoOverflowFlux() {
        return Flux.range(1, Integer.MAX_VALUE)
                .log()
                .concatMap(x -> Mono.delay(Duration.ofMillis(100)));
    }

    /**
     * Creates a Flux that generates Long values at a very high frequency
     * (1 millisecond interval) and applies a delay of 100 milliseconds for
     * each emitted value.
     *
     * @return a Flux emitting Long values with delays applied, which simulates
     *         an overflow scenario where the producer generates data faster than
     *         the consumer can handle.
     */
    private Flux<Long> createOverflowFlux() {
        return Flux.interval(Duration.ofMillis(1))
                .log()
                .concatMap(x -> Mono.delay(Duration.ofMillis(100)));
    }


    /**
     * Handles backpressure by dropping emitted items when the producer emits data
     * faster than the consumer can process. This method uses the
     * {@code onBackpressureDrop()} strategy, which discards excess items to prevent
     * memory issues. Each emitted item has a processing delay to simulate slower
     * consumer handling.
     *
     * @return a Flux that emits Long values, processing each with a delay and
     *         managing backpressure by dropping excess items emitted by the producer.
     */
    private Flux<Long> handleBackpressureWithDrop() {
        return Flux.interval(Duration.ofMillis(1))
                .onBackpressureDrop() // not the ideal solution
                .log()
                .concatMap(x -> Mono.delay(Duration.ofMillis(100)).thenReturn(x))
                .doOnNext(x -> System.out.println("Emitting for the consumer : " + x));
    }

    /**
     * Handles backpressure by buffering emitted items when the producer emits
     * data faster than the consumer can process. The buffering is limited to a
     * specified capacity of 50 items. Items from the producer are processed with
     * a delay to simulate slower consumer handling. If the buffer overflows,
     * further emitted items will be dropped.
     *
     * @return a Flux that emits Long values, processed with a delay, while
     *         managing backpressure using a bounded buffer to avoid overwhelming
     *         the consumer.
     */
    private Flux<Long> handleBackpressureWithBuffer() {
        return Flux.interval(Duration.ofMillis(1))
                .onBackpressureBuffer(50)
                .concatMap(x -> Mono.delay(Duration.ofMillis(100)).thenReturn(x))
                .doOnNext(x -> System.out.println("Emitting for the consumer : " + x));
    }

    private Flux<Long> handleBackpressureWithBufferByDroppingOldest() {
        return Flux.interval(Duration.ofMillis(1))
                .onBackpressureBuffer(50, BufferOverflowStrategy.DROP_OLDEST)
                .concatMap(x -> Mono.delay(Duration.ofMillis(100)).thenReturn(x))
                .doOnNext(x -> System.out.println("Emitting for the consumer : " + x));
    }

    public static void main(String[] args) {
        BackpressureSimulation backpressureSimulation = new BackpressureSimulation();
        System.out.println("-------> Test without backpressure, without overflow <-------");
        // Things to know :
        // Producer and consumer communicates by the "request" canal.
        // so the consumer in this example will ask only for one element, and the producer will stream only one element
        // backpressureSimulation
        //        .createNoOverflowFlux()
        //        .blockLast(); // it means : block until the Flux is complete

        System.out.println("-------> Test with backpressure, we get an overflow <-------");
        System.out.println("-------> we will get an OverflowException : could not emit tick 1 due to lack of requests");
        // backpressureSimulation
        //        .createOverflowFlux()
        //        .blockLast(); // it means : block until the Flux is completed

        System.out.println("-------> Handle backpressure with method : onBackPressureDrop() <-------");
        // backpressureSimulation
        //    .handleBackpressureWithDrop()
        //    .blockLast(); // it means : block until the Flux is completed

        System.out.println("-------> Handle backpressure with method : onBackPressureBuffer() <-------");
        System.out.println("-------> we will get an OverflowException : The receiver is overrun by more signals than expected (bounded queue...)");
        // backpressureSimulation
        //        .handleBackpressureWithBuffer()
        //        .blockLast(); // it means : block until the Flux is completed

        System.out.println("-------> Handle backpressure with method : onBackPressureBuffer() by deleting oldest <-------");
        backpressureSimulation
                .handleBackpressureWithBufferByDroppingOldest()
                .blockLast(); // it means : block until the Flux is completed


    }

}
