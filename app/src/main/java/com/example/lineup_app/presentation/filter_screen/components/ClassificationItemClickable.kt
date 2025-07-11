package com.example.lineup_app.presentation.filter_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lineup_app.R
import com.example.lineup_app.presentation.ui.theme.MyIcons

@Composable
fun ClassificationItemClickable(
    classificationName: String,
    onClassificationDeleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(end = dimensionResource(R.dimen.padding_extra_small))
            .clip(MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = classificationName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
                    .padding(start = dimensionResource(R.dimen.padding_medium))

            )
            IconButton(
                onClick = { onClassificationDeleted(classificationName) },
            ) {
                Icon(
                    imageVector = MyIcons.close,
                    contentDescription = stringResource(R.string.delete) + classificationName,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview
@Composable
private fun ClassificationItemClickablePreview() {
    ClassificationItemClickable(
        classificationName = "Metal",
        onClassificationDeleted = {}
    )
}