
package com.example.jobify.ui.posts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jobify.data.JobsRepository
import com.example.jobify.model.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.Instant

@ExperimentalCoroutinesApi
class PostsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var repository: JobsRepository

    private lateinit var viewModel: PostsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = PostsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test publishJob success updates ui state`() = runBlockingTest {
        // TODO: Implement this test
    }

    @Test
    fun `test publishJob failure shows error`() = runBlockingTest {
        // TODO: Implement this test
    }

    private fun getFakeJob(id: String, published: Boolean) = Job(
        id = id,
        title = "Test Job",
        company = "Test Company",
        companyLogoUrl = null,
        location = "Test Location",
        jobType = "Full-time",
        shortDescription = "Test description",
        experience = "1 year",
        salaryRange = "$100",
        applicantsCount = 0,
        skills = emptyList(),
        badge = null,
        published = published,
        postedAt = Instant.now()
    )
}
