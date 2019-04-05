package com.willkernel.kotlinapp.test;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Created by willkernel
 * on 2019/3/30.
 */
public interface GetRequest {
    //    @GET("http://baobab.kaiyanapp.com/api/v4/tabs/selected")
    @GET("openapi.do?keyfrom=Yanzhikai&key=2032414398&type=data&doctype=json&version=1.1&q=car")
    Call<Translation> getCall();

    /**
     * method：网络请求的方法（区分大小写）
     * path：网络请求地址路径
     * hasBody：是否有请求体
     * {id} 表示是一个变量
     * method 的值 retrofit 不会做处理，所以要自行保证准确
     */
    @HTTP(method = "GET", path = "blog/{id}", hasBody = false)
    Call<ResponseBody> getCall(@Path("id") int id);

    // 其使用方式同 @Field与@FieldMap
    @GET("/")
    Call<String> cate(@Query("cate") String cate);


    /**
     * 表明是一个表单格式的请求（Content-Type:application/x-www-form-urlencoded）
     * <code>Field("username")</code> 表示将后面的 <code>String name</code> 中name的取值作为 username 的值
     */
    @POST("/form")
    @FormUrlEncoded
    Call<ResponseBody> testFormUrlEncode(@Field("username") String name, @Field("age") int age);

    /**
     * Map的key作为表单的键
     */
    @POST("/form")
    @FormUrlEncoded
    Call<ResponseBody> testFormUrlEncode(@FieldMap Map<String, Object> map);


    /**
     * {@link Part} &{@link @PartMap}发送 Post请求 时提交请求的表单字段
     * 与@Field的区别：功能相同，但携带的参数类型更加丰富，包括数据流，所以适用于 有文件上传 的场景
     * <p>
     * {@link Part} 后面支持三种类型，{@link RequestBody}、{@link okhttp3.MultipartBody.Part} 、任意类型
     * 除 {@link okhttp3.MultipartBody.Part} 以外，其它类型都必须带上表单字段({@link okhttp3.MultipartBody.Part} 中已经包含了表单字段的信息)，
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testMultiPart(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part MultipartBody.Part file);


    /**
     * PartMap 注解支持一个Map作为参数，支持 {@link RequestBody } 类型，
     * 如果有其它的类型，会被{@link retrofit2.Converter}转换，如后面会介绍的 使用{@link com.google.gson.Gson} 的 {@link retrofit2.converter.gson.GsonRequestBodyConverter}
     * 所以{@link MultipartBody.Part} 就不适用了,所以文件只能用<b> @Part MultipartBody.Part </b>
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testMultiPart(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file);

    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload3(@PartMap Map<String, RequestBody> args);

//    单文件上传方法
    @Multipart
    @POST("user/register.do")
    Observable<BasicResponse<RegisterBean>> register(@Part List<MultipartBody.Part> partList);

//    多文件传方法
    @POST("upload/")
    @Multipart
    Observable<BasicResponse> uploadFiles(@Part("filename") String description,
                                          @Part("pic\"; filename=\"image1.png") RequestBody imgs1,
                                          @Part("pic\"; filename=\"image2.png") RequestBody imgs2);

//    多文件上传方法
    @POST("upload/")
    @Multipart
    Observable<BasicResponse> uploadFiles( @Part("filename") String description,
                                           @PartMap() Map<String, RequestBody> maps);

    //    @Header & @Headers
//    @Header("Authorization")
    @GET("user")
    Call<User> getUser(@Header("Authorization") String authorization);

    @Headers("Authorization:authorization")
    @GET("user")
    Call<User> getUser();
    // 以上的效果是一致的。
// 区别在于使用场景和使用方式
// 1. 使用场景：@Header用于添加不固定的请求头，@Headers用于添加固定的请求头
// 2. 使用方式：@Header作用于方法的参数；@Headers作用于方法


//    @Body 以 Post方式 传递 自定义数据类型 给服务器
//    特别注意：如果提交的是一个Map，那么作用相当于 @Field
//不过Map要经过 FormBody.Builder 类处理成为符合 Okhttp 格式的表单，如：
//    FormBody.Builder builder = new FormBody.Builder();
//builder.add("key","value");


    /**
     * {@link Path} 作用：URL地址的缺省值
     * 访问的API是：https://api.github.com/users/{user}/repos
     * 在发起请求时， {user} 会被替换为方法的第一个参数 user（被@Path注解作用）
     */
    @GET("users/{user}/repos")
    Call<ResponseBody> getBlog(@Path("user") String user);

    /**
     * {@link Url} 作用：直接传入一个请求的 URL变量 用于URL设置
     * 当有URL注解时，@GET传入的URL就可以省略
     * 当GET、POST...HTTP等方法中没有设置Url时，则必须使用 {@link Url}提供
     */
    @GET
    Call<ResponseBody> testUrlAndQuery(@Url String url, @Query("showAll") boolean showAll);


    /**
     * 金山词霸
     */
    @GET("ajax.php?a=fy")
    Call<Translation> translate(@Query("f") String ori, @Query("t") String dest, @Query("w") String words);

    /**
     * 有道翻译
     * 采用@Post表示Post方法进行请求（传入部分url地址）
     * 采用@FormUrlEncoded注解的原因:API规定采用请求格式x-www-form-urlencoded,即表单形式
     * 需要配合@Field 向服务器提交需要的字段
     */
    @POST("translate?doctype=json&jsonversion=&type=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=&abtest=")
    @FormUrlEncoded
    Call<TranslationYD> translateYoudao(@Field("i") String target);

    /**
     * RxJava方式
     */
    @GET("ajax.php?a=fy&f=auto&t=auto")
    Observable<Translation> translateRxJava(@Query("w") String words);

    /**
     * RxJava方式
     */
    @Headers("Cache-Control: public, max-age=86400") //  设置缓存
    @GET("ajax.php?a=fy&f=auto&t=auto")
    Observable<Translation1> translateRxJava1(@Query("w") String words);

}
