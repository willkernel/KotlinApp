package com.willkernel.kotlinapp.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.willkernel.kotlinapp.R;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by willkernel
 * on 2019/3/28.
 */
public class RxJavaTest extends Activity {
    private static final String TAG = "RxJavaTest";
    private int i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);

        //创建操作符
        createOperation();

        //转换操作符
        transferOperation();

        //组合操作符
        contactOperation();

        //过滤操作符
        filterOperation();

        //条件操作符
        conditionOperation();
    }

    private void conditionOperation() {
//        判断事件序列是否全部满足某个事件，如果都满足则返回 true，反之则返回 false
        Observable.just(1, 2, 3)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 5;
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "all " + aBoolean);
                    }
                });

//        可以设置条件，当某个数据满足条件时就会发送该数据，反之则不发送
        Observable.just(1, 2, 3)
                .takeWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 3;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "takeWhile  " + integer);
                    }
                });


//        skipWhile 可以设置条件，当某个数据满足条件时不发送该数据，反之则发送
        Observable.just(1, 2, 3, 4)
                .skipWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "skipWhile " + integer);
                    }
                });


//        可以设置条件，当事件满足此条件时，下一次的事件就不会被发送了
        Observable.just(1, 2, 3, 4)
                .takeUntil(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 3;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "takeUntil " + integer);
                    }
                });


//        当 skipUntil() 中的 Observable 发送事件了，原来的 Observable 才会发送事件给观察者
//        skipUntil() 里的 Observable 并不会发送事件给观察者
        Observable.just(1, 2, 3, 4)
                .skipUntil(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                        observer.onComplete();
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "skipUntil  " + integer);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

//        判断两个 Observable 发送的事件是否相同
        Observable.sequenceEqual(Observable.just(1, 2, 3), Observable.just(1, 2, 3))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d(TAG, "sequenceEqual  " + aBoolean);
                    }
                });


//        判断事件序列中是否含有某个元素，如果有则返回 true，如果没有则返回 false
        Observable.just(1, 2, 3)
                .contains(2)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d(TAG, "contains  " + aBoolean);
                    }
                });

//        判断事件序列是否为空
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onComplete();
                    }
                })
                .isEmpty()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d(TAG, "isEmpty " + aBoolean);
                    }
                });


//        amb() 要传入一个 Observable 集合，但是只会发送最先发送事件的 Observable 中的事件，其余 Observable 将会被丢弃
        ArrayList<Observable<Long>> list = new ArrayList<>();
        list.add(Observable.intervalRange(1, 5, 2, 1, TimeUnit.SECONDS));
        list.add(Observable.intervalRange(6, 5, 0, 1, TimeUnit.SECONDS));
        Observable.amb(list)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "amb  " + aLong);
                        //最先发送事件的 事件队列 处理完，其他事件队列不发送
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onComplete();
            }
        })
