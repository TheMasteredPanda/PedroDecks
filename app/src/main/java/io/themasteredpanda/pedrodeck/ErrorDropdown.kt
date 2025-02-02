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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@Composable
fun ErrorDropdown(viewModel: AppViewModel, visible: Boolean, title: String, message: String) {
    val localDensity = LocalDensity.current
    val errorMsg = viewModel.errorMessage!!

    AnimatedVisibility(
        visible = errorMsg.visible,
        enter = slideInVertically {
            with(localDensity) { -40.dp.roundToPx() }
        } + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = .3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
    ) {
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

                    Text(text = title, fontSize = 20.sp)
                    Spacer(modifier = Modifier.padding(3.dp))
                    Text(text = message, fontSize = 15.sp)
                }

                Button(onClick = { viewModel.clearErrorMessage() }, content = { Text(text = "Dismiss") }, modifier =
                Modifier
                    .fillMaxWidth
                        (), shape = RoundedCornerShape(0.dp)
                )
            }
        }
    }

    LaunchedEffect(errorMsg.visible) {
        if (errorMsg.visible) {
            delay(4000)
            viewModel.clearErrorMessage()
        }
    }
}
