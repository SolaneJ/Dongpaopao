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
		<scroll-view class="chat-content" scroll-y :scroll-top="scrollTop" @scrolltolower="loadMoreMessages"
			@touchstart="handleTouchStart" @touchend="handleTouchEnd" ref="scrollRef">
			<!-- 日期分隔线 -->
			<view class="date-divider" v-if="messages.length > 0">
				{{ currentDate }}
			</view>

			<view v-for="(message, index) in messages" :key="message.id"
				:class="['message-item', message.isMe ? 'message-right' : 'message-left']">
				<image v-if="!message.isMe" class="avatar" :src="message.avatar || defaultAvatar"
					@click="viewUserAvatar(message.avatar)" />
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
				<uni-loading type="circle" size="20" color="#999"></uni-loading>
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
		<uni-popup v-model="showMessageMenu" type="bottom" :mask-click="true">
			<view class="message-menu">
				<view class="menu-item" @click="copyMessage">
					<uni-icons type="copy" size="20" color="#666" />
					<text>复制</text>
				</view>
				<view class="menu-item" @click="forwardMessage" v-if="!currentMessage.isMe">
					<uni-icons type="forward" size="20" color="#666" />
					<text>转发</text>
				</view>
				<view class="menu-item" @click="deleteMessage" v-if="currentMessage.isMe">
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

