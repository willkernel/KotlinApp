package com.willkernel.kotlinapp.test;

import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 设置日志拦截器拦截服务器返回的json数据。Retrofit将请求到json数据直接转换成了实体类，但有时候我们需要查看json数据，Retrofit并没有提供直接获取json数据的功能。因此我们需要自定义一个日志拦截器拦截json数据，并输入到控制台。
 * <p>
 * 设置 Http 拦截器，处理缓存问题。通过拦截器拦截Http请求头，为请求头配置缓存信息，包括控制缓存的最大生命值，控制缓存的过期时间。
 * <p>
 * 获取Retrofit实例。通过单利模式获取Retrofit实例
 */
public class RetrofitUtil {
    private static final String TAG = "RetrofitUtil";
    private GetRequest request;
    private int i;
    private int currentRetryCount;
    private int waitRetryTime;

    private static class CREATE {
        private static RetrofitUtil INSTANCE = new RetrofitUtil();
    }

    public static RetrofitUtil getInstance() {
        return CREATE.INSTANCE;
    }

    private RetrofitUtil() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    Log.e(TAG, "RetrofitUtil----" + URLEncoder.encode(message, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        File cacheFile = new File(Environment.getDataDirectory(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .build();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
//                .client(client)
//                .baseUrl("http://fanyi.youdao.com/")
                //数据解析器（Converter）
                .addConverterFactory(GsonConverterFactory.create(gson))
                //网络请求适配器（CallAdapter）支持rxjava
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        request = retrofit.create(GetRequest.class);

    }

    private void init() {

        // @FormUrlEncoded
        Call<ResponseBody> call1 = request.testFormUrlEncode("tom", 12);


        Map<String, Object> map = new HashMap<>();
        map.put("username", "wk");
        map.put("age", 24);
        Call<ResponseBody> call2 = request.testFormUrlEncode(map);


        //  @Multipart
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "carson");
        RequestBody age = RequestBody.create(MediaType.parse("text/plain"), "25");
        RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), "text content");

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
        Call<ResponseBody> call3 = request.testMultiPart(name, age, filePart);


        Map<String, RequestBody> fileUploadArgs = new HashMap<>();
        fileUploadArgs.put("username", name);
        fileUploadArgs.put("age", age);
        Call<ResponseBody> call4 = request.testMultiPart(fileUploadArgs, filePart);

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("key", "value");

    }

