package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.core.data.HABITS_COLLECTION_ROUTE
import com.horizondev.habitbloom.core.data.USER_HABITS_COLLECTION_ROUTE
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.QuerySnapshot
import kotlinx.serialization.Serializable

class HabitsRemoteDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun getHabits(userId: String?): Result<List<HabitInfoResponse>> {
        fun extractHabits(
            querySnapshot: QuerySnapshot
        ): List<HabitInfoResponse> {
            return querySnapshot.documents.map { document ->
                document.data(HabitInfoResponse.serializer()).copy(id = document.id)
            }
        }

        return runCatching {
            val habitResponse = firestore.collection(HABITS_COLLECTION_ROUTE).get().let {
                extractHabits(it)
            }
            val habitPersonalHabits = firestore.collection(USER_HABITS_COLLECTION_ROUTE).get().let {
                extractHabits(it)
            }.filter { userId == userId }

            habitResponse + habitPersonalHabits
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

    suspend fun pushHabitsToFirestore() {
        val morningHabits = listOf(
            Habit(
                description = "Waking up early allows you to start your day with more time for productive activities. It helps in establishing a consistent routine and can improve sleep quality.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Wake Up Early",
                shortInfo = "Start by waking up 15 minutes earlier each week until you reach your desired wake-up time.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Making your bed is a simple task that can boost your sense of accomplishment and set a positive tone for the day. It also contributes to a tidy and organized environment.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Make Your Bed",
                shortInfo = "This habit is often recommended as the first step towards building a more organized life.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Hydrating first thing in the morning kick-starts your metabolism, flushes out toxins, and helps you wake up.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Drink Water",
                shortInfo = "Keep a glass of water by your bedside to drink as soon as you wake up.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Morning exercise boosts energy levels, improves mood, and enhances focus throughout the day. It also helps in maintaining physical health and fitness.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Exercise/Workout",
                shortInfo = "Start with simple exercises like stretching or a short walk, and gradually build up to more intense workouts.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Morning meditation helps to clear your mind, reduce stress, and set a calm, focused tone for the day. It also improves mindfulness and emotional regulation.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Meditation",
                shortInfo = "Start with 5 minutes of guided meditation and gradually increase the duration as you get more comfortable.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Reading or listening to audiobooks in the morning can inspire you, expand your knowledge, and stimulate creative thinking.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Read or Listen to an Audiobook",
                shortInfo = "Choose content that is motivational or educational to start your day on a positive note.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Planning your day helps in prioritizing tasks, reducing stress, and increasing productivity. It gives you a clear roadmap of what to accomplish.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Plan Your Day",
                shortInfo = "Use a planner or a digital tool to list your top 3 priorities for the day.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "A nutritious breakfast fuels your body, improves concentration, and prevents overeating later in the day.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Healthy Breakfast",
                shortInfo = "Include a balance of protein, fiber, and healthy fats in your breakfast.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Writing down things you're grateful for in the morning boosts positivity and can improve mental health over time.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Gratitude Journal",
                shortInfo = "Write 3 things you're grateful for each morning to cultivate a positive mindset.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Morning stretching or yoga increases blood flow, reduces muscle tension, and prepares your body for the day's activities.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Stretching/Yoga",
                shortInfo = "Spend 5-10 minutes stretching or doing a few simple yoga poses.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Cold showers can increase alertness, improve circulation, and boost your immune system.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Cold Shower",
                shortInfo = "Start with warm water and gradually switch to cold for the last 30 seconds.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Reviewing your short-term or long-term goals in the morning keeps you focused and motivated.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Review Goals",
                shortInfo = "Keep your goals visible, such as on a vision board or written in a journal.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Positive affirmations can boost self-esteem, reduce stress, and promote a positive outlook on life.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Affirmations",
                shortInfo = "Repeat affirmations that resonate with your personal goals or challenges.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Avoiding screens in the morning reduces stress and helps you start the day with more focus and mindfulness.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "No Phone/Screen Time",
                shortInfo = "Try not to check your phone for the first hour after waking up.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Dedicating time to practice a skill in the morning can lead to consistent improvement and mastery over time.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Practice a Skill",
                shortInfo = "Choose a skill you want to improve and spend 15-30 minutes on it each morning.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "A quick morning clean-up keeps your space tidy and organized, reducing stress and increasing productivity.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Clean Up/Organize",
                shortInfo = "Focus on small tasks like tidying your workspace or organizing your to-do list.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "A morning walk boosts mood, improves cardiovascular health, and gives you time to reflect or plan your day.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Go for a Walk",
                shortInfo = "Try walking in a park or natural setting to increase the benefits.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Writing in the morning can boost creativity, clarify thoughts, and improve emotional well-being.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Creative Writing/Journal",
                shortInfo = "Set aside 10-15 minutes for freewriting or journaling about your thoughts or goals.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Regularly reviewing your finances keeps you aware of your financial health and helps in making informed decisions.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Review Finances",
                shortInfo = "Spend a few minutes reviewing your budget or tracking your expenses.",
                timeOfDay = "Morning"
            ),
            Habit(
                description = "Breathing exercises help reduce stress, improve focus, and increase oxygen flow to your brain and body.",
                iconUrl = "https://i.ibb.co/3cW4xBY/image.webp",
                name = "Morning Breathing Exercises",
                shortInfo = "Try deep breathing or box breathing for 5 minutes each morning.",
                timeOfDay = "Morning"
            )
        )

        morningHabits.forEach { habit ->
            firestore.collection("habits").add(habit)
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
data class Habit(
    val description: String,
    val iconUrl: String,
    val name: String,
    val shortInfo: String,
    val timeOfDay: String,
    val userId: String? = null
)