//        如果观察者只发送一个 onComplete() 事件，则可以利用这个方法发送一个值
                .defaultIfEmpty(1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "defaultIfEmpty " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void contactOperation() {
        concat();//串行
        concatAry();//串行
//        merge();//并行
        concatMergeDelayError();
        zip();
        combineLatest();
        reduce();
        collect();
        startWith();
        count();
        delay();
        doOnEach();
        doOnNext();
        doAfterNext();
        doONComplete();
        doOnError();
        doOnSubscribe();
        doOnDispose();
        doOnLifecycle();
        doOnTerminate();
        doFinally();

        onErrorReturn();
        onErrorResumeNext();
        onExceptionResumeNext();
        retry();
        retryUntil();
        retryWhen();
        repeat();
        repeatWhen();

        subscribeOn();
    }

    private void filterOperation() {
        filter();
        ofType();
        skip();
        distinct();
        distinctUntilChanged();
        take();
        debounce();
        firstLastElement();
        elementAt();
    }

    private void elementAt() {
//        elementAt() 可以指定取出事件序列中事件，但是输入的 index 超出事件序列的总数的话就不会出现任何结果
//        发出异常信息用 elementAtOrError()
        Observable.just(1, 2, 3, 4)
//                .elementAt(1)
                .elementAtOrError(4)
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "elementAtOrError onSubscribe ");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e(TAG, "elementAtOrError onSuccess " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "elementAtOrError onError " + throwable);

                    }
                });
    }

    private void firstLastElement() {
//        firstElement() 取事件序列的第一个元素，lastElement() 取事件序列的最后一个元素
        Observable.just(1, 2, 3, 4)
                .firstElement()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "firstElement " + integer);
                    }
                });
        Observable.just(1, 2, 3, 4)
                .lastElement()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "lastElement " + integer);
                    }
                });
    }

    private void debounce() {
//        如果两件事件发送的时间间隔小于设定的时间间隔则前一件事件就不会发送给观察者
//        throttleWithTimeout() 与此方法的作用一样
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(1);
                Thread.sleep(1100);
//                Thread.sleep(500);
                observableEmitter.onNext(2);
            }
        })
//                .debounce(1, TimeUnit.SECONDS)
                .throttleWithTimeout(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "debounce " + integer);
                    }
                });
    }

    private void take() {
//        控制观察者接收的事件的数量
        Observable.just(1, 2, 3, 4)
                .take(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "take " + integer);
                    }
                });
    }

    private void distinctUntilChanged() {
        //        过滤掉连续重复的事件
        Observable.just(1, 2, 3, 3, 2, 1)
                .distinctUntilChanged()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "distinctUntilChanged " + integer);
