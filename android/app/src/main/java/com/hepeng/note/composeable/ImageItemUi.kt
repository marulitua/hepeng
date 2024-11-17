package com.hepeng.note.composeable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hepeng.note.R
import com.hepeng.note.data.Image
import com.hepeng.note.ui.constants.HepengItemActionButtonRippleRadius
import com.hepeng.note.ui.constants.HepengItemBackgroundColor
import com.hepeng.note.ui.constants.HepengItemHeight
import com.hepeng.note.ui.constants.HepengItemIconColor
import com.hepeng.note.ui.constants.HepengItemIconSize
import com.hepeng.note.ui.constants.HepengItemTextColor
import com.hepeng.note.ui.constants.HepengItemTitleTextStyle
import com.hepeng.note.ui.constants.LargeDp
import com.hepeng.note.ui.constants.MediumDp
import androidx.compose.foundation.Image as ImageUi

@Composable
fun ImageItemUi(
    imageItem: Image = Image(path = "xxxxx"),
    onItemClick: (Image) -> Unit = {},
    onUploadClick: (Image) -> Unit = {},
) {
    val backgroundColor = if (imageItem.isUpload) HepengItemBackgroundColor.copy(alpha = 0.5f) else HepengItemBackgroundColor
    val textColor = if (imageItem.isUpload) HepengItemTextColor.copy(alpha = 0.5f) else HepengItemTextColor

    val textDecoration = if (imageItem.isUpload) TextDecoration.LineThrough else null

    val iconId = if (imageItem.isUpload) R.drawable.ic_selected_check_box else R.drawable.ic_empty_check_box
    val iconColorFilter = if (imageItem.isUpload) ColorFilter.tint(HepengItemIconColor.copy(alpha = 0.5f)) else ColorFilter.tint(
        HepengItemIconColor)
    val iconTintColor = if (imageItem.isUpload) HepengItemIconColor.copy(alpha = 0.5f) else HepengItemIconColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(HepengItemHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = LargeDp),
        shape = RoundedCornerShape(size = MediumDp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .clickable (
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) { onItemClick(imageItem) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            /*
            ImageUi(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(MediumDp)
                    .size(HepengItemIconSize),
                colorFilter = iconColorFilter
            )
             */
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageItem.path)
                .build(),
                contentDescription = imageItem.path,
                modifier = Modifier
                    .padding(MediumDp)
                    .size(HepengItemIconSize),
                colorFilter = iconColorFilter
            )
            Text(
                text = imageItem.path,
                modifier = Modifier.weight(1f),
                style = HepengItemTitleTextStyle.copy(color = textColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = textDecoration
            )
            IconButton(
                onClick = { onUploadClick(imageItem) },
                modifier = Modifier.size(HepengItemActionButtonRippleRadius)
            ) {
                Icon(
                    modifier = Modifier.size(HepengItemIconSize),
                    painter = painterResource(id = R.drawable.upload),
                    contentDescription = null,
                    tint = iconTintColor
                )
            }
        }
    }
}

@Preview
@Composable
fun ImageItemUiPreview() {
    Column(
        modifier = Modifier.padding(MediumDp),
        verticalArrangement = Arrangement.spacedBy(MediumDp)
    ) {
        ImageItemUi(imageItem = Image(path = "foo"))
        ImageItemUi(imageItem = Image(path = "bar", isUpload = true, isParsed = false))
    }
}