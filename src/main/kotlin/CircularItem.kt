import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun circularItem(
    pagesList: List<Page>,
    index: Int,
    height: Dp = 60.dp,
    width: Dp = 90.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var page: Page
    try {
        page = pagesList[index]
    } catch (e: Exception){
        page = Page(0)
    }
    val lastTime = page.timeOfLastUse
    val referenced = page.referenced
    val modified = page.modified
    val selected = page.selected
    val infiniteTransition = rememberInfiniteTransition()

    val halfHeight = height/2

    Column(
        modifier = modifier
            .height(height)
            .width(width)
            .dashedBorder(Color.Black, 3.dp, 10.dp, selected)
    ) {
        Row (
            modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(halfHeight)
                .padding(horizontal = 5.dp)
                .align(Alignment.CenterHorizontally)
                .wrapContentHeight(align = Alignment.CenterVertically),
        ){
            Text("№ ")
            AnimatedCounter(page.id+1)
            Spacer(Modifier.width(2.dp))
            AnimatedCounter(lastTime)
        }
/*        Text(
            text = "$lastTime",
            modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(35.dp)
                .wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )*/
        Row(modifier = Modifier.border(1.dp, color = Color.Black)) {
            Text(text = "R=", modifier = Modifier.weight(0.5f).height(halfHeight)
                .wrapContentHeight(align = Alignment.CenterVertically),
                textAlign = TextAlign.Center)

            AnimatedCounter(count = if (referenced) 1 else 0, Modifier.weight(0.5f).height(halfHeight)
                .wrapContentHeight(align = Alignment.CenterVertically))
            Text(text = "M=", modifier = Modifier.weight(0.5f).height(halfHeight)
                .wrapContentHeight(align = Alignment.CenterVertically),
                textAlign = TextAlign.Center)
            AnimatedCounter(count = if (modified) 1 else 0, Modifier.weight(0.5f).height(halfHeight)
                .wrapContentHeight(align = Alignment.CenterVertically).padding(2.dp))
        }
    }
}