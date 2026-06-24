package com.example.newsapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.Dimens.AvatarSizeSmall
import com.example.newsapp.presentation.Dimens.CardRadiusLarge
import com.example.newsapp.presentation.Dimens.FeaturedImageHeight
import com.example.newsapp.ui.theme.BriefTheme

/** Hero card for the top article: image with a category badge, headline, source row. */
@Composable
fun FeaturedCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    time: String? = null,
) {
    val colors = BriefTheme.colors
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardRadiusLarge))
            .background(colors.card)
            .border(1.dp, colors.border, RoundedCornerShape(CardRadiusLarge))
            .clickable { onClick() },
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FeaturedImageHeight)
                    .background(colors.placeholder),
                model = ImageRequest.Builder(context).data(article.urlToImage).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            if (!badge.isNullOrBlank()) {
                Text(
                    text = badge.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .padding(11.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xA6080A09))
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp)) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight(700),
                ),
                color = colors.text,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(AvatarSizeSmall)
                        .clip(CircleShape)
                        .background(colors.accentSoft)
                        .border(1.dp, colors.border, CircleShape),
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = article.source.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textSec,
                )
                if (!time.isNullOrBlank()) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "· $time",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textTer,
                    )
                }
            }
        }
    }
}
