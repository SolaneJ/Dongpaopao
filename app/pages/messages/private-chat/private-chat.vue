<template>
	<view class="chat-container">

		<!-- 顶部导航栏 -->
		<view class="chat-header">
			<uni-icons type="back" size="24" color="#333" @click="handleBack" />
			<view class="user-info">
				<text class="chat-title">{{ userName }}</text>
				<text class="online-status" v-if="isOnline">在线</text>
				<text class="online-status offline" v-else>离线</text>
			</view>
			<view class="header-actions">
				<uni-icons type="tel" size="22" color="#333" @click="makeCall" />
				<uni-icons type="more-filled" size="24" color="#333" @click="showActionSheet" />
			</view>
		</view>

		<!-- 聊天内容区域 -->
		<scroll-view class="chat-content"
			scroll-y
			:scroll-top="scrollTop"
			enablePullDownRefresh="true"
			:refresher-triggered="refresherTriggered"
			@refresherrefresh="onRefresherRefresh"
			@touchstart="handleTouchStart"
			@touchend="handleTouchEnd"
			ref="scrollRef">
			<!-- 日期分隔线 -->
			<view class="date-divider" v-if="messages.length > 0">
				{{ currentDate }}
			</view>

			<view v-for="(message, index) in messages" :key="message.id"
				:class="['message-item', message.isMe ? 'message-right' : 'message-left']">
				<image v-if="!message.isMe" class="avatar" :src="message.avatar || userAvatar || defaultAvatar"
					@click="viewUserAvatar(message.avatar || userAvatar)" />
				<image v-if="message.isMe" class="avatar self-avatar" :src="myAvatar" />

				<!-- 已读状态 -->
				<view v-if="message.isMe" class="read-status">
					<uni-icons type="checkmark" size="16" color="rgba(153,153,153,0.8)" v-if="!message.read" />
					<uni-icons type="checkmark-double" size="16" color="#1989fa" v-if="message.read" />
				</view>

				<view class="message-bubble" :class="{
            'message-error': message.status === 'error',
            'voice-message': message.isVoice
          }" @longpress="showMessageAction(message, index)">
					<template v-if="message.isVoice">
						<view class="voice-content">
							<uni-icons :type="message.isMe ? 'volume-right' : 'volume-left'" size="18"
								:color="message.isMe ? '#fff' : '#666'" class="voice-icon" />
							<view class="voice-length" :style="{width: message.duration * 10 + 'rpx'}"></view>
							<text class="voice-time">{{ message.duration }}"</text>
						</view>
					</template>
					<template v-else>
						<text class="message-text">{{ message.content }}</text>
					</template>
					<text class="message-time">{{ message.time }}</text>

					<!-- 发送状态指示器 -->
					<uni-icons type="spinner-cycle" size="16" color="#999" class="sending-indicator"
						v-if="message.status === 'sending'" />
					<uni-icons type="close" size="16" color="#f53f3f" class="send-error"
						v-if="message.status === 'error'" @click="resendMessage(index)" />
				</view>
			</view>

			<!-- 加载更多提示 -->
			<view class="loading-more" v-if="isLoadingMore">
				<!-- <uni-loading type="circle" size="20" color="#999"></uni-loading> -->
				<text>加载更多历史消息...</text>
			</view>
		</scroll-view>

		<!-- 底部输入区域 -->
		<view class="input-area">
			<uni-icons type="image" size="28" color="#666" @click="chooseImage" />
			<uni-icons type="smile-o" size="28" color="#666" @click="showEmojiPanel" />
			<uni-icons :type="isRecording ? 'keyboard' : 'mic-filled'" size="28" color="#666"
				@click="toggleInputMode" />

			<template v-if="!isRecording">
				<input class="input-box" v-model="inputText" placeholder="输入消息..." placeholder-style="color:#999"
					@confirm="sendMessage" @input="autoResizeInput" :style="{height: inputHeight}" maxlength="500" />
				<uni-button type="primary" size="mini" :disabled="!inputText.trim()" @click="sendMessage"
					class="send-btn">
					发送
				</uni-button>
			</template>

			<template v-else>
				<view class="voice-button" @touchstart="startRecording" @touchend="stopRecording"
					@touchmove="cancelRecording" :class="{cancel: isCancelRecording}">
					{{ voiceTip }}
				</view>
			</template>
		</view>

		<!-- 录音弹窗 -->
		<view v-if="showVoiceModal" class="voice-modal">
			<view class="voice-modal-content">
				<uni-icons type="mic-filled" size="50" :color="isCancelRecording ? '#f53f3f' : '#1989fa'" />
				<text class="voice-tip">{{ voiceTip }}</text>
				<text class="voice-duration" v-if="recordDuration > 0">
					{{ recordDuration }}秒
				</text>
			</view>
		</view>

		<!-- 表情面板 -->
		<view class="emoji-panel" v-if="showEmoji">
			<view class="emoji-list">
				<text class="emoji-item" v-for="emoji in emojis" :key="emoji"
					@click="addEmoji(emoji)">{{ emoji }}</text>
			</view>
			<view class="emoji-footer">
				<uni-button type="text" @click="showEmoji = false">完成</uni-button>
			</view>
		</view>

		<!-- 消息操作菜单 -->
		<uni-popup v-model="showMessageMenu" type="bottom" :mask-click="true" @close="closeMessageMenu">
			<view class="message-menu">
				<view class="menu-item" @click="copyMessage">
					<uni-icons type="copy" size="20" color="#666" />
					<text>复制</text>
				</view>
				<view class="menu-item" @click="forwardMessage"
					v-if="currentMessage && typeof currentMessage.isMe === 'boolean' && !currentMessage.isMe">
					<uni-icons type="forward" size="20" color="#666" />
					<text>转发</text>
				</view>
				<view class="menu-item" @click="deleteMessage"
					v-if="currentMessage && typeof currentMessage.isMe === 'boolean' && currentMessage.isMe">
					<uni-icons type="trash" size="20" color="#666" />
					<text>删除</text>
				</view>
				<view class="menu-item cancel" @click="showMessageMenu = false">
					<text>取消</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script lang="ts">
	export default {
		data() {
			return {
				defaultAvatar: 'https://ai-public.mastergo.com/ai/img_res/523d6d73426c46b6cd95b111d9cb3156.jpg',
				myAvatar: 'https://ai-public.mastergo.com/ai/img_res/8f24da8382e95f64295c3f69c8e9e38.jpg',
				emojis: ['😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊', '🙂', '🙃', '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '👍', '👎', '👊', '✌️', '🤝', '🙏', '🎉', '🎊', '🔥', '🥳', '😢', '😭', '😱', '😡', '🤔', '🤫', '🤭', '😴'],
				scrollTop: 0,
				inputText: '',
				isRecording: false,
				recordStartTime: 0,
				showVoiceModal: false,
				voiceTip: '按住说话',
				recordDuration: 0,
				isCancelRecording: false,
				showEmoji: false,
				inputHeight: '80rpx',
				maxInputHeight: '200rpx',
				isLoadingMore: false,
				currentDate: '',
				showMessageMenu: false,
				currentMessage: undefined,
				currentMessageIndex: -1,
				scrollRef: null,
				startX: 0,
				userName: '',
				userAvatar: '',
				isOnline: true,
				messages: [
					{
						id: 1,
						content: '你好，最近怎么样？',
						time: '10:30',
						isMe: false,
						avatar: 'https://ai-public.mastergo.com/ai/img_res/5e99a8d9d5b5885d026bfe0121f707bc.jpg',
						status: 'read',
						read: false
					},
					{
						id: 2,
						content: '我很好，谢谢关心！你呢？',
						time: '10:32',
						isMe: true,
						status: 'read',
						read: true
					},
					{
						id: 3,
						content: '我也还不错，周末有空一起吃饭吗？',
						time: '10:33',
						isMe: false,
						avatar: 'https://ai-public.mastergo.com/ai/img_res/beaf48e744fa07b44e063a790d032f03.jpg',
						status: 'read',
						read: false
					},
					{
						id: 4,
						content: '好啊，周六中午怎么样？',
						time: '10:35',
						isMe: true,
						status: 'read',
						read: true
					},
					{
						id: 5,
						content: '可以，我知道一家不错的餐厅',
						time: '10:36',
						isMe: false,
						avatar: 'https://ai-public.mastergo.com/ai/img_res/99b035be8ed4e7aa48e861079872bd31.jpg',
						status: 'read',
						read: false
					},
					{
						id: 6,
						isVoice: true,
						duration: 3,
						time: '10:38',
						isMe: true,
						status: 'sending',
						read: false
					}
				],
				refresherTriggered: false
			};
		},
		methods: {
			handleBack() {
				uni.navigateBack({ delta: 1 });
			},
			showActionSheet() {
				uni.showActionSheet({
					itemList: ['查看资料', '设置备注', '消息免打扰', '删除会话'],
					success: (res) => {
						switch (res.tapIndex) {
							case 0: this.goToUserProfile(); break;
							case 1: this.setRemark(); break;
							case 2: this.setDoNotDisturb(); break;
							case 3: this.deleteConversation(); break;
						}
					}
				});
			},
			goToUserProfile() {
				uni.navigateTo({ url: '/pages/user-profile/index' });
			},
			setRemark() {
				uni.showModal({
					title: '设置备注',
					inputValue: this.userName,
					success: (res) => {
						if (res.confirm && res.inputValue) {
							this.userName = res.inputValue;
							uni.showToast({ title: '设置成功', icon: 'none' });
						}
					}
				});
			},
			setDoNotDisturb() {
				uni.showToast({ title: '已开启消息免打扰', icon: 'none' });
			},
			deleteConversation() {
				uni.showModal({
					title: '提示',
					content: '确定要删除该会话吗？',
					success: (res) => {
						if (res.confirm) {
							this.handleBack();
						}
					}
				});
			},
			loadMoreMessages() {
				if (this.isLoadingMore) return;
				this.isLoadingMore = true;
				setTimeout(() => {
					const newMessages = [
						{
							id: this.messages.length + 1,
							content: '还记得上次我们一起去看的电影吗？',
							time: '昨天 18:45',
							isMe: false,
							avatar: 'https://ai-public.mastergo.com/ai/img_res/5e99a8d9d5b5885d026bfe0121f707bc.jpg',
							status: 'read',
							read: false
						},
						{
							id: this.messages.length + 2,
							content: '当然记得，特别精彩！',
							time: '昨天 18:50',
							isMe: true,
							status: 'read',
							read: true
						}
					];
					this.messages = [...newMessages, ...this.messages];
					this.isLoadingMore = false;
				}, 1000);
			},
			sendMessage() {
				const content = this.inputText.trim();
				if (!content) return;
				const newMessage = {
					id: this.messages.length + 1,
					content,
					time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
					isMe: true,
					status: 'sending',
					read: false
				};
				this.messages.push(newMessage);
				this.inputText = '';
				this.resetInputHeight();
				this.scrollToBottom();
				setTimeout(() => {
					const msgIndex = this.messages.findIndex(m => m.id === newMessage.id);
					if (msgIndex !== -1) {
						this.messages[msgIndex].status = 'read';
						this.messages[msgIndex].read = true;
					}
				}, 1000);
			},
			receiveMessage(content) {
				const newMessage = {
					id: this.messages.length + 1,
					content,
					time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
					isMe: false,
					avatar: 'https://ai-public.mastergo.com/ai/img_res/5e99a8d9d5b5885d026bfe0121f707bc.jpg',
					status: 'read',
					read: false
				};
				this.messages.push(newMessage);
				this.scrollToBottom();
				uni.vibrateShort();
			},
			resendMessage(index) {
				if (index < 0 || index >= this.messages.length) return;
				this.messages[index].status = 'sending';
				setTimeout(() => {
					this.messages[index].status = 'read';
					this.messages[index].read = true;
				}, 1000);
			},
			scrollToBottom() {
				this.$nextTick(() => {
					this.scrollTop = 999999;
				});
			},
			handleTouchStart(e) {
				this.startX = e.touches[0].pageX;
			},
			handleTouchEnd(e) {
				const endX = e.changedTouches[0].pageX;
				if (this.startX - endX > 100) {
					this.handleBack();
				}
			},
			toggleInputMode() {
				this.isRecording = !this.isRecording;
				this.voiceTip = '按住说话';
				this.showEmoji = false;
			},
			startRecording() {
				this.recordStartTime = Date.now();
				this.showVoiceModal = true;
				this.voiceTip = '松开发送';
				this.isCancelRecording = false;
				const timer = setInterval(() => {
					this.recordDuration = Math.floor((Date.now() - this.recordStartTime) / 1000);
				}, 1000);
				uni.startRecord({
					success: () => { },
					fail: (err) => {
						clearInterval(timer);
						this.showVoiceModal = false;
						uni.showToast({ title: '录音失败', icon: 'none' });
					}
				});
				uni.recordTimer = timer;
			},
			stopRecording() {
				clearInterval(uni.recordTimer);
				const duration = Date.now() - this.recordStartTime;
				this.showVoiceModal = false;
				if (duration < 1000) {
					uni.showToast({ title: '说话时间太短', icon: 'none' });
					uni.stopRecord();
					this.recordDuration = 0;
					return;
				}
				if (this.isCancelRecording) {
					uni.stopRecord();
					this.recordDuration = 0;
					this.isCancelRecording = false;
					return;
				}
				uni.stopRecord({
					success: (res) => {
						this.messages.push({
							id: this.messages.length + 1,
							isVoice: true,
							duration: Math.floor(duration / 1000),
							time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
							isMe: true,
							status: 'read',
							read: true,
							tempFilePath: res.tempFilePath
						});
						this.scrollToBottom();
					},
					fail: (err) => {
						uni.showToast({ title: '录音保存失败', icon: 'none' });
					}
				});
				this.recordDuration = 0;
			},
			cancelRecording(e) {
				const touchY = e.touches[0].pageY;
				const buttonTop = e.target.offsetTop;
				if (touchY < buttonTop - 50) {
					this.voiceTip = '松开手指，取消发送';
					this.isCancelRecording = true;
				} else {
					this.voiceTip = '松开发送';
					this.isCancelRecording = false;
				}
			},
			chooseImage() {
				uni.chooseImage({
					count: 1,
					sizeType: ['original', 'compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						this.messages.push({
							id: this.messages.length + 1,
							isImage: true,
							imageUrl: res.tempFilePaths[0],
							time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
							isMe: true,
							status: 'sending',
							read: false
						});
						this.scrollToBottom();
						setTimeout(() => {
							const lastIndex = this.messages.length - 1;
							this.messages[lastIndex].status = 'read';
							this.messages[lastIndex].read = true;
						}, 1000);
					}
				});
			},
			showEmojiPanel() {
				this.showEmoji = !this.showEmoji;
				if (this.isRecording) {
					this.isRecording = false;
				}
			},
			addEmoji(emoji) {
				this.inputText += emoji;
			},
			autoResizeInput(e) {
				const inputDom = e.target;
				inputDom.style.height = 'auto';
				let newHeight = inputDom.scrollHeight + 'px';
				if (parseInt(newHeight) > parseInt(this.maxInputHeight)) {
					newHeight = this.maxInputHeight;
				}
				this.inputHeight = newHeight;
			},
			resetInputHeight() {
				this.inputHeight = '80rpx';
			},
			viewUserAvatar(avatarUrl) {
				uni.previewImage({ urls: [avatarUrl || this.defaultAvatar] });
			},
			makeCall() {
				uni.showModal({
					title: '提示',
					content: `是否拨打${this.userName}的语音电话？`,
					success: (res) => {
						if (res.confirm) {
							uni.showToast({ title: '正在拨打语音电话...', icon: 'none' });
						}
					}
				});
			},
			showMessageAction(message, index) {
				this.currentMessage = message;
				this.currentMessageIndex = index;
				this.showMessageMenu = true;
			},
			copyMessage() {
				if (!this.currentMessage) return;
				let text = '';
				if (this.currentMessage.isVoice) {
					text = '[语音消息]';
				} else if (this.currentMessage.isImage) {
					text = '[图片消息]';
				} else {
					text = this.currentMessage.content || '';
				}
				uni.setClipboardData({
					data: text,
					success: () => {
						uni.showToast({ title: '复制成功', icon: 'none' });
						this.showMessageMenu = false;
					}
				});
			},
			forwardMessage() {
				if (!this.currentMessage) return;
				uni.showToast({ title: '转发功能待实现', icon: 'none' });
				this.showMessageMenu = false;
			},
			deleteMessage() {
				if (!this.currentMessage || this.currentMessageIndex === -1) return;
				this.messages.splice(this.currentMessageIndex, 1);
				this.showMessageMenu = false;
				uni.showToast({ title: '已删除', icon: 'none' });
			},
			closeMessageMenu() {
				this.showMessageMenu = false;
				this.currentMessage = undefined;
				this.currentMessageIndex = -1;
			},
			onRefresherRefresh() {
				this.refresherTriggered = true;
				this.loadMoreMessages();
				setTimeout(() => {
					this.refresherTriggered = false;
				}, 800); // 加载完成后关闭动画
			},
		},
		onLoad(options) {
			if (options.name) this.userName = decodeURIComponent(options.name);
			if (options.avatar) this.userAvatar = decodeURIComponent(options.avatar);
			const date = new Date();
			this.currentDate = `${date.getMonth() + 1}月${date.getDate()}日`;
			this.scrollToBottom();
			setTimeout(() => {
				this.receiveMessage('那家餐厅的火锅特别好吃');
			}, 5000);
		},
		mounted() {
			// H5兼容参数获取
			if (!this.userName || !this.userAvatar) {
				try {
					const pages = getCurrentPages();
					const current = pages[pages.length - 1];
					const query = current.options || {};
					if (query.name) this.userName = decodeURIComponent(query.name);
					if (query.avatar) this.userAvatar = decodeURIComponent(query.avatar);
				} catch (e) { }
			}
			if (!this.currentDate) {
				const date = new Date();
				this.currentDate = `${date.getMonth() + 1}月${date.getDate()}日`;
			}
			this.scrollToBottom();
			setTimeout(() => {
				this.receiveMessage('那家餐厅的火锅特别好吃');
			}, 5000);
		}
	};
