package io.themasteredpanda.pedrodecks.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.themasteredpanda.pedrodecks.ConnectionManager

@Composable
fun Dashboard(conn: ConnectionManager? = null) {

}

@Preview(showBackground = true)
@Composable
fun DashboardPreview(conn: ConnectionManager? = null) {
    Dashboard()
}