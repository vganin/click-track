package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.about.AboutState
import com.vsevolodganin.clicktrack.about.AboutViewModel
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.utils.compose.UrlClickableText
import com.vsevolodganin.clicktrack.utils.compose.isSystemInLandscape
import com.vsevolodganin.clicktrack.utils.compose.textResource
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Artstation
import compose.icons.simpleicons.Twitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AboutScreenView(
    viewModel: AboutViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(R.string.about_screen_title)) },
            )
        },
        modifier = modifier,
    ) {
        Content(viewModel)
    }
}

@Composable
private fun Content(viewModel: AboutViewModel) {
    val state by viewModel.state.collectAsState()
    val isLandscape = isSystemInLandscape()

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .run {
                    if (isLandscape) align(Center) else this
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isLandscape) {
                Image(
                    painter = painterResource(R.drawable.myself),
                    contentDescription = null,
                    alignment = Alignment.TopEnd,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = stringResource(R.string.about_developed_by),
                style = MaterialTheme.typography.caption
            )
            Text(
                text = stringResource(R.string.about_developer_name),
                style = MaterialTheme.typography.h6
            )
            Row {
                IconButton(onClick = viewModel::onHomeClick) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null)
                }
                IconButton(onClick = viewModel::onTwitterClick) {
                    Icon(imageVector = SimpleIcons.Twitter, contentDescription = null)
                }
                IconButton(onClick = viewModel::onEmailClick) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.about_logo_by),
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = stringResource(R.string.about_logo_developer_name),
                style = MaterialTheme.typography.subtitle1
            )
            IconButton(onClick = viewModel::onArtstationClick) {
                Icon(imageVector = SimpleIcons.Artstation, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(24.dp))

            UrlClickableText(
                text = textResource(R.string.about_open_source_note),
                onUrlClick = { viewModel.onProjectLinkClick() },
                modifier = Modifier.padding(horizontal = 48.dp),
                textAlign = TextAlign.Center,
            )
        }

        Text(
            text = stringResource(R.string.drawer_version, state.displayVersion),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(ContentAlpha.medium)
                .navigationBarsPadding(),
            style = MaterialTheme.typography.caption
        )
    }
}

@ScreenPreviews
@Composable
private fun Preview() = ClickTrackTheme {
    AboutScreenView(
        viewModel = object : AboutViewModel {
            override val state: StateFlow<AboutState> = MutableStateFlow(
                AboutState(
                    displayVersion = "6.6.6"
                )
            )

            override fun onBackClick() = Unit
            override fun onHomeClick() = Unit
            override fun onTwitterClick() = Unit
            override fun onEmailClick() = Unit
            override fun onArtstationClick() = Unit
            override fun onProjectLinkClick() = Unit
        }
    )
}
