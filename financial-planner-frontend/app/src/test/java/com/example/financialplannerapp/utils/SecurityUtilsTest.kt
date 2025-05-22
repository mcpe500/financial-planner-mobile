package com.example.financialplannerapp.utils

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SecurityUtilsTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPrefs: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    // Extracted constants from SecurityUtils for verification (should match actual values)
    private val PREFS_SECURITY = "prefs_security"
    private val KEY_PIN_ENABLED = "key_pin_enabled"
    private val KEY_ENCRYPTED_PIN_HASH = "key_encrypted_pin_hash"
    private val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
    private val SHARED_PREF_KEY_IV = "pin_encryption_iv"
    private val KEY_LAST_AUTH_TIMESTAMP = "key_last_auth_timestamp"


    @Before
    fun setUp() {
        // Standard mocking for SharedPreferences
        Mockito.`when`(mockContext.getSharedPreferences(PREFS_SECURITY, Context.MODE_PRIVATE))
            .thenReturn(mockPrefs)
        Mockito.`when`(mockPrefs.edit()).thenReturn(mockEditor)

        // Mock editor methods to return the editor itself for chained calls like .putString().apply()
        Mockito.`when`(mockEditor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.remove(Mockito.anyString())).thenReturn(mockEditor)
        // No need to mock apply() specifically, Mockito handles void methods unless strict stubbing is used.
    }

    @Test
    fun testSetPinLockEnabled_True_SavesAndRetrievesCorrectly() {
        // Arrange: Mock SharedPreferences.getBoolean to return true after saving
        Mockito.`when`(mockPrefs.getBoolean(KEY_PIN_ENABLED, false)).thenReturn(true)

        // Act
        SecurityUtils.setPinLockEnabled(mockContext, true)

        // Assert: Verify SharedPreferences.Editor.putBoolean was called correctly
        Mockito.verify(mockEditor).putBoolean(KEY_PIN_ENABLED, true)
        Mockito.verify(mockEditor).apply() // Verify that changes are committed

        // Act: Retrieve the value
        val result = SecurityUtils.isPinLockEnabled(mockContext)

        // Assert: Verify the retrieved value
        assertTrue(result)
        Mockito.verify(mockPrefs).getBoolean(KEY_PIN_ENABLED, false) // Verify it was read
    }

    @Test
    fun testSetPinLockEnabled_False_SavesAndClearsPin() {
        // Arrange: Mock SharedPreferences.getBoolean to return false after saving
        Mockito.`when`(mockPrefs.getBoolean(KEY_PIN_ENABLED, false)).thenReturn(false)
        // For saveEncryptedPinHash(context, null) which is called internally
        Mockito.`when`(mockContext.getSharedPreferences(PREFS_SECURITY, Context.MODE_PRIVATE)).thenReturn(mockPrefs)


        // Act
        SecurityUtils.setPinLockEnabled(mockContext, false)

        // Assert: Verify SharedPreferences.Editor.putBoolean was called correctly
        Mockito.verify(mockEditor).putBoolean(KEY_PIN_ENABLED, false)
        // Verify that saveEncryptedPinHash(context, null) resulted in these calls
        Mockito.verify(mockEditor).remove(KEY_ENCRYPTED_PIN_HASH)
        Mockito.verify(mockEditor).remove(SHARED_PREF_KEY_IV)
        Mockito.verify(mockEditor, Mockito.atLeastOnce()).apply() // apply is called for setBoolean and for removes

        // Act: Retrieve the value
        val result = SecurityUtils.isPinLockEnabled(mockContext)

        // Assert: Verify the retrieved value
        assertFalse(result)
        Mockito.verify(mockPrefs, Mockito.times(2)).getBoolean(KEY_PIN_ENABLED, false) // Called once for the result, once for the setPinLockEnabled internal check if needed
    }

    @Test
    fun testBiometricAuthEnabled_SavesAndRetrievesCorrectly() {
        // Arrange: Mock SharedPreferences.getBoolean to return true after saving
        Mockito.`when`(mockPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)).thenReturn(true)

        // Act
        SecurityUtils.setBiometricAuthEnabled(mockContext, true)

        // Assert: Verify SharedPreferences.Editor.putBoolean was called correctly
        Mockito.verify(mockEditor).putBoolean(KEY_BIOMETRIC_ENABLED, true)
        Mockito.verify(mockEditor).apply()

        // Act: Retrieve the value
        val result = SecurityUtils.isBiometricAuthEnabled(mockContext)

        // Assert: Verify the retrieved value
        assertTrue(result)
        Mockito.verify(mockPrefs).getBoolean(KEY_BIOMETRIC_ENABLED, false)


        // Test with false
        Mockito.`when`(mockPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)).thenReturn(false)
        SecurityUtils.setBiometricAuthEnabled(mockContext, false)
        Mockito.verify(mockEditor).putBoolean(KEY_BIOMETRIC_ENABLED, false)
        Mockito.verify(mockEditor, Mockito.times(2)).apply() // Called once for true, once for false
        val resultFalse = SecurityUtils.isBiometricAuthEnabled(mockContext)
        assertFalse(resultFalse)
        Mockito.verify(mockPrefs, Mockito.times(2)).getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    @Test
    fun testUpdateAndGetLastAuthTimestamp() {
        val testTimestamp = System.currentTimeMillis() // Use a fixed time for predictability if System.currentTimeMillis() was mockable

        // Arrange: Mock SharedPreferences.getLong to return the testTimestamp after saving
        // We can't easily mock System.currentTimeMillis() without PowerMock, so we'll check for a non-zero value
        // and that putLong was called. For getLong, we will make it return a specific value.
        Mockito.`when`(mockPrefs.getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)).thenReturn(testTimestamp)


        // Act
        SecurityUtils.updateLastAuthTimestamp(mockContext)

        // Assert: Verify SharedPreferences.Editor.putLong was called
        // We expect it to be called with System.currentTimeMillis(). We can't verify the exact value without PowerMock.
        // So we verify it was called with *any* long for the correct key.
        Mockito.verify(mockEditor).putLong(Mockito.eq(KEY_LAST_AUTH_TIMESTAMP), Mockito.anyLong())
        Mockito.verify(mockEditor).apply()

        // Act: Retrieve the value
        val result = SecurityUtils.getLastAuthTimestamp(mockContext)

        // Assert: Verify the retrieved value
        assertEquals(testTimestamp, result)
        Mockito.verify(mockPrefs).getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)
    }

    @Test
    fun testIsAuthRequired_PinDisabled_ReturnsFalse() {
        // Arrange: Mock isPinLockEnabled to return false
        Mockito.`when`(mockPrefs.getBoolean(KEY_PIN_ENABLED, false)).thenReturn(false)

        // Act
        val result = SecurityUtils.isAuthRequired(mockContext, 30000L)

        // Assert
        assertFalse(result)
        // Verify isPinLockEnabled was checked
        Mockito.verify(mockPrefs).getBoolean(KEY_PIN_ENABLED, false)
    }

    @Test
    fun testIsAuthRequired_FirstTime_ReturnsTrue() {
        // Arrange: Mock isPinLockEnabled to return true and getLastAuthTimestamp to return 0L
        Mockito.`when`(mockPrefs.getBoolean(KEY_PIN_ENABLED, false)).thenReturn(true)
        Mockito.`when`(mockPrefs.getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)).thenReturn(0L)

        // Act
        val result = SecurityUtils.isAuthRequired(mockContext, 30000L)

        // Assert
        assertTrue(result)
        Mockito.verify(mockPrefs, Mockito.times(2)).getBoolean(KEY_PIN_ENABLED, false) // Once by isAuthRequired, once by isPinLockEnabled
        Mockito.verify(mockPrefs, Mockito.times(2)).getLong(KEY_LAST_AUTH_TIMESTAMP, 0L) // Once by isAuthRequired, once by getLastAuthTimestamp
    }

    @Test
    fun testIsAuthRequired_WithinTimeout_ReturnsFalse() {
        val currentTime = System.currentTimeMillis()
        val recentAuthTime = currentTime - 10000L // 10 seconds ago
        val timeoutMillis = 30000L // 30 seconds

        // Arrange: Mock isPinLockEnabled true, and getLastAuthTimestamp returns a recent time
        Mockito.`when`(mockPrefs.getBoolean(KEY_PIN_ENABLED, false)).thenReturn(true)
        Mockito.`when`(mockPrefs.getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)).thenReturn(recentAuthTime)

        // Act
        val result = SecurityUtils.isAuthRequired(mockContext, timeoutMillis)

        // Assert
        assertFalse(result)
        Mockito.verify(mockPrefs, Mockito.times(3)).getBoolean(KEY_PIN_ENABLED, false)
        Mockito.verify(mockPrefs, Mockito.times(3)).getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)
    }

    @Test
    fun testIsAuthRequired_AfterTimeout_ReturnsTrue() {
        val currentTime = System.currentTimeMillis()
        val oldAuthTime = currentTime - 60000L // 60 seconds ago
        val timeoutMillis = 30000L // 30 seconds

        // Arrange: Mock isPinLockEnabled true, and getLastAuthTimestamp returns an old time
        Mockito.`when`(mockPrefs.getBoolean(KEY_PIN_ENABLED, false)).thenReturn(true)
        Mockito.`when`(mockPrefs.getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)).thenReturn(oldAuthTime)

        // Act
        val result = SecurityUtils.isAuthRequired(mockContext, timeoutMillis)

        // Assert
        assertTrue(result)
        Mockito.verify(mockPrefs, Mockito.times(4)).getBoolean(KEY_PIN_ENABLED, false)
        Mockito.verify(mockPrefs, Mockito.times(4)).getLong(KEY_LAST_AUTH_TIMESTAMP, 0L)
    }
}
