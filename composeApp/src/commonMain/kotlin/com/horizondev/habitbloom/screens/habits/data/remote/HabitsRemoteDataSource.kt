package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.core.data.HABITS_COLLECTION_ROUTE
import com.horizondev.habitbloom.core.data.HABIT_CATEGORIES_COLLECTION_ROUTE
import com.horizondev.habitbloom.core.data.USER_HABITS_COLLECTION_ROUTE
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable

class HabitsRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val localeManager: AppLocaleManager
) {
    suspend fun getHabits(userId: String?): Result<List<HabitInfo>> {

        val locale = localeManager.getLocale()

        return runCatching {

            val habitResponse =
                firestore.collection(HABITS_COLLECTION_ROUTE).get().let { querySnapshot ->
                    querySnapshot.documents.map { document ->
                        document.data(OfficialHabitInfoResponse.serializer()).copy(id = document.id)
                    }
                }.mapNotNull { it.toDomainModel(locale.code) }

            val habitPersonalHabits =
                firestore.collection(USER_HABITS_COLLECTION_ROUTE).get().let { querySnapshot ->
                    querySnapshot.documents.map { document ->
                        document.data(HabitInfoResponse.serializer()).copy(id = document.id)
                    }
                }.filter { it.userId == userId }.map { it.toDomainModel() }

            habitResponse + habitPersonalHabits
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

    suspend fun savePersonalHabit(
        userId: String,
        timeOfDay: TimeOfDay,
        title: String,
        description: String,
        icon: String = DEFAULT_PHOTO_URL
    ): Result<Boolean> {
        return runCatching {
            firestore.collection(USER_HABITS_COLLECTION_ROUTE).add(
                data = HabitInfoResponse(
                    description = description,
                    name = title,
                    iconUrl = icon,
                    userId = userId,
                    shortInfo = "",
                    timeOfDay = timeOfDay.toNetworkModel()
                )
            ).id.isNotEmpty()
        }
    }

    /**
     * Deletes a custom habit from Firebase Firestore.
     *
     * @param habitId The ID of the custom habit to delete
     * @return Result containing success (true) or failure with error
     */
    suspend fun deleteCustomHabit(habitId: String): Result<Boolean> {
        return runCatching {
            firestore.collection(USER_HABITS_COLLECTION_ROUTE).document(habitId).delete()
            true
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