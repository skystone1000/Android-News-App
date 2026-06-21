package com.example.newsapp.presentation.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.Dimens.ArticleImageHeight
import com.example.newsapp.presentation.Dimens.MediumPadding1
import com.example.newsapp.presentation.details.components.AiInsightCard
import com.example.newsapp.presentation.details.components.DetailsTopBar

@Composable
fun DetailsScreen(
    article: Article,
    sideEffect: String?,
    aiState: AiInsightState,
    event: (DetailsEvent) -> Unit,
    onRequestInsight: () -> Unit,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(sideEffect) {
        sideEffect?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            event(DetailsEvent.RemoveSideEffect)
        }
    }

    LaunchedEffect(article.url) { onRequestInsight() }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = MediumPadding1)
    ) {
        item {
            DetailsTopBar(
                onBookmarkClick = { event(DetailsEvent.UpsertDeleteArticle(article)) },
                onBrowsingClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                },
                onBackClick = navigateUp
            )
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ArticleImageHeight),
                model = ImageRequest.Builder(context).data(article.urlToImage).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier.padding(horizontal = MediumPadding1, vertical = 12.dp),
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            AiInsightCard(
                state = aiState,
                modifier = Modifier.padding(horizontal = MediumPadding1)
            )
            Text(
                modifier = Modifier.padding(horizontal = MediumPadding1),
                text = article.content.ifEmpty { article.description },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
