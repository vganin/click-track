package com.vsevolodganin.clicktrack.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.state.redux.action.AboutAction
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.AboutUiState
import com.vsevolodganin.clicktrack.ui.widget.GenericTopBarWithBack
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Artstation
import compose.icons.simpleicons.Twitter

@Composable
fun AboutScreenView(state: AboutUiState, modifier: Modifier = Modifier, dispatch: Dispatch = Dispatch {}) {
    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.about_title, dispatch) },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }
}

@Composable
private fun Content(state: AboutUiState, dispatch: Dispatch) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .run {
                    if (isLandscape) align(Center) else this
                }
        ) {
            if (!isLandscape) {
                Image(
                    painter = painterResource(R.drawable.myself),
                    contentDescription = null,
                    alignment = Alignment.TopEnd,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = stringResource(R.string.about_developed_by),
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.caption
            )
            Text(
                text = stringResource(R.string.about_developer_name),
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.h6
            )
            Row(modifier = Modifier.align(CenterHorizontally)) {
                IconButton(onClick = { dispatch(AboutAction.GoHomePage) }) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null)
                }
                IconButton(onClick = { dispatch(AboutAction.GoTwitter) }) {
                    Icon(imageVector = SimpleIcons.Twitter, contentDescription = null)
                }
                IconButton(onClick = { dispatch(AboutAction.SendEmail) }) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.about_logo_by),
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = stringResource(R.string.about_logo_developer_name),
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.subtitle1
            )
            Row(modifier = Modifier.align(CenterHorizontally)) {
                IconButton(onClick = { dispatch(AboutAction.GoArtstation) }) {
                    Icon(imageVector = SimpleIcons.Artstation, contentDescription = null)
                }
            }
        }

        Text(
            text = stringResource(R.string.drawer_version, state.displayVersion),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(ContentAlpha.medium),
            style = MaterialTheme.typography.caption
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AboutScreenView(
        state = AboutUiState(
            displayVersion = "6.6.6"
        )
    )
}
