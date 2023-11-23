package com.practice.stream_chat_practice

import android.content.Context
import androidx.compose.ui.res.stringResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Config
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOfflinePluginFactory(
        @ApplicationContext context: Context
    ) = StreamOfflinePluginFactory(context)

    @Provides
    @Singleton
    fun provideStreamStatePluginFactory(
        @ApplicationContext context: Context
    ) = StreamStatePluginFactory(
        config = StatePluginConfig(),
        appContext = context
    )

    @Provides
    @Singleton
    fun provideChatClient(
        @ApplicationContext context: Context,
        offlinePluginFactory: StreamOfflinePluginFactory,
        streamStatePluginFactory: StreamStatePluginFactory
    ) = ChatClient.Builder(context.getString(R.string.api_key), context)
        .withPlugins(offlinePluginFactory, streamStatePluginFactory)
        .logLevel(ChatLogLevel.ALL)
        .build()
}