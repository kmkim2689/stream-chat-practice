package com.practice.stream_chat_practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

class ChannelListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // stream sdk에 속해있는 채팅 목록 스크린
            // 세 가지 파트
            // 1. Channel list header(상단 앱바에 해당) -> title
            // 2. Search input bar -> isShowingSearch
            // 3. channel list -> onItemClick : 채널 아이템 중 하나 클릭 시 발생할 이벤트
            ChatTheme {
                ChannelsScreen(
                    title = "Channel List",
                    isShowingSearch = true,
                    onItemClick = {

                    },
                    onBackPressed = {
                        finish()
                    }
                )
            }
        }
    }
}