    public void callTranslation() {
        //发送网络请求(异步)
        Call<Translation> translationCall = request.getCall();
        translationCall.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                Log.e(TAG, "异步resp " + response.body().toString());
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable t) {
                System.out.println("异步conn error");
            }
        });

        // 发送网络请求（同步）
        try {
            Response<Translation> response = translationCall.execute();
            Log.e(TAG, "同步resp " + response.body().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void translate(String ori, String dest, String content) {
        Call<Translation> translationCall = request.translate(ori, dest, content);
        Log.e(TAG, " requestUrl " + translationCall.request().url());
        translationCall.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                Log.e(TAG, "异步resp " + response.body().toString());
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable t) {
                Log.e(TAG, "异步onFailure" + t.toString());
            }
        });
    }

    public void translateYd(String content) {
        Call<TranslationYD> translationCall = request.translateYoudao(content);
        Log.e(TAG, "translateYd requestUrl " + translationCall.request().url());
        translationCall.enqueue(new Callback<TranslationYD>() {
            @Override
            public void onResponse(Call<TranslationYD> call, Response<TranslationYD> response) {
                Log.e(TAG, "异步resp " + response.body().toString());
            }

            @Override
            public void onFailure(Call<TranslationYD> call, Throwable t) {
                Log.e(TAG, "异步onFailure" + t.toString());
            }
        });
    }

    public void translateCiBaRxJava(String content) {
        Observable<Translation> translateCiba = null;
        try {
            translateCiba = request.translateRxJava(URLEncoder.encode(content, "UTF-8"));
            translateCiba.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Translation>() {
                        @Override
                        public void onSubscribe(Disposable disposable) {

                        }

                        @Override
                        public void onNext(Translation translation) {
                            Log.e(TAG, "translateCiBaRxJava onNext " + translation);
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void register(List<MultipartBody.Part> parts) {
        request.register(parts);
    }

    public void uploadFiles(String description,
                            RequestBody imgs1,
                            RequestBody imgs2) {
        request.uploadFiles(description,imgs1,imgs2);
    }

    public void uploadFiles(String description,Map<String,RequestBody> map) {
        request.uploadFiles(description, map);
    }
    /**
     * 网络轮询
     */
    public void intervalRepeat() {
//        无条件
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                    }
                }).subscribe();

//        有条件
        Observable.just(1, 2, 3)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                        if (i > 3) {
                            // 此处选择发送onError事件以结束轮询，因为可触发下游观察者的onError（）方法回调
                            return Observable.error(new Throwable("轮询结束"));
                        }
                        // 若轮询次数＜4次，则发送1Next事件以继续轮询
                        // 注：此处加入了delay操作符，作用 = 延迟一段时间发送（此处设置 = 2s），以实现轮询间间隔设置
                        return Observable.just(1).delay(2000, TimeUnit.MILLISECONDS);
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Integer integer) {
                i++;
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 网络请求嵌套
     */
    public void translate2Ciba(String w1, String w2) {
        request.translateRxJava(w1)
                .subscribeOn(Schedulers.io())// （初始被观察者）切换到IO线程进行网络请求1
                .observeOn(AndroidSchedulers.mainThread()) // （新观察者）切换到主线程 处理网络请求1的结果
                .doOnNext(new Consumer<Translation>() {
                    @Override
                    public void accept(Translation translation) throws Exception {
                        Log.e(TAG, "translate first " + translation);
                    }
                })
                // （新被观察者，同时也是新观察者）切换到IO线程去发起登录请求
                // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                // 但对于初始观察者，它则是新的被观察者  ,切换flatmap的线程为 io,否则报错NetworkOnMainThreadException
                .observeOn(Schedulers.io())

                .flatMap(new Function<Translation, ObservableSource<Translation1>>() {
                    @Override
                    public ObservableSource<Translation1> apply(Translation translation) throws Exception {
                        return request.translateRxJava1(w2);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())// （初始观察者）切换到主线程 处理网络请求2的结果
                .subscribe(new Consumer<Translation1>() {
                    @Override
                    public void accept(Translation1 translation1) throws Exception {
                        Log.e(TAG, "translate second " + translation1);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "translate second " + throwable);
                    }
                });

    }


    /**
     * 网络请求出错重连
     */
    public void requestRetry() {
        Observable.just(1).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                // 参数Observable<Throwable>中的泛型 = 上游操作符抛出的异常，可通过该条件来判断异常的类型
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {

                        // 输出异常信息
                        Log.d(TAG, "发生异常 = " + throwable.toString());

                        /**
                         * 需求1：根据异常类型选择是否重试
                         * 即，当发生的异常 = 网络异常 = IO异常 才选择重试
                         */
                        if (throwable instanceof IOException) {

                            Log.d(TAG, "属于IO异常，需重试");

                            /**
                             * 需求2：限制重试次数
                             * 即，当已重试次数 < 设置的重试次数，才选择重试
                             */
                            if (currentRetryCount < 3) {

                                // 记录重试次数
                                currentRetryCount++;
                                Log.d(TAG, "重试次数 = " + currentRetryCount);

                                /**
                                 * 需求2：实现重试
                                 * 通过返回的Observable发送的事件 = Next事件，从而使得retryWhen（）重订阅，最终实现重试功能
                                 *
                                 * 需求3：延迟1段时间再重试
                                 * 采用delay操作符 = 延迟一段时间发送，以实现重试间隔设置
                                 *
                                 * 需求4：遇到的异常越多，时间越长
                                 * 在delay操作符的等待时间内设置 = 每重试1次，增多延迟重试时间1s
                                 */
                                // 设置等待时间
                                waitRetryTime = 1000 + currentRetryCount * 1000;
                                Log.d(TAG, "等待时间 =" + waitRetryTime);
                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);


                            } else {
                                // 若重试次数已 > 设置重试次数，则不重试
                                // 通过发送error来停止重试（可在观察者的onError（）中获取信息）
                                return Observable.error(new Throwable("重试次数已超过设置次数 = " + currentRetryCount + "，即 不再重试"));

                            }
                        }

                        // 若发生的异常不属于I/O异常，则不重试
                        // 通过返回的Observable发送的事件 = Error事件 实现（可在观察者的onError（）中获取信息）
                        else {
                            return Observable.error(new Throwable("发生了非网络异常（非I/O异常）"));
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())               // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  // 切换回到主线程 处理请求结果
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer result) {
                        // 接收服务器返回的数据
                        Log.d(TAG, "发送成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 获取停止重试的信息
                        Log.d(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 合并数据源 & 统一显示
     * 此处采用Merge（） & Zip（）操作符
     * Merge（）例子 ：实现较为简单的从（网络 + 本地）获取数据 & 统一展示
     * Zip（）例子：结合Retrofit 与RxJava，实现较为复杂的合并2个网络请求向2个服务器获取数据 & 统一展示
     */
    String result = "";

    public void mergeTest() {
        Observable<String> network = Observable.just("network");
        Observable<String> file = Observable.just("file");

        Observable.merge(network, file)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept " + s);
                        result += s;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "result " + result);
                    }
                });
    }

    public void zipTest() {
        Observable c1 = request.translateRxJava("home").subscribeOn(Schedulers.io());
        Observable c2 = request.translateRxJava1("mall").subscribeOn(Schedulers.io());
        Observable
                .zip(c1, c2, new BiFunction<Translation, Translation1, String>() {
                    @Override
                    public String apply(Translation translation, Translation1 translation1) throws Exception {
                        return translation.getContent().getWord_mean() + translation1.getContent().getPh_en();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        Log.d(TAG, "最终接收到的数据是：" + o);
                    }
                });

    }

    /**
     * 从内存 磁盘中获取数据
     * firstElement（）和 concat（）
     */
    public void memoryDisk() {
        String memoryCache = null;
        String diskCache = "disk cache";

        Observable memoryObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                // 先判断内存缓存有无数据
                if (memoryCache != null) {
                    // 若有该数据，则发送
                    emitter.onNext(memoryCache);
                } else {
                    // 若无该数据，则直接发送结束事件
                    emitter.onComplete();
                }
            }
        });

        Observable diskObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                // 先判断磁盘缓存有无数据
                if (diskCache != null) {
                    // 若有该数据，则发送
                    emitter.onNext(diskCache);
                } else {
                    // 若无该数据，则直接发送结束事件
                    emitter.onComplete();
                }

            }
        });

        /*
         * 设置第3个Observable：通过网络获取数据
         **/
        Observable<String> network = Observable.just("从网络中获取数据");

