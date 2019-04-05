package com.willkernel.kotlinapp.mvp.model

import java.io.Serializable

/**
 * Created by willkernel
 * on 2019/4/2.
 */
data class CategoryBean(val id: Long, val name: String, val description: String, val bgPicture: String, val bgColor: String, val headerImage: String) : Serializable
