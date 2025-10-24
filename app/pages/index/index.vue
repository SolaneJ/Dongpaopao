<template>
	<u-notice-bar text="系统通知：本系统将于23:00~7:00进行维护。" speed="150" />
	<view class="page-container">
		<!-- Banner区 -->
		<view class="banner-container">
			<swiper class="swiper" circular interval="1500" autoplay="true">
				<swiper-item>
					<image class="banner-image"
						src="https://ai-public.mastergo.com/ai/img_res/6945a6b7e8506df1abcd0efce30213d7.jpg"
						mode="widthFix" />
				</swiper-item>
				<swiper-item>
					<image class="banner-image"
						src="https://ai-public.mastergo.com/ai/img_res/ea7587e8643368749c2e819938953839.jpg"
						mode="widthFix" />
				</swiper-item>
				<swiper-item>
					<image class="banner-image"
						src="https://ai-public.mastergo.com/ai/img_res/120fce42fd9e45edcd603cc750ec7054.jpg"
						mode="widthFix" />
				</swiper-item>
			</swiper>
		</view>
		<!-- 顶部功能图标区 -->
		<view class="function-grid">
			<view class="function-item" v-for="item in functionList" :key="item.id" @click="navigateTo(item.type)">
				<view class="icon-wrapper">
					<uni-icons :type="item.icon" size="40" :color="item.color"></uni-icons>
				</view>
				<text class="function-text">{{ item.name }}</text>
			</view>
		</view>
		<!-- 进行中订单区 -->
		<view class="active-order-section">
			<view class="section-header">
				<view class="section-title">
					<text>进行中订单</text>
					<u-badge type="primary" :value="activeOrders.length" />
				</view>
				<view class="more-link" @click="goToUserOrders">
					<text>更多</text>
					<uni-icons type="arrowright" size="20" color="#999" />
				</view>
			</view>
			<view class="active-order-card" v-for="order in activeOrders" :key="order.oid"
				@click="viewOrderDetail(order.oid)">
				<view class="order-header">
					<text class="order-title">{{ getOrderTypeText(order.orderType) }}</text>
				</view>
				<text class="order-desc">{{ getOrderDescription(order) }}</text>
				<view class="order-footer">
					<text class="order-time">期望送达时间：{{ formatTime(order.expectTime) }}</text>
					<text class="order-price">¥{{ order.amount }}</text>
				</view>
			</view>
			<u-empty mode="order" text="暂无进行中订单" v-if="activeOrders.length === 0" />
			<!-- 
			<view class="empty-tip" v-if="activeOrders.length === 0">
				<text>暂无进行中订单</text>
			</view> 
			-->
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref } from 'vue';
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user.js'
	import { onShow } from '@dcloudio/uni-app'

	const userInfo = useUserStore().info

	// 订单类型映射
	const orderTypeMap = {
		'E': '代取快递',
		'T': '代取外卖',
		'C': '搬运',
		'P': '代购'
	};

	// Uniapp 生命周期
	onShow(() => {
		fetchActiveOrders();
	})

	// 功能图标数据
	const functionList = ref([
		{ id: 1, name: '代取快递', icon: 'paperplane', color: '#4285F4', type: 'E' },
		{ id: 2, name: '代取外卖', icon: 'shop', color: '#EA4335', type: 'T' },
		{ id: 3, name: '搬运', icon: 'cart', color: '#FBBC05', type: 'C' },
		{ id: 4, name: '代购', icon: 'list', color: '#34A853', type: 'P' }
	])

	// 进行中订单数据
	const activeOrders = ref([]);

	// 获取进行中订单
	const fetchActiveOrders = async () => {
		request({
			url: '/order/progressing?type=jdr&uid=' + userInfo.uid,
		}).then((res) => {
			activeOrders.value = res.data
		})
	};

	// 获取订单类型文本
	const getOrderTypeText = (type) => {
		return orderTypeMap[type] || '未知类型';
	};

	// 获取订单描述
	const getOrderDescription = (order) => {
		try {
			const detail = JSON.parse(order.detail);
			switch (order.orderType) {
				case 'E':
					return `${detail.company || '快递'} | 取件码：${detail.code} | ${detail.location}`;
				case 'T':
					return `取餐码：${detail.code} | ${detail.location}`;
				case 'C':
					return `搬运服务 | ${detail.location}`;
				case 'P':
					return `代购服务 | ${detail.location}`;
				default:
					return '订单详情';
			}
		} catch (e) {
			return '订单详情';
		}
	};

	// 格式化时间
	const formatTime = (timeString) => {
		if (!timeString) return '';
		const date = new Date(timeString);
		const now = new Date();
		const diff = now.getTime() - date.getTime();
		const minutes = Math.floor(diff / (1000 * 60));
		const hours = Math.floor(diff / (1000 * 60 * 60));
		const days = Math.floor(diff / (1000 * 60 * 60 * 24));

		if (minutes < 60) {
			return `${minutes}分钟前`;
		} else if (hours < 24) {
			return `${hours}小时前`;
		} else {
			return `${days}天前`;
		}
	};

	const navigateTo = (type : string) => {
		uni.navigateTo({
			url: `/pages/index/order?type=${type}`
		})
	}
	const goToUserOrders = () => {
		uni.navigateTo({
			url: '/pages/user/orders/orders?tab=active'
		});
	};
	const viewOrderDetail = (oid : string) => {
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?oid=${oid}`
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
	}

	/* Banner区样式 */
	.banner-container {
		width: 100%;
		height: 300rpx;
	}

	.banner-image {
		width: 100%;
		height: 100%;
		border-radius: 0 0 16rpx 16rpx;
	}

	/* 功能图标区样式 */
	.function-grid {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 30rpx;
		background-color: #fff;
		margin-top: -20rpx;
		border-radius: 16rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		margin: -20rpx 20rpx 0;
		position: relative;
		z-index: 2;
	}

	.function-item {
		width: 20%;
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.icon-wrapper {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-bottom: 10rpx;
		border-radius: 50%;
	}

	.function-text {
		font-size: 24rpx;
		color: #333;
	}

	/* 进行中订单区样式 */
	.active-order-section {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: inline-flex;
		align-items: center;
	}

	.active-order-card {
		background-color: #f9f9f9;
		border-radius: 12rpx;
		padding: 25rpx;
		position: relative;
		margin-bottom: 20rpx;
	}

	.active-order-card:last-child {
		margin-bottom: 0;
	}

	.order-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 15rpx;
	}

	.order-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.order-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: block;
	}

	.order-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.order-time {
		font-size: 24rpx;
		color: #999;
	}

	.order-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #1a73e8;
	}

	.more-link {
		display: flex;
		align-items: center;
	}

	.more-link text {
		margin-right: 8rpx;
	}
</style>