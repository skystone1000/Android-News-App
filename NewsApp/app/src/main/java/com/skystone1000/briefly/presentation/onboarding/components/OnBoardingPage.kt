package com.skystone1000.briefly.presentation.onboarding.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skystone1000.briefly.presentation.onboarding.Page
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.skystone1000.briefly.presentation.Dimens.MediumPadding1
import com.skystone1000.briefly.presentation.Dimens.MediumPadding2
import com.skystone1000.briefly.presentation.onboarding.pages
import com.skystone1000.briefly.ui.theme.BriefTheme

@Composable
fun OnBoardingPage(
    modifier: Modifier = Modifier,
    page: Page
) {
    Column(modifier = modifier){
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.6f),
            painter = painterResource(id = page.image),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(MediumPadding1))
        Text(
            text = page.title,
            modifier = Modifier.padding(horizontal = MediumPadding2),
            style = MaterialTheme.typography.titleMedium,
            color = BriefTheme.colors.text
        )
        Text(
            text = page.description,
            modifier = Modifier.padding(horizontal = MediumPadding2),
            style = MaterialTheme.typography.bodyMedium,
            color = BriefTheme.colors.textSec
        )
    }
}


@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES , showBackground = true)
@Composable
fun onBoardingPagePreview(){
    BriefTheme {
        OnBoardingPage(page = pages[0])
    }
}