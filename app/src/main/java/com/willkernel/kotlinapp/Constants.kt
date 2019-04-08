package com.willkernel.kotlinapp

class Constants {
    companion object {
        //伴生对象 类似java静态访问属性 Constants.BUGLY_APPID
        val ARGUMENTS_TITLE = "title"
        val HOME_PAGE = 1

        val BUNDLE_VIDEO_DATA = "video_data"
        val BUNDLE_CATEGORY_DATA = "category_data"

        //腾讯 Bugly APP id
        val BUGLY_APPID = "1111111"


        //sp 存储的文件名
        val FILE_WATCH_HISTORY_NAME = "watch_history_file"   //观看记录

        val FILE_COLLECTION_NAME = "collection_file"    //收藏视屏缓存的文件名
        val BASE_URL="http://baobab.kaiyanapp.com/api/"
    }
}