//                        distinctUntilChanged 1
//                        distinctUntilChanged 2
//                        distinctUntilChanged 3
//                        distinctUntilChanged 2
//                        distinctUntilChanged 1
                    }
                });
    }

    private void distinct() {
//        过滤事件序列中的重复事件
        Observable.just(1, 2, 3, 3, 2, 1)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "distinct accept " + integer);
                    }
                });
    }

    private void skip() {
//        跳过正序某些事件，count 代表跳过事件的数量
        Observable.just(1, 2, 3)
//                .skip(2)
                .skipLast(2)//从后往前过滤
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "skip accept " + integer);
                    }
                });
    }

    private void ofType() {
        //过滤不符合该类型的事件
        Observable.just(1, 2, "tt", "ww")
                .ofType(Integer.class)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "ofType accept " + integer);
                    }
                });
    }

    private void filter() {
//        通过一定逻辑来过滤被观察者发送的事件，如果返回 true 则会发送事件，否则不会发送
        Observable.just(1, 2, 3)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "filter accept " + integer);
            }
        });
    }

    private void subscribeOn() {
//        指定被观察者的线程，要注意的时，如果多次调用此方法，只有第一次有效
//        指定观察者的线程，每指定一次就会生效一次
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.e(TAG, "subscribe on " + Thread.currentThread().getName());
                observableEmitter.onNext(1);
                observableEmitter.onNext(2);
                observableEmitter.onNext(3);
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())

                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "observeOn  " + Thread.currentThread().getName());
                        Log.e(TAG, "observeOn  " + integer);
                    }
                });
    }

    private void repeatWhen() {
//        这个方法可以会返回一个新的被观察者设定一定逻辑来决定是否重复发送事件
//        这里分三种情况，如果新的被观察者返回 onComplete 或者 onError 事件，则旧的被观察者不会继续发送事件。
//        如果被观察者返回其他事件，则会重复发送事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
//                return Observable.empty();
//                return Observable.error(new Exception("666"));
                return Observable.just(4);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "repeatWhen=======onSubscribe ");
            }

            @Override
            public void onNext(Integer integer) {
                x += integer;
                Log.e(TAG, "repeatWhen=======onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "repeatWhen=======onError ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "repeatWhen=======onComplete ");
            }
        });
    }

    private void repeat() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("roma");
                e.onNext("rose");
                e.onNext("tim");
                e.onComplete();//必须调研，否则不执行retry
            }
        }).repeat(3)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "repeat=======onSubscribe ");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "repeat=======onNext " + s);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "repeat=======onError ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "repeat=======onComplete ");
                    }
                });
    }

    private void retryWhen() {
//        当被观察者接收到异常或者错误事件时会回调该方法，这个方法会返回一个新的被观察者。
//        如果返回的被观察者发送 Error 事件则之前的被观察者不会继续发送事件，如果发送正常事件则之前的被观察者会继续不断重试发送事件
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("roma");
                e.onNext("rose");
                e.onNext("tim");
                e.onError(new Exception("404"));
//                e.onError(new Exception("300"));
                e.onNext("one");
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                if (!throwableObservable.toString().equals("java.lang.Exception: 404")) {
                    return Observable.just("ignore error");
                }
                return Observable.error(new Throwable("exit fault"));
//                return null;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "retryWhen=======onSubscribe ");
            }

            @Override
            public void onNext(String integer) {
                Log.e(TAG, "retryWhen=======onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "retryWhen=======onError " + throwable);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "retryWhen=======onComplete ");
            }
        });
    }

    private Integer x = 0;

    private void retryUntil() {
//        出现错误事件之后，可以通过此方法判断是否继续发送事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Exception("404"));
//                e.onError(new Error("404"));
            }
        }).retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                if (x == 6) {
                    return true;
                }
                return false;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "retryUntil=======onSubscribe ");
            }

            @Override
            public void onNext(Integer integer) {
                x += integer;
                Log.e(TAG, "retryUntil=======onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "retryUntil=======onError ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "retryUntil=======onComplete ");
            }
        });
    }

    private void retry() {
//        如果出现错误事件，则会重新发送所有事件序列。times 是代表重新发的次数,最后失败会报错
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Exception("404"));
//                e.onError(new Error("404"));
            }
        }).retry(2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "retry=======onSubscribe ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "retry=======onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "retry=======onError ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "retry=======onComplete ");
                    }
                });
    }

    private void onExceptionResumeNext() {
//        与 onErrorResumeNext() 作用基本一致，但是这个方法只能捕捉 Exception
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Exception("404"));
//                e.onError(new Error("404"));
            }
        })
                .onExceptionResumeNext(new Observable<Integer>() {
                    @Override
                    protected void subscribeActual(Observer<? super Integer> observer) {
                        observer.onNext(333);
                        observer.onComplete();
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onExceptionResumeNext=======onSubscribe ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onExceptionResumeNext======onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "==================onError ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onExceptionResumeNext=======onComplete ");
                    }
                });
    }

    private void onErrorResumeNext() {
//        当接收到 onError() 事件时，返回一个新的 Observable，并正常结束事件序列
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NumberFormatException());
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                return Observable.just(1, 2, 3, 4);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "onErrorResumeNext========onSubscribe ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onErrorResumeNext===========onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onErrorResumeNext===========onError ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onErrorResumeNext==========onComplete ");
            }
        });
    }

    private void onErrorReturn() {
//        当接受到一个 onError() 事件之后回调，返回的值会回调 onNext() 方法，并正常结束该事件序列
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NumberFormatException());

            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                Log.e(TAG, "onErrorReturn 404");
                return 404;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onErrorReturn onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onErrorReturn error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onErrorReturn onComplete");
            }
        });
    }

    private void doFinally() {
//        在所有事件发送完毕之后回调该方法
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();

            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doFinally run");
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "==================doOnDispose ");
            }
        }).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doFinally doAfterTerminate");
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable d;

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "==================onSubscribe ");
                this.d = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "==================onNext " + integer);
                d.dispose();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "doFinally onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "doFinally onComplete");
            }
        });
    }

    private void doOnTerminate() {
//        doOnTerminate 是在 onError 或者 onComplete 发送之前回调，
// 而 doAfterTerminate 则是 onError 或者 onComplete 发送之后回调
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
//      e.onError(new NullPointerException());
                e.onComplete();
            }
        })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "==================doOnTerminate ");
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "==================onSubscribe ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "==================onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "==================onError ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "==================onComplete ");
                    }
                });
    }

    private void doOnLifecycle() {
//      doOnLifecycle  在回调 onSubscribe 之前回调该方法的第一个参数的回调方法，可以使用该回调方法决定是否取消订阅
//        doOnLifecycle() 第二个参数的回调方法的作用与 doOnDispose() 是一样
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(1);
                observableEmitter.onNext(2);
                observableEmitter.onNext(3);
                observableEmitter.onComplete();
            }
        }).doOnLifecycle(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                Log.e(TAG, "doOnLifecycle accept(Disposable disposable)");
                // disposable.dispose();//doOnDispose Action 和 doOnLifecycle Action 都没有被回调
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doOnLifecycle run");
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doOnLifecycle doOnDispose run");
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable d;

            @Override
            public void onSubscribe(Disposable disposable) {
                Log.e(TAG, "doOnLifecycle onSubscribe");
                d = disposable;
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "doOnLifecycle onNext " + integer);
//                d.dispose();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void doOnDispose() {
//        当调用 Disposable 的 dispose() 之后回调该方法
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onNext(2);
                        observableEmitter.onNext(3);
                        observableEmitter.onComplete();
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doOnDispose run");
                    }
                })
                .subscribe(new Observer<Integer>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "doOnDispose onSubscribe");
                        d = disposable;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "doOnDispose onNext" + integer);
                        d.dispose();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "doOnDispose " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "doOnDispose onComplete");
                    }
                });
    }

    private void doOnSubscribe() {
//        Observable 每发送 onSubscribe() 之前都会回调这个方法
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onNext(2);
                        observableEmitter.onNext(3);
                        observableEmitter.onComplete();
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.e(TAG, "doOnSubscribe accept " + disposable);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "doOnSubscribe onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "doOnSubscribe onNext" + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "doOnSubscribe " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "doOnSubscribe onComplete");
                    }
                });
    }

    private void doOnError() {
//        Observable 每发送 onError() 之前都会回调这个方法
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onNext(2);
                        observableEmitter.onNext(3);
                        observableEmitter.onError(new NumberFormatException());
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "doOnError " + throwable);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "doOnError onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "doOnError onNext" + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "doOnError " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "doOnError onComplete");
                    }
                });
    }

    private void doONComplete() {
//        Observable 每发送 onComplete() 之前都会回调这个方法
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onNext(2);
                        observableEmitter.onNext(3);
                        observableEmitter.onComplete();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doOnComplete");
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnComplete accept" + integer);
                    }
                });
    }

    private void doAfterNext() {
//        Observable 每发送 onNext() 之后都会回调这个方法
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(1);
                observableEmitter.onNext(2);
                observableEmitter.onNext(3);
                observableEmitter.onComplete();
            }
        }).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "doAfterNext " + integer);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "doAfterNext accept" + integer);
            }
        });
    }

    private void doOnNext() {
//        Observable 每发送 onNext() 之前都会先回调这个方法
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onNext(2);
                        observableEmitter.onNext(3);
                        observableEmitter.onComplete();
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext " + integer);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext accept" + integer);
                    }
                });
    }

    private void doOnEach() {
//        Observable 每发送一件事件之前都会先回调这个方法
//        每发送一个事件之前都会回调 doOnEach 方法，并且可以取出 onNext() 发送的值
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(1);
                observableEmitter.onNext(2);
                observableEmitter.onNext(3);
                observableEmitter.onComplete();
            }
        }).doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> integerNotification) throws Exception {
                Log.e(TAG, "doOnEach notification " + integerNotification.getValue());
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "doOnEach accept " + integer);
            }
        });
    }

    private void delay() {
        Observable.just(1, 2, 3)
                .delay(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "delay  onSubscribe ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "delay  integer " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void count() {
//        返回被观察者发送事件的数量
        Observable.just(1, 2, 1)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "count  accept " + aLong);
                    }
                });
    }

    private void startWith() {
//        在发送事件之前追加事件，startWith() 追加一个事件，startWithArray() 可以追加多个事件。追加的事件会先发出
        Observable.just(4, 5, 6)
                .startWithArray(1, 2, 3)
                .startWith(0)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "startWith  accept " + integer);
                    }
                });
    }

    private void collect() {
//        将数据收集到数据结构当中
        Observable.just(1, 2, 3, 4)
                .collect(new Callable<List<Integer>>() {
                    @Override
                    public List<Integer> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<Integer>, Integer>() {
                    @Override
                    public void accept(List<Integer> integers, Integer integer) throws Exception {
                        integers.add(integer);
                    }
                })
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.d(TAG, "collect  accept " + integers);
                    }
                });
    }

    private void reduce() {
        //reduce 和 scan的区别， reduce组合后的最终结果发送给订阅者，scan 每组合一次发送一次事件给订阅者
        Observable.just(1, 2, 3, 4, 5)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "reduce integer " + integer);
                        Log.e(TAG, "reduce integer2 " + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "reduce accept " + integer);
                    }
                });
    }

    private void combineLatest() {
// combineLatest() 的作用与 zip() 类似，但是 combineLatest() 发送事件的序列是与发送的时间线有关的，
// 当 combineLatest() 中所有的 Observable 都发送了事件，只要其中有一个 Observable 发送事件，
// 这个事件就会和其他 Observable 最近发送的事件结合起来发送
        Observable.combineLatest(
                Observable.intervalRange(1, 4, 1, 1, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String s1 = "A" + aLong;
                                Log.d(TAG, "===================A 发送的事件 " + s1);
                                return s1;
                            }
                        }),
                Observable.intervalRange(1, 5, 2, 2, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String s1 = "B" + aLong;
                                Log.d(TAG, "===================B 发送的事件 " + s1);
                                return s1;
                            }
                        }),
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) throws Exception {
                        return s + s2;
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "combineLatest onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "combineLatest onNext" + s);
//                      当发送 A1 事件之后，因为 B 并没有发送任何事件，所以根本不会发生结合。当 B 发送了 B1 事件之后，
//                      就会与 A 最近发送的事件 A2 结合成 A2B1，这样只有后面一有被观察者发送事件，这个事件就会与其他被观察者最近发送的事件结合起来了
//                      因为 combineLatestDelayError() 就是多了延迟发送 onError() 功能
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "combineLatest onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "combineLatest onComplete");
                    }
                });
    }

    private void zip() {
//        会将多个被观察者合并，根据各个被观察者发送事件的顺序一个个结合起来，最终发送的事件数量会与源 Observable 中最少事件的数量一样
//        两个事件源 ，分别发送事件，个数不一样，最后合并的事件数量按事件个数少的发送
//        发现最终接收到的事件数量是5，那么为什么第二个 Observable 没有发送第6个事件呢？
//        因为在这之前第一个 Observable 已经发送了 onComplete 事件，所以第二个 Observable 不会再发送事件
        Observable.zip(
                Observable.intervalRange(1, 5, 1, 1, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                Log.e(TAG, "A send event");
                                return "A" + aLong;
                            }
                        }),
                Observable.intervalRange(1, 6, 1, 1, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                Log.e(TAG, "B send event");
                                return "B" + aLong;
                            }
                        }),
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) throws Exception {
                        return s + s2;
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "zip onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "zip onNext " + s);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "zip onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "zip onComplete");
                    }
                });
    }

    //    在 concatArray() 和 mergeArray() 两个方法当中，如果其中有一个被观察者发送了一个 Error 事件，
