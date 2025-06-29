<template>
	<view class="page-container">
		<!-- 消息列表 -->
		<view class="message-list">
			<view class="message-item" v-for="message in messageList" :key="message.id"
				@click="viewMessageDetail(message.id)">
				<view class="message-avatar">
					<image class="avatar-image" :src="message.avatar" mode="aspectFill"></image>
				</view>
				<view class="message-content">
					<view class="message-header">
						<text class="message-name">{{ message.name }}</text>
						<text class="message-time">{{ message.time }}</text>
					</view>
					<text class="message-preview">{{ message.preview }}</text>
					<view class="message-badge" v-if="message.unread">{{ message.unread }}</view>
				</view>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref } from 'vue';

	// 消息列表数据
	const messageList = ref([
		{
			id: 1,
			name: '校园跑腿客服',
			avatar: 'https://ai-public.mastergo.com/ai/img_res/bc200e34b1ae0510e3b5c7f1d9c78358.jpg',
			preview: '您好，您的订单已接单，配送员将在10分钟内到达',
			time: '10:30',
			unread: '2'
		},
		{
			id: 2,
			name: '张同学',
			avatar: 'https://ai-public.mastergo.com/ai/img_res/243353e22400be23e8aaed34fbefc5bd.jpg',
			preview: '我已经到宿舍楼下了，请下来取快递',
			time: '09:45',
			unread: ''
		},
		{
			id: 3,
			name: '李老师',
			avatar: 'https://ai-public.mastergo.com/ai/img_res/03f7525e6138851af1131f3c8915c965.jpg',
			preview: '关于作业辅导的问题，我明天下午3点有空',
			time: '昨天',
			unread: '1'
		},
		{
			id: 4,
			name: '王同学',
			avatar: 'https://ai-public.mastergo.com/ai/img_res/ce2b0e0d7dd0a19514339b1c9307cf66.jpg',
			preview: '奶茶已经买好了，正在前往图书馆',
			time: '昨天',
			unread: ''
		},
		{
			id: 5,
			name: '系统通知',
			avatar: 'https://ai-public.mastergo.com/ai/img_res/d6b95191a98aa37e1efc90035cc3a6fd.jpg',
			preview: '您有新的订单评价，点击查看详情',
			time: '前天',
			unread: ''
		},
		{
			id: 6,
			name: '赵同学',
			avatar: 'https://ai-public.mastergo.com/ai/img_res/8682ed2af5cee5a36100bd13bb10633a.jpg',
			preview: '药品已经送到医务室了，请查收',
			time: '前天',
			unread: ''
		}
	]);

	const viewMessageDetail = (id : number) => {
		uni.navigateTo({
			url: `/pages/message-detail/index?id=${id}`
		});
	};
</script>
<style>
	page {
		height: 100%;
	}

	.page-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		background-color: #f5f5f5;
		padding-bottom: 120rpx;
		/* 给tabbar留出空间 */
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
	}

	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.message-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 10rpx;
	}

	.message-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		max-width: 400rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.message-time {
		font-size: 24rpx;
		color: #999;
	}

	.message-preview {
		font-size: 26rpx;
		color: #666;
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.message-badge {
		position: absolute;
		right: 26rpx;
		top: 82rpx;
		width: 36rpx;
		height: 36rpx;
		line-height: 36rpx;
		text-align: center;
		background-color: #ff4d4f;
		color: #fff;
		font-size: 22rpx;
		border-radius: 50%;
	}
</style>