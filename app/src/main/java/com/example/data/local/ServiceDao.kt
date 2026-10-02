package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY name ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE category = :category AND isActive = 1")
    fun getServicesByCategory(category: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isPopular = 1 AND isActive = 1")
    fun getPopularServices(): Flow<List<ServiceEntity>>

    @Query("""
        SELECT * FROM services 
        WHERE isActive = 1 AND (
            name LIKE '%' || :query || '%' 
            OR category LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
        )
    """)
    fun searchServices(query: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getServicesCount(): Int
}