//    那么就会停止发送事件，如果你想 onError() 事件延迟到所有被观察者都发送完事件后再执行的话，
//    就可以使用 concatArrayDelayError() 和 mergeArrayDelayError()
    private void concatMergeDelayError() {
        Observable.concatArray(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onError(new NumberFormatException());
                    }
                }),
                Observable.just(2, 3, 4))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "concatMergeDelayError onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "concatMergeDelayError onNext" + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "concatMergeDelayError onError" + throwable);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "concatMergeDelayError onComplete");
                    }
                });

//      onError 事件是在所有被观察者发送完事件才发送的。mergeArrayDelayError() 也是有同样的作用
        Observable.concatArrayDelayError(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                        observableEmitter.onNext(1);
                        observableEmitter.onError(new NumberFormatException());
                    }
                }),
                Observable.just(2, 3, 4))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "concatMergeDelayError onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "concatMergeDelayError onNext" + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "concatMergeDelayError onError" + throwable);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "concatMergeDelayError onComplete");
                    }
                });
    }

    private void merge() {
//        Observable.concat( 串行
        //并行发送事件
//        Observable.mergeArray(
        Observable.merge(
                Observable.interval(1, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        return "A" + aLong;
                    }
                }),
                Observable.interval(1, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        return "B" + aLong;
                    }
                }))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.e(TAG, "merge onSubscribe");
                    }

                    @Override
                    public void onNext(String integer) {
                        Log.e(TAG, "merge onNext" + integer);
//                        RxJavaTest: merge onNextB4
//                        RxJavaTest: merge onNextA5
//                        RxJavaTest: merge onNextB5
//                        RxJavaTest: merge onNextA6
//                        从结果可以看出，A 和 B 的事件序列都可以发出，将以上的代码换成 concat() 看看打印结果
//                        concat:
//                       RxJavaTest: merge onNextA5
//                       RxJavaTest: merge onNextA6
//                       RxJavaTest: merge onNextA7
//                       RxJavaTest: merge onNextA8
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "merge onComplete");
                    }
                });
    }

    private void concatAry() {
        Observable.concatArray(Observable.just(1, 2),
                Observable.just(3, 4),
                Observable.just(5, 6),
                Observable.just(7, 8),
                Observable.just(9, 10))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "concatArray accept" + integer);
                    }
                });
    }

    private void concat() {
        //        多个观察者组合在一起，然后按照之前发送顺序发送事件,concat() 最多只可以发送4个事件
        Observable.concat(Observable.just(1, 2),
                Observable.just(3, 4),
                Observable.just(5, 6),
                Observable.just(7, 8))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concat onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "concat onComplete");
                    }
                });
    }

    private void transferOperation() {
//        map 可以将被观察者发送的数据类型转变成其他的类型
        Observable.just(1, 2, 3).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer + "-";
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return "$" + s;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "map " + s);
            }
        });
        List<Plan> plans = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        Plan plan1 = new Plan("12:00", "work");
        Plan plan2 = new Plan("12:00", "sing");
        Plan plan3 = new Plan("12:00", "sleep");
        actions.add("kotlin");
        actions.add("android");
        actions.add("ios");
        plan1.setActionList(actions);
        plans.add(plan1);
        plans.add(plan2);
        plans.add(plan3);

        Person person1 = new Person("tom", plans);
        Person person2 = new Person("wk", plans);
        List<Person> personList = new ArrayList<>();
        personList.add(person1);
        personList.add(person2);

        Observable.fromIterable(personList)
                .map(new Function<Person, List<Plan>>() {
                    @Override
                    public List<Plan> apply(Person person) throws Exception {
                        return person.getPlanList();
                    }
                })
                .subscribe(new Consumer<List<Plan>>() {
                    @Override
                    public void accept(List<Plan> plans) throws Exception {
                        for (Plan p : plans) {
                            for (String action : p.getActionList()) {
                                Log.e(TAG, "action=" + action);

                            }
                        }
                    }
                });
        // onNext() 用了嵌套 for 循环来实现，如果代码逻辑复杂起来的话，可能需要多重循环才可以实现。
        //现在看下使用 flatMap() 实现
        Observable.fromIterable(personList)
                .flatMap(new Function<Person, ObservableSource<Plan>>() {
                    @Override
                    public ObservableSource<Plan> apply(Person person) throws Exception {
                        return Observable.fromIterable(person.getPlanList());
                    }
                })
                .flatMap(new Function<Plan, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Plan plan) throws Exception {
                        return Observable.fromIterable(plan.getActionList());
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "flatMap ==================action=" + s);
                    }
                });


