package com.hepeng.note.composeable

import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import com.hepeng.note.Constants
import com.hepeng.note.GrpcActivity
import com.hepeng.note.ImageDetailActivity
import com.hepeng.note.data.Image
import com.hepeng.note.ui.constants.MediumDp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ImageItemsContainer(
    modifier: Modifier = Modifier,
    imageItemsFlow: Flow<List<Image>> = flowOf(listOf()),
    onItemUpload: (Image) -> Unit = {},
    overlappingElementsHeight: Dp = 0.dp
) {
    val images = imageItemsFlow.collectAsState(initial = listOf()).value

    val mContext = LocalContext.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(MediumDp),
        verticalArrangement = Arrangement.spacedBy(MediumDp)
    ) {
        // 3. Items Rendering
        items(images, key = { it.id }) { item ->
            ImageItemUi(
                imageItem = item,
                onItemClick = fun(image: Image) {
                    Log.e(Constants.TAG, "showing ${image.path}")

                    val intent  = Intent(mContext, ImageDetailActivity::class.java)
                    intent.putExtra("filename", image.path)
                    mContext.startActivity(
                        intent
                    )
                },
                onUploadClick = onItemUpload
            )
        }
        // 4. Layout Adjustment
        item { Spacer(modifier = Modifier.height(overlappingElementsHeight)) }
    }
}