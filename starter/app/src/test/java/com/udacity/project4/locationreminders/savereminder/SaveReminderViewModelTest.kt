package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects

    //Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveRemindersViewModel: SaveReminderViewModel
    private lateinit var reminderFakeDataSource: FakeDataSource

    @Before
    fun setupViewModel() {
        stopKoin()
        reminderFakeDataSource = FakeDataSource()
        saveRemindersViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), reminderFakeDataSource)
    }

    @Test
    fun validateEnteredData_success() = runBlockingTest {

        val reminder1 = ReminderDataItem("title1", "description1", "location1", 1.0, 1.0)
        val result = saveRemindersViewModel.validateEnteredData(reminder1)
        MatcherAssert.assertThat(result, `is`(true))
    }

    @Test
    fun validateEnteredData_setNull() = runBlockingTest {
        reminderFakeDataSource.deleteAllReminders()
        val reminder1 = ReminderDataItem(null, null, "location1", 1.0, 1.0)
        val reminder2 = ReminderDataItem(null, "description1", "location1", 1.0, 1.0)
        val reminder3 = ReminderDataItem("title1", "description1", null, 1.0, 1.0)
        val result1 = saveRemindersViewModel.validateEnteredData(reminder1)
        val result2 = saveRemindersViewModel.validateEnteredData(reminder2)
        val result3 = saveRemindersViewModel.validateEnteredData(reminder3)
        MatcherAssert.assertThat(result1, `is`(false))
        MatcherAssert.assertThat(result2, `is`(false))
        MatcherAssert.assertThat(result3, `is`(false))
    }

    @Test
    fun saveReminder_showsToast() = runBlockingTest {
        val reminder1 = ReminderDataItem("title1", "description1", "location1", 1.0, 1.0)
        saveRemindersViewModel.saveReminder(reminder1)
        MatcherAssert.assertThat(
            saveRemindersViewModel.showToast.getOrAwaitValue(),
            CoreMatchers.`is`( ApplicationProvider.getApplicationContext<Context>()
                .getString(R.string.reminder_saved))
        )
    }

    @Test
    fun saveReminder_showLoading() = runBlockingTest {
        val reminder1 = ReminderDataItem("title1", "description1", "location1", 1.0, 1.0)
        mainCoroutineRule.pauseDispatcher()
        saveRemindersViewModel.saveReminder(reminder1)
        MatcherAssert.assertThat(
            saveRemindersViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            saveRemindersViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

}