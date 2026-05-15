package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.core.data.HABITS_COLLECTION_ROUTE
import com.horizondev.habitbloom.core.data.HABIT_CATEGORIES_COLLECTION_ROUTE
import com.horizondev.habitbloom.core.data.HABIT_ICONS_COLLECTION_ROUTE
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable

class HabitsRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val localeManager: AppLocaleManager
) {
    suspend fun getHabits(): Result<List<HabitInfo>> {

        val locale = localeManager.getLocale()

        return runCatching {
            firestore.collection(HABITS_COLLECTION_ROUTE).get().let { querySnapshot ->
                querySnapshot.documents.map { document ->
                    document.data(OfficialHabitInfoResponse.serializer()).copy(id = document.id)
                }
            }.mapNotNull { it.toDomainModel(locale.code) }
        }
    }

    suspend fun getHabitCategories(): Result<List<HabitCategoryData>> {
        val locale = localeManager.getLocale()

        return runCatching {
            firestore.collection(HABIT_CATEGORIES_COLLECTION_ROUTE).get().let { querySnapshot ->
                querySnapshot.documents.map { document ->
                    document.data(HabitCategoryResponse.serializer()).copy(id = document.id)
                }
            }.map { it.toDomainModel(locale.code) }
        }
    }

    suspend fun getHabitIcons(): Result<List<String>> {
        return runCatching {
            firestore.collection(HABIT_ICONS_COLLECTION_ROUTE).get().let { querySnapshot ->
                querySnapshot.documents.map { document ->
                    document.data(HabitIconResponse.serializer())
                }
            }.map { it.iconUrl }
        }
    }
}


@Serializable
data class HabitLocalization(
    val name: String,
    val description: String
)

@Serializable
data class Habit(
    val iconUrl: String,
    val timeOfDay: String,
    val localizations: Map<String, HabitLocalization>
)

@Serializable
data class HabitIconResponse(
    val iconUrl: String
)