//        通过concat（）合并memory、disk、network 3个被观察者的事件（即检查内存缓存、磁盘缓存 & 发送网络请求）
//        并将它们按顺序串联成队列
        Observable.concat(memoryObservable, diskObservable, network)
                // 通过firstElement()，从串联队列中取出并发送第1个有效事件（Next事件），即依次判断检查memory、disk、network
                // 即本例的逻辑为：
                // a. firstElement()取出第1个事件 = memory，即先判断内存缓存中有无数据缓存；由于memoryCache = null，即内存缓存中无数据，所以发送结束事件（视为无效事件）
                // b. firstElement()继续取出第2个事件 = disk，即判断磁盘缓存中有无数据缓存：由于diskCache ≠ null，即磁盘缓存中有数据，所以发送Next事件（有效事件）
                // c. 即firstElement()已发出第1个有效事件（disk事件），所以停止判断。
                .firstElement()
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        Log.e(TAG, "fetch data " + o);
                    }
                });

    }

    private class HttpCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            //  将token统一放到请求头
            String token = SharedPreferencesHelper.getToken();
            //  也可以统一配置用户名
            String user_id = "123456";
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.d("Okhttp", "no network");
            }
            //有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
            okhttp3.Response oriResp = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                String cacheControl = request.cacheControl().toString();
                return oriResp.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .header("token", token)
                        .header("user_id", user_id)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return oriResp.newBuilder()
                        .header("Cache-Control", "public,only-if-cached,max-stale-2419200")
                        .removeHeader("Pragma")
                        .build();
            }

        }
    }

}
