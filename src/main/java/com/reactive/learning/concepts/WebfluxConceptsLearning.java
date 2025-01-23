package com.reactive.learning.concepts;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import javax.naming.OperationNotSupportedException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides examples and insights into various WebFlux concepts using Reactor's Mono and Flux publishers.
 * It demonstrates the principles of reactive programming, such as non-blocking asynchronous operations and
 * backpressure handling, by showcasing different operations available in the Reactor library.
 *
 * Key concepts and methods demonstrated in this class include:
 *
 * - Creating publishers with `Mono` and `Flux`.
 * - Transforming streams using operators like `map` and `flatMap`.
 * - Filtering data streams using operators like `skip` and `skipWhile`.
 * - Combining multiple publishers using operators like `concat`, `merge`, and `zip`.
 * - Collecting data into containers like `List` or `Map` using `collectList` and `collectMap`.
 * - Buffering items in chunks using `buffer`.
 * - Monitoring and managing stream execution with operators like `doOnEach`, `doAfterTerminate`, and `doOnSubscribe`.
 * - Handling errors and providing fallback mechanisms with operators like `onErrorContinue`, `onErrorReturn`, and `onResume`.
 *
 * These examples aim to provide a hands-on understanding of key WebFlux behavior, particularly its
 * non-blocking, event-driven nature and ability to work with streams of data efficiently.
 */
public class WebfluxConceptsLearning {

    // Webflux is based on Reactor java API specification project
    // Reactor offers two Publisher objects: Mono and Flux


    /**
     * Creates and returns a Mono that emits a single value asynchronously.
     * The method logs the different steps of data processing,
     * such as onSubscribe, onNext, and onComplete, to help track the flow of data.
     *
     * @return a Mono emitting a single value, or an empty Mono if the value is null
     */
    // let's create a publisher, who publish only one value
    private Mono<String> testMono() {
        return Mono.justOrEmpty("Java") //--> return only one element as an async call
                .log(); //--> to see how data go throw the subscription processor : allows to display different steps of
        // processed data, like : onSubscribe, request, onNext, onComplete...
    }

