package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.example.erjohnandroid.database.Model.LinesTable

@Dao
interface LineSegmentDao {

    @Query("Select * from LineSegment where lineId = :id ")
    fun getAllLinesegment(id:Int):List<LineSegmentTable>

    @Insert
    fun insertAllLinesegment(entity:List<LineSegmentTable>)

    @Query("DELETE FROM LineSegment")
    fun truncateLineSegment()
}