//        concatMap() 和 flatMap() 基本上是一样的，只不过 concatMap() 转发出来的事件是有序的，而 flatMap() 是无序的。
//        为了更好的验证 flatMap 是无序的，使用了一个 delay() 方法来延迟
        Observable.fromIterable(personList)
                .flatMap(new Function<Person, ObservableSource<Plan>>() {
                    @Override
                    public ObservableSource<Plan> apply(Person person) throws Exception {
                        if ("wk".equals(person.getName())) {
                            return Observable.fromIterable(person.getPlanList()).delay(100, TimeUnit.MILLISECONDS);
                        }
                        return Observable.fromIterable(person.getPlanList());
                    }
                })
                .subscribe(new Consumer<Plan>() {
                    @Override
                    public void accept(Plan p) throws Exception {
                        Log.e(TAG, "flatMap wuxu =====Plan=" + p.getContent());
                    }
                });
//        验证下 concatMap() 是否是有序的，使用上面同样的代码，只是把 flatMap() 换成 concatMap()


//        buffer 从需要发送的事件当中获取一定数量的事件，并将这些事件放到缓冲区当中一并发出
//        每次发送事件，指针都会往后移动一个元素再取值，直到指针移动到没有元素的时候就会停止取值
        Observable.just(1, 2, 3, 4, 5)
                .buffer(2, 1)
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, "================缓冲区大小： " + integers.size());
                        for (Integer i : integers) {
                            Log.d(TAG, "================元素： " + i);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });


