package com.example.financialplannerapp.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.model.ReceiptOCRRequest
import com.example.financialplannerapp.data.model.ReceiptOCRResponse
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Service for handling receipt image processing and OCR functionality
 */
class ReceiptService(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val context: Context
) {
    
    companion object {
        private const val TAG = "ReceiptService"
        private const val MAX_IMAGE_SIZE = 1024 * 1024 // 1MB
        private const val COMPRESSION_QUALITY = 80
    }

    /**
     * Process receipt image and extract transaction data via OCR
     */
    suspend fun processReceiptOCR(imageUri: Uri): Result<ReceiptOCRResponse> = withContext(Dispatchers.IO) {
        try {
            val userId = tokenManager.getUserId() ?: return@withContext Result.failure(
                Exception("User ID not available")
            )
            
            Log.d(TAG, "Starting receipt processing for user: $userId")
            
            // Validate image URI
            if (!isValidImageUri(imageUri)) {
                return@withContext Result.failure(Exception("Invalid image URI"))
            }
            
            // Convert image to base64
            val base64Image = convertImageToBase64(imageUri)
            if (base64Image == null) {
                return@withContext Result.failure(Exception("Failed to process image"))
            }
            
            Log.d(TAG, "Image converted to base64, size: ${base64Image.length} characters")
              // Create request
            val request = ReceiptOCRRequest(
                imageBase64 = base64Image,
                userId = userId
            )
            
            // Get auth token
            val authToken = tokenManager.getToken() ?: return@withContext Result.failure(
                Exception("Authentication token not available")
            )
            
            // Make API call
            Log.d(TAG, "Sending OCR request to backend")
            val response = apiService.processReceiptOCR(
                authHeader = "Bearer $authToken",
                request = request
            )
            
            if (response.isSuccessful && response.body() != null) {
                val ocrResponse = response.body()!!
                Log.d(TAG, "OCR processing successful: ${ocrResponse.success}")
                Log.d(TAG, "Extracted data - Amount: ${ocrResponse.data.totalAmount}, Merchant: ${ocrResponse.data.merchantName}")
                
                Result.success(ocrResponse)
            } else {
                val errorMessage = "OCR processing failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                Result.failure(Exception(errorMessage))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing receipt image", e)
            Result.failure(e)
        }
    }

    /**
     * Convert image URI to base64 string for API transmission
     */
    private suspend fun convertImageToBase64(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Converting image URI to base64: $imageUri")
            
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream for URI: $imageUri")
                return@withContext null
            }
            
            // Decode and compress image
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from URI")
                return@withContext null
            }
            
            Log.d(TAG, "Original image size: ${originalBitmap.width}x${originalBitmap.height}")
            
            // Resize if too large
            val resizedBitmap = resizeImageIfNeeded(originalBitmap)
            
            // Convert to base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            
            // Clean up
            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            originalBitmap.recycle()
            byteArrayOutputStream.close()
            
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d(TAG, "Image converted to base64, final size: ${byteArray.size} bytes")
            
            return@withContext base64String
            
        } catch (e: Exception) {
            Log.e(TAG, "Error converting image to base64", e)
            return@withContext null
        }
    }

    /**
     * Resize image if it's too large to optimize API transmission
     */
    private fun resizeImageIfNeeded(originalBitmap: Bitmap): Bitmap {
        val originalSize = originalBitmap.width * originalBitmap.height * 4 // Approximate size in bytes
        
        if (originalSize <= MAX_IMAGE_SIZE) {
            Log.d(TAG, "Image size is acceptable, no resizing needed")
            return originalBitmap
        }
        
        // Calculate scale factor to reduce size
        val scale = kotlin.math.sqrt(MAX_IMAGE_SIZE.toDouble() / originalSize)
        val newWidth = (originalBitmap.width * scale).toInt()
        val newHeight = (originalBitmap.height * scale).toInt()
        
        Log.d(TAG, "Resizing image from ${originalBitmap.width}x${originalBitmap.height} to ${newWidth}x${newHeight}")
        
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    }

    /**
     * Validate image before processing
     */
    fun isValidImageUri(imageUri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val isValid = inputStream != null
            inputStream?.close()
            Log.d(TAG, "Image URI validation result: $isValid for URI: $imageUri")
            isValid
        } catch (e: Exception) {
            Log.e(TAG, "Error validating image URI", e)
            false
        }
    }
}
