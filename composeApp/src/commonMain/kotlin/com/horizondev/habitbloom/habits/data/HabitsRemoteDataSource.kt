package com.horizondev.habitbloom.habits.data

import com.horizondev.habitbloom.core.data.HABITS_COLLECTION_ROUTE
import dev.gitlive.firebase.firestore.FirebaseFirestore

class HabitsRemoteDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun getHabits(): Result<List<HabitInfoResponse>> {
        return runCatching {
            val habitResponse = firestore.collection(HABITS_COLLECTION_ROUTE).get()
            habitResponse.documents.map { document ->
                document.data(HabitInfoResponse.serializer()).copy(id = document.id)
            }
        }
    }
}