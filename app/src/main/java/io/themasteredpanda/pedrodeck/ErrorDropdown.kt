package io.themasteredpanda.pedrodeck

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@Composable
fun ErrorDropdown(viewModel: AppViewModel) {
    if (viewModel.peekAtError() == null) {
        return
    }

    val localDensity = LocalDensity.current
    var visible by remember { mutableStateOf(true) }
    val error = viewModel.peekAtError()!!
    MainActivity.logger("Composing ErrorDropdown.")

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            with(localDensity) { -40.dp.roundToPx() }
        } + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = .3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
    ) {
        MainActivity.logger("In animated visibility.")
        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .padding(top = 25.dp, start = 15.dp, end = 15.dp, bottom = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {

                    Text(text = error.title, fontSize = 20.sp)
                    Spacer(modifier = Modifier.padding(3.dp))
                    Text(text = error.message, fontSize = 15.sp)
                }

                Button(
                    onClick = {
                        MainActivity.logger("Dismissed button clicked. Removing error from queue and changing visibility.")
                        visible = false
                        viewModel.removeFirstError()
                    },
                    content = { Text(text = "Dismiss") },
                    modifier =
                    Modifier
                        .fillMaxWidth
                            (),
                    shape = RoundedCornerShape(0.dp)
                )
            }
        }
    }

    MainActivity.logger("Attempting to LaunchEffect. Error Title: ${error.title} / Error Description: ${error.message}")
    LaunchedEffect(error.title) {
        MainActivity.logger("LaunchEffect ErrorDropdown. Duration ${error.duration}")
        delay(error.duration * 1000)
        MainActivity.logger("Duration elapsed.")
        visible = false
        viewModel.removeFirstError()
        MainActivity.logger("Deleted error from queue. Error: ${error.title} / ${error.message}")
    }
}
