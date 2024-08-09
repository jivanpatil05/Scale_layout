package com.example.scale_layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun Scale(MinValue: Int, MaxValue: Int, horizontal: Boolean = false) {
    if (horizontal) {
        HeightScaleH(MinValue,MaxValue)
    } else {
        HeightScaleV(MinValue,MaxValue)
    }
}

@Composable
fun HeightScaleH(
    MinValue: Int = 0,
    MaxValue: Int = 100,
    MajorLineHeight: Dp = 2.dp,
    NormalLineHeight: Dp = 1.dp,
    MiddleLineHeight: Dp = 2.dp,
    MajorLineWidth: Float = 0.3f,
    NormalLineWidth: Float = 0.2f,
    MiddleLineWidth: Float = 0.0f,
    MajorLineColor: Color = Color.Black,
    NormalLineColor: Color = Color.Gray,
    MiddleLineColor: Color = Color.Blue
) {
    var offsetY by remember { mutableStateOf(0f) }
    val stepSize = 10
    val dataList = (MinValue..MaxValue).toList()
    val totalHeight = (dataList.size - 1) * 20f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "What's your height?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        val selectedHeight =
            (MinValue + (offsetY / 20).coerceIn(0f, (dataList.size - 1).toFloat())
                .roundToInt()).coerceIn(MinValue, MaxValue)

        Text(
            text = "$selectedHeight cm",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .padding(vertical = 50.dp)
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { delta ->
                            val speedFactor = 1f
                            offsetY = (offsetY - delta * speedFactor).coerceIn(0f, totalHeight)
                            delta
                        }
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val step = 20f

                    for (i in dataList.indices) {
                        val yOffset = (i * step) - offsetY + height / 2
                        if (yOffset >= 0 && yOffset <= height) {
                            val isMajorStep = dataList[i] % stepSize == 0
                            val lineWidth = if (isMajorStep) width * MajorLineWidth else width * NormalLineWidth
                            val lineHeight = if (isMajorStep) MajorLineHeight.toPx() else NormalLineHeight.toPx()
                            val lineColor = if (isMajorStep) MajorLineColor else NormalLineColor

                            drawLine(
                                color = lineColor,
                                start = Offset(x = width - lineWidth, y = yOffset),
                                end = Offset(x = width, y = yOffset),
                                strokeWidth = lineHeight,
                                cap = StrokeCap.Round
                            )

                            if (isMajorStep) {
                                drawContext.canvas.nativeCanvas.drawText(
                                    dataList[i].toString(),
                                    width - lineWidth - 40,
                                    yOffset + 10,
                                    android.graphics.Paint().apply {
                                        textSize = 30f
                                        color = android.graphics.Color.BLACK
                                        textAlign = android.graphics.Paint.Align.RIGHT
                                    }
                                )
                            }
                        }
                    }

                    drawLine(
                        color = MiddleLineColor,
                        start = Offset(x = width * MiddleLineWidth, y = height / 2),
                        end = Offset(x = width, y = height / 2),
                        strokeWidth = MiddleLineHeight.toPx(),
                        cap = StrokeCap.Round
                    )

                    val gradientHeight = 150f
                    val gradientBrushTop = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 1f),
                            Color.White.copy(alpha = 0f)
                        ),
                        startY = 0f,
                        endY = gradientHeight
                    )
                    val gradientBrushBottom = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 1f)
                        ),
                        startY = size.height - gradientHeight,
                        endY = size.height
                    )

                    // Draw the top gradient
                    drawRect(
                        brush = gradientBrushTop,
                        topLeft = Offset(0f, 0f),
                        size = size.copy(height = gradientHeight)
                    )


                    drawRect(
                        brush = gradientBrushBottom,
                        topLeft = Offset(0f, size.height - gradientHeight),
                        size = size.copy(height = gradientHeight)
                    )
                }
            }
        }
    }
}


