<template>
	<view class="page-container">
		<!-- Banner区 -->
		<view class="banner-container">
			<image class="banner-image"
				src="https://ai-public.mastergo.com/ai/img_res/18ab4fc61c2f01fef4e5fb883df33b29.jpg" mode="widthFix">
			</image>
		</view>
		<!-- 顶部功能图标区 -->
		<view class="function-grid">
			<view class="function-item" v-for="item in functionList" :key="item.id">
				<view class="icon-wrapper">
					<uni-icons :type="item.icon" size="40" :color="item.color"></uni-icons>
				</view>
				<text class="function-text">{{ item.name }}</text>
			</view>
		</view>
		<!-- 进行中订单区 -->
		<view class="active-order-section">
			<view class="section-header">
				<text class="section-title">进行中订单<text class="order-count">1</text></text>
				<view class="more-link" @click="viewOrderDetail(activeOrder.id)">
					<text>更多</text>
					<uni-icons type="arrowright" size="20" color="#999"></uni-icons>
				</view>
			</view>
			<view class="active-order-card" v-for="order in activeOrders" :key="order.id">
				<view class="order-header">
					<text class="order-title">{{ order.title }}</text>
					<text class="order-status">{{ order.status }}</text>
				</view>
				<text class="order-desc">{{ order.desc }}</text>
				<view class="progress-container">
					<view class="progress-bar" :style="{ width: order.progress + '%' }"></view>
				</view>
				<view class="order-footer">
					<text class="order-time">预计{{ order.estimateTime }}完成</text>
				</view>
			</view>
			<view class="empty-tip" v-if="activeOrders.length === 0">
				<text>暂无进行中订单</text>
			</view>
		</view>
		<!-- 附近订单区 -->
		<view class="order-section">
			<view class="section-header">
				<text class="section-title">附近订单</text>
				<view class="more-link" @click="goToMoreOrders">
					<text>更多</text>
					<uni-icons type="arrowright" size="20" color="#999"></uni-icons>
				</view>
			</view>
			<scroll-view scroll-x class="horizontal-order-list">
				<view class="order-card" v-for="order in orderList" :key="order.id" @click="viewOrderDetail(order.id)">
					<view class="order-header">
						<text class="order-title">{{ order.title }}</text>
						<text class="order-distance">{{ order.distance }}</text>
					</view>
					<text class="order-desc">{{ order.desc }}</text>
					<view class="order-footer">
						<text class="order-price">¥{{ order.price }}</text>
						<text class="order-time">{{ order.time }}</text>
					</view>
				</view>
			</scroll-view>
		</view>
		<!-- 底部Tabbar -->
		<view class="tabbar">
			<view class="tabbar-item" v-for="tab in tabList" :key="tab.id">
				<uni-icons :type="tab.icon" size="24" :color="tab.active ? '#1a73e8' : '#666'"></uni-icons>
				<text class="tabbar-text" :style="{ color: tab.active ? '#1a73e8' : '#666' }">{{ tab.name }}</text>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref } from 'vue';
	// 功能图标数据
	const functionList = ref([
		{ id: 1, name: '代取快递', icon: 'paperplane', color: '#4285F4' },
		{ id: 2, name: '代取外卖', icon: 'shop', color: '#EA4335' },
		{ id: 3, name: '搬运', icon: 'cart', color: '#FBBC05' },
		{ id: 4, name: '代购', icon: 'list', color: '#34A853' }
	]);
	// 进行中订单数据
	const activeOrders = ref([
		{
			id: 1,
			title: '代取快递到宿舍',
			desc: '3个快递，从菜鸟驿站到3号宿舍楼',
			status: '配送中',
			progress: 60,
			estimateTime: '10分钟'
		},
		{
			id: 2,
			title: '代买奶茶',
			desc: '喜茶多肉葡萄，送到图书馆3楼',
			status: '待取件',
			progress: 30,
			estimateTime: '15分钟'
		}
	]);
	// 附近订单数据
	const orderList = ref([
		{
			id: 1,
			title: '代取快递到宿舍',
			desc: '3个快递，从菜鸟驿站到3号宿舍楼',
			price: '8',
			distance: '500m',
			time: '10分钟前'
		},
		{
			id: 2,
			title: '代买奶茶',
			desc: '喜茶多肉葡萄，送到图书馆3楼',
			price: '5',
			distance: '300m',
			time: '15分钟前'
		},
		{
			id: 3,
			title: '搬运行李',
			desc: '从5号楼到校门口，2个行李箱',
			price: '15',
			distance: '800m',
			time: '20分钟前'
		},
		{
			id: 4,
			title: '代买药品',
			desc: '感冒药和退烧药，送到医务室',
			price: '10',
			distance: '600m',
			time: '25分钟前'
		},
		{
			id: 5,
			title: '作业辅导',
			desc: '数据结构算法题讲解',
			price: '30',
			distance: '200m',
			time: '30分钟前'
		},
		{
			id: 6,
			title: '代买早餐',
			desc: '豆浆油条，送到教学楼201',
			price: '5',
			distance: '400m',
			time: '35分钟前'
		},
		{
			id: 7,
			title: '代打印资料',
			desc: '20页A4纸，送到实验室',
			price: '3',
			distance: '700m',
			time: '40分钟前'
		}
	]);
	// Tabbar数据
	const tabList = ref([
		{ id: 1, name: '首页', icon: 'home', active: true },
		{ id: 2, name: '订单', icon: 'list', active: false },
		{ id: 3, name: '消息', icon: 'chat', active: false },
		{ id: 4, name: '我的', icon: 'person', active: false }
	]);
	const goToMoreOrders = () => {
		uni.navigateTo({
			url: '/pages/orders/index'
		});
	};
	const viewOrderDetail = (id : number) => {
		uni.navigateTo({
			url: `/pages/order-detail/index?id=${id}`
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
		background-color: #f8f8f8;
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

	.order-count {
		display: inline-block;
		margin-left: 10rpx;
		min-width: 32rpx;
		height: 32rpx;
		line-height: 32rpx;
		text-align: center;
		background-color: #1a73e8;
		color: #fff;
		font-size: 22rpx;
		border-radius: 16rpx;
		padding: 0 10rpx;
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

	.order-status {
		font-size: 24rpx;
		color: #FF9800;
		background-color: #FFF3E0;
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
	}

	.order-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: block;
	}

	.progress-container {
		height: 8rpx;
		background-color: #eee;
		border-radius: 4rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
	}

	.progress-bar {
		height: 100%;
		background-color: #1a73e8;
		border-radius: 4rpx;
		transition: width 0.3s ease;
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

	.empty-tip {
		text-align: center;
		padding: 40rpx 0;
		color: #999;
		font-size: 28rpx;
	}

	/* 附近订单区样式 */
	.order-section {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.more-link {
		display: flex;
		align-items: center;
	}

	.more-link text {
		margin-right: 8rpx;
	}

	.horizontal-order-list {
		white-space: nowrap;
		padding-bottom: 10rpx;
	}

	.order-card {
		display: inline-block;
		width: 280rpx;
		background-color: #fff;
		border-radius: 16rpx;
		padding: 25rpx;
		margin-right: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
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
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.order-distance {
		font-size: 24rpx;
		color: #999;
	}

	.order-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.order-footer {
		display: flex;
		justify-content: space-between;
	}

	.order-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #1a73e8;
	}

	.order-time {
		font-size: 24rpx;
		color: #999;
	}

	/* Tabbar样式 */
	.tabbar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 120rpx;
		background-color: #fff;
		display: flex;
		justify-content: space-around;
		align-items: center;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		z-index: 100;
	}

	.tabbar-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.tabbar-text {
		font-size: 24rpx;
		margin-top: 10rpx;
	}
</style>