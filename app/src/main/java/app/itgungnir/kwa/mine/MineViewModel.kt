package app.itgungnir.kwa.mine

import android.annotation.SuppressLint
import app.itgungnir.kwa.common.http.HttpClient
import app.itgungnir.kwa.common.http.handleResult
import app.itgungnir.kwa.common.http.io2Main
import app.itgungnir.kwa.common.redux.AppRedux
import my.itgungnir.rxmvvm.core.mvvm.BaseViewModel

@SuppressLint("CheckResult")
class MineViewModel : BaseViewModel<MineState>(initialState = MineState()) {

    private var pageNo = 0

    /**
     * 获取数据
     */
    fun getMineData() {

        pageNo = 0

        HttpClient.api.mineCollections(pageNo)
            .handleResult()
            .io2Main()
            .map {
                setState {
                    copy(
                        hasMore = !it.over
                    )
                }
                it.datas.map { item ->
                    MineState.MineArticleVO(
                        author = item.author,
                        category = item.chapterName,
                        categoryId = item.chapterId,
                        title = item.title,
                        date = item.niceDate,
                        link = item.link
                    )
                }
            }.doOnSubscribe {
                setState {
                    copy(
                        refreshing = true,
                        error = null
                    )
                }
            }
            .subscribe({
                pageNo++
                setState {
                    copy(
                        refreshing = false,
                        items = it,
                        error = null
                    )
                }
            }, {
                setState {
                    copy(
                        refreshing = false,
                        error = if (AppRedux.instance.isUserIn()) it else null
                    )
                }
            })
    }

    /**
     * 加载更多数据
     */
    fun loadMoreMineData() {
        HttpClient.api.mineCollections(pageNo)
            .handleResult()
            .io2Main()
            .map {
                setState {
                    copy(
                        hasMore = !it.over
                    )
                }
                it.datas.map { item ->
                    MineState.MineArticleVO(
                        author = item.author,
                        category = item.chapterName,
                        categoryId = item.chapterId,
                        title = item.title,
                        date = item.niceDate,
                        link = item.link
                    )
                }
            }.doOnSubscribe {
                setState {
                    copy(
                        loading = true,
                        error = null
                    )
                }
            }
            .subscribe({
                pageNo++
                setState {
                    copy(
                        loading = false,
                        items = items.toMutableList() + it,
                        error = null
                    )
                }
            }, {
                setState {
                    copy(
                        loading = false,
                        error = it
                    )
                }
            })
    }
}