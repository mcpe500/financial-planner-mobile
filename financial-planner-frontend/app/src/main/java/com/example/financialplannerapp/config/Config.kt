package com.example.financialplannerapp.config

object Config {
    // API Configuration
    const val API_BASE_URL = "http://banachs.duckdns.org:8999/"

    // Auth Configuration
    const val AUTH_SCHEME = "finplanner"

    // Feature Flags
    const val ENABLE_ANALYTICS = false

    // Other configurations
    const val REQUEST_TIMEOUT = 30L // in seconds
}