//        groupBy()将发送的数据进行分组，每个分组都会返回一个被观察者
//        在 groupBy() 方法返回的参数是分组的名字，每返回一个值，那就代表会创建一个组，以上的代码就是将1~10的数据分成3组
        Observable.just(5, 2, 3, 4, 1, 6, 8, 9, 7, 10).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) throws Exception {
                return integer % 3;
            }
        }).subscribe(new Consumer<GroupedObservable<Integer, Integer>>() {
            @Override
            public void accept(GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) throws Exception {
                integerIntegerGroupedObservable.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "GroupedObservable key" + integerIntegerGroupedObservable.getKey() + "  value=" + integer);
//                        结果中是有3个组的
                    }
                });
            }
        });

//        scan() 将数据以一定的逻辑聚合起来,分步骤发送事件
        Observable.just(1, 2, 3, 4, 5)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.d(TAG, "scan=================apply ");
                        Log.d(TAG, "scan=================integer " + integer);
                        Log.d(TAG, "scan================integer2 " + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "scan=================accept " + integer);
                    }
                });


        //发送指定数量的事件时，就将这些事件分为一组。window 中的 count 的参数就是代表指定的数量，例如将 count 指定为2，
        // 那么每发2个数据就会将这2个数据分成一组
        Observable.just(1, 2, 3, 4, 5)
                .window(2)
                .subscribe(new Observer<Observable<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "=====================onSubscribe ");
                    }

                    @Override
                    public void onNext(Observable<Integer> integerObservable) {
                        integerObservable.subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "=====================integerObservable onSubscribe ");
                            }

                            @Override
                            public void onNext(Integer integer) {
                                Log.d(TAG, "=====================integerObservable onNext " + integer);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "=====================integerObservable onError ");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "=====================integerObservable onComplete ");
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "=====================onError ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "=====================onComplete ");
                    }
                });
    }

    private void createOperation() {
        create();
        just();
        from();
        fromCallable();
        fromFuture();
        fromIterable();
        defer();
        timer();
//        interval();
        intervalRange();
        range();
        emptyNeverError();
    }

    private void emptyNeverError() {
//        直接发送 onComplete() 事件
        Observable.empty().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "empty onSubscribe ");
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "empty onNext ");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "empty onComplete ");
            }
        });

