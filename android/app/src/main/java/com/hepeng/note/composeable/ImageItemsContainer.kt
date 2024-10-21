package com.hepeng.note.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import com.hepeng.note.data.Image
import com.hepeng.note.ui.constants.MediumDp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ImageItemsContainer(
    modifier: Modifier = Modifier,
    imageItemsFlow: Flow<List<Image>> = flowOf(listOf()),
    onItemClick: (Image) -> Unit = {},
    onItemDelete: (Image) -> Unit = {},
    overlappingElementsHeight: Dp = 0.dp
) {
    val images = imageItemsFlow.collectAsState(initial = listOf()).value
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(MediumDp),
        verticalArrangement = Arrangement.spacedBy(MediumDp)
    ) {
        // 3. Items Rendering
        items(images, key = { it.id }) { item ->
            ImageItemUi(
                imageItem = item,
                onItemClick = onItemClick,
                onItemDelete = onItemDelete
            )
        }
        // 4. Layout Adjustment
        item { Spacer(modifier = Modifier.height(overlappingElementsHeight)) }
    }
}