@Composable
fun HeightScaleV(
    MinValue: Int = 0,
    MaxValue: Int = 100,
    MajorLineHeight: Dp = 2.dp,
    NormalLineHeight: Dp = 1.dp,
    MiddleLineHeight: Dp = 2.dp,
    MajorLineWidth: Float = 0.3f,
    NormalLineWidth: Float = 0.2f,
    MiddleLineWidth: Float = 0f,
    MajorLineColor: Color = Color.Black,
    NormalLineColor: Color = Color.Gray,
    MiddleLineColor: Color = Color.Blue,
) {
    var offsetX by remember { mutableStateOf(0f) }
    var selectedUnit by remember { mutableStateOf("Kg") }
    var selectedHeight by remember { mutableStateOf(MinValue) }

    val MaxLbs = MaxValue * 2.20462
    val MinLbs = MinValue * 2.20462

    val currentMinValue by remember(selectedUnit) {
        derivedStateOf { if (selectedUnit == "Lbs") MinLbs.toInt() else MinValue }
    }
    val currentMaxValue by remember(selectedUnit) {
        derivedStateOf { if (selectedUnit == "Lbs") MaxLbs.toInt() else MaxValue }
    }

    val adjustedOffsetX by remember(offsetX, selectedUnit) {
        derivedStateOf {
            val scaleFactor = when (selectedUnit) {
                "Kg" -> 1f
                "Lbs" -> 2.20462f
                else -> 1f
            }
            offsetX * scaleFactor
        }
    }

    val lineSpacing = 20f
    val stepSize = 10
    val dataList = (currentMinValue..currentMaxValue).toList()

    val maxOffsetX = if (selectedUnit == "Lbs") {
        ((dataList.size - 1) * lineSpacing) / 2.20462f
    } else {
        (dataList.size - 1) * lineSpacing
    }

    val gradientWidthPx = with(LocalDensity.current) { 100.dp.toPx() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val kgValue = selectedHeight / 2.20462f
                    selectedUnit = "Kg"
                    selectedHeight = kgValue.toInt()
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedUnit == "Kg") Color.Gray else Color.LightGray)
            ) {
                Text("Kg")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val lbsValue = selectedHeight * 2.20462f
                    selectedUnit = "Lbs"
                    selectedHeight = lbsValue.toInt()
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedUnit == "Lbs") Color.Gray else Color.LightGray)
            ) {
                Text("Lbs")
            }
        }

        Text(
            text = "What's your height?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "$selectedHeight",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 20.dp)
                .scrollable(
                    orientation = Orientation.Horizontal,
                    state = rememberScrollableState { delta ->
                        val speedFactor = 1f
                        offsetX = (offsetX - delta * speedFactor).coerceIn(
                            0f,
                            maxOffsetX
                        )
                        selectedHeight =
                            ((adjustedOffsetX / lineSpacing).toInt() + currentMinValue).coerceIn(currentMinValue, currentMaxValue)
                        delta
                    }
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                for (i in dataList.indices) {
                    val xOffset = (i * lineSpacing) - adjustedOffsetX + width / 2
                    if (xOffset in 0f..width) {
                        val isMajorStep = (dataList[i] % stepSize == 0)
                        val lineHeight = if (isMajorStep) height * MajorLineWidth else height * NormalLineWidth
                        val lineWidth = if (isMajorStep) MajorLineHeight.toPx() else NormalLineHeight.toPx()
                        val lineColor = if (isMajorStep) MajorLineColor else NormalLineColor

                        drawLine(
                            color = lineColor,
                            start = Offset(x = xOffset, y = height - lineHeight),
                            end = Offset(x = xOffset, y = height),
                            strokeWidth = lineWidth,
                            cap = StrokeCap.Round
                        )

                        if (isMajorStep) {
                            drawContext.canvas.nativeCanvas.drawText(
                                dataList[i].toString(),
                                xOffset,
                                height - lineHeight - 10,
                                android.graphics.Paint().apply {
                                    textSize = 30f
                                    color = android.graphics.Color.BLACK
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }
                    }
                }

                // Start gradient overlay
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White, Color.Transparent),
                        startX = 0f,
                        endX = gradientWidthPx
                    ),
                    size = Size(gradientWidthPx, height)
                )

                // End gradient overlay
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.White),
                        startX = width - gradientWidthPx,
                        endX = width
                    ),
                    size = Size(gradientWidthPx, height),
                    topLeft = Offset(x = width - gradientWidthPx, y = 0f)
                )

                // Middle line
                drawLine(
                    color = MiddleLineColor,
                    start = Offset(x = width / 2, y = MiddleLineWidth),
                    end = Offset(x = width / 2, y = height),
                    strokeWidth = MiddleLineHeight.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}