    /**
     * Creates and returns a Flux that emits a sequence of predefined string values.
     * The method logs the stream's lifecycle events such as subscription, emitted items,
     * and completion, which can help in debugging and understanding the data flow in the stream.
     *
     * @return a Flux emitting multiple string values from a predefined list
     */
    // let's create a publisher who publish multiple values as they comes
    private Flux<String> testFlux() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        return Flux.fromIterable(languages) //--> publish multiple values using fromIterable, fromArray or with next
                //.delayElements(Duration.ofSeconds(1000))
                .log();
    }

    /**
     * Creates and returns a Flux that emits a sequence of string values transformed to uppercase.
     * The method applies a map operation to transform each string in the input stream
     * to its uppercase equivalent and logs the lifecycle events of the Flux.
     *
     * @return a Flux emitting uppercase string values transformed from a predefined list
     */
    private Flux<String> testMap() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        Flux<String> flux = Flux.fromIterable(languages);

        return flux.map(s -> s.toUpperCase(Locale.ROOT)) // map is similar to the map function of RxJs,
                .log();                                         // apply function to each element of the flux
    }

    /**
     * Transforms a Flux of predefined string values by applying a flatMap operation.
     * Each string in the input Flux is converted to lowercase, prefixed with "L-",
     * and returned in a new Flux.
     *
     * @return a Flux emitting transformed string values with a lowercase prefix "L-"
     */
    private Flux<String> testFlatMap() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        Flux<String> flux = Flux.fromIterable(languages);

        return flux.flatMap(s -> Mono.justOrEmpty("L-".concat(s.toLowerCase(Locale.ROOT))));
    }

    /**
     * Creates and returns a Flux that emits a sequence of predefined string values,
     * after skipping specific elements from the beginning, the end, or based on duration.
     * The method demonstrates the use of the skip and skipLast operations,
     * omitting selected items and transforming the remaining ones through mapping.
     *
     * @return a Flux emitting transformed string values after skipping specified elements
     */
    // Skip method of flux return a publisher who is going to skip some values we decided to ignore
    private Flux<String> testSkip() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        Flux<String> flux = Flux.fromIterable(languages)
                .delayElements(Duration.ofSeconds(1));
        // skip(2) -> will skip the two first elements of the list
        return flux
                //.skip(Duration.ofSeconds(3)) //--> by doing this I will skip the data published before the 2 seconds
                .skipLast(2) //--> skip the two last elements of the flux
                .map("keep-"::concat)
                .log(); //
    }

    /**
     * Creates and returns a Flux that emits a sequence of integer values, starting from 1 to 20.
     * This method uses the skipWhile operation to ignore elements until a condition
     * specified by the predicate is no longer true. The Flux will skip values less than 10
     * and emit the remaining values.
     *
     * @return a Flux emitting integer values starting from the first value that is
     *         not less than 10, up to 20
     */
    private Flux<Integer> testComplexSkip() {
        var numbers = Flux.range(1, 20);
        return numbers.skipWhile(v -> v < 10);
    }

    /**
     * Creates and returns a Flux that emits a concatenated sequence of integers from two ranges.
     * The first range starts from 1 to 20, and the second range starts from 101 to 120.
     * The two Fluxes are combined sequentially, with elements from the first range emitted completely
     * before starting to emit elements from the second range.
     *
     * @return a Flux emitting a sequential combination of integer values from two defined ranges
     */
    private Flux<Integer> testConcat() {
        var listOne = Flux.range(1, 20);
        var listTwo = Flux.range(101, 20);

        return Flux.concat(listOne, listTwo);
    }


    /**
     * Creates and returns a Flux that merges two parallel streams of integers.
     * The first Flux emits integers in the range of 1 to 20, and the second Flux emits
     * integers in the range of 21 to 40. Both streams have a delay of 500 milliseconds
     * between element emissions. The resulting Flux combines these streams and emits
     * elements as they become available, preserving the interleaving behavior typical
     * of a merge operation.
     *
     * @return a Flux emitting a merged stream of integers from two different ranges
     */
    private Flux<Integer> testMerge() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 20).delayElements(Duration.ofMillis(500));
        return Flux.merge(fOne, fTwo);
    }

    /**
     * Creates and returns a Flux by zipping two streams of integers.
     * The first Flux emits integers in the range of 1 to 20, while the second Flux emits integers
     * in the range of 21 to 40. Both streams have a delay of 500 milliseconds between element emissions.
     * The resulting Flux pairs corresponding elements from both streams into a Tuple2, such that the
     * first element of the tuple comes from the first Flux and the second element from the second Flux.
     *
     * @return a Flux emitting Tuple2 instances, each containing paired integers from the two input Fluxes
     */
    private Flux<Tuple2<Integer, Integer>> testZip() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 20).delayElements(Duration.ofMillis(500));

        return Flux.zip(fOne, fTwo);
    }

    /**
     * Combines two Flux streams of integers with different range sizes using the zip operation.
     * The first Flux emits integers from the range 1 to 20, and the second Flux emits integers
     * from the range 21 to 25. Both streams have a delay of 500 milliseconds between element emissions.
     * The resulting Flux pairs corresponding elements from both streams into a Tuple2. The zip operation
     * terminates once either of the Flux streams completes, meaning the resulting Flux will emit
     * as many pairs as possible until one of the input streams is exhausted.
     *
     * @return a Flux emitting Tuple2 instances, each containing paired integers from the two input Flux streams
     */
    private Flux<Tuple2<Integer, Integer>> testZipDifferentRangeSize() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 5).delayElements(Duration.ofMillis(500));

        return Flux.zip(fOne, fTwo); // he will give us only 5 tuple2, because once onComplete of one of this Flux is called,
        // the zip method finish.
    }

    /**
     * Combines three Flux streams of integers into a single Flux using the zip operation.
     * The first Flux emits integers in the range of 1 to 20, while the second and third Flux streams
     * each emit integers in the range of 21 to 40. All three streams have a delay of 500 milliseconds
     * between element emissions. The resulting Flux combines corresponding elements from all three streams
     * into a Tuple3, where each tuple contains one integer from each of the input streams.
     *
     * @return a Flux emitting Tuple3 instances, each containing three integers drawn from the three input Flux streams
     */
    private Flux<Tuple3<Integer, Integer, Integer>> testZip2() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 20).delayElements(Duration.ofMillis(500));
        var fThree = Flux.range(21, 20).delayElements(Duration.ofMillis(500));

        return Flux.zip(fOne, fTwo, fThree);
    }

    /**
     * Combines a Mono and a Flux using the zip operation to create a Flux of paired elements as Tuple2.
     * The input Flux emits integers in the range of 1 to 20 with a delay of 500 milliseconds between emissions,
     * while the Mono emits a single integer value. The resulting Flux contains up to one tuple since
     * the zip operation pairs elements based on the smallest size input, which in this case is the Mono.
     *
     * @return a Flux emitting a single Tuple2 instance containing an integer from the Flux and the single integer emitted by the Mono
     */
    private Flux<Tuple2<Integer, Integer>> testZipMonoAndFlux() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var mono = Mono.just(1);
        return Flux.zip(fOne, mono); //--> zip a mono with a flux is possible, the size of the flux matters,
        // the number of tuple will be 1
    }

    /**
     * Creates and returns a Mono that collects all emitted items from a Flux into a single list.
     * The method uses the `collectList` operator to aggregate the values emitted by the Flux
     * into a `List<Integer>` once the Flux completes.
     *
     * @return a Mono emitting a single list containing all integer values emitted by the Flux
     */
    private Mono<List<Integer>> testCollectList() {
        var fOne = Flux.range(1, 20);
        return fOne.collectList();
    }

    /**
     * Flux.buffer() collects multiple items into a list.
     * It sends out the whole list (the bucket) at once when it's ready.
     * Real life example:
     * Let’s say you’re watching videos on a streaming app:
     * The app gets data packets (small pieces of a video) one by one.
     * Instead of playing each tiny piece, it waits until it has enough packets to make a full chunk (like 10 seconds of video).
     * Then it shows you the video smoothly.
     */
    private Flux<List<Integer>> testBuffer() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofMillis(1000));
        //return flux.buffer(3);
        return flux.buffer(Duration.ofSeconds(3));
    }

    /**
     * Collects emitted items from a Flux of integers into a Map.
     * The keys in the resulting Map are the original integers emitted by the Flux,
     * while the values are the squares of those integers.
     *
     * @return a Mono emitting a Map where the keys are integers and the values are their squares
     */
    private Mono<Map<Integer, Integer>> testCollectMap() {
        var flux = Flux.range(1, 10);
        return flux.collectMap(integer -> integer, integer -> integer * integer);
    }

    /**
     * Creates and returns a Flux that emits a sequence of integers from 1 to 10.
     * The method logs each element in the sequence during the emission process.
     * For each signal, it checks if the signal denotes the completion of the
     * stream and prints "The end!" upon completion. Other signals, including
     * emitted items, are logged directly.
     *
     * @return a Flux emitting integers from 1 to 10 with logging of each emission
     *         and a message upon stream completion
     */
    private Flux<Integer> testDoOnEach() {
        var flux = Flux.range(1, 10);
        return flux.doOnEach(signal -> {
            if (signal.isOnComplete()) {
                System.out.println("The end!");
            } else {
                System.out.println(signal);
            }
        });
    }

    /**
     * Creates and returns a Flux that emits a sequence of integers from 1 to 10.
     * After the Flux has completed or terminated, the method executes a callback
     * operation. This callback can be used to perform actions such as resource
     * cleanup or invoking an external service.
     *
     * @return a Flux emitting integers from 1 to 10 with a callback action executed
     *         after the completion or termination of the stream
     */
    private Flux<Integer> testDoAfterTerminated() {
        var flux = Flux.range(1, 10);
        return flux.doAfterTerminate(() -> {
            System.out.println("flux after terminated, call external service for example");
        });
    }

    /**
     * when a subscribed to do the publisher, increment the value of number of subscriber
     */
    AtomicInteger nbSubscriber = new AtomicInteger(0);

    private Flux<Integer> testDoOnSubscribe() {
        var flux = Flux.range(1, 10);
        return flux.doOnSubscribe(subscription -> nbSubscriber.getAndIncrement())
                .doFinally((d) -> System.out.println("Number of subscriber : " + nbSubscriber.get()));
    }

    /**
     * Creates a Flux that emits a range of integers from 1 to 10 with a delay of 500 milliseconds
     * between each element. Logs a message when the subscription is canceled.
     *
     * @return a Flux emitting integers from 1 to 10, with a delay between elements, and a cancellation handler.
     */
    private Flux<Integer> testDoOnCancel() {
        return Flux.range(1, 10)
                .delayElements(Duration.ofMillis(500))
                .doOnCancel(() -> {
                    System.out.println("The subscriber has canceled !");
                });
    }

    /**
     * Executes a Flux operation that generates a range of integers, applies a mapping function, and
     * handles errors encountered during the processing of individual elements by continuing the
     * processing of remaining elements.
     *
     * The method emits integers from a range where even numbers trigger an exception. The errors
     * are handled using {@code onErrorContinue()} to log the error and the corresponding element without
     * terminating the stream.
     *
     * @return a Flux emitting processed integers, skipping those that caused an error, with a delay between emissions
     */
    private Flux<Integer> testOnError() {
        var f = Flux.range(1, 10)
                .map(integer -> {
                    if (integer % 2 == 0) {
                        throw new RuntimeException("It's an even number");
                    }
                    return integer;
                })
                .delayElements(Duration.ofMillis(500));
        // although we get an error on an element of a flux, we continue the treatment of remaining elements
        return f.onErrorContinue((throwable, o) -> {
            System.out.println("Don't worry : " + o + ", " + throwable.getMessage());
        });
    }

    /**
     * Emits a sequence of integers from 1 to 10, with a delay between each element.
     * If an exception occurs during the processing of the elements, the sequence will
     * terminate and emit a fallback value of -1.
     *
     * @return A Flux that emits integers from the range 1-10, or emits -1 in case
     *         of an error during emission or processing.
     */
    private Flux<Integer> testOnErrorReturn() {
        return Flux.range(1, 10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("It's an even number");
                    }
                    return integer;
                })
                .delayElements(Duration.ofMillis(500))
                .onErrorReturn(-1);
    }

    /**
     * when a flux A get an error or throw exception, we switch to another Flux B, to provide/publish different data
     * real life example : if service A fall down, a fallback flux could be used to recover the
     * situation (emergency service for example)
     * @return flux
     */
    private Flux<Integer> testOnResume() {
        return Flux.range(1, 5)
                .map(i -> {
                    if (i == 4) {
                        throw new RuntimeException("We don't expect 6");
                    }
                    return i;
                })
                .onErrorResume(throwable -> {
                    // it's like a falling back method allowing to provide another
                    // stream of data, if any error occur
                    return Flux.range(100, 2); // and we can also return a Mono instead of flux
                });
    }

    /**
     * Applies a filter to the Flux elements based on the provided conditions.
     * Filters elements that contain the letter 'a' (case-insensitive) and further filters
     * elements that also contain the letter 'l' (case-insensitive).
     *
     * @return a Flux containing elements that satisfy both filter conditions.
     */
    private Flux<String> testOfFilterWhen() {
        return Flux.fromIterable(List.of("Java-lang", "Python-lang", "Html-lang", "PyTorch-fram"))
                .filterWhen(value -> Mono.just(value.toLowerCase().contains("a")))
                .filterWhen(s -> Mono.just(s.toLowerCase().contains("l")));

    }

    private Flux<Integer> testOnErrorMap() {
        return Flux.range(0, 5)
                .map(i -> 2/i)
                .onErrorMap(throwable ->
                    // if error occur on Flux, we can turn it to another error/exception
                    new OperationNotSupportedException("Operation not supported")
                );
                //.onErrorContinue((throwable, o) -> {
                //    System.out.println("Operation not supported for value : " + o);
                //});
    }
    public static void main(String[] args) throws InterruptedException {
        WebfluxConceptsLearning webfluxConceptsLearning = new WebfluxConceptsLearning();
        // Let's test a subscriber
        // webfluxConceptsLearning.testMono(); //--> this is not going to do nothing because we are not subscribed yet
        // The publisher send data, but to get that one, the publisher should know the subscriber, so let's create one
        System.out.println("-------> Test mono");
        // webfluxConceptsLearning.testMono().subscribe(System.out::print);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test flux");
        //webfluxConceptsLearning.testFlux().subscribe(System.out::println);
        //Thread.sleep(700);
        // --------
        System.out.println("-------> Test map");
        // webfluxConceptsLearning.testMap().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test flatMap");
        // webfluxConceptsLearning.testFlatMap().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test skip");
        // webfluxConceptsLearning.testSkip().subscribe(System.out::println);
        // Thread.sleep(6_000); //--> this is necessary because testSkip is an async operation, so we have to keep app
        // running by using a sleep, in order to have enough time to display all pushed elements
        // --------
        System.out.println("-------> Test skip");
        // webfluxConceptsLearning.testComplexSkip().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test concat");
        // webfluxConceptsLearning.testConcat().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test merge");
        webfluxConceptsLearning.testMerge().subscribe(System.out::println);
        // Thread.sleep(11_000);
        // --------
        System.out.println("-------> Test zip -> tuple2 as result");
        // webfluxConceptsLearning.testZip().subscribe(System.out::println);
        // Thread.sleep(12_000);
        // --------
        System.out.println("-------> Test zip -> test Zip Different Range Size");
        // webfluxConceptsLearning.testZipDifferentRangeSize().subscribe(System.out::println);
        // Thread.sleep(6_000);
        // --------
        System.out.println("-------> Test zip -> tuple3 as result");
        // webfluxConceptsLearning.testZip2().subscribe(System.out::println);
        // Thread.sleep(12_000);
        // --------
        System.out.println("-------> Test zip ->  zip mono with flux");
        // webfluxConceptsLearning.testZipMonoAndFlux().subscribe(System.out::println);
        // Thread.sleep(5_000);
        // --------
        System.out.println("-------> Test collectList method");
        // webfluxConceptsLearning.testCollectList().subscribe(System.out::println);
        // to convert from async call to sync, we can use block() which is not recommended at all
        // webfluxConceptsLearning.testCollectList().block();
        // Thread.sleep(5_000);

        // --------
        System.out.println("-------> Test flux's buffer method");
        //webfluxConceptsLearning.testBuffer().subscribe(System.out::println);
        //Thread.sleep(15_000);
        // --------
        System.out.println("-------> Test flux's collectMap method/operator");
        //webfluxConceptsLearning.testCollectMap().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's doOnEach method/operator");
        //webfluxConceptsLearning.testDoOnEach().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's testDoAfterTerminated method/operator");
        //webfluxConceptsLearning.testDoAfterTerminated().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's DoOnSubscribe method/operator");
        //webfluxConceptsLearning.testDoOnSubscribe().subscribe(System.out::println);
        //webfluxConceptsLearning.testDoOnSubscribe().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's doOnCancel method/operator");
        //Disposable disposable = webfluxConceptsLearning.testDoOnCancel().subscribe(System.out::println);
        //Thread.sleep(1_000);
        //disposable.dispose();
        //Thread.sleep(8_000);
        // --------
        System.out.println("-------> Test flux's onErrorContinue method/operator");
        //webfluxConceptsLearning.testOnError().subscribe(System.out::println);
        //Thread.sleep(8_000);
        // --------
        System.out.println("-------> Test flux's onErrorReturn method/operator");
        //webfluxConceptsLearning.testOnErrorReturn().subscribe(System.out::println);
        //Thread.sleep(15_000);
        // --------
        System.out.println("-------> Test flux's onResume method/operator");
        //webfluxConceptsLearning.testOnResume().subscribe(System.out::println);
        //Thread.sleep(15_000);
        // --------
        System.out.println("-------> Test flux's onErrorMap method/operator");
        //webfluxConceptsLearning.testOnErrorMap().subscribe(System.out::println);
        //Thread.sleep(15_000);
        // --------
        System.out.println("-------> Test flux's filterWhen method/operator");
        webfluxConceptsLearning.testOfFilterWhen().subscribe(System.out::println);
        Thread.sleep(15_000);

    }


}
