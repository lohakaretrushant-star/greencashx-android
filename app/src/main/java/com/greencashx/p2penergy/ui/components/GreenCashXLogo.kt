package com.greencashx.p2penergy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greencashx.p2penergy.R
import com.greencashx.p2penergy.ui.theme.Poppins

@Composable
fun GreenCashXLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_greencashx),
            contentDescription = "GreenCashX Logo",
            modifier = Modifier.height(34.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color(0xFF69F0AE),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                ) { append("Green") }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                ) { append("CashX") }
            },
            fontSize = 20.sp
        )
    }
}
