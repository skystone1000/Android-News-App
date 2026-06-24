package com.example.newsapp.presentation.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.Dimens.HeroImageHeight
import com.example.newsapp.presentation.details.components.AiInsightCard
import com.example.newsapp.presentation.details.components.DetailsTopBar
import com.example.newsapp.ui.theme.BriefTheme

@Composable
fun DetailsScreen(
    article: Article,
    sideEffect: String?,
    aiState: AiInsightState,
    isBookmarked: Boolean,
    event: (DetailsEvent) -> Unit,
    onRequestInsight: () -> Unit,
    navigateUp: () -> Unit,
) {
    val colors = BriefTheme.colors
    val context = LocalContext.current
    val speaker = rememberArticleSpeaker()

    LaunchedEffect(sideEffect) {
        sideEffect?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            event(DetailsEvent.RemoveSideEffect)
        }
    }
    LaunchedEffect(article.url) { onRequestInsight() }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bg),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            DetailsTopBar(
                isSpeaking = speaker.isSpeaking,
                isBookmarked = isBookmarked,
                onListenClick = {
                    val body = article.content.ifEmpty { article.description }
                    speaker.toggle("${article.title}. $body")
                },
                onShareClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, article.title)
                        putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.url}")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share article"))
                },
                onBookmarkClick = { event(DetailsEvent.UpsertDeleteArticle(article)) },
                onBrowsingClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                },
                onBackClick = navigateUp,
            )
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeroImageHeight)
                    .background(colors.placeholder),
                model = ImageRequest.Builder(context).data(article.urlToImage).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Text(
                    text = article.source.name.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.accent,
                )
                Spacer(Modifier.height(11.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.text,
                )
                Row(
                    modifier = Modifier.padding(top = 15.dp, bottom = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(colors.accentSoft)
                            .border(1.dp, colors.border, CircleShape),
                    )
                    Spacer(Modifier.width(9.dp))
                    Column {
                        Text(article.source.name, style = MaterialTheme.typography.labelLarge, color = colors.text)
                        Text(article.publishedAt, style = MaterialTheme.typography.labelSmall, color = colors.textTer)
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = colors.divider)
                AiInsightCard(state = aiState, modifier = Modifier.padding(top = 16.dp))
                Spacer(Modifier.height(16.dp))
                Text(
                    text = article.content.ifEmpty { article.description },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSec,
                )
            }
        }
    }
}
