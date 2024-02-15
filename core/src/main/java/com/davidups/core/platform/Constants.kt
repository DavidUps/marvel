package com.davidups.core.platform

object Constants {
    object Error {
        const val SERVER_ERROR = "server error"
    }

    object Core {
        const val SHARED_PREFS_NAME = "marvel-shared-prefs"

        object Request {
            const val TS = "ts"
            const val API_KEY = "apikey"
            const val HASH = "hash"
        }
    }

    object Characters {
        const val LIMIT = 10
        const val DATABASE_NAME = "characters-database"
    }
}
