package com.example.riddles.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User::class, Riddle::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun riddleDao(): RiddleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "riddles_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val DatabaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL(
                    """
                    INSERT INTO users
                    (name, password, bestScore, totalGames, wins, losses)
                    VALUES (?, ?, 0, 0, 0, 0)
                    """.trimIndent(),
                    arrayOf("Игрок", "1234")
                )

                seedRiddles().forEach { riddle ->
                    db.execSQL(
                        """
                        INSERT INTO riddles
                        (level, question, option1, option2, option3, option4, correctIndex)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent(),
                        arrayOf(
                            riddle.level,
                            riddle.question,
                            riddle.option1,
                            riddle.option2,
                            riddle.option3,
                            riddle.option4,
                            riddle.correctIndex
                        )
                    )
                }
            }
        }

        private fun seedRiddles(): List<Riddle> = listOf(
            // Лёгкий уровень
            riddle("EASY", "В огороде вырастает, красным боком привлекает. В салат её кладут.", "Помидор", "Огурец", "Морковь", "Свёкла"),
            riddle("EASY", "Зелёный, длинный и хрустящий, в салате самый настоящий.", "Огурец", "Томат", "Картофель", "Лук"),
            riddle("EASY", "Сидит девица в темнице, а коса на улице.", "Морковь", "Репа", "Капуста", "Тыква"),
            riddle("EASY", "Сто одёжек и все без застёжек.", "Капуста", "Картофель", "Огурец", "Перец"),
            riddle("EASY", "Под землёй растёт клубок — в борщ отправится на срок.", "Свёкла", "Редис", "Морковь", "Лук"),
            riddle("EASY", "В земле лежит, а к осени вся семья её копит.", "Картофель", "Помидор", "Горох", "Огурец"),
            riddle("EASY", "Летом серый, зимой белый, длинноухий и умелый.", "Заяц", "Медведь", "Лиса", "Волк"),
            riddle("EASY", "По веткам скачет, орешки прячет.", "Белка", "Ёж", "Лось", "Бобр"),
            riddle("EASY", "Без рук, без ног, а ворота открывает.", "Ветер", "Дождь", "Снег", "Гром"),
            riddle("EASY", "Зимой и летом одним цветом.", "Ёлка", "Берёза", "Рябина", "Осина"),

            // Средний уровень
            riddle("MEDIUM", "Не огонь, а жжётся; не фонарь, а светит красным боком.", "Перец", "Кабачок", "Горох", "Репа"),
            riddle("MEDIUM", "Круглая, румяная, на грядке созревает; в супе и салате всех угощает.", "Томат", "Редис", "Чеснок", "Баклажан"),
            riddle("MEDIUM", "В земле сидит, зелёные перья над землёй растит.", "Лук", "Капуста", "Тыква", "Свёкла"),
            riddle("MEDIUM", "Жёлтая карета по грядке катится, семечками полна.", "Тыква", "Морковь", "Репа", "Картофель"),
            riddle("MEDIUM", "Зелёные стручки, внутри сидят дружки.", "Горох", "Огурец", "Фасоль", "Перец"),
            riddle("MEDIUM", "На ножке стоит, шляпку надел, после дождя в лесу поспел.", "Гриб", "Цветок", "Жёлудь", "Камень"),
            riddle("MEDIUM", "Весной зеленеет, осенью желтеет, а зимой без листьев стоит.", "Берёза", "Ель", "Сосна", "Можжевельник"),
            riddle("MEDIUM", "Летом в речке плавает, зимой в берлоге отдыхает.", "Бобр", "Ёж", "Медведь", "Заяц"),
            riddle("MEDIUM", "Над цветком порхает, сладкий нектар собирает.", "Бабочка", "Пчела", "Стрекоза", "Муравей"),
            riddle("MEDIUM", "С неба капает, по крыше стучит, землю и траву напоить спешит.", "Дождь", "Туман", "Снег", "Ветер"),

            // Сложный уровень
            riddle("HARD", "Сверху зелёная коса, в земле спрятана краса. Сладкая и длинная.", "Морковь", "Редис", "Свёкла", "Репа"),
            riddle("HARD", "Не лук, а с ним плачут; зубчиков много, а сам один.", "Чеснок", "Картофель", "Помидор", "Огурец"),
            riddle("HARD", "Круглый, жёлтый, не арбуз, но с грядки в кашу я гожусь.", "Кабачок", "Горох", "Лук", "Редис"),
            riddle("HARD", "Под снегом зимует, весной расцветает, жёлтыми цветами лес украшает.", "Мать-и-мачеха", "Ромашка", "Василёк", "Колокольчик"),
            riddle("HARD", "Красная шубка, белый хвостик, на грядке растёт совсем не гости.", "Редис", "Морковь", "Томат", "Перец"),
            riddle("HARD", "На спине иголки, но не шьёт рубашки; свернётся клубком — и нету мордашки.", "Ёж", "Дикобраз", "Белка", "Крот"),
            riddle("HARD", "Кто на ёлке вниз головой по стволу бежит за корой?", "Поползень", "Дятел", "Синица", "Сорока"),
            riddle("HARD", "Ночью летает, днём отдыхает, мышей в поле замечает.", "Сова", "Ворона", "Ласточка", "Голубь"),
            riddle("HARD", "Без кисти и красок мост разноцветный после дождя рисует.", "Радуга", "Молния", "Облако", "Заря"),
            riddle("HARD", "Зимой падает, весной тает, землю влагой наполняет.", "Снег", "Град", "Роса", "Туман")
        )

        private fun riddle(
            level: String,
            question: String,
            correctAnswer: String,
            option2: String,
            option3: String,
            option4: String
        ) = Riddle(
            level = level,
            question = question,
            option1 = correctAnswer,
            option2 = option2,
            option3 = option3,
            option4 = option4,
            correctIndex = 0
        )
    }
}