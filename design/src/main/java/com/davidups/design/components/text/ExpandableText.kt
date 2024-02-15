package com.davidups.design.components.text


import androidx.compose.animation.animateContentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    minLines: Int = Int.MIN_VALUE,
    state: Boolean,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        modifier = modifier.animateContentSize(),
        text = text,
        style = style,
        maxLines = if (state) Int.MAX_VALUE else minLines
    )
}