//        不发送任何事件
        Observable.never().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "never onSubscribe ");
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "never onComplete ");
            }
        });

        Observable.error(new Throwable("error test")).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "error onSubscribe ");
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error onError " + e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "error onComplete ");
            }
        });
    }

    private void range() {
//        同时发送一定范围的事件序列
        Observable.range(1, 3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "range accept " + integer);
            }
        });
        Observable.rangeLong(1, 3).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.e(TAG, "rangeLong accept " + aLong);
            }
        });
    }

    private void intervalRange() {
        Log.e(TAG, "intervalRange");
        Observable.intervalRange(1, 3, 1, 2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.e(TAG, "intervalRange accept " + aLong);
            }
        });
    }

    private void interval() {
        Log.e(TAG, "interval");
//        每隔一段时间就会发送一个事件，这个事件是从0开始，不断增1的数字
        Observable.interval(4, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept " + aLong);
                    }
                });

    }

    private void timer() {
//        当到指定时间后就会发送一个 0L 的值给观察者
        Log.e(TAG, "timer");
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.e(TAG, "accept " + aLong);
            }
        });
    }

    private void defer() {
        i = 100;
//        直到被观察者被订阅后才会创建被观察者
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });
        Observer observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "defer ");
            }

            @Override
            public void onNext(Integer o) {
                Log.d(TAG, "======================onNext " + o);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete ");
            }
        };
        observable.subscribe(observer);
        i = 200;
        observable.subscribe(observer);