<script lang="ts" setup>
	import { ref, onMounted, nextTick, reactive } from 'vue';

	// 常量定义
	const defaultAvatar = 'https://ai-public.mastergo.com/ai/img_res/523d6d73426c46b6cd95b111d9cb3156.jpg';
	const myAvatar = 'https://ai-public.mastergo.com/ai/img_res/8f24da8382e95f64295c3f69c8e9e38.jpg';
	const emojis = ['😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊', '🙂', '🙃', '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '👍', '👎', '👊', '✌️', '🤝', '🙏', '🎉', '🎊', '🔥', '🥳', '😢', '😭', '😱', '😡', '🤔', '🤫', '🤭', '😴'];

	// 状态管理
	const scrollTop = ref(0);
	const inputText = ref('');
	const isRecording = ref(false);
	const recordStartTime = ref(0);
	const showVoiceModal = ref(false);
	const voiceTip = ref('按住说话');
	const recordDuration = ref(0);
	const isCancelRecording = ref(false);
	const showEmoji = ref(false);
	const inputHeight = ref('80rpx');
	const maxInputHeight = ref('200rpx');
	const isLoadingMore = ref(false);
	const currentDate = ref('');
	const showMessageMenu = ref(false);
	const currentMessage = ref<any>(null);
	const currentMessageIndex = ref(-1);
	const scrollRef = ref<any>(null);
	const startX = ref(0);

	// 用户信息
	const userName = ref('张三');
	const isOnline = ref(true);

	// 消息列表 - 增加了消息状态、唯一ID等字段
	const messages = ref([
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
	]);

	// 初始化
	onMounted(() => {
		// 设置当前日期
		const date = new Date();
		currentDate.value = `${date.getMonth() + 1}月${date.getDate()}日`;

		// 自动滚动到底部
		scrollToBottom();

		// 模拟接收消息
		setTimeout(() => {
			receiveMessage('那家餐厅的火锅特别好吃');
		}, 5000);
	});

	// 导航返回
	const handleBack = () => {
		uni.navigateBack({
			delta: 1
		});
	};

	// 显示操作菜单
	const showActionSheet = () => {
		uni.showActionSheet({
			itemList: ['查看资料', '设置备注', '消息免打扰', '删除会话'],
			success: (res) => {
				switch (res.tapIndex) {
					case 0:
						goToUserProfile();
						break;
					case 1:
						setRemark();
						break;
					case 2:
						setDoNotDisturb();
						break;
					case 3:
						deleteConversation();
						break;
				}
			}
		});
	};

	// 查看用户资料
	const goToUserProfile = () => {
		uni.navigateTo({
			url: '/pages/user-profile/index'
		});
	};

	// 设置备注
	const setRemark = () => {
		uni.showModal({
			title: '设置备注',
			inputValue: userName.value,
			success: (res) => {
				if (res.confirm && res.inputValue) {
					userName.value = res.inputValue;
					uni.showToast({ title: '设置成功', icon: 'none' });
				}
			}
		});
	};

	// 设置消息免打扰
	const setDoNotDisturb = () => {
		uni.showToast({
			title: '已开启消息免打扰',
			icon: 'none'
		});
	};

	// 删除会话
	const deleteConversation = () => {
		uni.showModal({
			title: '提示',
			content: '确定要删除该会话吗？',
			success: (res) => {
				if (res.confirm) {
					handleBack();
				}
			}
		});
	};

	// 加载更多消息
	const loadMoreMessages = () => {
		if (isLoadingMore.value) return;

		isLoadingMore.value = true;

		// 模拟加载历史消息
		setTimeout(() => {
			const newMessages = [
				{
					id: messages.value.length + 1,
					content: '还记得上次我们一起去看的电影吗？',
					time: '昨天 18:45',
					isMe: false,
					avatar: 'https://ai-public.mastergo.com/ai/img_res/5e99a8d9d5b5885d026bfe0121f707bc.jpg',
					status: 'read',
					read: false
				},
				{
					id: messages.value.length + 2,
					content: '当然记得，特别精彩！',
					time: '昨天 18:50',
					isMe: true,
					status: 'read',
					read: true
				}
			];

			// 插入到消息列表前面
			messages.value = [...newMessages, ...messages.value];
			isLoadingMore.value = false;
		}, 1000);
	};

	// 发送消息
	const sendMessage = () => {
		const content = inputText.value.trim();
		if (!content) return;

		// 添加消息到列表
		const newMessage = {
			id: messages.value.length + 1,
			content,
			time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
			isMe: true,
			status: 'sending', // 发送中状态
			read: false
		};

		messages.value.push(newMessage);
		inputText.value = '';
		resetInputHeight();
		scrollToBottom();

		// 模拟发送成功
		setTimeout(() => {
			const msgIndex = messages.value.findIndex(m => m.id === newMessage.id);
			if (msgIndex !== -1) {
				messages.value[msgIndex].status = 'read';
				messages.value[msgIndex].read = true;
			}
		}, 1000);
	};

	// 接收消息
	const receiveMessage = (content : string) => {
		const newMessage = {
			id: messages.value.length + 1,
			content,
			time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
			isMe: false,
			avatar: 'https://ai-public.mastergo.com/ai/img_res/5e99a8d9d5b5885d026bfe0121f707bc.jpg',
			status: 'read',
			read: false
		};

		messages.value.push(newMessage);
		scrollToBottom();

		// 播放提示音
		uni.vibrateShort();
	};

	// 重发消息
	const resendMessage = (index : number) => {
		if (index < 0 || index >= messages.value.length) return;

		// 更新消息状态为发送中
		messages.value[index].status = 'sending';

		// 模拟发送成功
		setTimeout(() => {
			messages.value[index].status = 'read';
			messages.value[index].read = true;
		}, 1000);
	};

	// 滚动到底部
	const scrollToBottom = () => {
		nextTick(() => {
			scrollTop.value = 999999;
		});
	};

	// 处理触摸开始
	const handleTouchStart = (e : TouchEvent) => {
		startX.value = e.touches[0].pageX;
	};

	// 处理触摸结束
	const handleTouchEnd = (e : TouchEvent) => {
		const endX = e.changedTouches[0].pageX;
		if (startX.value - endX > 100) {
			handleBack();
		}
	};

	// 切换输入模式（文本/语音）
	const toggleInputMode = () => {
		isRecording.value = !isRecording.value;
		voiceTip.value = '按住说话';
		showEmoji.value = false;
	};

	// 开始录音
	const startRecording = () => {
		recordStartTime.value = Date.now();
		showVoiceModal.value = true;
		voiceTip.value = '松开发送';
		isCancelRecording.value = false;

		// 开始计时
		const timer = setInterval(() => {
			recordDuration.value = Math.floor((Date.now() - recordStartTime.value) / 1000);
		}, 1000);

		// 开始录音
		uni.startRecord({
			success: () => {
				console.log('开始录音');
			},
			fail: (err) => {
				console.error('录音失败', err);
				clearInterval(timer);
				showVoiceModal.value = false;
				uni.showToast({ title: '录音失败', icon: 'none' });
			}
		});

		// 存储计时器，用于停止录音时清除
		(uni as any).recordTimer = timer;
	};

	// 停止录音
	const stopRecording = () => {
		clearInterval((uni as any).recordTimer);
		const duration = Date.now() - recordStartTime.value;
		showVoiceModal.value = false;

		// 短于1秒的录音视为无效
		if (duration < 1000) {
			uni.showToast({
				title: '说话时间太短',
				icon: 'none'
			});
			uni.stopRecord();
			recordDuration.value = 0;
			return;
		}

		// 如果是取消状态，不发送录音
		if (isCancelRecording.value) {
			uni.stopRecord();
			recordDuration.value = 0;
			isCancelRecording.value = false;
			return;
		}

		// 停止录音并处理结果
		uni.stopRecord({
			success: (res : any) => {
				messages.value.push({
					id: messages.value.length + 1,
					isVoice: true,
					duration: Math.floor(duration / 1000),
					time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
					isMe: true,
					status: 'read',
					read: true,
					tempFilePath: res.tempFilePath
				});
				scrollToBottom();
			},
			fail: (err : any) => {
				console.error('停止录音失败', err);
				uni.showToast({ title: '录音保存失败', icon: 'none' });
			}
		});

		recordDuration.value = 0;
	};

	// 取消录音
	const cancelRecording = (e : TouchEvent) => {
		const touchY = e.touches[0].pageY;
		const buttonTop = (e.target as any).offsetTop;

		// 向上滑动超过一定距离视为取消录音
		if (touchY < buttonTop - 50) {
			voiceTip.value = '松开手指，取消发送';
			isCancelRecording.value = true;
		} else {
			voiceTip.value = '松开发送';
			isCancelRecording.value = false;
		}
	};

	// 选择图片
	const chooseImage = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['original', 'compressed'],
			sourceType: ['album', 'camera'],
			success: (res) => {
				// 添加图片消息
				messages.value.push({
					id: messages.value.length + 1,
					isImage: true,
					imageUrl: res.tempFilePaths[0],
					time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
					isMe: true,
					status: 'sending',
					read: false
				});

				scrollToBottom();

				// 模拟图片发送成功
				setTimeout(() => {
					const lastIndex = messages.value.length - 1;
					messages.value[lastIndex].status = 'read';
					messages.value[lastIndex].read = true;
				}, 1000);
			}
		});
	};

	// 显示表情面板
	const showEmojiPanel = () => {
		showEmoji.value = !showEmoji.value;
		if (isRecording.value) {
			isRecording.value = false;
		}
	};

	// 添加表情
	const addEmoji = (emoji : string) => {
		inputText.value += emoji;
	};

	// 自动调整输入框高度
	const autoResizeInput = (e : any) => {
		const inputDom = e.target;
		// 重置高度以获取正确的scrollHeight
		inputDom.style.height = 'auto';
		// 计算新高度，不超过最大高度
		let newHeight = inputDom.scrollHeight + 'px';
		if (parseInt(newHeight) > parseInt(maxInputHeight.value)) {
			newHeight = maxInputHeight.value;
		}
		inputHeight.value = newHeight;
	};

	// 重置输入框高度
	const resetInputHeight = () => {
		inputHeight.value = '80rpx';
	};

	// 查看用户头像
	const viewUserAvatar = (avatarUrl : string) => {
		uni.previewImage({
			urls: [avatarUrl || defaultAvatar]
		});
	};

	// 拨打语音电话
	const makeCall = () => {
		uni.showModal({
			title: '提示',
			content: `是否拨打${userName.value}的语音电话？`,
			success: (res) => {
				if (res.confirm) {
					// 这里可以添加实际拨打语音电话的逻辑
					uni.showToast({ title: '正在拨打语音电话...', icon: 'none' });
				}
			}
		});
	};

	// 显示消息操作菜单
	const showMessageAction = (message : any, index : number) => {
		currentMessage.value = message;
		currentMessageIndex.value = index;
		showMessageMenu.value = true;
	};

	// 复制消息
	const copyMessage = () => {
		if (!currentMessage.value) return;

		let text = '';
		if (currentMessage.value.isVoice) {
			text = '[语音消息]';
		} else if (currentMessage.value.isImage) {
			text = '[图片消息]';
		} else {
			text = currentMessage.value.content;
		}

		uni.setClipboardData({
			data: text,
			success: () => {
				uni.showToast({ title: '复制成功', icon: 'none' });
				showMessageMenu.value = false;
			}
		});
	};

	// 转发消息
	const forwardMessage = () => {
		if (!currentMessage.value) return;

		uni.showToast({ title: '转发功能待实现', icon: 'none' });
		showMessageMenu.value = false;
	};

	// 删除消息
	const deleteMessage = () => {
		if (currentMessageIndex.value === -1) return;

		messages.value.splice(currentMessageIndex.value, 1);
		showMessageMenu.value = false;
		uni.showToast({ title: '已删除', icon: 'none' });
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