</script>

<style>
	page {
		height: 100%;
		background-color: #f5f5f5;
	}

	.chat-container {
		display: flex;
		flex-direction: column;
		height: 100%;
	}

	.chat-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-bottom: 1px solid #eee;
	}

	.user-info {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.chat-title {
		font-size: 18px;
		font-weight: 500;
		color: #333;
	}

	.online-status {
		font-size: 12px;
		color: #07c160;
		margin-top: 4rpx;
	}

	.online-status.offline {
		color: #999;
	}

	.header-actions {
		display: flex;
		gap: 30rpx;
	}

	.chat-content {
		flex: 1;
		padding: 20rpx 30rpx;
		overflow: auto;
	}

	.date-divider {
		text-align: center;
		font-size: 12px;
		color: #999;
		margin: 20rpx 0;
		padding: 5rpx 15rpx;
		background-color: rgba(0, 0, 0, 0.05);
		border-radius: 4rpx;
		align-self: center;
	}

	.message-item {
		display: flex;
		margin-bottom: 30rpx;
		position: relative;
	}

	.message-left {
		justify-content: flex-start;
	}

	.message-right {
		justify-content: flex-end;
	}

	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.self-avatar {
		margin-left: 20rpx;
		margin-right: 0;
	}

	.message-bubble {
		max-width: 500rpx;
		padding: 20rpx;
		border-radius: 10px;
		position: relative;
		word-break: break-word;
	}

	.message-left .message-bubble {
		background-color: #fff;
		margin-left: 0;
	}

	.message-right .message-bubble {
		background-color: #1989fa;
		margin-right: 0;
	}

	.message-text {
		font-size: 16px;
		line-height: 1.5;
		word-break: break-word;
	}

	.message-left .message-text {
		color: #333;
	}

	.message-right .message-text {
		color: #fff;
	}

	.message-time {
		position: absolute;
		bottom: -30rpx;
		font-size: 12px;
		color: #999;
		opacity: 0.8;
		padding: 0 10rpx;
	}

	.message-left .message-time {
		left: 0;
	}

	.message-right .message-time {
		right: 0;
	}

	.read-status {
		position: absolute;
		right: -30rpx;
		bottom: -10rpx;
	}

	.input-area {
		display: flex;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-top: 1px solid #eee;
	}

	.input-box {
		flex: 1;
		padding: 0 20rpx;
		margin: 0 20rpx;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		font-size: 16px;
		line-height: 1.4;
		overflow-y: auto;
	}

	.send-btn {
		height: 70rpx;
		border-radius: 35rpx;
		line-height: 70rpx;
		padding: 0 30rpx;
	}

	.voice-button {
		flex: 1;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		margin: 0 20rpx;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		font-size: 16px;
		color: #666;
	}

	.voice-button.cancel {
		background-color: #ffeeee;
		color: #f53f3f;
	}

	.voice-modal {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.3);
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 999;
	}

	.voice-modal-content {
		width: 300rpx;
		height: 300rpx;
		background-color: rgba(0, 0, 0, 0.7);
		border-radius: 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	.voice-tip {
		margin-top: 20rpx;
		color: #fff;
		font-size: 14px;
	}

	.voice-duration {
		margin-top: 10rpx;
		color: #fff;
		font-size: 16px;
	}

	.emoji-panel {
		background-color: #fff;
		border-top: 1px solid #eee;
		padding: 20rpx;
	}

	.emoji-list {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
		max-height: 300rpx;
		overflow-y: auto;
	}

	.emoji-item {
		font-size: 30rpx;
		padding: 10rpx;
		border-radius: 6rpx;
	}

	.emoji-item:active {
		background-color: #f1f1f1;
	}

	.emoji-footer {
		display: flex;
		justify-content: flex-end;
		margin-top: 15rpx;
	}

	.loading-more {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 20rpx 0;
		font-size: 14px;
		color: #999;
		gap: 10rpx;
	}

	.message-menu {
		background-color: #fff;
		border-radius: 16rpx 16rpx 0 0;
	}

	.menu-item {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 100rpx;
		border-bottom: 1px solid #eee;
		font-size: 16px;
		gap: 15rpx;
	}

	.menu-item.cancel {
		color: #f53f3f;
		margin-top: 15rpx;
		border-bottom: none;
	}

	.voice-message {
		display: flex;
		align-items: center;
		padding: 25rpx 20rpx;
	}

	.voice-content {
		display: flex;
		align-items: center;
		gap: 15rpx;
		width: 100%;
	}

	.voice-icon {
		flex-shrink: 0;
	}

	.voice-length {
		height: 4rpx;
		background-color: rgba(255, 255, 255, 0.5);
	}

	.message-right .voice-length {
		background-color: rgba(255, 255, 255, 0.5);
	}

	.message-left .voice-length {
		background-color: rgba(0, 0, 0, 0.1);
	}

	.voice-time {
		font-size: 12px;
		color: rgba(255, 255, 255, 0.7);
		flex-shrink: 0;
	}

	.message-left .voice-time {
		color: #999;
	}

	.sending-indicator {
		position: absolute;
		right: 10rpx;
		bottom: 10rpx;
	}

	.send-error {
		position: absolute;
		right: 10rpx;
		bottom: 10rpx;
	}

	.message-error {
		background-color: #fff3f3;
	}

	.message-error .message-text {
		color: #f53f3f;
	}
</style>