<template>
	<view class="page-container">
		<!-- 消息列表 -->
		<view class="message-list">
			<view class="message-item" v-for="conversation in conversationList" :key="conversation.cid"
				@click="viewMessageDetail(conversation)">
				<view class="message-avatar">
					<image class="avatar-image" :src="getOtherUserAvatar(conversation)" mode="aspectFill"></image>
				</view>
				<view class="message-content">
					<view class="message-header">
						<text class="message-name">{{ getOtherUserName(conversation) }}</text>
						<text class="message-time">{{ formatTime(conversation.lastMessageTime) }}</text>
					</view>
					<text class="message-preview">{{ getLastMessagePreview(conversation) }}</text>
					<view class="message-badge" v-if="conversation.unreadCount > 0">{{ conversation.unreadCount > 99 ? '99+' : conversation.unreadCount }}</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="conversationList.length === 0 && !loading">
				<text class="empty-text">暂无消息</text>
			</view>
			
			<!-- 加载状态 -->
			<view class="loading-state" v-if="loading">
				<text class="loading-text">加载中...</text>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, onMounted } from 'vue';
	import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
	import { useUserStore } from '@/stores/user'
	import request from '../../utils/request';

	// 用户信息
	const userStore = useUserStore();
	const userInfo = userStore.info;

	// 会话列表数据
	const conversationList = ref([]);
	const loading = ref(false);

	// 获取用户会话列表
	const fetchConversationList = async () => {
		loading.value = true;
		try {
			const uid = userInfo?.uid;
			
			if (!uid) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				return;
			}

			const res = await request({
				url: `/conversation/list?uid=${uid}`,
				method: 'GET'
			});
			
			if (res.errCode === 0) {
				// 直接使用返回的 data 数组
				conversationList.value = res.data || [];
			} else {
				uni.showToast({
					title: res.msg || '加载失败',
					icon: 'none'
				});
			}
		} catch (error) {
			uni.showToast({
				title: '网络错误',
				icon: 'none'
			});
		} finally {
			loading.value = false;
			uni.stopPullDownRefresh();
		}
	};

	// 获取对方用户头像
	const getOtherUserAvatar = (conversation) => {
		return conversation.otherUserAvatar || '/static/images/default-avatar.png';
	};

	// 获取对方用户名称
	const getOtherUserName = (conversation) => {
		return conversation.otherUserNickname || '用户';
	};

	// 获取最后一条消息预览
	const getLastMessagePreview = (conversation) => {
		// 根据订单类型生成默认消息预览
		if (conversation.lastMessageContent) {
			return conversation.lastMessageContent;
		}
		
		const orderTypeMap = {
			'E': '快递代取',
			'T': '外卖代取', 
			'C': '物品搬运',
			'P': '商品代购'
		};
		
		const orderTypeText = orderTypeMap[conversation.orderType] || '订单';
		
		if (conversation.orderStatus === 'D') {
			return `${orderTypeText}等待接单中`;
		} else if (conversation.orderStatus === 'J') {
			return `${orderTypeText}进行中`;
		} else if (conversation.orderStatus === 'S') {
			return `${orderTypeText}已完成`;
		} else {
			return `您有一个${orderTypeText}订单`;
		}
	};

	// 查看消息详情
	const viewMessageDetail = (conversation) => {
		uni.navigateTo({
			url: `/pages/messages/private-chat/private-chat?conversationId=${conversation.cid}&otherUserId=${conversation.otherUserId}&otherUserName=${encodeURIComponent(conversation.otherUserNickname || '用户')}&otherUserAvatar=${encodeURIComponent(conversation.otherUserAvatar || '')}&orderId=${conversation.oid}`
		});
	};

	// 格式化时间显示
	const formatTime = (time: string | Date) => {
		if (!time) return '';
		
		const date = new Date(time);
		const now = new Date();
		const diff = now.getTime() - date.getTime();
		const days = Math.floor(diff / (1000 * 60 * 60 * 24));
		const hours = Math.floor(diff / (1000 * 60 * 60));
		const minutes = Math.floor(diff / (1000 * 60));
		
		if (minutes < 1) {
			return '刚刚';
		} else if (hours < 1) {
			return `${minutes}分钟前`;
		} else if (days === 0) {
			// 今天
			return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
		} else if (days === 1) {
			// 昨天
			return '昨天';
		} else if (days < 7) {
			// 一周内
			return `${days}天前`;
		} else {
			// 更早
			return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
		}
	};

	// 生命周期
	onMounted(() => {
		fetchConversationList();
	});

	onShow(() => {
		// 每次页面显示时刷新数据
		fetchConversationList();
	});

	onPullDownRefresh(() => {
		fetchConversationList();
	});
</script>

<style>
	page {
		height: 100%;
		background-color: #f5f5f5;
	}

	.page-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		background-color: #f5f5f5;
		padding-bottom: 120rpx; /* 给tabbar留出空间 */
	}

	/* 消息列表样式 */
	.message-list {
		flex: 1;
		overflow-y: auto;
		padding: 20rpx;
	}

	.message-item {
		display: flex;
		padding: 25rpx;
		background-color: #fff;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		position: relative;
	}

	.message-avatar {
		width: 100rpx;
		height: 100rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.avatar-image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		background-color: #f0f0f0;
	}

	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
		min-width: 0; /* 防止内容溢出 */
	}

	.message-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.message-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		flex: 1;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		margin-right: 20rpx;
	}

	.message-time {
		font-size: 24rpx;
		color: #999;
		flex-shrink: 0;
	}

	.message-preview {
		font-size: 26rpx;
		color: #666;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.message-badge {
		position: absolute;
		right: 26rpx;
		top: 82rpx;
		min-width: 36rpx;
		height: 36rpx;
		line-height: 36rpx;
		text-align: center;
		background-color: #ff4d4f;
		color: #fff;
		font-size: 22rpx;
		border-radius: 18rpx;
		padding: 0 8rpx;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}

	/* 加载状态 */
	.loading-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 40rpx 0;
	}

	.loading-text {
		font-size: 28rpx;
		color: #999;
	}
</style>