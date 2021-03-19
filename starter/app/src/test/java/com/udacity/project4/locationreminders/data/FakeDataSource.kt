package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

//  Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        reminders?.let {
            return Result.Success(ArrayList(it))
        }
        return Result.Error("Reminder not found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        if (shouldReturnError) {
            return Result.Error("Test exception")
        }

        val reminder = reminders?.firstOrNull() {
            it.id == id
        }
        return if(null != reminder){
            Result.Success(reminder)
        } else {
            Result.Error("Reminder not found")
        }

    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}