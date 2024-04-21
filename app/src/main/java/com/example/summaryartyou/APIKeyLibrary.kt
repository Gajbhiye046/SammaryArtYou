package com.example.summaryartyou

class APIKeyLibrary {
    companion object {
        init {
            System.loadLibrary("api-keys")
        }
        external fun getAPIKey(): String
    }
}