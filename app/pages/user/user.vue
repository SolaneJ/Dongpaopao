<template>
	<view class="page-container">
		<!-- 用户信息区域 -->
		<view class="user-info"  @click="navigateTo('/pages/user/profile/profile')">
			<view class="user-avatar">
				<image class="avatar-image" :src="userInfo.avatar" mode="aspectFill"></image>
			</view>
			<view class="user-detail">
				<text class="user-name">{{ userInfo.name }}</text>
				<text class="user-id">ID: {{ userInfo.id }}</text>
			</view>
			<uni-icons type="arrowright" size="20" color="#999"></uni-icons>
		</view>

		<!-- 功能列表 -->
		<view class="function-list">
			<view class="function-item" v-for="item in functionList" :key="item.id" @click="navigateTo(item.path)">
				<uni-icons :type="item.icon" size="24" color="#666"></uni-icons>
				<text class="function-name">{{ item.name }}</text>
				<uni-icons type="arrowright" size="20" color="#999"></uni-icons>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref } from 'vue';

	// 用户信息
	const userInfo = ref({
		name: '校园跑腿达人',
		id: 'ST20230001',
		avatar: 'https://ai-public.mastergo.com/ai/img_res/8243a04aa54f4e3cda41894c1e6cd0bd.jpg'
	});

	// 功能列表
	const functionList = ref([
		{ id: 1, name: '我的订单', icon: 'list', path: '/pages/user/orders/orders' },
		{ id: 2, name: '我的钱包', icon: 'wallet', path: '/pages/wallet/index' },
		{ id: 3, name: '我的地址', icon: 'location', path: '/pages/user/address/address' },
		{ id: 4, name: '设置', icon: 'gear', path: '/pages/settings/index' },
		{ id: 5, name: '帮助中心', icon: 'help', path: '/pages/help/index' },
		{ id: 6, name: '关于我们', icon: 'info', path: '/pages/about/index' }
	]);

	const navigateTo = (path: string) => {
	  uni.navigateTo({
	    url: path
	  });
	};
	// 示例：跳转到pages目录下的detail页面
	uni.navigateTo({
	  url: '/pages/detail/detail?id=123&name=uniapp'
	});
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

	/* 用户信息区域 */
	.user-info {
		display: flex;
		align-items: center;
		padding: 40rpx 30rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
	}

	.user-avatar {
		width: 120rpx;
		height: 120rpx;
		margin-right: 30rpx;
		flex-shrink: 0;
	}

	.avatar-image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
	}

	.user-detail {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.user-name {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.user-id {
		font-size: 24rpx;
		color: #999;
	}

	/* 功能列表 */
	.function-list {
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		margin: 0 20rpx;
	}

	.function-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.function-item:last-child {
		border-bottom: none;
	}

	.function-name {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		margin-left: 20rpx;
	}
</style>