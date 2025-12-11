package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.about_developed_by
import clicktrack.multiplatform.generated.resources.about_developer_name
import clicktrack.multiplatform.generated.resources.about_logo_by
import clicktrack.multiplatform.generated.resources.about_logo_developer_name
import clicktrack.multiplatform.generated.resources.about_open_source_note
import clicktrack.multiplatform.generated.resources.about_screen_title
import clicktrack.multiplatform.generated.resources.about_version
import clicktrack.multiplatform.generated.resources.myself
import com.vsevolodganin.clicktrack.about.AboutState
import com.vsevolodganin.clicktrack.about.AboutViewModel
import com.vsevolodganin.clicktrack.ui.piece.DarkTopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.compose.UrlClickableText
import com.vsevolodganin.clicktrack.utils.compose.isSystemInLandscape
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Artstation
import compose.icons.simpleicons.Twitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AboutScreenView(viewModel: AboutViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            DarkTopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(Res.string.about_screen_title)) },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Content(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun Content(
    viewModel: AboutViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val isLandscape = isSystemInLandscape()

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!isLandscape) {
            Image(
                painter = painterResource(Res.drawable.myself),
                contentDescription = null,
                alignment = Alignment.TopEnd,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = stringResource(Res.string.about_developed_by),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(Res.string.about_developer_name),
            style = MaterialTheme.typography.headlineSmall,
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
            text = stringResource(Res.string.about_logo_by),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(Res.string.about_logo_developer_name),
            style = MaterialTheme.typography.bodyLarge,
        )
        IconButton(onClick = viewModel::onArtstationClick) {
            Icon(imageVector = SimpleIcons.Artstation, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(24.dp))

        UrlClickableText(
            textWithUrls = stringResource(Res.string.about_open_source_note),
            onUrlClick = { viewModel.onProjectLinkClick() },
            modifier = Modifier.padding(horizontal = 48.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.about_version, state.displayVersion),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview
@Composable
fun AboutScreenPreview() = ClickTrackTheme {
    AboutScreenView(
        viewModel = object : AboutViewModel {
            override val state: StateFlow<AboutState> = MutableStateFlow(
                AboutState(
                    displayVersion = "6.6.6",
                ),
            )

            override fun onBackClick() = Unit

            override fun onHomeClick() = Unit

            override fun onTwitterClick() = Unit

            override fun onEmailClick() = Unit

            override fun onArtstationClick() = Unit

            override fun onProjectLinkClick() = Unit
        },
    )
}
