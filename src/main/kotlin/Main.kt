import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    val composableScope = rememberCoroutineScope()
    val newComposableScope = rememberCoroutineScope()
    var pagesQuantity by remember { mutableStateOf("6") }
    val time = 100
    var pageSize = 6
    var checked by remember { mutableStateOf(false) }

    val wsclock = WSClock(pageSize, 2023, time)

    MaterialTheme {

        // A surface container using the 'background' color from the theme
        Row(verticalAlignment = Alignment.CenterVertically) {
            val pages = wsclock.pages.collectAsState()
            val currentTime = wsclock.currentTime.collectAsState()


            BoxWithConstraints(

            ) {
                CircularLayout( this.maxHeight/2.19f) {
                    for (i in 0 until pageSize){
                        circularItem(pages.value, i){}
                    }

                }
            }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Text(
                    "Текущий элемент: "
                )
                Spacer(Modifier.height(5.dp))
                circularItem(pages.value, pages.value.firstOrNull {
                    it.selected
                }?.id ?: (if (wsclock.handled-1>=0){
                    wsclock.handled-1
                }else{
                    0
                })){}
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "Время работы с страницей: ${time}"
                )
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "Текущее время: ${currentTime.value}"
                )
                Spacer(Modifier.height(5.dp))
                BeautyButton(onClick = {
                    composableScope.launch {
                        wsclock.replacePage()
                    }

                },
                    modifier = Modifier.width(200.dp)){
                    Text("Добавить страницу")
                }
                Spacer(Modifier.height(10.dp))
                BeautyButton(
                    onClick = {
                        newComposableScope.launch{
                            pageSize = 6
                            wsclock.changePagesSize(0)
                            wsclock.currentTime.emit(2023)
                            wsclock.changePagesSize(6)
                            wsclock.handled = 6
                        }
                    },
                    modifier = Modifier.width(200.dp)
                ){
                    Text("Перезагрузить процессы")
                }
                Spacer(Modifier.height(10.dp))
                TextField(label = { Text("Количество страниц") }, value = pagesQuantity, onValueChange = {
                    pagesQuantity = it
                }, modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xFFE773),
                        focusedIndicatorColor =  Color.Transparent, //hide the indicator
                    )
                )
                Spacer(Modifier.height(10.dp))
                BeautyButton(
                    onClick = {
                        newComposableScope.launch{
                            try {
                                wsclock.changePagesSize(pagesQuantity.toInt())
                                pageSize = pagesQuantity.toInt()
                            }catch (e: Exception){
                                wsclock.changePagesSize(6)
                                wsclock.handled = 0
                                pageSize = 6
                                pagesQuantity = "6"
                            }
                        }
                    },
                    modifier = Modifier.width(200.dp)
                ){
                    Text("Изменить количество страниц")
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.offset(10.dp)) {
                    Text("Изменять текущее время после добавления страницы", modifier = Modifier.width(180.dp))

                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            wsclock.isTimeGo = it
                        }
                    )
                }
            }


        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication,
        WindowState(width = 900.dp,)
        ) {
        App()
    }
}

@Composable
fun BeautyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
){
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
        backgroundColor =  Color(0xFFFF9500),
        contentColor = Color.White
    ), onClick = onClick, content = content)
}

