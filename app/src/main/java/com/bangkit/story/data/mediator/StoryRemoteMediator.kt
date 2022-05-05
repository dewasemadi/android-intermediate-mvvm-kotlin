package com.bangkit.story.data.mediator

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bangkit.story.data.local.entity.RemoteKeys
import com.bangkit.story.data.local.preferences.SessionManager
import com.bangkit.story.data.local.room.StoryDatabase
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.data.remote.retrofit.ApiService
import com.bangkit.story.utils.wrapEspressoIdlingResource
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val storyDatabase: StoryDatabase, private val apiService: ApiService, context: Context) : RemoteMediator<Int, Story>() {

    private val sessionManager = SessionManager(context)

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            try {
                val location = if (sessionManager.getIsWithLocation()) 1 else 0
                val data = apiService.getAllStories(page, state.config.pageSize, location).listStory
                val endOfPaginationReached = data?.isEmpty()

                storyDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        storyDatabase.remoteKeysDao().deleteRemoteKeys()
                        storyDatabase.storyDao().deleteAll()
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached == true) null else page + 1
                    val keys = data?.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    if (keys != null) {
                        storyDatabase.remoteKeysDao().insertAll(keys)
                        storyDatabase.storyDao().insertStory(data)
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached ?: false)
            } catch (exception: Exception) {
                return when (exception) {
                    is HttpException -> MediatorResult.Error(exception)
                    else -> MediatorResult.Error(exception)
                }
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}