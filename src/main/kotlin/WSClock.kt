import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class Page(
    val id: Int,
    var referenced: Boolean = Random.nextBoolean(),
    var modified: Boolean = Random.nextBoolean(),
    var timeOfLastUse: Int = 1700 + Random.nextInt(300),
    var selected: Boolean = false
)

class WSClock(
    var pageSize: Int,
    val startCurrentTime: Int,
    val time: Int
) {
    val currentTime = MutableStateFlow(startCurrentTime)
    var isTimeGo = false
    private val _pages = MutableStateFlow(listOf<Page>())
    val pages = _pages.asStateFlow()
    var handled = 0

    init {
        initPages()
    }

    fun initPages(){
        for (i in 0 until pageSize) {
            _pages.update { it ->
                val list = it.toMutableList()
                list.add(Page(if (list.isNotEmpty()) list.last().id+1 else i))
                list.toList()
            }
        }
    }

    suspend fun changePagesSize(newSize: Int){
        if (newSize >= pageSize){
            pageSize = newSize
            initPages()
        } else{
            println(newSize)
            pageSize = newSize
            val pages = _pages.value.subList(0, newSize)
            _pages.emit(pages)
        }

    }

    suspend fun replacePage() {
        val pagesList = _pages.value.toMutableList()
        handlePages(handled, pageSize)
    }

    private suspend fun findEmptyPage(){
        val pagesList = _pages.value.toMutableList()
        var min = 1000000
        var page = Page(-1)
        var minId = -1
        pagesList.forEach {
            if (it.timeOfLastUse < min && !it.referenced && !it.modified){
                page = it
                min = it.timeOfLastUse
                minId = it.id
            }
        }
        if (minId == -1 && min == 1000000){
            return
        }

        pagesList[minId] = Page(page.id, referenced = true, timeOfLastUse = currentTime.value, selected = true)
        page = pagesList[minId]
        handled = (minId+1) % pageSize
        _pages.emit(pagesList.toList())
        if (isTimeGo){
            currentTime.update { it + 100 }
            pagesList.forEach {
                if (it.modified){
                    it.modified = Random.nextBoolean()
                }
            }
        }
        delay(1500)
        pagesList[minId] = Page(page.id, page.referenced, page.modified, timeOfLastUse = page.timeOfLastUse, selected = false)
        _pages.emit(pagesList.toList())
    }

    suspend fun handlePages(startHandled: Int, size: Int){

        for (index in startHandled until  size+startHandled){
            val i = index % pageSize
            val pagesList = _pages.value.toMutableList()
            val page = pagesList[i]
            _pages.value = pagesList
            pagesList.forEach {
                it.selected = false
            }
            pagesList[i] = Page(page.id, referenced = page.referenced, modified = page.modified, timeOfLastUse = page.timeOfLastUse, selected = true)
            _pages.emit(pagesList.toList())
            delay(1000)
            if ((!page.referenced && currentTime.value-page.timeOfLastUse>time) and (!page.modified)){
                pagesList[i] = Page(page.id, referenced = true, timeOfLastUse = currentTime.value, selected = true)

                _pages.emit(pagesList.toList())

                delay(1500)
                handled = (i+1) % pageSize
                val lPage = _pages.value[i]
                pagesList[i] = Page(lPage.id, lPage.referenced, lPage.modified, timeOfLastUse = lPage.timeOfLastUse, selected = false)
                if (isTimeGo){
                    currentTime.update { it + 100 }
                    pagesList.forEach {
                        if (it.modified){
                            it.modified = Random.nextBoolean()
                        }
                    }
                }
                _pages.emit(pagesList.toList())
                return
            } else {
                pagesList[i] = Page(page.id, false, page.modified, timeOfLastUse = page.timeOfLastUse, selected = true)
                _pages.emit(pagesList.toList())
            }
            delay(1500)
            if (i == (startHandled+size-1)%pageSize){
                val lPage = _pages.value[i]
                pagesList[i] = Page(lPage.id, lPage.referenced, lPage.modified, timeOfLastUse = lPage.timeOfLastUse, selected = false)
                _pages.emit(pagesList.toList())
                findEmptyPage()
            }
        }
    }
}