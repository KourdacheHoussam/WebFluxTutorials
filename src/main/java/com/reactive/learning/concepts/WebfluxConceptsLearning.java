package com.reactive.learning.concepts;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Houssam KOURDACHE
 */
public class WebfluxConceptsLearning {

    // Webflux is based on Reactor java API specification project
    // Reactor offers two Publisher objects: Mono and Flux


    // let's create a publisher, who publish only one value
    private Mono<String> testMono() {
        return Mono.justOrEmpty("Java") //--> return only one element as an async call
                .log(); //--> to see how data go throw the subscription processor : allows to display different steps of
        // processed data, like : onSubscribe, request, onNext, onComplete...
    }

    // let's create a publisher who publish multiple values as they comes
    private Flux<String> testFlux() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        return Flux.fromIterable(languages) //--> publish multiple values using fromIterable, fromArray or with next
                //.delayElements(Duration.ofSeconds(1000))
                .log();
    }

    private Flux<String> testMap() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        Flux<String> flux = Flux.fromIterable(languages);

        return flux.map(s -> s.toUpperCase(Locale.ROOT)) // map is similar to the map function of RxJs,
                .log();                                         // apply function to each element of the flux
    }

    private Flux<String> testFlatMap() {
        var languages = List.of("Java", "Angular", "Kafka", "Kubernetes");
        Flux<String> flux = Flux.fromIterable(languages);

        return flux.flatMap(s -> Mono.justOrEmpty("L-".concat(s.toLowerCase(Locale.ROOT))));
    }

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

    private Flux<Integer> testComplexSkip() {
        var numbers = Flux.range(1, 20);
        return numbers
                .skipWhile(v -> v < 10);
    }

    private Flux<Integer> testConcat() {
        var listOne = Flux.range(1, 20);
        var listTwo = Flux.range(101, 20);

        return Flux.concat(listOne, listTwo);
    }


    private Flux<Integer> testMerge() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 20).delayElements(Duration.ofMillis(500));
        return Flux.merge(fOne, fTwo);
    }

    private Flux<Tuple2<Integer, Integer>> testZip() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 20).delayElements(Duration.ofMillis(500));

        return Flux.zip(fOne, fTwo);
    }

    private Flux<Tuple2<Integer, Integer>> testZipDifferentRangeSize() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 5).delayElements(Duration.ofMillis(500));

        return Flux.zip(fOne, fTwo); // he will give us only 5 tuple2, because once onComplete of one of this Flux is called,
        // the zip method finish.
    }

    private Flux<Tuple3<Integer, Integer, Integer>> testZip2() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var fTwo = Flux.range(21, 20).delayElements(Duration.ofMillis(500));
        var fThree = Flux.range(21, 20).delayElements(Duration.ofMillis(500));

        return Flux.zip(fOne, fTwo, fThree);
    }

    private Flux<Tuple2<Integer, Integer>> testZipMonoAndFlux() {
        var fOne = Flux.range(1, 20).delayElements(Duration.ofMillis(500));
        var mono = Mono.just(1);
        return Flux.zip(fOne, mono); //--> zip a mono with a flux is possible, the size of the flux matters,
        // the number of tuple will be 1
    }

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

    private Mono<Map<Integer, Integer>> testCollectMap() {
        var flux = Flux.range(1, 10);
        return flux.collectMap(integer -> integer, integer -> integer * integer);
    }

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

    private Flux<Integer> testDoOnCancel() {
        return Flux.range(1, 10)
                .delayElements(Duration.ofMillis(500))
                .doOnCancel(() -> {
                    System.out.println("The subscriber has canceled !");
                });
    }

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

    public static void main(String[] args) throws InterruptedException {
        WebfluxConceptsLearning webfluxConceptsLearning = new WebfluxConceptsLearning();
        // Let's test a subscriber
        // reactiveTutorial.testMono(); //--> this is not going to do nothing because we are not subscribed yet
        // The publisher send data, but to get that one, the publisher should know the subscriber, so let's create one
        System.out.println("-------> Test mono");
        // reactiveTutorial.testMono().subscribe(System.out::print);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test flux");
        webfluxConceptsLearning.testFlux().subscribe(System.out::println);
        Thread.sleep(700);
        // --------
        System.out.println("-------> Test map");
        // reactiveTutorial.testMap().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test flatMap");
        // reactiveTutorial.testFlatMap().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test skip");
        // reactiveTutorial.testSkip().subscribe(System.out::println);
        // Thread.sleep(6_000); //--> this is necessary because testSkip is an async operation, so we have to keep app
        // running by using a sleep, in order to have enough time to display all pushed elements
        // --------
        System.out.println("-------> Test skip");
        // reactiveTutorial.testComplexSkip().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test concat");
        // reactiveTutorial.testConcat().subscribe(System.out::println);
        // Thread.sleep(700);
        // --------
        System.out.println("-------> Test merge");
        // reactiveTutorial.testMerge().subscribe(System.out::println);
        // Thread.sleep(11_000);
        // --------
        System.out.println("-------> Test zip -> tuple2 as result");
        // reactiveTutorial.testZip().subscribe(System.out::println);
        // Thread.sleep(12_000);
        // --------
        System.out.println("-------> Test zip -> test Zip Different Range Size");
        // reactiveTutorial.testZipDifferentRangeSize().subscribe(System.out::println);
        // Thread.sleep(6_000);
        // --------
        System.out.println("-------> Test zip -> tuple3 as result");
        // reactiveTutorial.testZip2().subscribe(System.out::println);
        // Thread.sleep(12_000);
        // --------
        System.out.println("-------> Test zip ->  zip mono with flux");
        // reactiveTutorial.testZipMonoAndFlux().subscribe(System.out::println);
        // Thread.sleep(5_000);
        // --------
        System.out.println("-------> Test collectList method");
        // reactiveTutorial.testCollectList().subscribe(System.out::println);
        // to convert from async call to sync, we can use block() which is not recommended at all
        // reactiveTutorial.testCollectList().block();
        // Thread.sleep(5_000);

        // --------
        System.out.println("-------> Test flux's buffer method");
        //reactiveTutorial.testBuffer().subscribe(System.out::println);
        //Thread.sleep(15_000);
        // --------
        System.out.println("-------> Test flux's collectMap method");
        //reactiveTutorial.testCollectMap().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's doOnEach method");
        //reactiveTutorial.testDoOnEach().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's testDoAfterTerminated method");
        //reactiveTutorial.testDoAfterTerminated().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's DoOnSubscribe method");
        //reactiveTutorial.testDoOnSubscribe().subscribe(System.out::println);
        //reactiveTutorial.testDoOnSubscribe().subscribe(System.out::println);
        // --------
        System.out.println("-------> Test flux's doOnCancel method");
        Disposable disposable = webfluxConceptsLearning.testDoOnCancel().subscribe(System.out::println);
        //Thread.sleep(1_000);
        //disposable.dispose();
        //Thread.sleep(8_000);
        // --------
        System.out.println("-------> Test flux's onErrorContinue methods");
        //webfluxConceptsLearning.testOnError().subscribe(System.out::println);
        //Thread.sleep(8_000);
        // --------
        System.out.println("-------> Test flux's onErrorReturn methods");
        webfluxConceptsLearning.testOnErrorReturn().subscribe(System.out::println);
        Thread.sleep(15_000);

    }


}
