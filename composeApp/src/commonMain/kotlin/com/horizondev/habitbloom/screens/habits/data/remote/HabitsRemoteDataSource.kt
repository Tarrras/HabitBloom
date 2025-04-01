package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.core.data.HABITS_COLLECTION_ROUTE
import com.horizondev.habitbloom.core.data.USER_HABITS_COLLECTION_ROUTE
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
        val MorningHabits = listOf(
            Habit(
                iconUrl = "https://i.ibb.co/g7xXBgc/wake-up-early.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Wake Up Early",
                        "Waking up early allows you to start your day with more time for productive activities. It helps in establishing a consistent routine and can improve sleep quality. Start by waking up 15 minutes earlier each week until you reach your desired wake-up time."
                    ),
                    "uk" to HabitLocalization(
                        "Прокидатися рано",
                        "Раннє пробудження дає більше часу для продуктивних справ. Це сприяє формуванню стабільного розпорядку і може покращити якість сну. Почніть прокидатися на 15 хвилин раніше щотижня, поки не досягнете бажаного часу пробудження."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/KGwhJk7/make-your-bed.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Make Your Bed",
                        "Making your bed is a simple task that can boost your sense of accomplishment and set a positive tone for the day. It also contributes to a tidy and organized environment. This habit is often recommended as the first step towards building a more organized life."
                    ),
                    "uk" to HabitLocalization(
                        "Застеляти ліжко",
                        "Застеляння ліжка — це просте завдання, яке створює відчуття досягнення і налаштовує на позитивний лад. Це також допомагає підтримувати чистоту та порядок. Цю звичку часто радять як перший крок до впорядкованого життя."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/d5w8ZKq/drink-water.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Drink Water",
                        "Hydrating first thing in the Morning kick-starts your metabolism, flushes out toxins, and helps you wake up. Keep a glass of water by your bedside to drink as soon as you wake up."
                    ),
                    "uk" to HabitLocalization(
                        "Пити воду",
                        "Гідратація зранку запускає обмін речовин, виводить токсини та допомагає прокинутися. Тримайте склянку води біля ліжка, щоб випити її одразу після пробудження."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/mTF2LDp/exercise.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Exercise/Workout",
                        "Morning exercise boosts energy levels, improves mood, and enhances focus throughout the day. It also helps in maintaining physical health and fitness. Start with simple exercises like stretching or a short walk, and gradually build up to more intense workouts."
                    ),
                    "uk" to HabitLocalization(
                        "Фізичні вправи",
                        "Ранкові фізичні вправи підвищують рівень енергії, покращують настрій і підвищують концентрацію протягом дня. Вони також підтримують фізичне здоров’я та форму. Почніть з простих вправ, таких як розтяжка або коротка прогулянка, поступово переходячи до інтенсивніших."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/rb21DPS/meditation.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Meditation",
                        "Morning meditation helps to clear your mind, reduce stress, and set a calm, focused tone for the day. It also improves mindfulness and emotional regulation. Start with 5 minutes of guided meditation and gradually increase the duration as you get more comfortable."
                    ),
                    "uk" to HabitLocalization(
                        "Медитація",
                        "Ранкова медитація очищує розум, зменшує стрес і налаштовує на спокійний і зосереджений день. Вона також покращує усвідомленість і емоційну стійкість. Почніть з 5 хвилин медитації з гідом і поступово збільшуйте час."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/Nxp50XS/read-or-listen-to-an-audiobook.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Read or Listen to an Audiobook",
                        "Reading or listening to audiobooks in the Morning can inspire you, expand your knowledge, and stimulate creative thinking. Choose content that is motivational or educational to start your day on a positive note."
                    ),
                    "uk" to HabitLocalization(
                        "Читати або слухати аудіокнигу",
                        "Читання або прослуховування аудіокниг зранку надихає, розширює кругозір та стимулює креативність. Обирайте мотиваційний або освітній контент, щоб розпочати день позитивно."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/sK9sHN1/plan-your-day.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Plan Your Day",
                        "Planning your day helps in prioritizing tasks, reducing stress, and increasing productivity. It gives you a clear roadmap of what to accomplish. Use a planner or a digital tool to list your top 3 priorities for the day."
                    ),
                    "uk" to HabitLocalization(
                        "Планувати день",
                        "Планування дня допомагає розставити пріоритети, зменшує стрес і підвищує продуктивність. Воно надає чітке уявлення про завдання на день. Використовуйте щоденник або цифровий інструмент, щоб визначити три головні пріоритети."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/bQYDrVq/healthy-breakfast.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Healthy Breakfast",
                        "A nutritious breakfast fuels your body, improves concentration, and prevents overeating later in the day. Include a balance of protein, fiber, and healthy fats in your breakfast."
                    ),
                    "uk" to HabitLocalization(
                        "Здоровий сніданок",
                        "Поживний сніданок заряджає енергією, покращує концентрацію та запобігає переїданню протягом дня. Додайте до сніданку білки, клітковину і корисні жири."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/4Y8Z2ZM/gratitude-journal.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Gratitude Journal",
                        "Writing down things you're grateful for in the Morning boosts positivity and can improve mental health over time. Write 3 things you're grateful for each Morning to cultivate a positive mindset."
                    ),
                    "uk" to HabitLocalization(
                        "Щоденник подяки",
                        "Записування того, за що ви вдячні, підвищує рівень позитиву і з часом може покращити психічне здоров’я. Щоранку запишіть 3 речі, за які ви вдячні, щоб сформувати позитивний настрій."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/3SK3CZx/stretching-yoga.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Stretching/Yoga",
                        "Morning stretching or yoga increases blood flow, reduces muscle tension, and prepares your body for the day's activities. Spend 5-10 minutes stretching or doing a few simple yoga poses."
                    ),
                    "uk" to HabitLocalization(
                        "Розтяжка або йога",
                        "Ранкова розтяжка або йога покращує кровообіг, зменшує напруження в м’язах і готує тіло до активного дня. Приділіть 5–10 хвилин розтяжці або простим позам йоги."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/pxKp0Dc/cold-shower.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Cold Shower",
                        "Cold showers can increase alertness, improve circulation, and boost your immune system. Start with warm water and gradually switch to cold for the last 30 seconds."
                    ),
                    "uk" to HabitLocalization(
                        "Контрастний душ",
                        "Холодний душ підвищує бадьорість, покращує кровообіг і зміцнює імунну систему. Почніть з теплої води, а в кінці переключіться на холодну протягом 30 секунд."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/NnRdxdK/review-goals-tasks.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Review Goals",
                        "Reviewing your short-term or long-term goals in the Morning keeps you focused and motivated. Keep your goals visible, such as on a vision board or written in a journal."
                    ),
                    "uk" to HabitLocalization(
                        "Огляд цілей",
                        "Огляд короткострокових або довгострокових цілей вранці допомагає залишатися зосередженим і мотивованим. Тримайте цілі на видному місці — наприклад, на дошці візуалізації або в щоденнику."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/Mnt6Dwf/affirmations.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Affirmations",
                        "Positive affirmations can boost self-esteem, reduce stress, and promote a positive outlook on life. Repeat affirmations that resonate with your personal goals or challenges."
                    ),
                    "uk" to HabitLocalization(
                        "Аффірмації",
                        "Позитивні аффірмації підвищують самооцінку, зменшують стрес і формують позитивне мислення. Повторюйте ті твердження, які резонують з вашими цілями або викликами."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/wg3KNnq/no-phone-screen-time.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "No Phone/Screen Time",
                        "Avoiding screens in the Morning reduces stress and helps you start the day with more focus and mindfulness. Try not to check your phone for the first hour after waking up."
                    ),
                    "uk" to HabitLocalization(
                        "Без телефону/екранів",
                        "Уникнення екранів зранку знижує рівень стресу і дозволяє почати день більш усвідомлено. Намагайтесь не перевіряти телефон протягом першої години після пробудження."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/59Z8RWF/practice-a-skill.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Practice a Skill",
                        "Dedicating time to practice a skill in the Morning can lead to consistent improvement and mastery over time. Choose a skill you want to improve and spend 15-30 minutes on it each Morning."
                    ),
                    "uk" to HabitLocalization(
                        "Практика навички",
                        "Ранкова практика навички сприяє регулярному прогресу і поступовому досягненню майстерності. Оберіть навичку, яку хочете покращити, і приділіть їй 15–30 хвилин вранці."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/ch2Gjt9/clean-up.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Clean Up/Organize",
                        "A quick Morning clean-up keeps your space tidy and organized, reducing stress and increasing productivity. Focus on small tasks like tidying your workspace or organizing your to-do list."
                    ),
                    "uk" to HabitLocalization(
                        "Ранкове прибирання",
                        "Швидке прибирання зранку допомагає підтримувати чистоту і порядок, що знижує рівень стресу і підвищує продуктивність. Сфокусуйтеся на дрібницях — наприклад, робочому місці або списку справ."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/f1PMgmc/go-for-a-walk.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Go for a Walk",
                        "A Morning walk boosts mood, improves cardiovascular health, and gives you time to reflect or plan your day. Try walking in a park or natural setting to increase the benefits."
                    ),
                    "uk" to HabitLocalization(
                        "Ранкова прогулянка",
                        "Прогулянка зранку покращує настрій, серцево-судинне здоров’я та дає час на роздуми. Намагайтеся гуляти у парку або на природі для ще більшого ефекту."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/Z6QJ8bW/creative-writing-journal.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Creative Writing/Journal",
                        "Writing in the Morning can boost creativity, clarify thoughts, and improve emotional well-being. Set aside 10-15 minutes for freewriting or journaling about your thoughts or goals."
                    ),
                    "uk" to HabitLocalization(
                        "Креативне письмо / щоденник",
                        "Писання вранці підвищує креативність, прояснює думки і покращує емоційний стан. Виділіть 10–15 хвилин для вільного письма або щоденника з думками і цілями."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/W20pBTY/review-finances.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Review Finances",
                        "Regularly reviewing your finances keeps you aware of your financial health and helps in making informed decisions. Spend a few minutes reviewing your budget or tracking your expenses."
                    ),
                    "uk" to HabitLocalization(
                        "Перевірка фінансів",
                        "Регулярний перегляд фінансів допомагає залишатися обізнаним про свій фінансовий стан і приймати розумні рішення. Приділіть кілька хвилин перегляду бюджету або відстеженню витрат."
                    )
                )
            ),
            Habit(
                iconUrl = "https://i.ibb.co/ZM4ykX7/morning-breathing-exercises.png",
                timeOfDay = "Morning",
                localizations = mapOf(
                    "en" to HabitLocalization(
                        "Morning Breathing Exercises",
                        "Breathing exercises help reduce stress, improve focus, and increase oxygen flow to your brain and body. Try deep breathing or box breathing for 5 minutes each Morning."
                    ),
                    "uk" to HabitLocalization(
                        "Ранкові дихальні вправи",
                        "Дихальні вправи зранку знижують стрес, покращують концентрацію і збільшують приплив кисню до мозку та тіла. Спробуйте глибоке або квадратне дихання протягом 5 хвилин щоранку."
                    )
                )
            )
        )

        MorningHabits.forEach { habit ->
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