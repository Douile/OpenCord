package com.xinto.opencord.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xinto.opc.OverlappingPanels
import com.xinto.opc.OverlappingPanelsValue
import com.xinto.opc.rememberOverlappingPanelsState
import com.xinto.opencord.ui.screens.home.panels.channel.ChannelsList
import com.xinto.opencord.ui.screens.home.panels.chat.Chat
import com.xinto.opencord.ui.screens.home.panels.currentuser.CurrentUser
import com.xinto.opencord.ui.screens.home.panels.guild.GuildsList
import com.xinto.opencord.ui.util.animateCornerBasedShapeAsState
import com.xinto.opencord.ui.viewmodel.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit,
    onPinsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val panelState = rememberOverlappingPanelsState(OverlappingPanelsValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val currentUserViewModel: CurrentUserViewModel = getViewModel()
    val chatViewModel: ChatViewModel = getViewModel()
    val guildsViewModel: GuildsViewModel = getViewModel()
    val channelsViewModel: ChannelsViewModel = getViewModel()
    val channelPinsViewModel: ChannelPinsViewModel = getViewModel()

    Surface(modifier = modifier) {
        OverlappingPanels(
            modifier = Modifier.fillMaxSize(),
            panelsState = panelState,
            panelStart = {
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                    ) {
                        GuildsList(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp),
                            onGuildSelect = channelsViewModel::load,
                            viewModel = guildsViewModel,
                        )
                        ChannelsList(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            onChannelSelect = chatViewModel::load,
                            viewModel = channelsViewModel,
                        )
                    }
                    CurrentUser(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp),
                        viewModel = currentUserViewModel,
                        onSettingsClick = onSettingsClick,
                    )
                }
            },
            panelCenter = {
                val shape by animateCornerBasedShapeAsState(
                    if (panelState.offsetNotZero) {
                        MaterialTheme.shapes.large
                    } else {
                        RoundedCornerShape(0.dp)
                    },
                )
                Chat(
                    onChannelsButtonClick = {
                        coroutineScope.launch {
                            panelState.openStartPanel()
                        }
                    },
                    onMembersButtonClick = {
                        coroutineScope.launch {
                            panelState.openEndPanel()
                        }
                    },
                    onPinsButtonClick = {
                        channelPinsViewModel.load()
                        onPinsClick()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    viewModel = chatViewModel,
                )
            },
            panelEnd = {
                // members panel
            },
        )
    }
}