//因为 defer() 只有观察者订阅的时候才会创建新的被观察者，所以每订阅一次就会打印一次，并且都是打印 i 最新的值

    }

    private void fromIterable() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        Observable.fromIterable(list)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "fromIterable");
                        Log.d(TAG, "======================accept " + integer);
                    }
                });
    }

    private void fromFuture() {
        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Log.e(TAG, "call is running");
                return "返回的结果";
            }
        });
//        doOnSubscribe() 的作用就是只有订阅时才会发送事件
        Observable.fromFuture(task).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                task.run();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "======================accept " + s);
            }
        });
    }

    private void fromCallable() {
        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Log.d(TAG, "fromCallable");
                return 1;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "======================onNext " + integer);
            }
        });
    }

    private void from() {
        int[] ary = {1, 2, 3, 4};
        Observable.fromArray(ary).subscribe(new Observer<int[]>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "fromArray ");
            }

            @Override
            public void onNext(int[] ints) {
                Log.d(TAG, "======================onNext " + Arrays.toString(ints));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void just() {
        Observable.just(1, 2, 3).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "just ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "======================onNext " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void create() {
        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "thread " + Thread.currentThread().getName());
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        Observer observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "======================onNext " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(observer);

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}

class Person {
    private String name;
    private List<Plan> planList = new ArrayList<>();

    public Person(String name, List<Plan> planList) {
        this.name = name;
        this.planList = planList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Plan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
    }
}

class Plan {
    private String time;
    private String content;
    private List<String> actionList = new ArrayList<>();

    public Plan(String time, String content) {
        this.time = time;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getActionList() {
        return actionList;
    }

    public void setActionList(List<String> actionList) {
        this.actionList = actionList;
    }
}