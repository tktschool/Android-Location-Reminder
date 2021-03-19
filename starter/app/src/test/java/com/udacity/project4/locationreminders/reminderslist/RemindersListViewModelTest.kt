package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // provide testing to the RemindersListViewModel and its live data objects

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var reminderFakeDataSource: FakeDataSource

    @Before
    fun setupViewModel() {
        stopKoin()
        reminderFakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderFakeDataSource)
    }

    @Test
    fun loadReminders_saveReminder() = runBlockingTest  {

        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0)

        reminderFakeDataSource.deleteAllReminders()
        reminderFakeDataSource.saveReminder(reminder1)

        remindersListViewModel.loadReminders()
        val value = remindersListViewModel.remindersList.getOrAwaitValue()
        Assert.assertThat(value.size, `is`(1))
        Assert.assertThat(value[0].title, `is`(reminder1.title))
        Assert.assertThat(value[0].description, `is`(reminder1.description))
        Assert.assertThat(value[0].location, `is`(reminder1.location))

    }

    @Test
    fun loadReminders_setReturnError() = runBlockingTest  {

        reminderFakeDataSource.deleteAllReminders()
        reminderFakeDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()

        MatcherAssert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("Test exception")
        )
        MatcherAssert.assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

    }

    @Test
    fun loadReminders_showsLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }



}