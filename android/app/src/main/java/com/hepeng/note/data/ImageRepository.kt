package com.hepeng.note.data

import kotlinx.coroutines.flow.Flow

class ImageRepository(private val imageDao: ImageDao) {

    val allImages: Flow<List<Image>> = imageDao.getAll()

    suspend fun insert(image: Image) {
        imageDao.insert(image)
    }

    suspend fun delete(image: Image) {
        imageDao.delete(image)
    }
}